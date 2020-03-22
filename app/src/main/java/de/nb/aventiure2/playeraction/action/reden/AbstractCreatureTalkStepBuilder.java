package de.nb.aventiure2.playeraction.action.reden;

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

import static de.nb.aventiure2.playeraction.action.reden.CreatureTalkStep.ALWAYS_POSSIBLE;
import static de.nb.aventiure2.playeraction.action.reden.CreatureTalkStep.DEFAULT_ENTRY_RE_ENTRY_NAME;
import static de.nb.aventiure2.playeraction.action.reden.CreatureTalkStep.DEFAULT_EXIT_NAME;

/**
 * Abstrakte Oberklasse zum Erzeugen von {@link CreatureTalkStep}s für eine
 * {@link de.nb.aventiure2.data.world.creature.Creature}.
 */
abstract class AbstractCreatureTalkStepBuilder {
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

    AbstractCreatureTalkStepBuilder(final AvDatabase db, final StoryState initialStoryState,
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

    List<CreatureTalkStep> getPossibleSteps() {
        final ImmutableList.Builder<CreatureTalkStep> res =
                ImmutableList.builder();

        for (final CreatureTalkStep step : getAllStepsForCurrentState()) {
            if (step.isStepPossible()) {
                res.add(step);
            }
        }

        return res.build();
    }

    abstract List<CreatureTalkStep> getAllStepsForCurrentState();

    static CreatureTalkStep entrySt(
            final CreatureTalkStep.TalkStepNarrationAndAction narrationAndAction) {
        return entrySt(DEFAULT_ENTRY_RE_ENTRY_NAME, narrationAndAction);
    }

    static CreatureTalkStep entrySt(final Praedikat entryName,
                                    final CreatureTalkStep.TalkStepNarrationAndAction narrationAndAction) {
        return entrySt(ALWAYS_POSSIBLE, entryName, narrationAndAction);
    }

    /**
     * Creates a {@link CreatureTalkStep} to enter a conversation.
     *
     * @param condition If this condition does not hold, this step is impossible
     *                  (there won't be an action for this step).
     */
    static CreatureTalkStep entrySt(
            final CreatureTalkStep.TalkStepCondition condition,
            final CreatureTalkStep.TalkStepNarrationAndAction narrationAndAction) {
        return entrySt(condition, DEFAULT_ENTRY_RE_ENTRY_NAME, narrationAndAction);
    }

    /**
     * Creates a {@link CreatureTalkStep} to enter a conversation.
     *
     * @param condition If this condition does not hold, this step is impossible
     *                  (there won't be an action for this step).
     */
    static CreatureTalkStep entrySt(
            final CreatureTalkStep.TalkStepCondition condition,
            final Praedikat entryName,
            final CreatureTalkStep.TalkStepNarrationAndAction narrationAndAction) {
        return new CreatureTalkStep(CreatureTalkStep.Type.ENTRY_RE_ENTRY, condition, entryName,
                narrationAndAction);
    }

    static CreatureTalkStep immReEntrySt(
            final CreatureTalkStep.TalkStepNarrationAndAction narrationAndAction) {
        return immReEntrySt(DEFAULT_ENTRY_RE_ENTRY_NAME, narrationAndAction);
    }

    static CreatureTalkStep immReEntrySt(final Praedikat entryName,
                                         final CreatureTalkStep.TalkStepNarrationAndAction narrationAndAction) {
        return immReEntrySt(ALWAYS_POSSIBLE, entryName, narrationAndAction);
    }

    /**
     * Creates a {@link CreatureTalkStep} to immediately re-enter a conversation.
     *
     * @param condition If this condition does not hold, this step is impossible
     *                  (there won't be an action for this step).
     */
    static CreatureTalkStep immReEntrySt(final CreatureTalkStep.TalkStepCondition condition,
                                         final CreatureTalkStep.TalkStepNarrationAndAction narrationAndAction) {
        return immReEntrySt(condition, DEFAULT_ENTRY_RE_ENTRY_NAME, narrationAndAction);
    }

    /**
     * Creates a {@link CreatureTalkStep} to immediately re-enter a conversation.
     *
     * @param condition If this condition does not hold, this step is impossible
     *                  (there won't be an action for this step).
     */
    static CreatureTalkStep immReEntrySt(
            final CreatureTalkStep.TalkStepCondition condition,
            final Praedikat entryName,
            final CreatureTalkStep.TalkStepNarrationAndAction narrationAndAction) {
        return new CreatureTalkStep(CreatureTalkStep.Type.IMMEDIATE_RE_ENTRY, condition,
                entryName, narrationAndAction);
    }

    static CreatureTalkStep reEntrySt(
            final CreatureTalkStep.TalkStepNarrationAndAction narrationAndAction) {
        return reEntrySt(DEFAULT_ENTRY_RE_ENTRY_NAME, narrationAndAction);
    }

    static CreatureTalkStep reEntrySt(final Praedikat entryName,
                                      final CreatureTalkStep.TalkStepNarrationAndAction narrationAndAction) {
        return reEntrySt(ALWAYS_POSSIBLE, entryName, narrationAndAction);
    }

    /**
     * Creates a {@link CreatureTalkStep} to re-enter a conversation (but not immediately)
     *
     * @param condition If this condition does not hold, this step is impossible
     *                  (there won't be an action for this step).
     */
    static CreatureTalkStep reEntrySt(
            final CreatureTalkStep.TalkStepCondition condition,
            final CreatureTalkStep.TalkStepNarrationAndAction narrationAndAction) {
        return reEntrySt(condition, DEFAULT_ENTRY_RE_ENTRY_NAME, narrationAndAction);
    }

    /**
     * Creates a {@link CreatureTalkStep} to re-enter a conversation (but not immediately)
     *
     * @param condition If this condition does not hold, this step is impossible
     *                  (there won't be an action for this step).
     */
    static CreatureTalkStep reEntrySt(
            final CreatureTalkStep.TalkStepCondition condition,
            final Praedikat entryName,
            final CreatureTalkStep.TalkStepNarrationAndAction narrationAndAction) {
        return new CreatureTalkStep(CreatureTalkStep.Type.ENTRY_RE_ENTRY, condition, entryName,
                narrationAndAction);
    }

    static CreatureTalkStep st(final Praedikat name,
                               final CreatureTalkStep.TalkStepNarrationAndAction narrationAndAction) {
        return st(ALWAYS_POSSIBLE, name, narrationAndAction);
    }

    /**
     * Creates a normal {@link CreatureTalkStep}
     *
     * @param condition If this condition does not hold, this step is impossible
     *                  (there won't be an action for this step).
     */
    static CreatureTalkStep st(
            final CreatureTalkStep.TalkStepCondition condition,
            final Praedikat name,
            final CreatureTalkStep.TalkStepNarrationAndAction narrationAndAction) {
        return new CreatureTalkStep(
                CreatureTalkStep.Type.NORMAL, condition, name, narrationAndAction);
    }

    static CreatureTalkStep exitSt(
            final CreatureTalkStep.TalkStepNarrationAndAction narrationAndAction) {
        return exitSt(DEFAULT_EXIT_NAME, narrationAndAction);
    }

    static CreatureTalkStep exitSt(final Praedikat exitName,
                                   final CreatureTalkStep.TalkStepNarrationAndAction narrationAndAction) {
        return new CreatureTalkStep(CreatureTalkStep.Type.EXIT, ALWAYS_POSSIBLE, exitName,
                narrationAndAction);
    }

    StoryStateBuilder alt(final ImmutableCollection.Builder<StoryStateBuilder> alternatives) {
        return alt(alternatives.build());
    }

    StoryStateBuilder alt(final Collection<StoryStateBuilder> alternatives) {
        return alt(alternatives.toArray(new StoryStateBuilder[alternatives.size()]));
    }

    StoryStateBuilder alt(final StoryStateBuilder... alternatives) {
        return n.chooseNextFrom(alternatives);
    }

    StoryStateBuilder t(
            @NonNull final StoryState.StartsNew startsNew,
            @NonNull final String text) {
        return StoryStateBuilder.t(currentActionClass, startsNew, text)
                .letzterRaum(db.playerLocationDao().getPlayerLocation().getRoom())
                // Sensible default - caller may override this setting
                .imGespraechMit(creatureData.getCreature());
    }
}
