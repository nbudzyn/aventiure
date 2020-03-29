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
import de.nb.aventiure2.data.world.time.AvTimeSpan;

import static de.nb.aventiure2.data.world.creature.Creature.Key.FROSCHPRINZ;
import static de.nb.aventiure2.data.world.creature.Creature.Key.SCHLOSSWACHE;
import static de.nb.aventiure2.data.world.time.AvTimeSpan.noTime;

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

    public AvTimeSpan onLeaveRoom(final AvRoom oldRoom,
                                  final List<CreatureData> creaturesInOldRoom) {
        AvTimeSpan timeElapsed = noTime();
        final StoryState currentStoryState = n.getStoryState();

        for (final CreatureData creatureInOldRoom : creaturesInOldRoom) {
            timeElapsed = timeElapsed.plus(
                    getReactions(creatureInOldRoom).onLeaveRoom(oldRoom, creatureInOldRoom,
                            currentStoryState));
        }

        return timeElapsed;
    }

    public AvTimeSpan onEnterRoom(final AvRoom oldRoom,
                                  final AvRoom newRoom,
                                  final List<CreatureData> creaturesInNewRoom) {
        AvTimeSpan timeElapsed = noTime();
        final StoryState currentStoryState = n.getStoryState();

        for (final CreatureData creatureInNewRoom : creaturesInNewRoom) {
            timeElapsed = timeElapsed.plus(
                    getReactions(creatureInNewRoom).onEnterRoom(oldRoom, newRoom, creatureInNewRoom,
                            currentStoryState));
        }

        return timeElapsed;
    }

    public AvTimeSpan onNehmen(final AvRoom room,
                               final AbstractEntityData entityData) {
        AvTimeSpan timeElapsed = noTime();
        final StoryState currentStoryState = n.getStoryState();

        for (final CreatureData creatureInRoom : getCreaturesInRoom(room)) {
            timeElapsed = timeElapsed.plus(getReactions(creatureInRoom)
                    .onNehmen(room, creatureInRoom, entityData, currentStoryState));
        }

        return timeElapsed;
    }

    public AvTimeSpan onAblegen(final AvRoom room, final AbstractEntityData entityData) {
        AvTimeSpan timeElapsed = noTime();
        final StoryState currentStoryState = n.getStoryState();

        for (final CreatureData creatureInRoom : getCreaturesInRoom(room)) {
            timeElapsed = timeElapsed.plus(getReactions(creatureInRoom)
                    .onAblegen(room, creatureInRoom, entityData,
                            currentStoryState));
        }

        return timeElapsed;
    }

    public AvTimeSpan onHochwerfen(final AvRoom room, final ObjectData objectData) {
        AvTimeSpan timeElapsed = noTime();
        final StoryState currentStoryState = n.getStoryState();

        for (final CreatureData creatureInRoom : getCreaturesInRoom(room)) {
            timeElapsed = timeElapsed.plus(getReactions(creatureInRoom)
                    .onHochwerfen(room, creatureInRoom, objectData,
                            currentStoryState));
        }

        return timeElapsed;
    }

    private List<CreatureData> getCreaturesInRoom(final AvRoom room) {
        return db.creatureDataDao().getCreaturesInRoom(room);
    }

    private AbstractCreatureReactions getReactions(final CreatureData creature) {
        return allCreatureReactions.get(creature.getCreature().getKey());
    }
}
