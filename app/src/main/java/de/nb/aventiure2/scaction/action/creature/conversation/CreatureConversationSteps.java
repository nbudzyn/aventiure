package de.nb.aventiure2.scaction.action.creature.conversation;

import com.google.common.collect.ImmutableList;

import java.util.List;

import de.nb.aventiure2.data.database.AvDatabase;
import de.nb.aventiure2.data.storystate.StoryState;
import de.nb.aventiure2.data.world.alive.ILivingBeingGO;
import de.nb.aventiure2.data.world.storingplace.IHasStoringPlaceGO;

import static de.nb.aventiure2.data.world.gameobjects.GameObjects.FROSCHPRINZ;

/**
 * Contains all {@link CreatureConversationStep}s;
 */
public class CreatureConversationSteps {
    public static List<CreatureConversationStep> getPossibleSteps(
            final AvDatabase db, final StoryState initialStoryState,
            final IHasStoringPlaceGO room,
            final ILivingBeingGO creature) {
        AbstractCreatureConversationStepBuilder stepBuilder = null;
        if (creature.is(FROSCHPRINZ)) {
            stepBuilder =
                    new FroschprinzConversationStepBuilder(db, initialStoryState,
                            room, creature);
        }

        if (stepBuilder == null) {
            return ImmutableList.of();
        }

        return stepBuilder.getPossibleSteps();
    }

    private CreatureConversationSteps() {
    }
}
