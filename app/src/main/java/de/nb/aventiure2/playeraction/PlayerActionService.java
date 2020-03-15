package de.nb.aventiure2.playeraction;

import android.app.Application;

import com.google.common.collect.ImmutableList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.nb.aventiure2.data.database.AvDatabase;
import de.nb.aventiure2.data.world.creature.CreatureData;
import de.nb.aventiure2.data.world.creature.CreatureDataDao;
import de.nb.aventiure2.data.world.object.AvObject;
import de.nb.aventiure2.data.world.object.ObjectData;
import de.nb.aventiure2.data.world.object.ObjectDataDao;
import de.nb.aventiure2.data.world.player.inventory.PlayerInventoryDao;
import de.nb.aventiure2.data.world.player.location.PlayerLocationDao;
import de.nb.aventiure2.data.world.player.stats.PlayerStats;
import de.nb.aventiure2.data.world.player.stats.PlayerStatsDao;
import de.nb.aventiure2.data.world.room.AvRoom;
import de.nb.aventiure2.data.world.room.connection.RoomConnection;
import de.nb.aventiure2.playeraction.action.AblegenAction;
import de.nb.aventiure2.playeraction.action.BewegenAction;
import de.nb.aventiure2.playeraction.action.HeulenAction;
import de.nb.aventiure2.playeraction.action.HochwerfenAction;
import de.nb.aventiure2.playeraction.action.NehmenAction;
import de.nb.aventiure2.playeraction.action.RedenAction;

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

    public List<AbstractPlayerAction> getPlayerActions() {
        final PlayerStats stats = playerStatsDao.getPlayerStats();

        final AvRoom room = playerLocationDao.getPlayerLocation().getRoom();

        final List<ObjectData> allObjects = objectDataDao.getAll();

        final Map<AvObject.Key, ObjectData> allObjectsByKey = new HashMap<>();
        for (final ObjectData objectData : allObjects) {
            allObjectsByKey.put(objectData.getObject().getKey(), objectData);
        }

        final List<AvObject> inventory = playerInventoryDao.getInventory();

        final List<CreatureData> creaturesInRoom = creatureDataDao.getCreaturesInRoom(room);

        final List<AbstractPlayerAction> res = new ArrayList<>();

        res.addAll(buildCreatureInRoomActions(room, allObjectsByKey, creaturesInRoom));
        res.addAll(buildPlayerOnlyAction(stats, creaturesInRoom));
        res.addAll(buildObjectInRoomActions(room, allObjectsByKey));
        res.addAll(buildInventoryActions(room, allObjectsByKey, inventory));
        res.addAll(buildRoomConnectionActions(room));

        return res;
    }

    private ImmutableList<AbstractPlayerAction> buildCreatureInRoomActions(
            final AvRoom room, final Map<AvObject.Key, ObjectData> allObjectsByKey,
            final List<CreatureData> creaturesInRoom) {
        final ImmutableList.Builder<AbstractPlayerAction> res = ImmutableList.builder();

        for (final CreatureData creatureData : creaturesInRoom) {
            res.addAll(RedenAction.buildActions(db, room, allObjectsByKey, creatureData));
        }

        return res.build();
    }

    private ImmutableList<AbstractPlayerAction> buildPlayerOnlyAction(
            final PlayerStats stats, final List<CreatureData> creaturesInRoom) {
        final ImmutableList.Builder<AbstractPlayerAction> res = ImmutableList.builder();

        res.addAll(HeulenAction.buildActions(db, stats, creaturesInRoom));

        return res.build();
    }

    private ImmutableList<AbstractPlayerAction> buildObjectInRoomActions(
            final AvRoom room,
            final Map<AvObject.Key, ObjectData> allObjectsByKey) {
        final ImmutableList.Builder<AbstractPlayerAction> res = ImmutableList.builder();
        for (final ObjectData objectData : allObjectsByKey.values()) {
            if (room == objectData.getRoom()) {
                res.addAll(NehmenAction.buildActions(db, room, objectData));
            }
        }
        return res.build();
    }

    private ImmutableList<AbstractPlayerAction> buildInventoryActions(
            final AvRoom room, final Map<AvObject.Key, ObjectData> allObjectsByKey,
            final List<AvObject> inventory) {
        final ImmutableList.Builder<AbstractPlayerAction> res = ImmutableList.builder();
        for (final AvObject inventoryObject : inventory) {
            final ObjectData objectData = allObjectsByKey.get(inventoryObject.getKey());

            res.addAll(HochwerfenAction.buildActions(db, room, objectData));
            res.addAll(AblegenAction.buildActions(db, room, objectData));
        }
        return res.build();
    }

    private ImmutableList<AbstractPlayerAction> buildRoomConnectionActions(
            final AvRoom room) {
        final ImmutableList.Builder<AbstractPlayerAction> res = ImmutableList.builder();
        for (final AvRoom connectedRoom : RoomConnection.getFrom(room).keySet()) {
            res.addAll(BewegenAction.buildActions(db, room, connectedRoom));
        }
        return res.build();
    }
}