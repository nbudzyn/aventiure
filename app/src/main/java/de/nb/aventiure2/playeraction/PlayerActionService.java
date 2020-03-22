package de.nb.aventiure2.playeraction;

import android.app.Application;

import com.google.common.collect.ImmutableList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.nb.aventiure2.data.database.AvDatabase;
import de.nb.aventiure2.data.storystate.StoryState;
import de.nb.aventiure2.data.world.creature.CreatureData;
import de.nb.aventiure2.data.world.object.AvObject;
import de.nb.aventiure2.data.world.object.ObjectData;
import de.nb.aventiure2.data.world.player.stats.PlayerStats;
import de.nb.aventiure2.data.world.room.AvRoom;
import de.nb.aventiure2.data.world.room.connection.RoomConnection;
import de.nb.aventiure2.playeraction.action.AblegenAction;
import de.nb.aventiure2.playeraction.action.BewegenAction;
import de.nb.aventiure2.playeraction.action.HeulenAction;
import de.nb.aventiure2.playeraction.action.HochwerfenAction;
import de.nb.aventiure2.playeraction.action.NehmenAction;
import de.nb.aventiure2.playeraction.action.RedenAction;

import static de.nb.aventiure2.data.world.creature.Creature.Key.FROSCHPRINZ;

/**
 * Repository for the actions the player can choose from.
 */
public class PlayerActionService {
    private final AvDatabase db;

    // Note that in order to unit test the repository, you have to remove the Application
    // dependency. This adds complexity and much more code, and this sample is not about testing.
    // See the BasicSample in the android-architecture-components repository at
    // https://github.com/googlesamples
    public PlayerActionService(final Application application) {
        db = AvDatabase.getDatabase(application);
    }

    public List<AbstractPlayerAction> getPlayerActions() {
        final StoryState currentStoryState = db.storyStateDao().getStoryState();

        final PlayerStats stats = db.playerStatsDao().getPlayerStats();

        final AvRoom room = db.playerLocationDao().getPlayerLocation().getRoom();

        final List<ObjectData> allObjects = db.objectDataDao().getAll();

        final Map<AvObject.Key, ObjectData> allObjectsByKey = new HashMap<>();
        for (final ObjectData objectData : allObjects) {
            allObjectsByKey.put(objectData.getObject().getKey(), objectData);
        }

        final List<AvObject> inventory = db.playerInventoryDao().getInventory();

        final List<CreatureData> creaturesInRoom = db.creatureDataDao().getCreaturesInRoom(room);
        final CreatureData froschprinz = db.creatureDataDao().getCreature(FROSCHPRINZ);

        final List<AbstractPlayerAction> res = new ArrayList<>();

        res.addAll(buildCreatureInRoomActions(currentStoryState, room, allObjectsByKey,
                creaturesInRoom));
        if (!currentStoryState.talkingToAnyone()) {
            res.addAll(buildPlayerOnlyAction(currentStoryState, stats, creaturesInRoom));
            res.addAll(buildObjectInRoomActions(currentStoryState, room, allObjectsByKey));
            res.addAll(buildInventoryActions(currentStoryState, room, allObjectsByKey,
                    froschprinz, inventory));
            res.addAll(buildRoomConnectionActions(currentStoryState, room));
        }

        return res;
    }

    private ImmutableList<AbstractPlayerAction> buildCreatureInRoomActions(
            final StoryState currentStoryState,
            final AvRoom room, final Map<AvObject.Key, ObjectData> allObjectsByKey,
            final List<CreatureData> creaturesInRoom) {
        final ImmutableList.Builder<AbstractPlayerAction> res = ImmutableList.builder();

        for (final CreatureData creatureData : creaturesInRoom) {
            res.addAll(RedenAction.buildActions(db, currentStoryState, room, allObjectsByKey,
                    creatureData));
        }

        return res.build();
    }

    private ImmutableList<AbstractPlayerAction> buildPlayerOnlyAction(
            final StoryState currentStoryState,
            final PlayerStats stats, final List<CreatureData> creaturesInRoom) {
        final ImmutableList.Builder<AbstractPlayerAction> res = ImmutableList.builder();

        res.addAll(HeulenAction.buildActions(db, currentStoryState, stats, creaturesInRoom));

        return res.build();
    }

    private ImmutableList<AbstractPlayerAction> buildObjectInRoomActions(
            final StoryState currentStoryState,
            final AvRoom room,
            final Map<AvObject.Key, ObjectData> allObjectsByKey) {
        final ImmutableList.Builder<AbstractPlayerAction> res = ImmutableList.builder();
        for (final ObjectData objectData : allObjectsByKey.values()) {
            if (room == objectData.getRoom()) {
                res.addAll(NehmenAction.buildActions(db, currentStoryState, room, objectData));
            }
        }
        return res.build();
    }

    private ImmutableList<AbstractPlayerAction> buildInventoryActions(
            final StoryState currentStoryState,
            final AvRoom room, final Map<AvObject.Key, ObjectData> allObjectsByKey,
            final CreatureData froschprinz,
            final List<AvObject> inventory) {
        final ImmutableList.Builder<AbstractPlayerAction> res = ImmutableList.builder();
        for (final AvObject inventoryObject : inventory) {
            final ObjectData objectData = allObjectsByKey.get(inventoryObject.getKey());

            res.addAll(HochwerfenAction
                    .buildActions(db, currentStoryState, room, objectData, froschprinz));
            res.addAll(AblegenAction.buildActions(db, currentStoryState, room, objectData));
        }
        return res.build();
    }

    private ImmutableList<AbstractPlayerAction> buildRoomConnectionActions(
            final StoryState currentStoryState,
            final AvRoom room) {
        final ImmutableList.Builder<AbstractPlayerAction> res = ImmutableList.builder();

        final Set<AvRoom> connectedRooms = RoomConnection.getFrom(room).keySet();

        final boolean exactlyOneConnectedRoom = connectedRooms.size() == 1;

        for (final AvRoom connectedRoom : connectedRooms) {
            res.addAll(BewegenAction.buildActions(
                    db, currentStoryState, room, connectedRoom, exactlyOneConnectedRoom));
        }
        return res.build();
    }
}