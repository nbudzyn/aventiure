package de.nb.aventiure2.scaction.action.creature.conversation;

import com.google.common.collect.ImmutableList;

import java.util.List;

import de.nb.aventiure2.data.database.AvDatabase;
import de.nb.aventiure2.data.storystate.StoryState;
import de.nb.aventiure2.data.world.syscomp.description.IDescribableGO;
import de.nb.aventiure2.data.world.syscomp.location.ILocatableGO;
import de.nb.aventiure2.data.world.syscomp.state.IHasStateGO;
import de.nb.aventiure2.data.world.syscomp.storingplace.IHasStoringPlaceGO;
import de.nb.aventiure2.data.world.syscomp.talking.ITalkerGO;

import static de.nb.aventiure2.data.world.gameobjects.GameObjects.FROSCHPRINZ;

/**
 * Contains all {@link ConversationStep}s;
 */
public class CreatureConversationSteps {
    public static <TALKER extends IDescribableGO & ITalkerGO,
            LOC_DESC extends ILocatableGO & IDescribableGO,
            F extends IDescribableGO & IHasStateGO & ITalkerGO>
    List<ConversationStep> getPossibleSteps(
            final AvDatabase db, final StoryState initialStoryState,
            final IHasStoringPlaceGO room,
            final TALKER talker) {
        AbstractConversationStepBuilder stepBuilder = null;
        if (talker.is(FROSCHPRINZ)) {
            stepBuilder =
                    new FroschprinzConversationStepBuilder<LOC_DESC, F>(db, initialStoryState,
                            room, (F) talker);
        }

        if (stepBuilder == null) {
            return ImmutableList.of();
        }

        return stepBuilder.getPossibleSteps();
    }

    private CreatureConversationSteps() {
    }
}
