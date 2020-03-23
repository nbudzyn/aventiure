package de.nb.aventiure2.playeraction.action.creature.reaction;

import com.google.common.collect.ImmutableMap;

import java.util.List;
import java.util.Map;

import de.nb.aventiure2.data.database.AvDatabase;
import de.nb.aventiure2.data.storystate.IPlayerAction;
import de.nb.aventiure2.data.storystate.StoryState;
import de.nb.aventiure2.data.storystate.StoryStateDao;
import de.nb.aventiure2.data.world.creature.Creature;
import de.nb.aventiure2.data.world.creature.CreatureData;
import de.nb.aventiure2.data.world.entity.AbstractEntityData;
import de.nb.aventiure2.data.world.object.ObjectData;
import de.nb.aventiure2.data.world.room.AvRoom;

import static de.nb.aventiure2.data.world.creature.Creature.Key.FROSCHPRINZ;
import static de.nb.aventiure2.data.world.creature.Creature.Key.SCHLOSSWACHE;

public final class CreatureReactionsCoordinator {
    private final AvDatabase db;
    private final StoryStateDao n;

    private final Map<Creature.Key, AbstractCreatureReactions> allCreatureReactions;

    public CreatureReactionsCoordinator(final AvDatabase db,
                                        final Class<? extends IPlayerAction> playerActionClass) {
        this.db = db;
        n = db.storyStateDao();

        allCreatureReactions = ImmutableMap.<Creature.Key, AbstractCreatureReactions>builder()
                .put(FROSCHPRINZ, new FroschprinzCreatureReactions(db, playerActionClass))
                .put(SCHLOSSWACHE, new SchlosswacheCreatureReactions(db, playerActionClass))
                .build();
    }

    public void onLeaveRoom(final AvRoom oldRoom,
                            final List<CreatureData> creaturesInOldRoom) {
        final StoryState currentStoryState = n.getStoryState();

        for (final CreatureData creatureInOldRoom : creaturesInOldRoom) {
            getReactions(creatureInOldRoom).onLeaveRoom(oldRoom, creatureInOldRoom,
                    currentStoryState);
        }
    }

    public void onEnterRoom(final AvRoom oldRoom,
                            final AvRoom newRoom, final List<CreatureData> creaturesInNewRoom) {
        final StoryState currentStoryState = n.getStoryState();

        for (final CreatureData creatureInNewRoom : creaturesInNewRoom) {
            getReactions(creatureInNewRoom).onEnterRoom(oldRoom, newRoom, creatureInNewRoom,
                    currentStoryState);
        }
    }

    public void onNehmen(final AvRoom room,
                         final AbstractEntityData entityData) {
        final StoryState currentStoryState = n.getStoryState();

        for (final CreatureData creatureInRoom : getCreaturesInRoom(room)) {
            getReactions(creatureInRoom).onNehmen(room, creatureInRoom, entityData,
                    currentStoryState);
        }
    }

    public void onAblegen(final AvRoom room, final AbstractEntityData entityData) {
        final StoryState currentStoryState = n.getStoryState();

        for (final CreatureData creatureInRoom : getCreaturesInRoom(room)) {
            getReactions(creatureInRoom).onAblegen(room, creatureInRoom, entityData,
                    currentStoryState);
        }
    }

    public void onHochwerfen(final AvRoom room, final ObjectData objectData) {
        final StoryState currentStoryState = n.getStoryState();

        for (final CreatureData creatureInRoom : getCreaturesInRoom(room)) {
            getReactions(creatureInRoom).onHochwerfen(room, creatureInRoom, objectData,
                    currentStoryState);
        }
    }

    private List<CreatureData> getCreaturesInRoom(final AvRoom room) {
        return db.creatureDataDao().getCreaturesInRoom(room);
    }

    private AbstractCreatureReactions getReactions(final CreatureData creature) {
        return allCreatureReactions.get(creature.getCreature().getKey());
    }
}
