package de.nb.aventiure2.scaction.impl;

import androidx.annotation.NonNull;

import com.google.common.collect.ImmutableList;

import java.util.Collection;

import de.nb.aventiure2.data.database.AvDatabase;
import de.nb.aventiure2.data.narration.Narrator;
import de.nb.aventiure2.data.world.gameobject.*;
import de.nb.aventiure2.data.world.gameobject.player.*;
import de.nb.aventiure2.data.world.syscomp.alive.ILivingBeingGO;
import de.nb.aventiure2.data.world.syscomp.description.IDescribableGO;
import de.nb.aventiure2.data.world.syscomp.location.ILocatableGO;
import de.nb.aventiure2.data.world.syscomp.memory.Action;
import de.nb.aventiure2.data.world.syscomp.state.IHasStateGO;
import de.nb.aventiure2.data.world.syscomp.state.impl.FroschprinzState;
import de.nb.aventiure2.data.world.syscomp.talking.ITalkerGO;
import de.nb.aventiure2.data.world.syscomp.talking.impl.FroschprinzTalkingComp;
import de.nb.aventiure2.german.description.AbstractDescription;
import de.nb.aventiure2.scaction.AbstractScAction;

import static de.nb.aventiure2.data.world.gameobject.World.*;
import static de.nb.aventiure2.data.world.syscomp.feelings.Mood.NEUTRAL;
import static de.nb.aventiure2.data.world.syscomp.feelings.Mood.UNTROESTLICH;
import static de.nb.aventiure2.data.world.syscomp.state.impl.FroschprinzState.HAT_SC_HILFSBEREIT_ANGESPROCHEN;
import static de.nb.aventiure2.data.world.syscomp.state.impl.FroschprinzState.UNAUFFAELLIG;
import static de.nb.aventiure2.data.world.time.AvTimeSpan.*;
import static de.nb.aventiure2.german.description.AllgDescription.neuerSatz;
import static de.nb.aventiure2.german.description.AllgDescription.satzanschluss;
import static de.nb.aventiure2.german.description.DuDescriptionBuilder.du;

/**
 * Der Spielercharakter heult.
 */
public class HeulenAction extends AbstractScAction {
    public static Collection<HeulenAction> buildActions(
            final AvDatabase db,
            final Narrator n, final World world,
            final SpielerCharakter sc) {
        final ImmutableList.Builder<HeulenAction> res = ImmutableList.builder();
        // STORY Verhindern, dass der Benutzer nicht mehr untröstlich ist, wenn er z.B. erst
        //  schläft. Z.B. Benutzer traurig machen, wenn er den Brunnen sieht und sich an
        //  seine goldene Kugel erinnert o.Ä.

        if (sc.feelingsComp().hasMood(UNTROESTLICH)) {
            res.add(new HeulenAction(db, n, world));
        }

        return res.build();
    }

    private HeulenAction(final AvDatabase db,
                         final Narrator n,
                         final World world) {
        super(db, n, world);
    }

    @Override
    public String getType() {
        return "actionHeulen";
    }

    @Override
    @NonNull
    public String getName() {
        return "Heulen";
    }

    @Override
    public void narrateAndDo() {
        if (isDefinitivWiederholung()) {
            narrateAndDoWiederholung();
            return;
        }

        narrateAndDoErstesMal();
    }

    private <F extends IDescribableGO &
            ILocatableGO &
            IHasStateGO<FroschprinzState> &
            ITalkerGO<FroschprinzTalkingComp> &
            ILivingBeingGO>
    void narrateAndDoWiederholung() {
        final F froschprinz = (F) world.load(FROSCHPRINZ);

        if (froschprinz.locationComp().hasRecursiveLocation(
                world.loadSC().locationComp().getLocation()) &&
                (froschprinz.stateComp().hasState(UNAUFFAELLIG))) {
            narrateAndDoFroschprinzUnauffaellig(froschprinz);
            return;
        }

        sc.memoryComp().setLastAction(buildMemorizedAction());

        final ImmutableList.Builder<AbstractDescription<?>> alt = ImmutableList.builder();
        if (n.requireNarration().allowsAdditionalDuSatzreihengliedOhneSubjekt()) {
            alt.add(du("weinst", mins(1))
                    .undWartest());
            alt.add(satzanschluss(", so viele Tränen haben sich angestaut", mins(1)));
        }

        alt.add(du("kannst", "dich gar nicht mehr beruhigen", mins(1))
                .undWartest());
        n.narrateAlt(alt);
    }

    private <F extends IDescribableGO &
            IHasStateGO<FroschprinzState> &
            ITalkerGO<FroschprinzTalkingComp>>
    void narrateAndDoFroschprinzUnauffaellig(final F froschprinz) {
        // FIXME Nachts schläft der Frosch?!

        sc.memoryComp().setLastAction(buildMemorizedAction());

        n.narrate(du("weinst", "immer lauter und kannst dich gar nicht trösten. " +
                "Und wie du so klagst, ruft dir jemand zu: „Was hast du vor, " +
                "du schreist ja, dass sich ein Stein erbarmen möchte.“ Du siehst " +
                "dich um, woher " +
                "die Stimme käme, da erblickst du " +
                world.getDescription(froschprinz).akk(), "immer lauter", secs(30)));

        froschprinz.stateComp().narrateAndSetState(HAT_SC_HILFSBEREIT_ANGESPROCHEN);
        froschprinz.talkingComp().setTalkingTo(sc);
        sc.feelingsComp().requestMood(NEUTRAL);
        world.loadSC().memoryComp().upgradeKnown(FROSCHPRINZ);
    }

    private void narrateAndDoErstesMal() {
        sc.memoryComp().setLastAction(buildMemorizedAction());

        final ImmutableList.Builder<AbstractDescription<?>> alt = ImmutableList.builder();
        alt.add(neuerSatz("Dich überkommt ein Schluchzen", mins(1)));

        if (n.requireNarration().dann()) {
            alt.add(neuerSatz("Dann bricht die Trauer aus dir heraus und du heulst los",
                    mins(1)));
        }

        alt.add(du("weinst", mins(1))
                .undWartest()
                .dann());
        n.narrateAlt(alt);
    }

    @Override
    protected boolean isDefinitivWiederholung() {
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
