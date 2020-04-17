package de.nb.aventiure2.scaction.action.creature.reaction;

import com.google.common.collect.ImmutableMap;

import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import de.nb.aventiure2.data.database.AvDatabase;
import de.nb.aventiure2.data.storystate.IPlayerAction;
import de.nb.aventiure2.data.storystate.StoryState;
import de.nb.aventiure2.data.storystate.StoryStateDao;
import de.nb.aventiure2.data.world.base.GameObject;
import de.nb.aventiure2.data.world.base.GameObjectId;
import de.nb.aventiure2.data.world.entity.base.AbstractEntityData;
import de.nb.aventiure2.data.world.entity.creature.CreatureData;
import de.nb.aventiure2.data.world.entity.object.ObjectData;
import de.nb.aventiure2.data.world.time.AvDateTime;
import de.nb.aventiure2.data.world.time.AvTimeSpan;

import static de.nb.aventiure2.data.world.entity.creature.Creatures.FROSCHPRINZ;
import static de.nb.aventiure2.data.world.entity.creature.Creatures.SCHLOSSWACHE;
import static de.nb.aventiure2.data.world.time.AvTimeSpan.noTime;

public final class CreatureReactionsCoordinator {
    private final AvDatabase db;
    private final StoryStateDao n;

    private final Map<GameObjectId, AbstractCreatureReactions> allCreatureReactions;

    private final NullCreatureReactions nullCreatureReactions;

    public CreatureReactionsCoordinator(final AvDatabase db,
                                        final Class<? extends IPlayerAction> playerActionClass) {
        this.db = db;
        n = db.storyStateDao();

        allCreatureReactions = ImmutableMap.<GameObjectId, AbstractCreatureReactions>builder()
                .put(FROSCHPRINZ, new FroschprinzReactions(db, playerActionClass))
                .put(SCHLOSSWACHE, new SchlosswacheReactions(db, playerActionClass))
                .build();

        nullCreatureReactions = new NullCreatureReactions(db, playerActionClass);
    }

    public AvTimeSpan onLeaveRoom(final GameObject oldRoom,
                                  final List<CreatureData> creaturesInOldRoom) {
        AvTimeSpan timeElapsed = noTime();

        for (final CreatureData creatureInOldRoom : creaturesInOldRoom) {
            timeElapsed = timeElapsed.plus(
                    getReactions(creatureInOldRoom).onLeaveRoom(oldRoom, creatureInOldRoom,
                            getCurrentStoryState()));
        }

        return timeElapsed;
    }

    public AvTimeSpan onEnterRoom(final GameObject oldRoom,
                                  final GameObject newRoom,
                                  final List<CreatureData> creaturesInNewRoom) {
        AvTimeSpan timeElapsed = noTime();

        for (final CreatureData creatureInNewRoom : creaturesInNewRoom) {
            timeElapsed = timeElapsed.plus(
                    getReactions(creatureInNewRoom).onEnterRoom(oldRoom, newRoom, creatureInNewRoom,
                            getCurrentStoryState()));
        }

        return timeElapsed;
    }

    public AvTimeSpan onNehmen(final GameObject room,
                               final AbstractEntityData entityData) {
        AvTimeSpan timeElapsed = noTime();

        for (final CreatureData creatureInRoom : getCreaturesInRoom(room)) {
            timeElapsed = timeElapsed.plus(getReactions(creatureInRoom)
                    .onNehmen(room, creatureInRoom, entityData, getCurrentStoryState()));
        }

        return timeElapsed;
    }

    public AvTimeSpan onEssen(final GameObject room) {
        AvTimeSpan timeElapsed = noTime();

        for (final CreatureData creature : getAllCreatures()) {
            timeElapsed = timeElapsed.plus(getReactions(creature)
                    .onEssen(room, creature, getCurrentStoryState()));
        }

        return timeElapsed;
    }

    public AvTimeSpan onAblegen(final GameObject room, final AbstractEntityData entityData) {
        AvTimeSpan timeElapsed = noTime();

        for (final CreatureData creatureInRoom : getCreaturesInRoom(room)) {
            timeElapsed = timeElapsed.plus(getReactions(creatureInRoom)
                    .onAblegen(room, creatureInRoom, entityData,
                            getCurrentStoryState()));
        }

        return timeElapsed;
    }

    public AvTimeSpan onHochwerfen(final GameObject room, final ObjectData objectData) {
        AvTimeSpan timeElapsed = noTime();

        for (final CreatureData creatureInRoom : getCreaturesInRoom(room)) {
            timeElapsed = timeElapsed.plus(getReactions(creatureInRoom)
                    .onHochwerfen(room, creatureInRoom, objectData,
                            getCurrentStoryState()));
        }

        return timeElapsed;
    }


    public AvTimeSpan onTimePassed(final AvDateTime lastTime, final AvDateTime now) {
        AvTimeSpan timeElapsed = noTime();

        for (final CreatureData creatureInRoom : getAllCreatures()) {
            timeElapsed = timeElapsed.plus(getReactions(creatureInRoom)
                    .onTimePassed(lastTime, now, getCurrentStoryState()));
        }

        return timeElapsed;
    }

    private List<CreatureData> getAllCreatures() {
        return db.creatureDataDao().getAll();
    }


    private StoryState getCurrentStoryState() {
        return n.getStoryState();
    }

    private List<CreatureData> getCreaturesInRoom(final GameObject room) {
        return db.creatureDataDao().getCreaturesInRoom(room);
    }

    @Nonnull
    private AbstractCreatureReactions getReactions(final CreatureData creature) {
        @Nullable final AbstractCreatureReactions res =
                allCreatureReactions.get(creature.getGameObjectId());
        if (res != null) {
            return res;
        }

        return nullCreatureReactions;
    }
}
