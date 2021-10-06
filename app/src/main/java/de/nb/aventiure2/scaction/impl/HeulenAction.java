package de.nb.aventiure2.scaction.impl;

import static de.nb.aventiure2.data.time.AvTimeSpan.mins;
import static de.nb.aventiure2.data.time.AvTimeSpan.secs;
import static de.nb.aventiure2.data.world.gameobject.World.*;
import static de.nb.aventiure2.data.world.syscomp.feelings.Mood.BETRUEBT;
import static de.nb.aventiure2.data.world.syscomp.feelings.Mood.NEUTRAL;
import static de.nb.aventiure2.data.world.syscomp.state.impl.FroschprinzState.HAT_SC_HILFSBEREIT_ANGESPROCHEN;
import static de.nb.aventiure2.data.world.syscomp.state.impl.FroschprinzState.UNAUFFAELLIG;
import static de.nb.aventiure2.german.description.AltTimedDescriptionsBuilder.altTimed;
import static de.nb.aventiure2.german.description.DescriptionBuilder.du;
import static de.nb.aventiure2.german.description.DescriptionBuilder.neuerSatz;
import static de.nb.aventiure2.german.description.DescriptionBuilder.satzanschluss;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.common.collect.ImmutableList;

import java.util.Collection;

import de.nb.aventiure2.data.narration.Narrator;
import de.nb.aventiure2.data.time.TimeTaker;
import de.nb.aventiure2.data.world.gameobject.*;
import de.nb.aventiure2.data.world.gameobject.player.*;
import de.nb.aventiure2.data.world.syscomp.alive.ILivingBeingGO;
import de.nb.aventiure2.data.world.syscomp.description.IDescribableGO;
import de.nb.aventiure2.data.world.syscomp.location.ILocatableGO;
import de.nb.aventiure2.data.world.syscomp.memory.Action;
import de.nb.aventiure2.data.world.syscomp.spatialconnection.CardinalDirection;
import de.nb.aventiure2.data.world.syscomp.state.IHasStateGO;
import de.nb.aventiure2.data.world.syscomp.state.impl.FroschprinzState;
import de.nb.aventiure2.data.world.syscomp.talking.ITalkerGO;
import de.nb.aventiure2.data.world.syscomp.talking.impl.FroschprinzTalkingComp;
import de.nb.aventiure2.german.description.AltTimedDescriptionsBuilder;
import de.nb.aventiure2.scaction.AbstractScAction;
import de.nb.aventiure2.scaction.stepcount.SCActionStepCountDao;

/**
 * Der Spielercharakter heult.
 */
@SuppressWarnings("IfStatementWithIdenticalBranches")
public class HeulenAction extends AbstractScAction {
    public static Collection<HeulenAction> buildActions(
            final SCActionStepCountDao scActionStepCountDao,
            final TimeTaker timeTaker,
            final Narrator n, final World world,
            final SpielerCharakter sc) {
        final ImmutableList.Builder<HeulenAction> res = ImmutableList.builder();

        if (sc.feelingsComp().isTraurigerAls(BETRUEBT)) {
            res.add(new HeulenAction(scActionStepCountDao, timeTaker, n, world));
        }

        return res.build();
    }

    private HeulenAction(final SCActionStepCountDao scActionStepCountDao,
                         final TimeTaker timeTaker,
                         final Narrator n,
                         final World world) {
        super(scActionStepCountDao, timeTaker, n, world);
    }

    @Override
    public String getType() {
        return "actionHeulen";
    }

    @Nullable
    @Override
    public CardinalDirection getCardinalDirection() {
        return null;
    }

    @Override
    @NonNull
    public String getName() {
        return "Heulen";
    }

    @Override
    public void narrateAndDo() {
        if (isDefinitivFortsetzung()) {
            narrateAndDoWiederholungFortsetzung();
            return;
        }

        narrateAndDoErstesMal();
    }

    private <F extends IDescribableGO &
            ILocatableGO &
            IHasStateGO<FroschprinzState> &
            ITalkerGO<FroschprinzTalkingComp> &
            ILivingBeingGO>
    void narrateAndDoWiederholungFortsetzung() {
        final F froschprinz = world.loadRequired(FROSCHPRINZ);

        if (froschprinz.locationComp().hasRecursiveLocation(
                world.loadSC().locationComp().getLocation()) &&
                (froschprinz.stateComp().hasState(UNAUFFAELLIG))) {
            narrateAndDoFroschprinzUnauffaellig(froschprinz);
            return;
        }

        sc.memoryComp().setLastAction(buildMemorizedAction());

        final AltTimedDescriptionsBuilder alt = altTimed();
        if (n.allowsAdditionalDuSatzreihengliedOhneSubjekt()) {
            alt.add(du("weinst").timed(mins(1))
                    .schonLaenger()
                    .undWartest());
            alt.add(satzanschluss(", so viele Tränen haben sich angestaut")
                    .schonLaenger()
                    .timed(mins(1)));
        }

        alt.add(du("kannst", "dich gar nicht mehr beruhigen").schonLaenger()
                .timed(mins(1))
                .undWartest());
        n.narrateAlt(alt);
    }

    private <F extends IDescribableGO &
            IHasStateGO<FroschprinzState> &
            ITalkerGO<FroschprinzTalkingComp>>
    void narrateAndDoFroschprinzUnauffaellig(final F froschprinz) {
        sc.memoryComp().setLastAction(buildMemorizedAction());

        n.narrate(du("weinst",
                "immer lauter und kannst dich gar nicht trösten.",
                "Und wie du so klagst, ruft dir jemand zu: „Was hast du vor,",
                "du schreist ja, dass sich ein Stein erbarmen möchte.“ Du siehst",
                "dich um, woher",
                "die Stimme käme, da erblickst du",
                world.getDescription(froschprinz).akkK())
                .schonLaenger()
                .mitVorfeldSatzglied("immer lauter")
                .timed(secs(30)));

        froschprinz.stateComp().narrateAndSetState(HAT_SC_HILFSBEREIT_ANGESPROCHEN);
        froschprinz.talkingComp().setTalkingTo(sc);
        sc.feelingsComp().requestMood(NEUTRAL);
        world.narrateAndUpgradeScKnownAndAssumedState(FROSCHPRINZ);
    }

    private void narrateAndDoErstesMal() {
        sc.memoryComp().setLastAction(buildMemorizedAction());

        final AltTimedDescriptionsBuilder alt = altTimed();
        alt.add(neuerSatz("Dich überkommt ein Schluchzen")
                .timed(mins(1)));

        if (n.dann()) {
            alt.add(neuerSatz("Dann bricht die Trauer aus dir heraus und du heulst los")
                    .timed(mins(1)));
        }

        alt.add(du("weinst").timed(mins(1))
                .schonLaenger()
                .undWartest()
                .dann());
        n.narrateAlt(alt);
    }

    @Override
    protected boolean isDefinitivWiederholung() {
        return false;
    }

    @Override
    protected boolean isDefinitivFortsetzung() {
        return sc.memoryComp().lastActionWas(buildMemorizedAction());
    }

    @Override
    protected boolean isDefinitivDiskontinuitaet() {
        if (n.lastNarrationWasFromReaction()) {
            return false;
        }

        return false;
    }

    private static Action buildMemorizedAction() {
        return new Action(Action.Type.HEULEN);
    }
}
