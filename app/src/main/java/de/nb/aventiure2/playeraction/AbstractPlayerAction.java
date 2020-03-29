package de.nb.aventiure2.playeraction;

import androidx.annotation.NonNull;

import com.google.common.collect.ImmutableCollection;

import java.util.Collection;

import de.nb.aventiure2.data.database.AvDatabase;
import de.nb.aventiure2.data.storystate.IPlayerAction;
import de.nb.aventiure2.data.storystate.StoryState;
import de.nb.aventiure2.data.storystate.StoryState.StructuralElement;
import de.nb.aventiure2.data.storystate.StoryStateBuilder;
import de.nb.aventiure2.data.storystate.StoryStateDao;
import de.nb.aventiure2.data.world.time.AvDateTime;
import de.nb.aventiure2.data.world.time.AvTimeSpan;
import de.nb.aventiure2.playeraction.action.creature.reaction.CreatureReactionsCoordinator;
import de.nb.aventiure2.playeraction.action.invisible.reaction.InvisibleReactionsCoordinator;

/**
 * An action the player could choose to advance the story.
 */
public abstract class AbstractPlayerAction implements IPlayerAction {
    protected final AvDatabase db;
    protected final StoryStateDao n;

    protected final CreatureReactionsCoordinator creatureReactionsCoordinator;
    protected final InvisibleReactionsCoordinator invisibleReactionsCoordinator;

    /**
     * The {@link StoryState} at the beginning of the action.
     */
    protected final StoryState initialStoryState;

    protected AbstractPlayerAction(final AvDatabase db, final StoryState initialStoryState) {
        this.db = db;

        n = db.storyStateDao();

        creatureReactionsCoordinator = new CreatureReactionsCoordinator(db, getClass());
        invisibleReactionsCoordinator = new InvisibleReactionsCoordinator(db, getClass());

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
        final AvDateTime lastTimeForWorldUpdate = db.dateTimeDao().getDateTime();

        final AvTimeSpan timeElapsed = narrateAndDo();

        // STORY Z.B. Das Fest starten, wenn die Zeit erreicht ist.

        final AvDateTime newTime =
                updateWorld(lastTimeForWorldUpdate, lastTimeForWorldUpdate.plus(timeElapsed));

        db.dateTimeDao().setDateTime(newTime);
    }

    private AvDateTime updateWorld(final AvDateTime lastTime, final AvDateTime now) {
        AvTimeSpan totalTimeElapsed = now.minus(lastTime);

        AvTimeSpan tmpTimeElapsed = now.minus(lastTime);
        while (!tmpTimeElapsed.isNoTime()) {
            tmpTimeElapsed =
                    creatureReactionsCoordinator.onTimePassed(lastTime, now);
            tmpTimeElapsed =
                    tmpTimeElapsed.plus(
                            invisibleReactionsCoordinator.onTimePassed(lastTime, now));
            totalTimeElapsed = totalTimeElapsed.plus(tmpTimeElapsed);
        }

        return lastTime.plus(totalTimeElapsed);
    }

    abstract public AvTimeSpan narrateAndDo();

    protected StoryStateBuilder alt(
            final ImmutableCollection.Builder<StoryStateBuilder> alternatives) {
        return alt(alternatives.build());
    }

    private StoryStateBuilder alt(final Collection<StoryStateBuilder> alternatives) {
        return alt(alternatives.toArray(new StoryStateBuilder[alternatives.size()]));
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