package de.nb.aventiure2.scaction;

import android.app.Application;

import com.google.common.collect.ImmutableList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.nb.aventiure2.data.database.AvDatabase;
import de.nb.aventiure2.data.storystate.StoryState;
import de.nb.aventiure2.data.world.base.GameObjectId;
import de.nb.aventiure2.data.world.entity.creature.CreatureData;
import de.nb.aventiure2.data.world.entity.object.AvObject;
import de.nb.aventiure2.data.world.entity.object.ObjectData;
import de.nb.aventiure2.data.world.player.stats.ScStats;
import de.nb.aventiure2.data.world.room.AvRoom;
import de.nb.aventiure2.scaction.action.AblegenAction;
import de.nb.aventiure2.scaction.action.BewegenAction;
import de.nb.aventiure2.scaction.action.EssenAction;
import de.nb.aventiure2.scaction.action.HeulenAction;
import de.nb.aventiure2.scaction.action.HochwerfenAction;
import de.nb.aventiure2.scaction.action.KletternAction;
import de.nb.aventiure2.scaction.action.NehmenAction;
import de.nb.aventiure2.scaction.action.RedenAction;
import de.nb.aventiure2.scaction.action.SchlafenAction;

import static de.nb.aventiure2.data.world.entity.creature.Creatures.FROSCHPRINZ;

/**
 * Repository for the actions the player can choose from.
 */
public class ScActionService {
    private final AvDatabase db;

    // Note that in order to unit test the repository, you have to remove the Application
    // dependency. This adds complexity and much more code, and this sample is not about testing.
    // See the BasicSample in the android-architecture-components repository at
    // https://github.com/googlesamples
    public ScActionService(final Application application) {
        db = AvDatabase.getDatabase(application);
    }

    public List<AbstractScAction> getPlayerActions() {
        final StoryState currentStoryState = db.storyStateDao().getStoryState();

        final ScStats stats = db.playerStatsDao().getPlayerStats();

        final AvRoom room = db.playerLocationDao().getPlayerLocation().getRoom();

        final List<ObjectData> allObjects = db.objectDataDao().getAll();

        final Map<GameObjectId, ObjectData> allObjectsById = new HashMap<>();
        for (final ObjectData objectData : allObjects) {
            allObjectsById.put(objectData.getGameObjectId(), objectData);
        }

        final List<AvObject> inventory = db.playerInventoryDao().getInventory();

        final List<CreatureData> creaturesInRoom = db.creatureDataDao().getCreaturesInRoom(room);
        final CreatureData froschprinz = db.creatureDataDao().getCreature(FROSCHPRINZ);

        final List<AbstractScAction> res = new ArrayList<>();

        res.addAll(buildCreatureInRoomActions(currentStoryState, room, allObjectsById,
                creaturesInRoom));
        if (!currentStoryState.talkingToAnyone()) {
            res.addAll(buildPlayerOnlyAction(currentStoryState, room, stats, creaturesInRoom));
            res.addAll(buildObjectInRoomActions(currentStoryState, room, allObjectsById));
            res.addAll(buildInventoryActions(currentStoryState, room, allObjectsById,
                    froschprinz, inventory));
            res.addAll(buildRoomActions(currentStoryState, room));
        }

        return res;
    }

    private ImmutableList<AbstractScAction> buildCreatureInRoomActions(
            final StoryState currentStoryState,
            final AvRoom room, final Map<GameObjectId, ObjectData> allObjectsById,
            final List<CreatureData> creaturesInRoom) {
        final ImmutableList.Builder<AbstractScAction> res = ImmutableList.builder();

        for (final CreatureData creatureData : creaturesInRoom) {
            res.addAll(RedenAction.buildActions(db, currentStoryState, room, allObjectsById,
                    creatureData));
            res.addAll(
                    NehmenAction.buildCreatureActions(db, currentStoryState, room,
                            creatureData));
        }

        return res.build();
    }

    private ImmutableList<AbstractScAction> buildPlayerOnlyAction(
            final StoryState currentStoryState,
            final AvRoom room, final ScStats stats,
            final List<CreatureData> creaturesInRoom) {
        final ImmutableList.Builder<AbstractScAction> res = ImmutableList.builder();

        res.addAll(HeulenAction.buildActions(db, currentStoryState, stats, creaturesInRoom));
        res.addAll(SchlafenAction.buildActions(db, currentStoryState, room));

        return res.build();
    }

    private ImmutableList<AbstractScAction> buildObjectInRoomActions(
            final StoryState currentStoryState,
            final AvRoom room,
            final Map<GameObjectId, ObjectData> allObjectsById) {
        final ImmutableList.Builder<AbstractScAction> res = ImmutableList.builder();
        for (final ObjectData objectData : allObjectsById.values()) {
            if (room == objectData.getRoom()) {
                res.addAll(
                        NehmenAction.buildObjectActions(db, currentStoryState, room, objectData));
            }
        }

        res.addAll(EssenAction.buildActions(db, currentStoryState, room));

        return res.build();
    }

    private ImmutableList<AbstractScAction> buildInventoryActions(
            final StoryState currentStoryState,
            final AvRoom room, final Map<GameObjectId, ObjectData> allObjectsById,
            final CreatureData froschprinz,
            final List<AvObject> inventory) {
        final ImmutableList.Builder<AbstractScAction> res = ImmutableList.builder();

        res.addAll(AblegenAction.buildCreatureActions(db, currentStoryState, room, froschprinz));

        for (final AvObject inventoryObject : inventory) {
            final ObjectData objectData = allObjectsById.get(inventoryObject.getId());
            res.addAll(HochwerfenAction
                    .buildActions(db, currentStoryState, room, objectData, froschprinz));
            res.addAll(AblegenAction.buildObjectActions(db, currentStoryState, room, objectData));
        }
        return res.build();
    }

    private ImmutableList<AbstractScAction> buildRoomActions(
            final StoryState currentStoryState,
            final AvRoom room) {
        final ImmutableList.Builder<AbstractScAction> res = ImmutableList.builder();

        res.addAll(buildRoomSpecificActions(currentStoryState, room));
        res.addAll(BewegenAction.buildActions(db, currentStoryState, room));

        return res.build();
    }

    private ImmutableList<AbstractScAction> buildRoomSpecificActions(
            final StoryState currentStoryState, final AvRoom room) {
        final ImmutableList.Builder<AbstractScAction> res = ImmutableList.builder();

        res.addAll(KletternAction.buildActions(db, currentStoryState, room));

        return res.build();
    }
}