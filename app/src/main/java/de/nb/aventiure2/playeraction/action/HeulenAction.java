package de.nb.aventiure2.playeraction.action;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.common.collect.ImmutableList;

import java.util.Collection;
import java.util.List;

import de.nb.aventiure2.data.database.AvDatabase;
import de.nb.aventiure2.data.storystate.StoryState;
import de.nb.aventiure2.data.storystate.StoryStateBuilder;
import de.nb.aventiure2.data.world.creature.Creature;
import de.nb.aventiure2.data.world.creature.CreatureData;
import de.nb.aventiure2.data.world.player.stats.PlayerStateOfMind;
import de.nb.aventiure2.data.world.player.stats.PlayerStats;
import de.nb.aventiure2.playeraction.AbstractPlayerAction;

import static de.nb.aventiure2.data.storystate.StoryState.StructuralElement.PARAGRAPH;
import static de.nb.aventiure2.data.storystate.StoryState.StructuralElement.SENTENCE;
import static de.nb.aventiure2.data.storystate.StoryState.StructuralElement.WORD;
import static de.nb.aventiure2.data.world.creature.Creature.Key.FROSCHPRINZ;
import static de.nb.aventiure2.data.world.creature.CreatureState.HAT_SC_HILFSBEREIT_ANGESPROCHEN;
import static de.nb.aventiure2.data.world.creature.CreatureState.UNAUFFAELLIG;

/**
 * Der Spielercharakter heult.
 */
public class HeulenAction extends AbstractPlayerAction {
    private final List<CreatureData> creatures;

    public static Collection<AbstractPlayerAction> buildActions(
            final AvDatabase db,
            final StoryState initialStoryState,
            final PlayerStats stats, final List<CreatureData> creaturesInRoom) {
        final ImmutableList.Builder<AbstractPlayerAction> res = ImmutableList.builder();
        if (stats.getStateOfMind() == PlayerStateOfMind.UNTROESTLICH) {
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
    public void narrateAndDo() {
        if (initialStoryState.lastActionWas(HeulenAction.class)) {
            narrateAndDoWiederholung();
            return;
        }

        narrateAndDoErstesMal();
    }

    private void narrateAndDoWiederholung() {
        @Nullable final CreatureData froschprinz =
                findCreatureInRoom(FROSCHPRINZ);

        if (froschprinz != null) {
            narrateAndDoFroschprinz(froschprinz);
            return;
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
    }

    private void narrateAndDoFroschprinz(final CreatureData froschprinz) {
        if (froschprinz.hasState(UNAUFFAELLIG)) {
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
            db.playerStatsDao().setStateOfMind(PlayerStateOfMind.NEUTRAL);
            return;
        }

        throw new IllegalStateException("Unexpected creature state: " + froschprinz.getState());
    }

    @Nullable
    private CreatureData findCreatureInRoom(final Creature.Key key) {
        for (final CreatureData creatureData : creatures) {
            if (creatureData.creatureIs(key)) {
                return creatureData;
            }
        }

        return null;
    }

    private void narrateAndDoErstesMal() {
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
    }
}
