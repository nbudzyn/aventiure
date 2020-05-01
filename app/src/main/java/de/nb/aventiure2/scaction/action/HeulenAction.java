package de.nb.aventiure2.scaction.action;

import androidx.annotation.NonNull;

import com.google.common.collect.ImmutableList;

import java.util.Collection;
import java.util.List;

import de.nb.aventiure2.data.database.AvDatabase;
import de.nb.aventiure2.data.storystate.StoryState;
import de.nb.aventiure2.data.world.base.GameObjectId;
import de.nb.aventiure2.data.world.gameobjects.player.SpielerCharakter;
import de.nb.aventiure2.data.world.syscomp.alive.ILivingBeingGO;
import de.nb.aventiure2.data.world.syscomp.description.IDescribableGO;
import de.nb.aventiure2.data.world.syscomp.feelings.Mood;
import de.nb.aventiure2.data.world.syscomp.memory.Action;
import de.nb.aventiure2.data.world.syscomp.memory.Known;
import de.nb.aventiure2.data.world.syscomp.state.IHasStateGO;
import de.nb.aventiure2.data.world.syscomp.talking.ITalkerGO;
import de.nb.aventiure2.data.world.time.AvTimeSpan;
import de.nb.aventiure2.german.base.AbstractDescription;
import de.nb.aventiure2.scaction.AbstractScAction;

import static de.nb.aventiure2.data.world.gameobjects.GameObjects.FROSCHPRINZ;
import static de.nb.aventiure2.data.world.gameobjects.GameObjects.load;
import static de.nb.aventiure2.data.world.syscomp.feelings.Mood.UNTROESTLICH;
import static de.nb.aventiure2.data.world.syscomp.state.GameObjectState.HAT_SC_HILFSBEREIT_ANGESPROCHEN;
import static de.nb.aventiure2.data.world.syscomp.state.GameObjectState.UNAUFFAELLIG;
import static de.nb.aventiure2.data.world.time.AvTimeSpan.mins;
import static de.nb.aventiure2.data.world.time.AvTimeSpan.secs;
import static de.nb.aventiure2.german.base.AllgDescription.neuerSatz;
import static de.nb.aventiure2.german.base.AllgDescription.satzanschluss;
import static de.nb.aventiure2.german.base.DuDescription.du;

/**
 * Der Spielercharakter heult.
 */
public class HeulenAction extends AbstractScAction {
    private final List<? extends ILivingBeingGO> creaturesInRoom;

    public static Collection<HeulenAction> buildActions(
            final AvDatabase db,
            final StoryState initialStoryState,
            final SpielerCharakter sc, final List<? extends ILivingBeingGO> creaturesInRoom) {
        final ImmutableList.Builder<HeulenAction> res = ImmutableList.builder();
        if (sc.feelingsComp().hasMood(UNTROESTLICH)) {
            res.add(new HeulenAction(db, initialStoryState, creaturesInRoom));
        }

        return res.build();
    }

    private HeulenAction(final AvDatabase db,
                         final StoryState initialStoryState,
                         final List<? extends ILivingBeingGO> creaturesInRoom) {
        super(db, initialStoryState);
        this.creaturesInRoom = creaturesInRoom;
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
    public AvTimeSpan narrateAndDo() {
        if (isDefinitivWiederholung()) {
            return narrateAndDoWiederholung();
        }

        return narrateAndDoErstesMal();
    }

    private <F extends IDescribableGO & IHasStateGO & ITalkerGO & ILivingBeingGO>
    AvTimeSpan narrateAndDoWiederholung() {
        final F froschprinz = (F) load(db, FROSCHPRINZ);
        if (creaturesInRoom.contains(froschprinz) &&
                (froschprinz.stateComp().hasState(UNAUFFAELLIG))) {
            return narrateAndDoFroschprinzUnauffaellig(froschprinz);
        }

        sc.memoryComp().setLastAction(buildMemorizedAction());

        final ImmutableList.Builder<AbstractDescription<?>> alt = ImmutableList.builder();
        if (initialStoryState.allowsAdditionalDuSatzreihengliedOhneSubjekt()) {
            alt.add(satzanschluss("und weinst", mins(1))
                    .undWartest());
            alt.add(satzanschluss(", so viele Tränen haben sich angestaut", mins(1)));
        }

        alt.add(du("kannst", "dich gar nicht mehr beruhigen", mins(1))
                .undWartest());
        return n.addAlt(alt);
    }

    private <F extends IDescribableGO & IHasStateGO & ITalkerGO>
    AvTimeSpan narrateAndDoFroschprinzUnauffaellig(final F froschprinz) {
        // STORY Nachts schläft der Frosch?!

        sc.memoryComp().setLastAction(buildMemorizedAction());
        froschprinz.stateComp().setState(HAT_SC_HILFSBEREIT_ANGESPROCHEN);
        froschprinz.talkingComp().setTalkingTo(sc);
        sc.feelingsComp().setMood(Mood.NEUTRAL);
        sc.memoryComp().upgradeKnown(FROSCHPRINZ, Known.getKnown(
                sc.locationComp().getLocation().getLichtverhaeltnisseInside()));

        return n.add(du("weinst", "immer lauter und kannst dich gar nicht trösten. " +
                "Und wie du so klagst, ruft dir jemand zu: „Was hast du vor, " +
                "du schreist ja, dass sich ein Stein erbarmen möchte.“ Du siehst " +
                "dich um, woher " +
                "die Stimme käme, da erblickst du " +
                getDescription(froschprinz).akk(), "immer lauter", secs(30)));
    }

    @Override
    protected boolean isDefinitivWiederholung() {
        return sc.memoryComp().lastActionWas(buildMemorizedAction());
    }

    private static Action buildMemorizedAction() {
        return new Action(Action.Type.HEULEN, (GameObjectId) null);
    }

    private AvTimeSpan narrateAndDoErstesMal() {
        sc.memoryComp().setLastAction(buildMemorizedAction());

        final ImmutableList.Builder<AbstractDescription<?>> alt = ImmutableList.builder();
        alt.add(neuerSatz("Dich überkommt ein Schluchzen", mins(1)));

        if (initialStoryState.dann()) {
            alt.add(neuerSatz("Dann bricht die Trauer aus dir heraus und du heulst los",
                    mins(1)));
        }

        alt.add(du("weinst", mins(1))
                .undWartest()
                .dann());
        return n.addAlt(alt);
    }
}
