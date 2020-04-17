package de.nb.aventiure2.scaction.action;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.common.collect.ImmutableList;

import java.util.Collection;
import java.util.List;

import de.nb.aventiure2.data.database.AvDatabase;
import de.nb.aventiure2.data.storystate.StoryState;
import de.nb.aventiure2.data.storystate.StoryStateBuilder;
import de.nb.aventiure2.data.world.base.GameObjectId;
import de.nb.aventiure2.data.world.entity.creature.CreatureData;
import de.nb.aventiure2.data.world.player.stats.ScStateOfMind;
import de.nb.aventiure2.data.world.player.stats.ScStats;
import de.nb.aventiure2.data.world.time.AvTimeSpan;
import de.nb.aventiure2.scaction.AbstractScAction;

import static de.nb.aventiure2.data.storystate.StoryState.StructuralElement.PARAGRAPH;
import static de.nb.aventiure2.data.storystate.StoryState.StructuralElement.SENTENCE;
import static de.nb.aventiure2.data.storystate.StoryState.StructuralElement.WORD;
import static de.nb.aventiure2.data.world.entity.creature.Creature.FROSCHPRINZ;
import static de.nb.aventiure2.data.world.entity.creature.CreatureState.HAT_SC_HILFSBEREIT_ANGESPROCHEN;
import static de.nb.aventiure2.data.world.entity.creature.CreatureState.UNAUFFAELLIG;
import static de.nb.aventiure2.data.world.time.AvTimeSpan.mins;
import static de.nb.aventiure2.data.world.time.AvTimeSpan.secs;

/**
 * Der Spielercharakter heult.
 */
public class HeulenAction extends AbstractScAction {
    private final List<CreatureData> creatures;

    public static Collection<HeulenAction> buildActions(
            final AvDatabase db,
            final StoryState initialStoryState,
            final ScStats stats, final List<CreatureData> creaturesInRoom) {
        final ImmutableList.Builder<HeulenAction> res = ImmutableList.builder();
        if (stats.getStateOfMind() == ScStateOfMind.UNTROESTLICH) {
            res.add(new HeulenAction(db, initialStoryState, creaturesInRoom));
        }

        return res.build();
    }

    private HeulenAction(final AvDatabase db,
                         final StoryState initialStoryState,
                         final List<CreatureData> creatures) {
        super(db, initialStoryState);
        this.creatures = creatures;
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
        if (initialStoryState.lastActionWas(HeulenAction.class)) {
            return narrateAndDoWiederholung();
        }

        return narrateAndDoErstesMal();
    }

    private AvTimeSpan narrateAndDoWiederholung() {
        @Nullable final CreatureData froschprinz =
                findCreatureInRoom(FROSCHPRINZ);

        if (froschprinz != null) {
            return narrateAndDoFroschprinz(froschprinz);
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

        return mins(1);
    }

    private AvTimeSpan narrateAndDoFroschprinz(final CreatureData froschprinz) {
        if (froschprinz.hasState(UNAUFFAELLIG)) {
            // STORY Nachts schläft der Frosch
            final String desc = "weinst immer lauter und kannst dich gar nicht trösten. " +
                    "Und wie du so klagst, ruft dir jemand zu: „Was hast du vor, " +
                    "du schreist ja, dass sich ein Stein erbarmen möchte.“ Du siehst " +
                    "dich um, woher " +
                    "die Stimme käme, da erblickst du " +
                    froschprinz.akk();
            if (initialStoryState.allowsAdditionalDuSatzreihengliedOhneSubjekt()) {
                n.add(t(WORD, "und " + desc)
                        .imGespraechMit(froschprinz.getCreature()));
            } else {
                n.add(t(SENTENCE, "Du " + desc)
                        .imGespraechMit(froschprinz.getCreature()));
            }

            db.creatureDataDao().setState(FROSCHPRINZ, HAT_SC_HILFSBEREIT_ANGESPROCHEN);
            db.creatureDataDao().setKnown(FROSCHPRINZ);
            db.playerStatsDao().setStateOfMind(ScStateOfMind.NEUTRAL);
            return secs(30);
        }

        throw new IllegalStateException("Unexpected creature state: " + froschprinz.getState());
    }

    @Nullable
    private CreatureData findCreatureInRoom(final GameObjectId id) {
        for (final CreatureData creatureData : creatures) {
            if (creatureData.creatureIs(id)) {
                return creatureData;
            }
        }

        return null;
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

        return mins(1);
    }
}
