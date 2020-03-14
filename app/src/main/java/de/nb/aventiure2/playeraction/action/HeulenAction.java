package de.nb.aventiure2.playeraction.action;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.common.collect.ImmutableList;

import java.util.Collection;
import java.util.List;

import de.nb.aventiure2.data.database.AvDatabase;
import de.nb.aventiure2.data.storystate.StoryState;
import de.nb.aventiure2.data.world.creature.Creature;
import de.nb.aventiure2.data.world.creature.CreatureData;
import de.nb.aventiure2.data.world.player.stats.PlayerStateOfMind;
import de.nb.aventiure2.data.world.player.stats.PlayerStats;
import de.nb.aventiure2.playeraction.AbstractPlayerAction;

import static de.nb.aventiure2.data.storystate.StoryState.StartsNew.PARAGRAPH;
import static de.nb.aventiure2.data.storystate.StoryState.StartsNew.SENTENCE;
import static de.nb.aventiure2.data.storystate.StoryState.StartsNew.WORD;
import static de.nb.aventiure2.data.world.creature.Creature.Key.FROSCHPRINZ;
import static de.nb.aventiure2.data.world.creature.CreatureState.HAT_SC_HILFSBEREIT_ANGESPROCHEN;
import static de.nb.aventiure2.data.world.creature.CreatureState.UNAUFFAELLIG;

/**
 * Der Spieler(charakter) heult.
 */
public class HeulenAction extends AbstractPlayerAction {
    private final List<CreatureData> creatures;

    public HeulenAction(final AvDatabase db,
                        final List<CreatureData> creatures) {
        super(db);
        this.creatures = creatures;
    }

    public static Collection<AbstractPlayerAction> buildActions(
            final AvDatabase db, final PlayerStats stats, final List<CreatureData> creaturesInRoom) {
        final ImmutableList.Builder<AbstractPlayerAction> res = ImmutableList.builder();
        if (stats.getStateOfMind() == PlayerStateOfMind.UNTROESTLICH) {
            res.add(new HeulenAction(db, creaturesInRoom));
        }

        return res.build();
    }

    @Override
    @NonNull
    public String getName() {
        return "Heulen";
    }

    @Override
    public void narrateAndDo(final StoryState currentStoryState) {
        if (currentStoryState.lastActionWas(HeulenAction.class)) {
            narrateAndDoWiederholung(currentStoryState);
            return;
        }

        narrateAndDoErstesMal(currentStoryState);
    }

    private void narrateAndDoWiederholung(final StoryState currentStoryState) {
        @Nullable final CreatureData froschprinz =
                findCreatureInRoom(FROSCHPRINZ);

        if (froschprinz != null) {
            narrateAndDoFroschprinz(currentStoryState, froschprinz);
            return;
        }

        if (currentStoryState.allowsAdditionalDuSatzreihengliedOhneSubjekt()) {
            switch (PlayerActionUtil.random(3)) {
                case 1:
                    n.add(t(WORD, "und weinst")
                            .undWartest());
                    return;
                case 2:
                    n.add(t(WORD, ", so viele Tränen haben sich angestaut"));
                    return;
                //  3: Go on
            }
        }

        n.add(

                t(SENTENCE,
                        "Du kannst dich gar nicht mehr beruhigen")
                        .undWartest());
    }

    private void narrateAndDoFroschprinz(final StoryState currentStoryState,
                                         final CreatureData froschprinz) {
        if (froschprinz.hasState(UNAUFFAELLIG)) {
            final String desc = "weinst immer lauter und kannst dich gar nicht trösten. " +
                    "Und wie du so klagst, ruft dir jemand zu: „Was hast du vor, " +
                    "du schreist ja, dass sich ein Stein erbarmen möchte.“ Du siehst " +
                    "dich um, woher " +
                    "die Stimme käme, da erblickst du " +
                    froschprinz.akk();
            if (currentStoryState.allowsAdditionalDuSatzreihengliedOhneSubjekt()) {
                n.add(t(WORD, "und " + desc));
            } else {
                n.add(t(SENTENCE, "Du " + desc));
            }

            creatureDataDao.setState(FROSCHPRINZ, HAT_SC_HILFSBEREIT_ANGESPROCHEN);
            creatureDataDao.setKnown(FROSCHPRINZ);
            playerStatsDao.setStateOfMind(PlayerStateOfMind.NEUTRAL);
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

    private void narrateAndDoErstesMal(final StoryState currentStoryState) {
        if (PlayerActionUtil.random(2) == 1) {
            n.add(t(PARAGRAPH,
                    "Plötzlich überkommt dich ein Schluchzen"));
            return;
        }

        if (currentStoryState.dann()) {
            n.add(t(PARAGRAPH,
                    "Dann bricht die Trauer aus dir heraus und du heulst los"));
            return;
        }

        n.add(t(PARAGRAPH,
                "Du weinst")
                .undWartest()
                .dann());
    }
}
