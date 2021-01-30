package de.nb.aventiure2.scaction;

import android.content.Context;

import androidx.annotation.Nullable;

import com.google.common.collect.ImmutableList;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.ParametersAreNonnullByDefault;

import de.nb.aventiure2.data.database.AvDatabase;
import de.nb.aventiure2.data.narration.Narrator;
import de.nb.aventiure2.data.time.TimeTaker;
import de.nb.aventiure2.data.world.base.IGameObject;
import de.nb.aventiure2.data.world.gameobject.*;
import de.nb.aventiure2.data.world.gameobject.player.*;
import de.nb.aventiure2.data.world.syscomp.alive.ILivingBeingGO;
import de.nb.aventiure2.data.world.syscomp.description.IDescribableGO;
import de.nb.aventiure2.data.world.syscomp.location.ILocatableGO;
import de.nb.aventiure2.data.world.syscomp.storingplace.ILocationGO;
import de.nb.aventiure2.data.world.syscomp.taking.ITakerGO;
import de.nb.aventiure2.data.world.syscomp.talking.ITalkerGO;
import de.nb.aventiure2.scaction.impl.AblegenAction;
import de.nb.aventiure2.scaction.impl.BewegenAction;
import de.nb.aventiure2.scaction.impl.EssenAction;
import de.nb.aventiure2.scaction.impl.GebenAction;
import de.nb.aventiure2.scaction.impl.HeulenAction;
import de.nb.aventiure2.scaction.impl.HochwerfenAction;
import de.nb.aventiure2.scaction.impl.NehmenAction;
import de.nb.aventiure2.scaction.impl.RastenAction;
import de.nb.aventiure2.scaction.impl.RedenAction;
import de.nb.aventiure2.scaction.impl.RufenAction;
import de.nb.aventiure2.scaction.impl.SchlafenAction;
import de.nb.aventiure2.scaction.impl.WartenAction;
import de.nb.aventiure2.scaction.stepcount.SCActionStepCountDao;

import static de.nb.aventiure2.data.world.gameobject.World.*;

/**
 * Repository for the actions the player can choose from.
 */
@ParametersAreNonnullByDefault
public class ScActionService {
    private final AvDatabase db;
    private final TimeTaker timeTaker;
    private final Narrator n;
    private final World world;

    // Note that in order to unit test the repository, you have to remove the Application
    // dependency. This adds complexity and much more code, and this sample is not about testing.
    // See the BasicSample in the android-architecture-components repository at
    // https://github.com/googlesamples
    public ScActionService(final Context context) {
        db = AvDatabase.getDatabase(context);
        timeTaker = TimeTaker.getInstance(db);
        n = Narrator.getInstance(db, timeTaker);
        world = World.getInstance(db, timeTaker, n);
    }

    public <DESC_OBJ extends ILocatableGO & IDescribableGO,
            LIV extends ILocatableGO & IDescribableGO & ILivingBeingGO> List<AbstractScAction>
    getPlayerActions() {
        final SpielerCharakter spielerCharakter = world.loadSC();

        final @Nullable ILocationGO location = spielerCharakter.locationComp().getLocation();

        final ImmutableList<LIV> scInventoryLivingBeings =
                world.loadDescribableLivingRecursiveInventory(SPIELER_CHARAKTER);
        final ImmutableList<DESC_OBJ> scInventoryObjects =
                world.loadDescribableNonLivingRecursiveInventory(SPIELER_CHARAKTER);
        final ImmutableList<DESC_OBJ> wasSCInDenHaendenHat =
                world.loadDescribableRecursiveInventory(HAENDE_DES_SPIELER_CHARAKTERS);

        final ImmutableList<DESC_OBJ> objectsInLocation =
                location != null ? world.loadDescribableNonLivingRecursiveInventory(location) :
                        ImmutableList.of();
        final ImmutableList<LIV> livingBeings =
                world.loadDescribableLocatableLivingBeings();

        final List<AbstractScAction> res = new ArrayList<>();

        if (location != null) {
            res.addAll(buildLivingBeingsActions(spielerCharakter,
                    wasSCInDenHaendenHat, livingBeings,
                    scInventoryLivingBeings, scInventoryObjects,
                    location));
        }
        res.addAll(buildPlayerOnlyAction(
                spielerCharakter, wasSCInDenHaendenHat, location));
        if (location != null) {
            res.addAll(buildObjectInLocationActions(
                    spielerCharakter,
                    wasSCInDenHaendenHat, location, objectsInLocation,
                    scInventoryLivingBeings, scInventoryObjects));
        }

        if (!spielerCharakter.talkingComp().isInConversation()) {
            res.addAll(buildInventoryActions(db.scActionStepCountDao(), timeTaker,
                    wasSCInDenHaendenHat, location,
                    scInventoryLivingBeings));
            res.addAll(buildInventoryActions(db.scActionStepCountDao(), timeTaker,
                    wasSCInDenHaendenHat, location,
                    scInventoryObjects));

            if (location != null) {
                res.addAll(buildLocationActions(location));
            }
        }

        return res;
    }

    private <DESC_OBJ extends ILocatableGO & IDescribableGO,
            LIV extends IDescribableGO & ILocatableGO & ILivingBeingGO,
            TALKER extends IDescribableGO & ILocatableGO & ITalkerGO<?>,
            TAKER extends IDescribableGO & ILocatableGO & ITakerGO<?>>
    ImmutableList<AbstractScAction> buildLivingBeingsActions(
            final SpielerCharakter spielerCharakter,
            final List<DESC_OBJ> wasSCInDenHaendenHat,
            final List<LIV> creatures,
            final List<LIV> scInventoryLivingBeings,
            final ImmutableList<DESC_OBJ> scInventoryObjects,
            final @Nullable ILocationGO location) {
        final ImmutableList.Builder<AbstractScAction> res = ImmutableList.builder();

        for (final LIV creature : creatures) {
            if (creature instanceof ITalkerGO) {
                res.addAll(RedenAction.buildActions(db.scActionStepCountDao(), timeTaker,
                        n, world, (TALKER) creature));
            }

            if (scCanGiveSomthingTo(creature)) {
                if (!wasSCInDenHaendenHat.isEmpty()) {
                    res.addAll(GebenAction.buildActions(
                            db.scActionStepCountDao(), timeTaker, n, world, (TAKER) creature,
                            wasSCInDenHaendenHat));
                } else {
                    res.addAll(GebenAction
                            .buildActions(db.scActionStepCountDao(), timeTaker, n, world,
                                    (TAKER) creature,
                                    scInventoryLivingBeings));
                    res.addAll(GebenAction
                            .buildActions(db.scActionStepCountDao(), timeTaker, n, world,
                                    (TAKER) creature,
                                    scInventoryObjects));
                }
            }

            if (wasSCInDenHaendenHat.isEmpty() &&
                    !spielerCharakter.talkingComp().isInConversation()) {
                if (creature.locationComp().isMovable()) {
                    res.addAll(
                            NehmenAction.buildCreatureActions(db, timeTaker, n, world, creature));
                }
            }

            if (location != null) {
                res.addAll(WartenAction
                        .buildActions(db.counterDao(), db.scActionStepCountDao(), timeTaker, n,
                                world, creature,
                                location));
            }
        }

        return res.build();
    }

    private ImmutableList<AbstractScAction> buildPlayerOnlyAction(
            final SpielerCharakter spielerCharakter,
            final List<? extends ILocatableGO> wasSCInDenHaendenHat,
            final @Nullable ILocationGO location) {
        final ImmutableList.Builder<AbstractScAction> res = ImmutableList.builder();

        if (!spielerCharakter.talkingComp().isInConversation()) {
            res.addAll(
                    HeulenAction.buildActions(db.scActionStepCountDao(), timeTaker, n, world,
                            spielerCharakter));

            if (wasSCInDenHaendenHat.isEmpty()) {
                res.addAll(RastenAction
                        .buildActions(db.scActionStepCountDao(), timeTaker, n, world,
                                location));
                res.addAll(SchlafenAction
                        .buildActions(db.scActionStepCountDao(), timeTaker, n, world,
                                location));
            }
        }

        return res.build();
    }

    private <DESC_OBJ extends ILocatableGO & IDescribableGO,
            LIV extends IDescribableGO & ILocatableGO & ILivingBeingGO,
            TALKER extends IDescribableGO & ILocatableGO & ITalkerGO<?>,
            TAKER extends IDescribableGO & ILocatableGO & ITakerGO<?>>
    ImmutableList<AbstractScAction> buildObjectInLocationActions(
            final SpielerCharakter spielerCharakter,
            final List<DESC_OBJ> wasSCInDenHaendenHat,
            final ILocationGO location,
            final List<? extends DESC_OBJ> objectsInLocation,
            final List<LIV> scInventoryLivingBeings,
            final ImmutableList<DESC_OBJ> scInventoryObjects) {
        final ImmutableList.Builder<AbstractScAction> res = ImmutableList.builder();
        for (final DESC_OBJ object : objectsInLocation) {
            if (object instanceof ITalkerGO) {
                if (spielerCharakter.talkingComp().isTalkingTo((TALKER) object) ||
                        !spielerCharakter.talkingComp().isInConversation()) {
                    res.addAll(RedenAction.buildActions(db.scActionStepCountDao(), timeTaker,
                            n, world, (TALKER) object));
                }
            }

            if (scCanGiveSomthingTo(object)) {
                if (!wasSCInDenHaendenHat.isEmpty()) {
                    res.addAll(GebenAction.buildActions(
                            db.scActionStepCountDao(), timeTaker, n, world, (TAKER) object,
                            wasSCInDenHaendenHat));
                } else {
                    res.addAll(GebenAction
                            .buildActions(db.scActionStepCountDao(), timeTaker, n, world,
                                    (TAKER) object,
                                    scInventoryLivingBeings));
                    res.addAll(GebenAction
                            .buildActions(db.scActionStepCountDao(), timeTaker, n, world,
                                    (TAKER) object,
                                    scInventoryObjects));
                }
            }

            if (!spielerCharakter.talkingComp().isInConversation()) {
                if (wasSCInDenHaendenHat.isEmpty() && object.locationComp().isMovable()) {
                    res.addAll(NehmenAction.buildObjectActions(db, timeTaker, n, world, object));
                }
            }
        }

        if (!spielerCharakter.talkingComp().isInConversation()) {
            if (wasSCInDenHaendenHat.isEmpty()) {
                res.addAll(EssenAction
                        .buildActions(db.scActionStepCountDao(), timeTaker, db.counterDao(),
                                n, world, location));
            }
        }

        return res.build();
    }

    private static <DESC_OBJ extends ILocatableGO & IDescribableGO> boolean scCanGiveSomthingTo(
            final DESC_OBJ potentialTaker) {
        if (!(potentialTaker instanceof ITakerGO<?>)) {
            return false;
        }

        if (!(potentialTaker instanceof ITalkerGO<?>)) {
            // Wer kein Talker ist, dem kann man potenziell immer etwas geben
            return true;
        }

        if (((ITalkerGO<?>) potentialTaker).talkingComp().isTalkingTo(SPIELER_CHARAKTER)) {
            // Wer Talker ist, mit dem muss man im Gespräch sein, um ihm etwas geben zu
            // können. (Einfach so den Leuten was unter die Nase zu halten wäre extremely
            // creapy.)
            return true;
        }
        return false;
    }

    private <DESC_OBJ extends IDescribableGO & ILocatableGO>
    ImmutableList<AbstractScAction> buildInventoryActions(
            final SCActionStepCountDao scActionStepCountDao,
            final TimeTaker timeTaker,
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
                                    db.scActionStepCountDao(), timeTaker, db.counterDao(),
                                    n, world, location,
                                    inventoryObject));
                    res.addAll(AblegenAction.buildActions(scActionStepCountDao, timeTaker,
                            n, world, inventoryObject, location));
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

        res.addAll(RufenAction
                .buildActions(db.scActionStepCountDao(), timeTaker, n, world, location));
        res.addAll(BewegenAction
                .buildActions(db.scActionStepCountDao(), timeTaker, db.counterDao(),
                        n, world, location));

        return res.build();
    }
}