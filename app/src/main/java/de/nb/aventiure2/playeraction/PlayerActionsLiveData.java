package de.nb.aventiure2.playeraction;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;

import com.google.common.collect.ImmutableList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.nb.aventiure2.data.database.AvDatabase;
import de.nb.aventiure2.data.world.creature.CreatureData;
import de.nb.aventiure2.data.world.object.AvObject;
import de.nb.aventiure2.data.world.object.ObjectData;
import de.nb.aventiure2.data.world.player.location.PlayerLocation;
import de.nb.aventiure2.data.world.player.stats.PlayerStats;
import de.nb.aventiure2.data.world.room.AvRoom;
import de.nb.aventiure2.data.world.room.connection.RoomConnection;
import de.nb.aventiure2.playeraction.action.AblegenAction;
import de.nb.aventiure2.playeraction.action.BewegenAction;
import de.nb.aventiure2.playeraction.action.HeulenAction;
import de.nb.aventiure2.playeraction.action.HochwerfenAction;
import de.nb.aventiure2.playeraction.action.NehmenAction;
import de.nb.aventiure2.playeraction.action.RedenAction;

/**
 * The actions the player can choose from as an (observable) {@link LiveData}.
 */
class PlayerActionsLiveData extends MediatorLiveData<List<AbstractPlayerAction>> {
    private final AvDatabase db;

    @Nullable
    private PlayerStats stats;

    @Nullable
    private AvRoom room;

    @Nullable
    private final Map<AvObject.Key, ObjectData> allObjectsByKey =
            new HashMap<>();

    @Nullable
    private List<CreatureData> creaturesInRoom;

    @Nullable
    private List<AvObject> inventory;

    PlayerActionsLiveData(final AvDatabase db,
                          final LiveData<PlayerStats> playerStats,
                          final LiveData<PlayerLocation> playerLocation,
                          final LiveData<List<ObjectData>> allObjects,
                          final LiveData<List<CreatureData>> creaturesInRoom,
                          final LiveData<List<AvObject>> inventory
    ) {
        this.db = db;

        super.addSource(playerStats, stats -> {
            this.stats = stats;
            updateValue();
        });
        super.addSource(playerLocation, loc -> {
            room = loc == null ? null : loc.getRoom();
            updateValue();
        });
        super.addSource(allObjects, o -> {
            allObjectsByKey.clear();

            if (o != null) {
                for (final ObjectData objectData : o) {
                    allObjectsByKey.put(objectData.getObject().getKey(), objectData);
                }
            }
            updateValue();
        });
        super.addSource(creaturesInRoom, creatures -> {
            this.creaturesInRoom = creatures;
            updateValue();
        });
        super.addSource(inventory, objects -> {
            this.inventory = objects;
            updateValue();
        });
    }

    private void updateValue() {
        setValue(calcValue());
    }

    private List<AbstractPlayerAction> calcValue() {
        if (stats == null || room == null || allObjectsByKey == null || creaturesInRoom == null) {
            return ImmutableList.of();
        }

        final List<AbstractPlayerAction> res = new ArrayList<>();

        res.addAll(buildCreatureInRoomActions());
        res.addAll(buildPlayerOnlyAction());
        res.addAll(buildObjectInRoomActions());
        res.addAll(buildInventoryActions());
        res.addAll(buildRoomConnectionActions());

        return res;
    }

    private ImmutableList<AbstractPlayerAction> buildCreatureInRoomActions() {
        final ImmutableList.Builder<AbstractPlayerAction> res = ImmutableList.builder();

        for (final CreatureData creatureData : creaturesInRoom) {
            res.addAll(RedenAction.buildActions(db, room, allObjectsByKey, creatureData));
        }

        return res.build();
    }

    private ImmutableList<AbstractPlayerAction> buildPlayerOnlyAction() {
        final ImmutableList.Builder<AbstractPlayerAction> res = ImmutableList.builder();

        res.addAll(HeulenAction.buildActions(db, stats, creaturesInRoom));

        return res.build();
    }

    private ImmutableList<AbstractPlayerAction> buildObjectInRoomActions() {
        final ImmutableList.Builder<AbstractPlayerAction> res = ImmutableList.builder();
        for (final ObjectData objectData : allObjectsByKey.values()) {
            if (room == objectData.getRoom()) {
                res.addAll(NehmenAction.buildActions(db, room, objectData));
            }
        }
        return res.build();
    }

    private ImmutableList<AbstractPlayerAction> buildInventoryActions() {
        final ImmutableList.Builder<AbstractPlayerAction> res = ImmutableList.builder();
        for (final AvObject inventoryObject : inventory) {
            final ObjectData objectData = allObjectsByKey.get(inventoryObject.getKey());

            res.addAll(HochwerfenAction.buildActions(db, room, objectData));
            res.addAll(AblegenAction.buildActions(db, room, objectData));
        }
        return res.build();
    }

    private ImmutableList<AbstractPlayerAction> buildRoomConnectionActions() {
        final ImmutableList.Builder<AbstractPlayerAction> res = ImmutableList.builder();
        for (final AvRoom connectedRoom : RoomConnection.getFrom(room).keySet()) {
            res.addAll(BewegenAction.buildActions(db, room, connectedRoom));
        }
        return res.build();
    }
}
