package de.nb.aventiure2.scaction.action;

import androidx.annotation.NonNull;

import com.google.common.collect.ImmutableList;

import java.util.Collection;
import java.util.List;

import de.nb.aventiure2.data.database.AvDatabase;
import de.nb.aventiure2.data.storystate.StoryState;
import de.nb.aventiure2.data.storystate.StoryStateBuilder;
import de.nb.aventiure2.data.world.base.GameObjectId;
import de.nb.aventiure2.data.world.gameobjects.player.SpielerCharakter;
import de.nb.aventiure2.data.world.syscomp.alive.ILivingBeingGO;
import de.nb.aventiure2.data.world.syscomp.description.IDescribableGO;
import de.nb.aventiure2.data.world.syscomp.feelings.Mood;
import de.nb.aventiure2.data.world.syscomp.memory.Action;
import de.nb.aventiure2.data.world.syscomp.memory.Known;
import de.nb.aventiure2.data.world.syscomp.state.IHasStateGO;
import de.nb.aventiure2.data.world.time.AvTimeSpan;
import de.nb.aventiure2.scaction.AbstractScAction;

import static de.nb.aventiure2.data.storystate.StoryState.StructuralElement.PARAGRAPH;
import static de.nb.aventiure2.data.storystate.StoryState.StructuralElement.SENTENCE;
import static de.nb.aventiure2.data.storystate.StoryState.StructuralElement.WORD;
import static de.nb.aventiure2.data.world.gameobjects.GameObjects.FROSCHPRINZ;
import static de.nb.aventiure2.data.world.gameobjects.GameObjects.load;
import static de.nb.aventiure2.data.world.syscomp.feelings.Mood.UNTROESTLICH;
import static de.nb.aventiure2.data.world.syscomp.state.GameObjectState.HAT_SC_HILFSBEREIT_ANGESPROCHEN;
import static de.nb.aventiure2.data.world.syscomp.state.GameObjectState.UNAUFFAELLIG;
import static de.nb.aventiure2.data.world.time.AvTimeSpan.mins;
import static de.nb.aventiure2.data.world.time.AvTimeSpan.secs;

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

    private <F extends IDescribableGO & IHasStateGO & ILivingBeingGO>
    AvTimeSpan narrateAndDoWiederholung() {
        final F froschprinz = (F) load(db, FROSCHPRINZ);
        if (creaturesInRoom.contains(froschprinz) &&
                (froschprinz.stateComp().hasState(UNAUFFAELLIG))) {
            return narrateAndDoFroschprinzUnauffaellig(froschprinz);
        }

        final ImmutableList.Builder<StoryStateBuilder> alt = ImmutableList.builder();
        if (initialStoryState.allowsAdditionalDuSatzreihengliedOhneSubjekt()) {
            alt.add(t(WORD, "und weinst")
                    .undWartest());
            alt.add(t(WORD, ", so viele Tränen haben sich angestaut"));
        }

        alt.add(t(SENTENCE,
                "Du kannst dich gar nicht mehr beruhigen")
                .undWartest());
        n.add(alt(alt));

        sc.memoryComp().setLastAction(buildMemorizedAction());

        return mins(1);
    }

    private <F extends IDescribableGO & IHasStateGO & ILivingBeingGO>
    AvTimeSpan narrateAndDoFroschprinzUnauffaellig(final F froschprinz) {
        // STORY Nachts schläft der Frosch?!
        final String desc = "weinst immer lauter und kannst dich gar nicht trösten. " +
                "Und wie du so klagst, ruft dir jemand zu: „Was hast du vor, " +
                "du schreist ja, dass sich ein Stein erbarmen möchte.“ Du siehst " +
                "dich um, woher " +
                "die Stimme käme, da erblickst du " +
                getDescription(froschprinz).akk();
        if (initialStoryState.allowsAdditionalDuSatzreihengliedOhneSubjekt()) {
            n.add(t(WORD, "und " + desc)
                    .imGespraechMit(froschprinz));
        } else {
            n.add(t(SENTENCE, "Du " + desc)
                    .imGespraechMit(froschprinz));
        }

        sc.memoryComp().setLastAction(buildMemorizedAction());
        froschprinz.stateComp().setState(HAT_SC_HILFSBEREIT_ANGESPROCHEN);
        sc.memoryComp().upgradeKnown(FROSCHPRINZ, Known.getKnown(getLichtverhaeltnisse(
                sc.locationComp().getLocation())));
        sc.feelingsComp().setMood(Mood.NEUTRAL);
        return secs(30);
    }

    @Override
    protected boolean isDefinitivWiederholung() {
        return sc.memoryComp().lastActionWas(buildMemorizedAction());
    }

    private static Action buildMemorizedAction() {
        return new Action(Action.Type.HEULEN, (GameObjectId) null);
    }

    private AvTimeSpan narrateAndDoErstesMal() {
        final ImmutableList.Builder<StoryStateBuilder> alt = ImmutableList.builder();

        alt.add(t(PARAGRAPH,
                "Dich überkommt ein Schluchzen"));

        if (initialStoryState.dann()) {
            alt.add(t(PARAGRAPH,
                    "Dann bricht die Trauer aus dir heraus und du heulst los"));
        }

        alt.add(t(PARAGRAPH,
                "Du weinst")
                .undWartest()
                .dann());
        n.add(alt(alt));

        sc.memoryComp().setLastAction(buildMemorizedAction());

        return mins(1);
    }
}
