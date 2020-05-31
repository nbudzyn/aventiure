package de.nb.aventiure2.scaction;

import android.app.Application;

import androidx.annotation.Nullable;

import com.google.common.collect.ImmutableList;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.ParametersAreNonnullByDefault;

import de.nb.aventiure2.data.database.AvDatabase;
import de.nb.aventiure2.data.storystate.StoryState;
import de.nb.aventiure2.data.world.base.IGameObject;
import de.nb.aventiure2.data.world.gameobjects.player.SpielerCharakter;
import de.nb.aventiure2.data.world.syscomp.alive.ILivingBeingGO;
import de.nb.aventiure2.data.world.syscomp.description.IDescribableGO;
import de.nb.aventiure2.data.world.syscomp.location.ILocatableGO;
import de.nb.aventiure2.data.world.syscomp.spatialconnection.ISpatiallyConnectedGO;
import de.nb.aventiure2.data.world.syscomp.storingplace.IHasStoringPlaceGO;
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

import static de.nb.aventiure2.data.world.gameobjects.GameObjects.SPIELER_CHARAKTER;
import static de.nb.aventiure2.data.world.gameobjects.GameObjects.loadDescribableLivingInventory;
import static de.nb.aventiure2.data.world.gameobjects.GameObjects.loadDescribableNonLivingInventory;
import static de.nb.aventiure2.data.world.gameobjects.GameObjects.loadSC;

/**
 * Repository for the actions the player can choose from.
 */
@ParametersAreNonnullByDefault
public class ScActionService {
    private final AvDatabase db;

    // Note that in order to unit test the repository, you have to remove the Application
    // dependency. This adds complexity and much more code, and this sample is not about testing.
    // See the BasicSample in the android-architecture-components repository at
    // https://github.com/googlesamples
    public ScActionService(final Application application) {
        db = AvDatabase.getDatabase(application);
    }

    // TODO Have a convention like "Never do this", better have typical combinations
    //  available?
    public <DESC_OBJ extends ILocatableGO & IDescribableGO,
            LIV extends ILocatableGO & IDescribableGO & ILivingBeingGO> List<AbstractScAction>
    getPlayerActions() {
        final StoryState currentStoryState = db.storyStateDao().getStoryState();

        final SpielerCharakter spielerCharakter = loadSC(db);

        final @Nullable IHasStoringPlaceGO room = spielerCharakter.locationComp().getLocation();

        final ImmutableList<DESC_OBJ> objectInventory =
                loadDescribableNonLivingInventory(db, SPIELER_CHARAKTER);
        final ImmutableList<LIV> livingBeingsInventory =
                loadDescribableLivingInventory(db, SPIELER_CHARAKTER);

        final ImmutableList<DESC_OBJ> objectsInRoom =
                room != null ? loadDescribableNonLivingInventory(db, room) : ImmutableList.of();
        final ImmutableList<LIV> livingBeingsInRoom =
                room != null ? loadDescribableLivingInventory(db, room) : ImmutableList.of();

        final List<AbstractScAction> res = new ArrayList<>();

        if (room != null) {
            res.addAll(buildCreatureInRoomActions(currentStoryState, room,
                    livingBeingsInRoom));
        }
        if (!spielerCharakter.talkingComp().isInConversation()) {
            res.addAll(
                    buildPlayerOnlyAction(currentStoryState, spielerCharakter,
                            room, livingBeingsInRoom));
            if (room != null) {
                res.addAll(buildObjectInRoomActions(currentStoryState, room, objectsInRoom));
            }
            res.addAll(buildInventoryActions(currentStoryState, room, livingBeingsInventory));
            res.addAll(buildInventoryActions(currentStoryState, room, objectInventory));

            if (room instanceof ISpatiallyConnectedGO) {
                res.addAll(buildRoomActions(
                        currentStoryState, (IHasStoringPlaceGO & ISpatiallyConnectedGO) room));
            }
        }

        return res;
    }

    private <LIV extends IDescribableGO & ILocatableGO & ILivingBeingGO>
    ImmutableList<AbstractScAction> buildCreatureInRoomActions(
            final StoryState currentStoryState,
            final IHasStoringPlaceGO room,
            final List<LIV> creaturesInRoom) {
        final ImmutableList.Builder<AbstractScAction> res = ImmutableList.builder();

        for (final LIV creature : creaturesInRoom) {
            if (creature instanceof ITalkerGO) {
                res.addAll(RedenAction.buildActions(db, currentStoryState, room,
                        (IDescribableGO & ITalkerGO) creature));
            }
            if (creature.locationComp().isMovable()) {
                res.addAll(
                        NehmenAction.buildCreatureActions(db, currentStoryState,
                                room, creature));
            }
        }

        return res.build();
    }

    private ImmutableList<AbstractScAction> buildPlayerOnlyAction(
            final StoryState currentStoryState, final SpielerCharakter spielerCharakter,
            final @Nullable IGameObject room,
            final List<? extends ILivingBeingGO> creaturesInRoom) {
        final ImmutableList.Builder<AbstractScAction> res = ImmutableList.builder();

        res.addAll(HeulenAction
                .buildActions(db, currentStoryState, spielerCharakter, creaturesInRoom));

        res.addAll(SchlafenAction.buildActions(db, currentStoryState, room));

        return res.build();
    }

    private <DESC_OBJ extends ILocatableGO & IDescribableGO>
    ImmutableList<AbstractScAction> buildObjectInRoomActions(
            final StoryState currentStoryState,
            final IHasStoringPlaceGO room,
            final List<DESC_OBJ> objectsInRoom) {
        final ImmutableList.Builder<AbstractScAction> res = ImmutableList.builder();
        for (final DESC_OBJ object : objectsInRoom) {
            if (object instanceof ITalkerGO) {
                res.addAll(RedenAction.buildActions(db, currentStoryState, room,
                        (IDescribableGO & ITalkerGO) object));
            }

            if (object.locationComp().isMovable()) {
                res.addAll(
                        NehmenAction.buildObjectActions(db, currentStoryState,
                                room, object));
            }
        }

        res.addAll(EssenAction.buildActions(db, currentStoryState, room));

        return res.build();
    }

    private <DESC_OBJ extends IDescribableGO & ILocatableGO>
    ImmutableList<AbstractScAction> buildInventoryActions(
            final StoryState currentStoryState,
            final @Nullable IHasStoringPlaceGO room,
            final List<DESC_OBJ> inventory) {
        final ImmutableList.Builder<AbstractScAction> res = ImmutableList.builder();

        for (final DESC_OBJ inventoryObject : inventory) {
            if (room != null) {
                // Das inventoryObject könnte auch ein ILivingBeing sein!
                res.addAll(HochwerfenAction
                        .buildActions(
                                db, currentStoryState, room, inventoryObject));
                res.addAll(
                        AblegenAction.buildActions(
                                db, currentStoryState, inventoryObject, room));
            }
        }
        return res.build();
    }

    private <R extends ISpatiallyConnectedGO & IHasStoringPlaceGO>
    ImmutableList<AbstractScAction> buildRoomActions(
            final StoryState currentStoryState,
            final R room) {
        final ImmutableList.Builder<AbstractScAction> res = ImmutableList.builder();

        res.addAll(buildRoomSpecificActions(currentStoryState, room));
        res.addAll(BewegenAction.buildActions(db, currentStoryState, room));

        return res.build();
    }

    private ImmutableList<AbstractScAction> buildRoomSpecificActions(
            final StoryState currentStoryState, final IHasStoringPlaceGO room) {
        final ImmutableList.Builder<AbstractScAction> res = ImmutableList.builder();

        res.addAll(KletternAction.buildActions(db, currentStoryState, room));

        return res.build();
    }
}