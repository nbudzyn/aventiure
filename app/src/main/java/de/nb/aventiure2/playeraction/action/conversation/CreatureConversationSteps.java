package de.nb.aventiure2.playeraction.action.conversation;

import com.google.common.collect.ImmutableList;

import java.util.List;
import java.util.Map;

import de.nb.aventiure2.data.database.AvDatabase;
import de.nb.aventiure2.data.storystate.IPlayerAction;
import de.nb.aventiure2.data.storystate.StoryState;
import de.nb.aventiure2.data.world.creature.CreatureData;
import de.nb.aventiure2.data.world.object.AvObject;
import de.nb.aventiure2.data.world.object.ObjectData;
import de.nb.aventiure2.data.world.room.AvRoom;

import static de.nb.aventiure2.data.world.creature.Creature.Key.FROSCHPRINZ;

/**
 * Contains all {@link CreatureConversationStep}s;
 */
public class CreatureConversationSteps {
    public static List<CreatureConversationStep> getPossibleSteps(
            final AvDatabase db, final StoryState initialStoryState,
            final Class<? extends IPlayerAction> currentActionClass,
            final AvRoom room,
            final Map<AvObject.Key, ObjectData> allObjectsByKey,
            final CreatureData creatureData) {
        AbstractCreatureConversationStepBuilder stepBuilder = null;
        if (creatureData.creatureIs(FROSCHPRINZ)) {
            stepBuilder =
                    new FroschprinzConversationStepBuilder(db, initialStoryState,
                            currentActionClass,
                            room, allObjectsByKey, creatureData);
        }

        if (stepBuilder == null) {
            return ImmutableList.of();
        }

        return stepBuilder.getPossibleSteps();
    }

    private CreatureConversationSteps() {
    }
}
