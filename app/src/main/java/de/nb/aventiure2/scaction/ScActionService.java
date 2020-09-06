package de.nb.aventiure2.scaction;

import android.content.Context;

import androidx.annotation.Nullable;

import com.google.common.collect.ImmutableList;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.ParametersAreNonnullByDefault;

import de.nb.aventiure2.data.database.AvDatabase;
import de.nb.aventiure2.data.world.base.IGameObject;
import de.nb.aventiure2.data.world.gameobject.World;
import de.nb.aventiure2.data.world.gameobject.player.SpielerCharakter;
import de.nb.aventiure2.data.world.syscomp.alive.ILivingBeingGO;
import de.nb.aventiure2.data.world.syscomp.description.IDescribableGO;
import de.nb.aventiure2.data.world.syscomp.location.ILocatableGO;
import de.nb.aventiure2.data.world.syscomp.storingplace.ILocationGO;
import de.nb.aventiure2.data.world.syscomp.talking.ITalkerGO;
import de.nb.aventiure2.scaction.impl.AblegenAction;
import de.nb.aventiure2.scaction.impl.BewegenAction;
import de.nb.aventiure2.scaction.impl.EssenAction;
import de.nb.aventiure2.scaction.impl.HeulenAction;
import de.nb.aventiure2.scaction.impl.HochwerfenAction;
import de.nb.aventiure2.scaction.impl.NehmenAction;
import de.nb.aventiure2.scaction.impl.RastenAction;
import de.nb.aventiure2.scaction.impl.RedenAction;
import de.nb.aventiure2.scaction.impl.SchlafenAction;

import static de.nb.aventiure2.data.world.gameobject.World.HAENDE_DES_SPIELER_CHARAKTERS;
import static de.nb.aventiure2.data.world.gameobject.World.SPIELER_CHARAKTER;

/**
 * Repository for the actions the player can choose from.
 */
@ParametersAreNonnullByDefault
public class ScActionService {
    private final AvDatabase db;
    private final World world;

    // Note that in order to unit test the repository, you have to remove the Application
    // dependency. This adds complexity and much more code, and this sample is not about testing.
    // See the BasicSample in the android-architecture-components repository at
    // https://github.com/googlesamples
    public ScActionService(final Context context) {
        db = AvDatabase.getDatabase(context);

        world = World.getInstance(db);
    }

    // TODO Better have typical combinations available at a central place?
    public <DESC_OBJ extends ILocatableGO & IDescribableGO,
            LIV extends ILocatableGO & IDescribableGO & ILivingBeingGO> List<AbstractScAction>
    getPlayerActions() {
        final SpielerCharakter spielerCharakter = world.loadSC();

        final @Nullable ILocationGO location = spielerCharakter.locationComp().getLocation();

        final ImmutableList<DESC_OBJ> scInventoryObjects =
                world.loadDescribableNonLivingRecursiveInventory(SPIELER_CHARAKTER);
        final ImmutableList<LIV> scInventoryLivingBeings =
                world.loadDescribableLivingRecursiveInventory(SPIELER_CHARAKTER);
        final ImmutableList<? extends ILocatableGO> wasSCInDenHaendenHat =
                world.loadDescribableRecursiveInventory(HAENDE_DES_SPIELER_CHARAKTERS);

        final ImmutableList<DESC_OBJ> objectsInLocation =
                location != null ? world.loadDescribableNonLivingRecursiveInventory(location) :
                        ImmutableList.of();
        final ImmutableList<LIV> livingBeings =
                world.loadDescribableLocatableLivingBeings();

        final List<AbstractScAction> res = new ArrayList<>();

        if (location != null) {
            // TODO Hier alle Creatures übergeben - unabhängig von ihrer location!
            res.addAll(buildLivingBeingsActions(spielerCharakter,
                    wasSCInDenHaendenHat, livingBeings));
        }
        if (!spielerCharakter.talkingComp().isInConversation()) {
            res.addAll(buildPlayerOnlyAction(
                    spielerCharakter, wasSCInDenHaendenHat, location));
            if (location != null) {
                res.addAll(buildObjectInLocationActions(
                        wasSCInDenHaendenHat, location, objectsInLocation));
            }
            res.addAll(buildInventoryActions(wasSCInDenHaendenHat, location,
                    scInventoryLivingBeings));
            res.addAll(buildInventoryActions(wasSCInDenHaendenHat, location,
                    scInventoryObjects));

            if (location != null) {
                res.addAll(buildLocationActions(location));
            }
        }

        return res;
    }

    private <LIV extends IDescribableGO & ILocatableGO & ILivingBeingGO,
            TALKER extends IDescribableGO & ILocatableGO & ITalkerGO<?>>
    ImmutableList<AbstractScAction> buildLivingBeingsActions(
            final SpielerCharakter spielerCharakter,
            final List<? extends ILocatableGO> wasSCInDenHaendenHat,
            final List<LIV> creatures) {
        final ImmutableList.Builder<AbstractScAction> res = ImmutableList.builder();

        for (final LIV creature : creatures) {
            if (creature instanceof ITalkerGO) {
                res.addAll(RedenAction.buildActions(db, world, (TALKER) creature));
            }
            if (wasSCInDenHaendenHat.isEmpty() &&
                    !spielerCharakter.talkingComp().isInConversation()) {
                if (creature.locationComp().isMovable()) {
                    res.addAll(NehmenAction.buildCreatureActions(db, world, creature));
                }
            }
        }

        return res.build();
    }

    private ImmutableList<AbstractScAction> buildPlayerOnlyAction(
            final SpielerCharakter spielerCharakter,
            final List<? extends ILocatableGO> wasSCInDenHaendenHat,
            final @Nullable ILocationGO location) {
        final ImmutableList.Builder<AbstractScAction> res = ImmutableList.builder();

        res.addAll(HeulenAction.buildActions(db, world, spielerCharakter));

        if (wasSCInDenHaendenHat.isEmpty()) {
            res.addAll(RastenAction.buildActions(db, world, location));
            res.addAll(SchlafenAction.buildActions(db, world, location));
        }

        return res.build();
    }

    private <DESC_OBJ extends ILocatableGO & IDescribableGO,
            TALKER extends IDescribableGO & ILocatableGO & ITalkerGO<?>>
    ImmutableList<AbstractScAction> buildObjectInLocationActions(
            final List<? extends ILocatableGO> wasSCInDenHaendenHat,
            final ILocationGO location,
            final List<? extends DESC_OBJ> objectsInLocation) {
        final ImmutableList.Builder<AbstractScAction> res = ImmutableList.builder();
        for (final DESC_OBJ object : objectsInLocation) {
            if (object instanceof ITalkerGO) {
                res.addAll(RedenAction.buildActions(db, world, (TALKER) object));
            }

            if (wasSCInDenHaendenHat.isEmpty() && object.locationComp().isMovable()) {
                res.addAll(NehmenAction.buildObjectActions(db, world, object));
            }
        }

        if (wasSCInDenHaendenHat.isEmpty()) {
            res.addAll(EssenAction.buildActions(db, world, location));
        }

        return res.build();
    }

    private <DESC_OBJ extends IDescribableGO & ILocatableGO>
    ImmutableList<AbstractScAction> buildInventoryActions(
            final List<? extends ILocatableGO> wasSCInDenHaendenHat,
            final @Nullable ILocationGO location,
            final List<DESC_OBJ> inventory) {
        final ImmutableList.Builder<AbstractScAction> res = ImmutableList.builder();

        for (final DESC_OBJ inventoryObject : inventory) {
            if (location != null) {
                if (scHatHoechstensInDenHaenden(wasSCInDenHaendenHat, inventoryObject) &&
                        inventoryObject.locationComp().isMovable()) {
                    // Das inventoryObject könnte auch ein ILivingBeing sein!
                    res.addAll(HochwerfenAction
                            .buildActions(
                                    db, world, location, inventoryObject));
                    res.addAll(
                            AblegenAction.buildActions(
                                    db, world, inventoryObject, location));
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

    private ImmutableList<AbstractScAction> buildLocationActions(final ILocationGO location) {
        final ImmutableList.Builder<AbstractScAction> res = ImmutableList.builder();

        // TODO RufenAction
        res.addAll(BewegenAction.buildActions(db, world, location));

        return res.build();
    }
}