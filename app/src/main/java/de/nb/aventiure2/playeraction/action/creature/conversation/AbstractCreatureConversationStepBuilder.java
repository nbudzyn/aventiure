package de.nb.aventiure2.playeraction.action.creature.conversation;

import androidx.annotation.NonNull;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import de.nb.aventiure2.data.database.AvDatabase;
import de.nb.aventiure2.data.storystate.IPlayerAction;
import de.nb.aventiure2.data.storystate.StoryState;
import de.nb.aventiure2.data.storystate.StoryStateBuilder;
import de.nb.aventiure2.data.storystate.StoryStateDao;
import de.nb.aventiure2.data.world.creature.CreatureData;
import de.nb.aventiure2.data.world.object.AvObject;
import de.nb.aventiure2.data.world.object.ObjectData;
import de.nb.aventiure2.data.world.room.AvRoom;
import de.nb.aventiure2.german.praedikat.Praedikat;

import static de.nb.aventiure2.playeraction.action.creature.conversation.CreatureConversationStep.ALWAYS_POSSIBLE;
import static de.nb.aventiure2.playeraction.action.creature.conversation.CreatureConversationStep.DEFAULT_ENTRY_RE_ENTRY_NAME;
import static de.nb.aventiure2.playeraction.action.creature.conversation.CreatureConversationStep.DEFAULT_EXIT_NAME;

/**
 * Abstrakte Oberklasse zum Erzeugen von {@link CreatureConversationStep}s für eine
 * {@link de.nb.aventiure2.data.world.creature.Creature}.
 */
abstract class AbstractCreatureConversationStepBuilder {
    protected final AvDatabase db;
    protected final StoryStateDao n;

    /**
     * The {@link StoryState} at the beginning of the step.
     */
    protected final StoryState initialStoryState;

    protected final Class<? extends IPlayerAction> currentActionClass;

    protected final AvRoom room;
    protected final Map<AvObject.Key, ObjectData> allObjectsByKey;

    @NonNull
    protected final CreatureData creatureData;

    AbstractCreatureConversationStepBuilder(final AvDatabase db, final StoryState initialStoryState,
                                            final Class<? extends IPlayerAction> currentActionClass,
                                            final AvRoom room,
                                            final Map<AvObject.Key, ObjectData> allObjectsByKey,
                                            @NonNull final CreatureData creatureData) {
        this.db = db;

        n = db.storyStateDao();

        this.initialStoryState = initialStoryState;
        this.currentActionClass = currentActionClass;
        this.room = room;
        this.creatureData = creatureData;
        this.allObjectsByKey = allObjectsByKey;
    }

    List<CreatureConversationStep> getPossibleSteps() {
        final ImmutableList.Builder<CreatureConversationStep> res =
                ImmutableList.builder();

        for (final CreatureConversationStep step : getAllStepsForCurrentState()) {
            if (step.isStepPossible()) {
                res.add(step);
            }
        }

        return res.build();
    }

    abstract List<CreatureConversationStep> getAllStepsForCurrentState();

    static CreatureConversationStep entrySt(
            final CreatureConversationStep.NarrationAndAction narrationAndAction) {
        return entrySt(DEFAULT_ENTRY_RE_ENTRY_NAME, narrationAndAction);
    }

    static CreatureConversationStep entrySt(final Praedikat entryName,
                                            final CreatureConversationStep.NarrationAndAction narrationAndAction) {
        return entrySt(ALWAYS_POSSIBLE, entryName, narrationAndAction);
    }

    /**
     * Creates a {@link CreatureConversationStep} to enter a conversation.
     *
     * @param condition If this condition does not hold, this step is impossible
     *                  (there won't be an action for this step).
     */
    static CreatureConversationStep entrySt(
            final CreatureConversationStep.Condition condition,
            final CreatureConversationStep.NarrationAndAction narrationAndAction) {
        return entrySt(condition, DEFAULT_ENTRY_RE_ENTRY_NAME, narrationAndAction);
    }

    /**
     * Creates a {@link CreatureConversationStep} to enter a conversation.
     *
     * @param condition If this condition does not hold, this step is impossible
     *                  (there won't be an action for this step).
     */
    static CreatureConversationStep entrySt(
            final CreatureConversationStep.Condition condition,
            final Praedikat entryName,
            final CreatureConversationStep.NarrationAndAction narrationAndAction) {
        return new CreatureConversationStep(CreatureConversationStep.Type.ENTRY_RE_ENTRY, condition,
                entryName,
                narrationAndAction);
    }

    static CreatureConversationStep immReEntrySt(
            final CreatureConversationStep.NarrationAndAction narrationAndAction) {
        return immReEntrySt(DEFAULT_ENTRY_RE_ENTRY_NAME, narrationAndAction);
    }

    static CreatureConversationStep immReEntrySt(final Praedikat entryName,
                                                 final CreatureConversationStep.NarrationAndAction narrationAndAction) {
        return immReEntrySt(ALWAYS_POSSIBLE, entryName, narrationAndAction);
    }

    /**
     * Creates a {@link CreatureConversationStep} to immediately re-enter a conversation.
     *
     * @param condition If this condition does not hold, this step is impossible
     *                  (there won't be an action for this step).
     */
    static CreatureConversationStep immReEntrySt(final CreatureConversationStep.Condition condition,
                                                 final CreatureConversationStep.NarrationAndAction narrationAndAction) {
        return immReEntrySt(condition, DEFAULT_ENTRY_RE_ENTRY_NAME, narrationAndAction);
    }

    /**
     * Creates a {@link CreatureConversationStep} to immediately re-enter a conversation.
     *
     * @param condition If this condition does not hold, this step is impossible
     *                  (there won't be an action for this step).
     */
    static CreatureConversationStep immReEntrySt(
            final CreatureConversationStep.Condition condition,
            final Praedikat entryName,
            final CreatureConversationStep.NarrationAndAction narrationAndAction) {
        return new CreatureConversationStep(CreatureConversationStep.Type.IMMEDIATE_RE_ENTRY,
                condition,
                entryName, narrationAndAction);
    }

    static CreatureConversationStep reEntrySt(
            final CreatureConversationStep.NarrationAndAction narrationAndAction) {
        return reEntrySt(DEFAULT_ENTRY_RE_ENTRY_NAME, narrationAndAction);
    }

    static CreatureConversationStep reEntrySt(final Praedikat entryName,
                                              final CreatureConversationStep.NarrationAndAction narrationAndAction) {
        return reEntrySt(ALWAYS_POSSIBLE, entryName, narrationAndAction);
    }

    /**
     * Creates a {@link CreatureConversationStep} to re-enter a conversation (but not immediately)
     *
     * @param condition If this condition does not hold, this step is impossible
     *                  (there won't be an action for this step).
     */
    static CreatureConversationStep reEntrySt(
            final CreatureConversationStep.Condition condition,
            final CreatureConversationStep.NarrationAndAction narrationAndAction) {
        return reEntrySt(condition, DEFAULT_ENTRY_RE_ENTRY_NAME, narrationAndAction);
    }

    /**
     * Creates a {@link CreatureConversationStep} to re-enter a conversation (but not immediately)
     *
     * @param condition If this condition does not hold, this step is impossible
     *                  (there won't be an action for this step).
     */
    static CreatureConversationStep reEntrySt(
            final CreatureConversationStep.Condition condition,
            final Praedikat entryName,
            final CreatureConversationStep.NarrationAndAction narrationAndAction) {
        return new CreatureConversationStep(CreatureConversationStep.Type.ENTRY_RE_ENTRY, condition,
                entryName,
                narrationAndAction);
    }

    static CreatureConversationStep st(final Praedikat name,
                                       final CreatureConversationStep.NarrationAndAction narrationAndAction) {
        return st(ALWAYS_POSSIBLE, name, narrationAndAction);
    }

    /**
     * Creates a normal {@link CreatureConversationStep}
     *
     * @param condition If this condition does not hold, this step is impossible
     *                  (there won't be an action for this step).
     */
    static CreatureConversationStep st(
            final CreatureConversationStep.Condition condition,
            final Praedikat name,
            final CreatureConversationStep.NarrationAndAction narrationAndAction) {
        return new CreatureConversationStep(
                CreatureConversationStep.Type.NORMAL, condition, name, narrationAndAction);
    }

    static CreatureConversationStep exitSt(
            final CreatureConversationStep.NarrationAndAction narrationAndAction) {
        return exitSt(DEFAULT_EXIT_NAME, narrationAndAction);
    }

    static CreatureConversationStep exitSt(final Praedikat exitName,
                                           final CreatureConversationStep.NarrationAndAction narrationAndAction) {
        return new CreatureConversationStep(CreatureConversationStep.Type.EXIT, ALWAYS_POSSIBLE,
                exitName,
                narrationAndAction);
    }

    StoryStateBuilder alt(final ImmutableCollection.Builder<StoryStateBuilder> alternatives) {
        return alt(alternatives.build());
    }

    StoryStateBuilder alt(final Collection<StoryStateBuilder> alternatives) {
        return alt(alternatives.toArray(new StoryStateBuilder[alternatives.size()]));
    }

    /**
     * Wählt einen {@link StoryStateBuilder} aus den Alternativen -
     * versucht dabei vor allem, Wiederholgungen mit dem unmittelbar zuvor geschriebenen
     * Story-Text zu vermeiden.
     */
    StoryStateBuilder alt(final StoryStateBuilder... alternatives) {
        return n.chooseNextFrom(alternatives);
    }

    StoryStateBuilder t(
            @NonNull final StoryState.StructuralElement startsNew,
            @NonNull final String text) {
        return StoryStateBuilder.t(currentActionClass, startsNew, text)
                .letzterRaum(db.playerLocationDao().getPlayerLocation().getRoom())
                // Sensible default - caller may override this setting
                .imGespraechMit(creatureData.getCreature());
    }
}
