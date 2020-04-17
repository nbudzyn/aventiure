package de.nb.aventiure2.scaction;

import androidx.annotation.NonNull;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;

import java.util.Collection;

import de.nb.aventiure2.data.database.AvDatabase;
import de.nb.aventiure2.data.storystate.IPlayerAction;
import de.nb.aventiure2.data.storystate.StoryState;
import de.nb.aventiure2.data.storystate.StoryState.StructuralElement;
import de.nb.aventiure2.data.storystate.StoryStateBuilder;
import de.nb.aventiure2.data.storystate.StoryStateDao;
import de.nb.aventiure2.data.world.lichtverhaeltnisse.Lichtverhaeltnisse;
import de.nb.aventiure2.data.world.room.AvRoom;
import de.nb.aventiure2.data.world.time.AvDateTime;
import de.nb.aventiure2.data.world.time.AvTimeSpan;
import de.nb.aventiure2.data.world.time.Tageszeit;
import de.nb.aventiure2.german.base.AbstractDescription;
import de.nb.aventiure2.scaction.action.creature.reaction.CreatureReactionsCoordinator;
import de.nb.aventiure2.scaction.action.invisible.reaction.InvisibleReactionsCoordinator;
import de.nb.aventiure2.scaction.action.scautomaticreaction.ScAutomaticReactions;

import static de.nb.aventiure2.data.world.time.AvTimeSpan.noTime;

/**
 * An action the player could choose to advance the story.
 */
public abstract class AbstractScAction implements IPlayerAction {
    protected final AvDatabase db;
    protected final StoryStateDao n;

    protected final CreatureReactionsCoordinator creatureReactionsCoordinator;
    protected final InvisibleReactionsCoordinator invisibleReactionsCoordinator;
    protected final ScAutomaticReactions scAutomaticReactions;

    /**
     * The {@link StoryState} at the beginning of the action.
     */
    protected final StoryState initialStoryState;

    protected AbstractScAction(@NonNull final AvDatabase db, final StoryState initialStoryState) {
        this.db = db;

        n = db.storyStateDao();

        creatureReactionsCoordinator = new CreatureReactionsCoordinator(db, getClass());
        invisibleReactionsCoordinator = new InvisibleReactionsCoordinator(db, getClass());
        scAutomaticReactions = new ScAutomaticReactions(db, getClass());

        this.initialStoryState = initialStoryState;
    }

    /**
     * Returns the name of the action as it is displayed to the player.
     */
    abstract public String getName();

    /**
     * Führt die Aktion aus (inkl. Erzählung), lässt die entsprechende Zeit verstreichen.
     * Aktualisiert dabei auch die Welt.
     */
    public final void doAndPassTime() {
        final AvDateTime start = db.dateTimeDao().now();

        final AvTimeSpan timeElapsed = narrateAndDo();

        final AvDateTime dateTimeAfterActionBeforeUpdateWorld = start.plus(timeElapsed);
        db.dateTimeDao().setNow(dateTimeAfterActionBeforeUpdateWorld);

        final AvTimeSpan extraTimeElapsedDuringWorldUpdate =
                updateWorld(start, start.plus(timeElapsed));

        db.dateTimeDao().setNow(dateTimeAfterActionBeforeUpdateWorld
                .plus(extraTimeElapsedDuringWorldUpdate));
    }

    private AvTimeSpan updateWorld(final AvDateTime lastTime, @NonNull final AvDateTime now) {
        if (now.equals(lastTime)) {
            return noTime();
        }

        AvTimeSpan additionalTimeElapsed =
                creatureReactionsCoordinator.onTimePassed(lastTime, now);
        additionalTimeElapsed =
                additionalTimeElapsed.plus(
                        invisibleReactionsCoordinator.onTimePassed(lastTime, now));

        additionalTimeElapsed =
                additionalTimeElapsed.plus(
                        scAutomaticReactions.onTimePassed(lastTime, now));

        additionalTimeElapsed =
                additionalTimeElapsed.plus(updateWorld(now, now.plus(additionalTimeElapsed)));

        return additionalTimeElapsed;
    }

    abstract public AvTimeSpan narrateAndDo();

    protected Lichtverhaeltnisse getLichtverhaeltnisse(final AvRoom room) {
        return Lichtverhaeltnisse.getLichtverhaeltnisse(getTageszeit(), room.getKey());
    }

    protected Tageszeit getTageszeit() {
        return db.dateTimeDao().now().getTageszeit();
    }

    protected AbstractDescription alt(final AbstractDescription... alternatives) {
        final Collection<StoryStateBuilder> alternativesAsStoryStates =
                toStoryStateBuilders(ImmutableList.copyOf(alternatives));

        final int index = n.chooseNextIndexFrom(alternativesAsStoryStates.toArray(
                new StoryStateBuilder[alternatives.length]));
        return alternatives[index];
    }

    private StoryStateBuilder toHauptsatzStoryStateBuilder(
            @NonNull final AbstractDescription desc) {
        return t(StructuralElement.SENTENCE,
                desc.getDescriptionHauptsatz())
                .komma(desc.kommaStehtAus())
                .undWartest(desc.allowsAdditionalDuSatzreihengliedOhneSubjekt())
                .dann(desc.dann());
    }

    protected StoryStateBuilder alt(
            @NonNull final ImmutableCollection.Builder<StoryStateBuilder> alternatives) {
        return alt(alternatives.build());
    }

    protected StoryStateBuilder alt(@NonNull final Collection<StoryStateBuilder> alternatives) {
        return alt(alternatives.toArray(new StoryStateBuilder[0]));
    }

    private ImmutableList<StoryStateBuilder> toStoryStateBuilders(
            @NonNull final Collection<AbstractDescription> alternatives) {
        final ImmutableList.Builder<StoryStateBuilder> res = ImmutableList.builder();

        for (final AbstractDescription alternativeDesc : alternatives) {
            res.add(toHauptsatzStoryStateBuilder(alternativeDesc));
        }

        return res.build();
    }

    protected StoryStateBuilder alt(final StoryStateBuilder... alternatives) {
        return n.chooseNextFrom(alternatives);
    }

    protected StoryStateBuilder t(
            final StructuralElement startsNew,
            @NonNull final String text) {
        return StoryStateBuilder.t(this, startsNew, text)
                // Sensible default - caller may override this setting
                .letzterRaum(db.playerLocationDao().getPlayerLocation().getRoom());
    }
}