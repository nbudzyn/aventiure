package de.nb.aventiure2.playeraction;

import androidx.annotation.NonNull;

import de.nb.aventiure2.data.database.AvDatabase;
import de.nb.aventiure2.data.storystate.IPlayerAction;
import de.nb.aventiure2.data.storystate.StoryState;
import de.nb.aventiure2.data.storystate.StoryState.StartsNew;
import de.nb.aventiure2.data.storystate.StoryStateBuilder;
import de.nb.aventiure2.data.storystate.StoryStateDao;

/**
 * An action the player could choose to advance the story.
 */
public abstract class AbstractPlayerAction implements IPlayerAction {
    protected final AvDatabase db;
    protected final StoryStateDao n;

    /**
     * The {@link StoryState} at the beginning of the action.
     */
    protected final StoryState initialStoryState;

    protected AbstractPlayerAction(final AvDatabase db, final StoryState initialStoryState) {
        this.db = db;

        n = db.storyStateDao();

        this.initialStoryState = initialStoryState;
    }

    /**
     * Returns the name of the action as it is displayed to the player.
     */
    abstract public String getName();

    abstract public void narrateAndDo();

    protected StoryStateBuilder t(
            final StartsNew startsNew,
            @NonNull final String text) {
        return StoryStateBuilder.t(this, startsNew, text)
                // Sensible default - caller may override this setting
                .letzterRaum(db.playerLocationDao().getPlayerLocation().getRoom());
    }
}