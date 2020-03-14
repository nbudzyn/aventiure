package de.nb.aventiure2.data.storystate;

import android.app.Application;

import de.nb.aventiure2.data.database.AvDatabase;

/**
 * Android Room repository for {@link StoryState}s.
 */
public class StoryStateRepository {
    private final StoryStateDao storyStateDao;

    public StoryStateRepository(final Application application) {
        final AvDatabase db = AvDatabase.getDatabase(application);
        storyStateDao = db.storyStateDao();
    }

}
