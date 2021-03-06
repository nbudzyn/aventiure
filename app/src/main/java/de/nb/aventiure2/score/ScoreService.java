package de.nb.aventiure2.score;

import android.content.Context;

import javax.annotation.ParametersAreNonnullByDefault;

import de.nb.aventiure2.data.database.AvDatabase;
import de.nb.aventiure2.data.narration.Narrator;
import de.nb.aventiure2.data.time.TimeTaker;
import de.nb.aventiure2.data.world.gameobject.*;
import de.nb.aventiure2.data.world.syscomp.story.IStoryWebGO;

import static de.nb.aventiure2.data.world.gameobject.World.*;

/**
 * Repository for score (percentage of story nodes) the player has
 * reached.
 */
@ParametersAreNonnullByDefault
public class ScoreService {
    private final World world;

    // Note that in order to unit test the repository, you have to remove the Application
    // dependency. See ScActionService.
    public ScoreService(final Context context) {
        final AvDatabase db = AvDatabase.getDatabase(context);
        final TimeTaker timeTaker = TimeTaker.getInstance(db);
        final Narrator n = Narrator.getInstance(db, timeTaker);
        world = World.getInstance(db, timeTaker, n);
    }

    public int getScore() {
        final IStoryWebGO storyWeb = world.load(STORY_WEB);
        return storyWeb.storyWebComp().getScore();
    }
}