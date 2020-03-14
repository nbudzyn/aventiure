package de.nb.aventiure2.data.world.player.location;

import android.app.Application;

import androidx.lifecycle.LiveData;

import de.nb.aventiure2.data.database.AvDatabase;

public class PlayerLocationRepository {
    // TODO Repositories are meant to mediate between different data sources. In this simple
    // example, you only have one data source, so the Repository doesn't do much. See the
    // BasicSample for a more complex implementation.
    private final PlayerLocationDao playerLocationDao;

    // Note that in order to unit test the repository, you have to remove the Application
    // dependency. This adds complexity and much more code, and this sample is not about testing.
    // See the BasicSample in the android-architecture-components repository at
    // https://github.com/googlesamples
    public PlayerLocationRepository(final Application application) {
        final AvDatabase db = AvDatabase.getDatabase(application);
        playerLocationDao = db.playerLocationDao();
    }

    // Room executes all queries on a separate thread.
    // Observed LiveData will notify the observer when the data has changed.
    public LiveData<PlayerLocation> getPlayerLocation() {
        return playerLocationDao.getPlayerLocation();
    }

    // You must call this on a non-UI thread or your app will throw an exception. Room ensures
    // that you're not doing any long running operations on the main thread, blocking the UI.
    public void save(final PlayerLocation playerLocation) {
        AvDatabase.databaseWriteExecutor.execute(() -> {
            playerLocationDao.insert(playerLocation);
        });
    }
}
