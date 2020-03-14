package de.nb.aventiure2.playeraction;

import android.app.Application;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;

import java.util.List;

import de.nb.aventiure2.data.database.AvDatabase;
import de.nb.aventiure2.data.world.creature.CreatureDataDao;
import de.nb.aventiure2.data.world.object.ObjectDataDao;
import de.nb.aventiure2.data.world.player.inventory.PlayerInventoryDao;
import de.nb.aventiure2.data.world.player.location.PlayerLocationDao;
import de.nb.aventiure2.data.world.player.stats.PlayerStatsDao;

/**
 * Repository for the actions the player can choose from.
 */
public class PlayerActionService {
    private final AvDatabase db;
    private final ObjectDataDao objectDataDao;
    private final CreatureDataDao creatureDataDao;
    private final PlayerStatsDao playerStatsDao;
    private final PlayerLocationDao playerLocationDao;
    private final PlayerInventoryDao playerInventoryDao;

    // Note that in order to unit test the repository, you have to remove the Application
    // dependency. This adds complexity and much more code, and this sample is not about testing.
    // See the BasicSample in the android-architecture-components repository at
    // https://github.com/googlesamples
    public PlayerActionService(final Application application) {
        db = AvDatabase.getDatabase(application);
        objectDataDao = db.objectDataDao();
        creatureDataDao = db.creatureDataDao();
        playerStatsDao = db.playerStatsDao();
        playerLocationDao = db.playerLocationDao();
        playerInventoryDao = db.playerInventoryDao();
    }

    // Room executes all queries on a separate thread.
    // Observed LiveData will notify the observer when the data has changed.
    public LiveData<List<AbstractPlayerAction>> getPlayerActions() {
        return new PlayerActionsLiveData(
                db,
                playerStatsDao.getPlayerStats(),
                playerLocationDao.getPlayerLocation(),
                objectDataDao.getAll(),
                Transformations.switchMap(
                        playerLocationDao.getPlayerLocation(),
                        loc -> loc == null ?
                                null :
                                creatureDataDao.getCreaturesInRoom(loc.getRoom())),
                playerInventoryDao.getInventory());
    }
}