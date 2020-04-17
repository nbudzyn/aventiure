package de.nb.aventiure2.scaction.action.creature.conversation;

import com.google.common.collect.ImmutableList;

import java.util.List;
import java.util.Map;

import de.nb.aventiure2.data.database.AvDatabase;
import de.nb.aventiure2.data.storystate.IPlayerAction;
import de.nb.aventiure2.data.storystate.StoryState;
import de.nb.aventiure2.data.world.base.GameObjectId;
import de.nb.aventiure2.data.world.entity.creature.CreatureData;
import de.nb.aventiure2.data.world.entity.object.ObjectData;
import de.nb.aventiure2.data.world.room.AvRoom;

import static de.nb.aventiure2.data.world.entity.creature.Creatures.FROSCHPRINZ;

/**
 * Contains all {@link CreatureConversationStep}s;
 */
public class CreatureConversationSteps {
    public static List<CreatureConversationStep> getPossibleSteps(
            final AvDatabase db, final StoryState initialStoryState,
            final Class<? extends IPlayerAction> currentActionClass,
            final AvRoom room,
            final Map<GameObjectId, ObjectData> allObjectsById,
            final CreatureData creatureData) {
        AbstractCreatureConversationStepBuilder stepBuilder = null;
        if (creatureData.creatureIs(FROSCHPRINZ)) {
            stepBuilder =
                    new FroschprinzConversationStepBuilder(db, initialStoryState,
                            currentActionClass,
                            room, allObjectsById, creatureData);
        }

        if (stepBuilder == null) {
            return ImmutableList.of();
        }

        return stepBuilder.getPossibleSteps();
    }

    private CreatureConversationSteps() {
    }
}
