package de.nb.aventiure2.data.world.syscomp.reaction.impl;

import androidx.annotation.Nullable;

import de.nb.aventiure2.data.database.AvDatabase;
import de.nb.aventiure2.data.narration.Narrator;
import de.nb.aventiure2.data.time.AvDateTime;
import de.nb.aventiure2.data.time.AvTimeSpan;
import de.nb.aventiure2.data.time.TimeTaker;
import de.nb.aventiure2.data.world.base.IGameObject;
import de.nb.aventiure2.data.world.counter.CounterDao;
import de.nb.aventiure2.data.world.gameobject.*;
import de.nb.aventiure2.data.world.syscomp.feelings.FeelingsComp;
import de.nb.aventiure2.data.world.syscomp.location.ILocatableGO;
import de.nb.aventiure2.data.world.syscomp.location.LocationSystem;
import de.nb.aventiure2.data.world.syscomp.reaction.AbstractReactionsComp;
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

import static de.nb.aventiure2.data.time.AvTimeSpan.min;
import static de.nb.aventiure2.data.time.AvTimeSpan.mins;
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
    private final WaitingComp waitingComp;
    private final FeelingsComp feelingsComp;

    public ScAutomaticReactionsComp(final AvDatabase db,
                                    final TimeTaker timeTaker,
                                    final Narrator n,
                                    final World world,
                                    final WaitingComp waitingComp,
                                    final FeelingsComp feelingsComp) {
        super(SPIELER_CHARAKTER, n, world);
        counterDao = db.counterDao();
        this.timeTaker = timeTaker;
        this.waitingComp = waitingComp;
        this.feelingsComp = feelingsComp;
    }

    @Override
    public void onLeave(final ILocatableGO locatable, final ILocationGO from,
                        @Nullable final ILocationGO to) {
        waitingComp.stopWaiting();
    }

    @Override
    public void onEnter(final ILocatableGO locatable, @Nullable final ILocationGO from,
                        final ILocationGO to) {
        waitingComp.stopWaiting();

        if (locatable.is(SPIELER_CHARAKTER)) {
            onSCEnter(from, to);
            return;
        }
    }

    private void onSCEnter(@Nullable final ILocationGO from, final ILocationGO to) {
        if (!LocationSystem.haveSameOuterMostLocation(from, to)) {
            if (to.is(WALDWILDNIS_HINTER_DEM_BRUNNEN)) {


                // FIXME Automatisch die Zauberin loslaufen lassen
                //  (RapunzelsZauberinState.VOR_DEM_NAECHSTEN_RAPUNZEL_BESUCH)
                //  und auf der Kreuzung treffen lassen, wenn der SC vom
                //  Brunnen erstmals zur Kreuzung zurückkehrt und es
                //  Tag ist etc., so dass der SC der Zauberin auf jeden Fall einmal im Wald begegnet


            } else if (to.is(WALDWILDNIS_HINTER_DEM_BRUNNEN)) {
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

        final AvTimeSpan remainingWaitTime = waitingComp.getEndTime().minus(endTime);
        if (remainingWaitTime.longerThan(AvTimeSpan.NO_TIME)) {
            // Erzwingen, dass sich die Welt noch weitere 3 Minuten weiterdreht
            // (oder die remainingWaitTime - wenn die kleiner ist)
            timeTaker.passTime(min(mins(3), remainingWaitTime));
        }
    }

    @Override
    public void afterScActionAndFirstWorldUpdate() {
        feelingsComp.narrateScMuedigkeitIfNecessary();
    }
}
