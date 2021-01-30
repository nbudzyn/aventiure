package de.nb.aventiure2.data.world.syscomp.reaction.impl;

import androidx.annotation.Nullable;

import com.google.common.collect.ImmutableList;

import de.nb.aventiure2.data.database.AvDatabase;
import de.nb.aventiure2.data.narration.Narrator;
import de.nb.aventiure2.data.time.AvDateTime;
import de.nb.aventiure2.data.time.TimeTaker;
import de.nb.aventiure2.data.world.base.IGameObject;
import de.nb.aventiure2.data.world.counter.CounterDao;
import de.nb.aventiure2.data.world.gameobject.*;
import de.nb.aventiure2.data.world.syscomp.description.IDescribableGO;
import de.nb.aventiure2.data.world.syscomp.feelings.FeelingsComp;
import de.nb.aventiure2.data.world.syscomp.location.ILocatableGO;
import de.nb.aventiure2.data.world.syscomp.location.LocationComp;
import de.nb.aventiure2.data.world.syscomp.location.LocationSystem;
import de.nb.aventiure2.data.world.syscomp.mentalmodel.MentalModelComp;
import de.nb.aventiure2.data.world.syscomp.movement.IMovingGO;
import de.nb.aventiure2.data.world.syscomp.reaction.AbstractReactionsComp;
import de.nb.aventiure2.data.world.syscomp.reaction.IResponder;
import de.nb.aventiure2.data.world.syscomp.reaction.interfaces.IEssenReactions;
import de.nb.aventiure2.data.world.syscomp.reaction.interfaces.IMovementReactions;
import de.nb.aventiure2.data.world.syscomp.reaction.interfaces.IRufReactions;
import de.nb.aventiure2.data.world.syscomp.reaction.interfaces.ISCActionReactions;
import de.nb.aventiure2.data.world.syscomp.reaction.interfaces.IStateChangedReactions;
import de.nb.aventiure2.data.world.syscomp.reaction.interfaces.ITimePassedReactions;
import de.nb.aventiure2.data.world.syscomp.reaction.interfaces.Ruftyp;
import de.nb.aventiure2.data.world.syscomp.state.IHasStateGO;
import de.nb.aventiure2.data.world.syscomp.storingplace.ILocationGO;
import de.nb.aventiure2.data.world.syscomp.waiting.WaitingComp;
import de.nb.aventiure2.scaction.impl.EssenAction;

import static de.nb.aventiure2.data.world.gameobject.World.*;
import static de.nb.aventiure2.data.world.syscomp.feelings.Hunger.HUNGRIG;

/**
 * "Automatische" Reaktionen des Spielercharakters, z.B. darauf, dass Zeit vergeht.
 * (Z.B.: Spielercharakter wird hungrig.)
 */
public class ScAutomaticReactionsComp
        extends AbstractReactionsComp
        implements IMovementReactions, IStateChangedReactions, IRufReactions,
        ITimePassedReactions, IEssenReactions,
        ISCActionReactions {
    private final CounterDao counterDao;
    private final TimeTaker timeTaker;
    private final LocationComp locationComp;
    private final MentalModelComp mentalModelComp;
    private final WaitingComp waitingComp;
    private final FeelingsComp feelingsComp;

    public ScAutomaticReactionsComp(final AvDatabase db,
                                    final TimeTaker timeTaker,
                                    final Narrator n,
                                    final World world,
                                    final LocationComp locationComp,
                                    final MentalModelComp mentalModelComp,
                                    final WaitingComp waitingComp,
                                    final FeelingsComp feelingsComp) {
        super(SPIELER_CHARAKTER, n, world);
        counterDao = db.counterDao();
        this.timeTaker = timeTaker;
        this.locationComp = locationComp;
        this.mentalModelComp = mentalModelComp;
        this.waitingComp = waitingComp;
        this.feelingsComp = feelingsComp;
    }

    @Override
    public void onLeave(final ILocatableGO locatable, final ILocationGO from,
                        @Nullable final ILocationGO to) {
        waitingComp.stopWaiting();

        if (locatable.is(SPIELER_CHARAKTER)) {
            onSCLeave(from, to);
            return;
        }
    }

    private <MOV extends IMovingGO & ILocatableGO & IDescribableGO>
    void onSCLeave(final ILocationGO from, @Nullable final ILocationGO to) {
        if (!LocationSystem.haveSameOuterMostLocation(from, to)) {
            // Der SC verlässt den Raum.

            final ImmutableList<MOV> movingBeingsMovingDescribableInventory =
                    world.loadMovingBeingsMovingDescribableInventory(from.getId());

            // Dann aus dem mental
            // Model alle MovingBeings entfernen, die sich in Bewegung befinden.
            // Der SC hat vermutlich gesehen, dass sie sich bewegen - und es
            // ergibt keinen Sinn, sich später zu wundern, dass sich nicht mehr
            // an diesem Ort sind.

            mentalModelComp.unsetAssumedLocations(movingBeingsMovingDescribableInventory);
        }
    }

    @Override
    public boolean verbirgtSichVorEintreffendemSC() {
        return false;
    }

    @Override
    public void onEnter(final ILocatableGO locatable, @Nullable final ILocationGO from,
                        final ILocationGO to) {
        waitingComp.stopWaiting();

        if (locatable.is(SPIELER_CHARAKTER)) {
            onSCEnter(from, to);
            return;
        }

        if (locationComp.hasSameOuterMostLocationAs(from) ||
                locationComp.hasSameOuterMostLocationAs(to)) {
            onEnterFromOrTOScOuterMostLocation(locatable, from, to);
        }
    }

    private void onEnterFromOrTOScOuterMostLocation(
            final ILocatableGO locatable, @Nullable final ILocationGO from, final ILocationGO to) {
        if (locationComp.hasSameOuterMostLocationAs(to)) {
            if (scBemerktOnEnter(locatable)) {
                // Der SC hat den Wechsel auf den neuen Ort miterlebt.
                mentalModelComp.setAssumedLocation(locatable, to);
            }
        } else {
            // !locationComp.hasSameOuterMostLocationAs(to)
            // && locationComp.hasSameOuterMostLocationAs(from)
            // Der SC erlebt, wie das Locatable seinen aktuellen Raum
            // verlässt. Der SC "vergisst" die Location.
            mentalModelComp.unsetAssumedLocation(locatable);
        }
    }

    private static boolean scBemerktOnEnter(final ILocatableGO locatable) {
        if (!(locatable instanceof IResponder)) {
            return true;
        }

        final IResponder responder = (IResponder) locatable;
        if (!(responder.reactionsComp() instanceof IMovementReactions)) {
            return true;
        }

        return !((IMovementReactions) responder.reactionsComp()).verbirgtSichVorEintreffendemSC();
    }

    private void onSCEnter(@Nullable final ILocationGO from, final ILocationGO to) {
        if (!LocationSystem.haveSameOuterMostLocation(from, to)) {
            if (to.is(WALDWILDNIS_HINTER_DEM_BRUNNEN)) {
                counterDao.reset(EssenAction.COUNTER_FELSENBIRNEN_SEIT_ENTER);
            }
        }
    }

    @Override
    public void onStateChanged(final IHasStateGO<?> gameObject, final Enum<?> oldState,
                               final Enum<?> newState) {
        if (loadSC().locationComp().hasLocation(
                SCHLOSS_VORHALLE, SCHLOSS_VORHALLE_AM_TISCH_BEIM_FEST,
                DRAUSSEN_VOR_DEM_SCHLOSS) &&
                gameObject.is(SCHLOSSFEST)) {
            waitingComp.stopWaiting();
        }
    }

    @Override
    public void onRuf(final ILocatableGO rufer, final Ruftyp ruftyp) {
        if (world.hasSameOuterMostLocationAsSC(rufer)) {
            waitingComp.stopWaiting();
        }
    }

    @Override
    public void onEssen(final IGameObject gameObject) {
        if (feelingsComp.getHunger() == HUNGRIG
                && world.hasSameOuterMostLocationAsSC(gameObject)) {
            waitingComp.stopWaiting();
        }
    }

    @Override
    public void onTimePassed(final AvDateTime startTime, final AvDateTime endTime) {
        feelingsComp.onTimePassed(startTime, endTime);
        waitingComp.ifWaitingDoWaitStep(endTime);
    }

    @Override
    public void afterScActionAndFirstWorldUpdate() {
        feelingsComp.narrateScMuedigkeitIfNecessary();
    }
}
