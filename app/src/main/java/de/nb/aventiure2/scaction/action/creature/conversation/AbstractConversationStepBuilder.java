package de.nb.aventiure2.scaction.action.creature.conversation;

import androidx.annotation.NonNull;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;

import java.util.Collection;
import java.util.List;

import de.nb.aventiure2.data.database.AvDatabase;
import de.nb.aventiure2.data.storystate.StoryState;
import de.nb.aventiure2.data.storystate.StoryStateBuilder;
import de.nb.aventiure2.data.storystate.StoryStateDao;
import de.nb.aventiure2.data.world.gameobjects.GameObjects;
import de.nb.aventiure2.data.world.gameobjects.player.SpielerCharakter;
import de.nb.aventiure2.data.world.syscomp.description.IDescribableGO;
import de.nb.aventiure2.data.world.syscomp.storingplace.IHasStoringPlaceGO;
import de.nb.aventiure2.data.world.syscomp.talking.ITalkerGO;
import de.nb.aventiure2.german.base.Nominalphrase;
import de.nb.aventiure2.german.praedikat.Praedikat;

import static de.nb.aventiure2.data.world.gameobjects.GameObjects.loadSC;
import static de.nb.aventiure2.scaction.action.creature.conversation.ConversationStep.ALWAYS_POSSIBLE;
import static de.nb.aventiure2.scaction.action.creature.conversation.ConversationStep.DEFAULT_ENTRY_RE_ENTRY_NAME;
import static de.nb.aventiure2.scaction.action.creature.conversation.ConversationStep.DEFAULT_EXIT_NAME;

/**
 * Abstrakte Oberklasse zum Erzeugen von {@link ConversationStep}s für eine
 * Kreatur.
 */
abstract class AbstractConversationStepBuilder<TALKER extends ITalkerGO> {
    protected final AvDatabase db;

    protected final SpielerCharakter sc;

    protected final StoryStateDao n;

    /**
     * The {@link StoryState} at the beginning of the step.
     */
    protected final StoryState initialStoryState;

    protected final IHasStoringPlaceGO room;

    @NonNull
    protected final TALKER talker;

    AbstractConversationStepBuilder(final AvDatabase db, final StoryState initialStoryState,
                                    final IHasStoringPlaceGO room,
                                    @NonNull final TALKER talker) {
        this.db = db;

        n = db.storyStateDao();

        sc = loadSC(db);

        this.initialStoryState = initialStoryState;
        this.room = room;
        this.talker = talker;
    }

    List<ConversationStep> getPossibleSteps() {
        final ImmutableList.Builder<ConversationStep> res =
                ImmutableList.builder();

        for (final ConversationStep step : getAllStepsForCurrentState()) {
            if (step.isStepPossible()) {
                res.add(step);
            }
        }

        return res.build();
    }

    abstract List<ConversationStep> getAllStepsForCurrentState();

    static ConversationStep entrySt(
            final ConversationStep.NarrationAndAction narrationAndAction) {
        return entrySt(DEFAULT_ENTRY_RE_ENTRY_NAME, narrationAndAction);
    }

    static ConversationStep entrySt(final Praedikat entryName,
                                    final ConversationStep.NarrationAndAction narrationAndAction) {
        return entrySt(ALWAYS_POSSIBLE, entryName, narrationAndAction);
    }

    /**
     * Creates a {@link ConversationStep} to enter a conversation.
     *
     * @param condition If this condition does not hold, this step is impossible
     *                  (there won't be an action for this step).
     */
    static ConversationStep entrySt(
            final ConversationStep.Condition condition,
            final ConversationStep.NarrationAndAction narrationAndAction) {
        return entrySt(condition, DEFAULT_ENTRY_RE_ENTRY_NAME, narrationAndAction);
    }

    /**
     * Creates a {@link ConversationStep} to enter a conversation.
     *
     * @param condition If this condition does not hold, this step is impossible
     *                  (there won't be an action for this step).
     */
    static ConversationStep entrySt(
            final ConversationStep.Condition condition,
            final Praedikat entryName,
            final ConversationStep.NarrationAndAction narrationAndAction) {
        return new ConversationStep(ConversationStep.Type.ENTRY_RE_ENTRY, condition,
                entryName,
                narrationAndAction);
    }

    static ConversationStep immReEntrySt(
            final ConversationStep.NarrationAndAction narrationAndAction) {
        return immReEntrySt(DEFAULT_ENTRY_RE_ENTRY_NAME, narrationAndAction);
    }

    static ConversationStep immReEntrySt(final Praedikat entryName,
                                         final ConversationStep.NarrationAndAction narrationAndAction) {
        return immReEntrySt(ALWAYS_POSSIBLE, entryName, narrationAndAction);
    }

    /**
     * Creates a {@link ConversationStep} to immediately re-enter a conversation.
     *
     * @param condition If this condition does not hold, this step is impossible
     *                  (there won't be an action for this step).
     */
    static ConversationStep immReEntrySt(final ConversationStep.Condition condition,
                                         final ConversationStep.NarrationAndAction narrationAndAction) {
        return immReEntrySt(condition, DEFAULT_ENTRY_RE_ENTRY_NAME, narrationAndAction);
    }

    /**
     * Creates a {@link ConversationStep} to immediately re-enter a conversation.
     *
     * @param condition If this condition does not hold, this step is impossible
     *                  (there won't be an action for this step).
     */
    static ConversationStep immReEntrySt(
            final ConversationStep.Condition condition,
            final Praedikat entryName,
            final ConversationStep.NarrationAndAction narrationAndAction) {
        return new ConversationStep(ConversationStep.Type.IMMEDIATE_RE_ENTRY,
                condition,
                entryName, narrationAndAction);
    }

    static ConversationStep reEntrySt(
            final ConversationStep.NarrationAndAction narrationAndAction) {
        return reEntrySt(DEFAULT_ENTRY_RE_ENTRY_NAME, narrationAndAction);
    }

    static ConversationStep reEntrySt(final Praedikat entryName,
                                      final ConversationStep.NarrationAndAction narrationAndAction) {
        return reEntrySt(ALWAYS_POSSIBLE, entryName, narrationAndAction);
    }

    /**
     * Creates a {@link ConversationStep} to re-enter a conversation (but not immediately)
     *
     * @param condition If this condition does not hold, this step is impossible
     *                  (there won't be an action for this step).
     */
    static ConversationStep reEntrySt(
            final ConversationStep.Condition condition,
            final ConversationStep.NarrationAndAction narrationAndAction) {
        return reEntrySt(condition, DEFAULT_ENTRY_RE_ENTRY_NAME, narrationAndAction);
    }

    /**
     * Creates a {@link ConversationStep} to re-enter a conversation (but not immediately)
     *
     * @param condition If this condition does not hold, this step is impossible
     *                  (there won't be an action for this step).
     */
    static ConversationStep reEntrySt(
            final ConversationStep.Condition condition,
            final Praedikat entryName,
            final ConversationStep.NarrationAndAction narrationAndAction) {
        return new ConversationStep(ConversationStep.Type.ENTRY_RE_ENTRY, condition,
                entryName,
                narrationAndAction);
    }

    static ConversationStep st(final Praedikat name,
                               final ConversationStep.NarrationAndAction narrationAndAction) {
        return st(ALWAYS_POSSIBLE, name, narrationAndAction);
    }

    /**
     * Creates a normal {@link ConversationStep}
     *
     * @param condition If this condition does not hold, this step is impossible
     *                  (there won't be an action for this step).
     */
    static ConversationStep st(
            final ConversationStep.Condition condition,
            final Praedikat name,
            final ConversationStep.NarrationAndAction narrationAndAction) {
        return new ConversationStep(
                ConversationStep.Type.NORMAL, condition, name, narrationAndAction);
    }

    static ConversationStep exitSt(
            final ConversationStep.NarrationAndAction narrationAndAction) {
        return exitSt(DEFAULT_EXIT_NAME, narrationAndAction);
    }

    static ConversationStep exitSt(final Praedikat exitName,
                                   final ConversationStep.NarrationAndAction narrationAndAction) {
        return new ConversationStep(ConversationStep.Type.EXIT, ALWAYS_POSSIBLE,
                exitName,
                narrationAndAction);
    }

    /**
     * Gibt eine (evtl. auch etwas längere) Nominalphrase zurück, die das Game Object beschreibt.
     * Die Phrase wird in der Regel unterschiedlich sein, je nachdem, ob
     * ob der Spieler das Game Object schon kennt oder nicht.
     */
    protected Nominalphrase getDescription(final IDescribableGO gameObject) {
        return getDescription(gameObject, false);
    }

    /**
     * Gibt eine Nominalphrase zurück, die das Game Object beschreibt.
     * Die Phrase wird in der Regel unterschiedlich sein, je nachdem, ob
     * ob der Spieler das Game Object schon kennt oder nicht.
     *
     * @param shortIfKnown <i>Falls der Spieler(-charakter)</i> das
     *                     Game Object schon kennt, wird eher eine
     *                     kürzere Beschreibung gewählt
     */
    protected Nominalphrase getDescription(final IDescribableGO gameObject,
                                           final boolean shortIfKnown) {
        return GameObjects.getPOVDescription(sc, gameObject, shortIfKnown);
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
}