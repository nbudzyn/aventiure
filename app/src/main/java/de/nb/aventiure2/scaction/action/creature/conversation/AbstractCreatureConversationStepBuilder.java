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
import de.nb.aventiure2.data.world.syscomp.alive.ILivingBeingGO;
import de.nb.aventiure2.data.world.syscomp.description.IDescribableGO;
import de.nb.aventiure2.data.world.syscomp.storingplace.IHasStoringPlaceGO;
import de.nb.aventiure2.german.base.Nominalphrase;
import de.nb.aventiure2.german.praedikat.Praedikat;

import static de.nb.aventiure2.data.world.gameobjects.GameObjects.loadSC;
import static de.nb.aventiure2.scaction.action.creature.conversation.CreatureConversationStep.ALWAYS_POSSIBLE;
import static de.nb.aventiure2.scaction.action.creature.conversation.CreatureConversationStep.DEFAULT_ENTRY_RE_ENTRY_NAME;
import static de.nb.aventiure2.scaction.action.creature.conversation.CreatureConversationStep.DEFAULT_EXIT_NAME;

/**
 * Abstrakte Oberklasse zum Erzeugen von {@link CreatureConversationStep}s für eine
 * Kreatur.
 */
abstract class AbstractCreatureConversationStepBuilder<LIV extends ILivingBeingGO> {
    protected final AvDatabase db;

    protected final SpielerCharakter sc;

    protected final StoryStateDao n;

    /**
     * The {@link StoryState} at the beginning of the step.
     */
    protected final StoryState initialStoryState;

    protected final IHasStoringPlaceGO room;

    @NonNull
    protected final LIV creature;

    AbstractCreatureConversationStepBuilder(final AvDatabase db, final StoryState initialStoryState,
                                            final IHasStoringPlaceGO room,
                                            @NonNull final LIV creature) {
        this.db = db;

        n = db.storyStateDao();

        sc = loadSC(db);

        this.initialStoryState = initialStoryState;
        this.room = room;
        this.creature = creature;
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

    StoryStateBuilder t(
            @NonNull final StoryState.StructuralElement startsNew,
            @NonNull final String text) {
        return StoryStateBuilder.t(startsNew, text)
                .letzterRaum(sc.locationComp().getLocation())
                // Sensible default - caller may override this setting
                .imGespraechMit(creature);
    }
}
