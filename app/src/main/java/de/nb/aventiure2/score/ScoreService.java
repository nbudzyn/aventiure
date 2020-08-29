package de.nb.aventiure2.score;

import android.content.Context;

import javax.annotation.ParametersAreNonnullByDefault;

import de.nb.aventiure2.data.database.AvDatabase;
import de.nb.aventiure2.data.world.gameobject.World;
import de.nb.aventiure2.data.world.syscomp.story.IStoryWebGO;

import static de.nb.aventiure2.data.world.gameobject.World.STORY_WEB;

/**
 * Repository for score (percentage of story nodes) the player has
 * reached.
 */
@ParametersAreNonnullByDefault
public class ScoreService {
    private final AvDatabase db;
    private final World world;

    // Note that in order to unit test the repository, you have to remove the Application
    // dependency. See ScActionService.
    public ScoreService(final Context context) {
        db = AvDatabase.getDatabase(context);

        world = World.getInstance(db);
    }

    public int getScore() {
        final IStoryWebGO storyWeb = (IStoryWebGO) world.load(STORY_WEB);
        return storyWeb.storyWebComp().getScore();
    }
}