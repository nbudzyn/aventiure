package de.nb.aventiure2.scaction;

import android.content.Context;

import androidx.annotation.Nullable;

import com.google.common.collect.ImmutableList;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.ParametersAreNonnullByDefault;

import de.nb.aventiure2.data.database.AvDatabase;
import de.nb.aventiure2.data.storystate.StoryState;
import de.nb.aventiure2.data.world.base.IGameObject;
import de.nb.aventiure2.data.world.gameobjects.GameObjectService;
import de.nb.aventiure2.data.world.gameobjects.player.SpielerCharakter;
import de.nb.aventiure2.data.world.syscomp.alive.ILivingBeingGO;
import de.nb.aventiure2.data.world.syscomp.description.IDescribableGO;
import de.nb.aventiure2.data.world.syscomp.location.ILocatableGO;
import de.nb.aventiure2.data.world.syscomp.spatialconnection.ISpatiallyConnectedGO;
import de.nb.aventiure2.data.world.syscomp.storingplace.ILocationGO;
import de.nb.aventiure2.data.world.syscomp.talking.ITalkerGO;
import de.nb.aventiure2.scaction.impl.AblegenAction;
import de.nb.aventiure2.scaction.impl.BewegenAction;
import de.nb.aventiure2.scaction.impl.EssenAction;
import de.nb.aventiure2.scaction.impl.HeulenAction;
import de.nb.aventiure2.scaction.impl.HochwerfenAction;
import de.nb.aventiure2.scaction.impl.KletternAction;
import de.nb.aventiure2.scaction.impl.NehmenAction;
import de.nb.aventiure2.scaction.impl.RedenAction;
import de.nb.aventiure2.scaction.impl.SchlafenAction;

import static de.nb.aventiure2.data.world.gameobjects.GameObjectService.HAENDE_DES_SPIELER_CHARAKTERS;
import static de.nb.aventiure2.data.world.gameobjects.GameObjectService.SPIELER_CHARAKTER;

/**
 * Repository for the actions the player can choose from.
 */
@ParametersAreNonnullByDefault
public class ScActionService {
    private final AvDatabase db;
    private final GameObjectService gos;

    // Note that in order to unit test the repository, you have to remove the Application
    // dependency. This adds complexity and much more code, and this sample is not about testing.
    // See the BasicSample in the android-architecture-components repository at
    // https://github.com/googlesamples
    public ScActionService(final Context context) {
        db = AvDatabase.getDatabase(context);

        gos = GameObjectService.getInstance(db);
    }

    // TODO Have a convention like "Never do this", better have typical combinations
    //  available?
    public <DESC_OBJ extends ILocatableGO & IDescribableGO,
            LIV extends ILocatableGO & IDescribableGO & ILivingBeingGO> List<AbstractScAction>
    getPlayerActions() {
        final StoryState currentStoryState = db.storyStateDao().getStoryState();

        final SpielerCharakter spielerCharakter = gos.loadSC();

        final @Nullable ILocationGO room = spielerCharakter.locationComp().getLocation();

        final ImmutableList<DESC_OBJ> scInventoryObjects =
                gos.loadDescribableNonLivingRecursiveInventory(SPIELER_CHARAKTER);
        final ImmutableList<LIV> scInventoryLivingBeings =
                gos.loadDescribableLivingRecursiveInventory(SPIELER_CHARAKTER);
        final ImmutableList<? extends ILocatableGO> wasSCInDenHaendenHat =
                gos.loadDescribableRecursiveInventory(HAENDE_DES_SPIELER_CHARAKTERS);

        final ImmutableList<DESC_OBJ> objectsInRoom =
                room != null ? gos.loadDescribableNonLivingRecursiveInventory(room) :
                        ImmutableList.of();
        final ImmutableList<LIV> livingBeingsInRoom =
                room != null ? gos.loadDescribableLivingRecursiveInventory(room) :
                        ImmutableList.of();

        final List<AbstractScAction> res = new ArrayList<>();

        if (room != null) {
            res.addAll(buildCreatureInRoomActions(currentStoryState, spielerCharakter,
                    wasSCInDenHaendenHat, room, livingBeingsInRoom));
        }
        if (!spielerCharakter.talkingComp().isInConversation()) {
            res.addAll(buildPlayerOnlyAction(
                    currentStoryState, spielerCharakter, wasSCInDenHaendenHat, room,
                    livingBeingsInRoom));
            if (room != null) {
                res.addAll(buildObjectInRoomActions(
                        currentStoryState, wasSCInDenHaendenHat, room, objectsInRoom));
            }
            res.addAll(buildInventoryActions(currentStoryState, wasSCInDenHaendenHat, room,
                    scInventoryLivingBeings));
            res.addAll(buildInventoryActions(currentStoryState, wasSCInDenHaendenHat, room,
                    scInventoryObjects));

            if (room instanceof ISpatiallyConnectedGO) {
                res.addAll(buildRoomActions(
                        currentStoryState, (ILocationGO & ISpatiallyConnectedGO) room));
            }
        }

        return res;
    }

    private <DESC_OBJ extends ILocatableGO & IDescribableGO,
            LIV extends IDescribableGO & ILocatableGO & ILivingBeingGO>
    ImmutableList<AbstractScAction> buildCreatureInRoomActions(
            final StoryState currentStoryState,
            final SpielerCharakter spielerCharakter,
            final List<? extends ILocatableGO> wasSCInDenHaendenHat,
            final ILocationGO room,
            final List<LIV> creaturesInRoom) {
        final ImmutableList.Builder<AbstractScAction> res = ImmutableList.builder();

        for (final LIV creature : creaturesInRoom) {
            if (creature instanceof ITalkerGO) {
                res.addAll(RedenAction.buildActions(db, gos, currentStoryState, room,
                        (IDescribableGO & ITalkerGO) creature));
            }
            if (wasSCInDenHaendenHat.isEmpty() &&
                    !spielerCharakter.talkingComp().isInConversation()) {
                if (creature.locationComp().isMovable()) {
                    res.addAll(NehmenAction
                            .buildCreatureActions(db, gos, currentStoryState, creature));
                }
            }
        }

        return res.build();
    }

    private <DESC_OBJ extends ILocatableGO & IDescribableGO>
    ImmutableList<AbstractScAction> buildPlayerOnlyAction(
            final StoryState currentStoryState, final SpielerCharakter spielerCharakter,
            final List<? extends ILocatableGO> wasSCInDenHaendenHat,
            final @Nullable IGameObject room,
            final List<? extends ILivingBeingGO> creaturesInRoom) {
        final ImmutableList.Builder<AbstractScAction> res = ImmutableList.builder();

        res.addAll(HeulenAction
                .buildActions(db, gos, currentStoryState, spielerCharakter, creaturesInRoom));

        if (wasSCInDenHaendenHat.isEmpty()) {
            res.addAll(SchlafenAction.buildActions(db, gos, currentStoryState, room));
        }

        return res.build();
    }

    private <DESC_OBJ extends ILocatableGO & IDescribableGO>
    ImmutableList<AbstractScAction> buildObjectInRoomActions(
            final StoryState currentStoryState,
            final List<? extends ILocatableGO> wasSCInDenHaendenHat,
            final ILocationGO room,
            final List<? extends DESC_OBJ> objectsInRoom) {
        final ImmutableList.Builder<AbstractScAction> res = ImmutableList.builder();
        for (final DESC_OBJ object : objectsInRoom) {
            if (object instanceof ITalkerGO) {
                res.addAll(RedenAction.buildActions(db, gos, currentStoryState, room,
                        (IDescribableGO & ITalkerGO) object));
            }

            if (wasSCInDenHaendenHat.isEmpty() && object.locationComp().isMovable()) {
                res.addAll(
                        NehmenAction.buildObjectActions(db, gos, currentStoryState, object));
            }
        }

        if (wasSCInDenHaendenHat.isEmpty()) {
            res.addAll(EssenAction.buildActions(db, gos, currentStoryState, room));
        }

        return res.build();
    }

    private <DESC_OBJ extends IDescribableGO & ILocatableGO>
    ImmutableList<AbstractScAction> buildInventoryActions(
            final StoryState currentStoryState,
            final List<? extends ILocatableGO> wasSCInDenHaendenHat,
            final @Nullable ILocationGO room,
            final List<DESC_OBJ> inventory) {
        final ImmutableList.Builder<AbstractScAction> res = ImmutableList.builder();

        for (final DESC_OBJ inventoryObject : inventory) {
            if (room != null) {
                if (scHatHoechstensInDenHaenden(wasSCInDenHaendenHat, inventoryObject) &&
                        inventoryObject.locationComp().isMovable()) {
                    // Das inventoryObject könnte auch ein ILivingBeing sein!
                    res.addAll(HochwerfenAction
                            .buildActions(
                                    db, gos, currentStoryState, room, inventoryObject));
                    res.addAll(
                            AblegenAction.buildActions(
                                    db, gos, currentStoryState, inventoryObject, room));
                }
            }
        }
        return res.build();
    }

    /**
     * Gibt <code>true</code> zurück, wenn der Spieler oder nur dieses Game Object in
     * den Händen hat - sonst <code>false</code>.
     */
    private static boolean scHatHoechstensInDenHaenden(
            final List<? extends ILocatableGO> wasSCInDenHaendenHat, final IGameObject gameObject) {
        if (wasSCInDenHaendenHat.isEmpty()) {
            return true;
        }

        if (wasSCInDenHaendenHat.size() > 1) {
            return false;
        }

        return wasSCInDenHaendenHat.iterator().next().equals(gameObject);
    }

    private <R extends ISpatiallyConnectedGO & ILocationGO>
    ImmutableList<AbstractScAction> buildRoomActions(
            final StoryState currentStoryState,
            final R room) {
        final ImmutableList.Builder<AbstractScAction> res = ImmutableList.builder();

        res.addAll(buildRoomSpecificActions(currentStoryState, room));
        res.addAll(BewegenAction.buildActions(db, gos, currentStoryState, room));

        return res.build();
    }

    private ImmutableList<AbstractScAction> buildRoomSpecificActions(
            final StoryState currentStoryState, final ILocationGO room) {
        final ImmutableList.Builder<AbstractScAction> res = ImmutableList.builder();

        res.addAll(KletternAction.buildActions(db, gos, currentStoryState, room));

        return res.build();
    }
}