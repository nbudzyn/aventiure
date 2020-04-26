package de.nb.aventiure2.scaction.action.creature.reaction;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

import de.nb.aventiure2.data.database.AvDatabase;
import de.nb.aventiure2.data.storystate.StoryState;
import de.nb.aventiure2.data.storystate.StoryStateDao;
import de.nb.aventiure2.data.world.alive.ILivingBeingGO;
import de.nb.aventiure2.data.world.base.GameObjectId;
import de.nb.aventiure2.data.world.base.IGameObject;
import de.nb.aventiure2.data.world.description.IDescribableGO;
import de.nb.aventiure2.data.world.location.ILocatableGO;
import de.nb.aventiure2.data.world.storingplace.IHasStoringPlaceGO;
import de.nb.aventiure2.data.world.time.AvDateTime;
import de.nb.aventiure2.data.world.time.AvTimeSpan;

import static de.nb.aventiure2.data.world.gameobjects.GameObjects.FROSCHPRINZ;
import static de.nb.aventiure2.data.world.gameobjects.GameObjects.SCHLOSSWACHE;
import static de.nb.aventiure2.data.world.gameobjects.GameObjects.loadDescribableLivingInventory;
import static de.nb.aventiure2.data.world.time.AvTimeSpan.noTime;

public final class CreatureReactionsCoordinator {
    private final AvDatabase db;
    private final StoryStateDao n;

    private final Map<GameObjectId, AbstractCreatureReactions> allCreatureReactions;

    public CreatureReactionsCoordinator(final AvDatabase db) {
        this.db = db;
        n = db.storyStateDao();

        allCreatureReactions = ImmutableMap.<GameObjectId, AbstractCreatureReactions>builder()
                .put(FROSCHPRINZ, new FroschprinzReactions(db))
                .put(SCHLOSSWACHE, new SchlosswacheReactions(db))
                .build();
    }

    public AvTimeSpan onLeaveRoom(final IGameObject oldRoom,
                                  final List<? extends ILivingBeingGO> creaturesInOldRoom) {
        return doReactions(creaturesInOldRoom,
                r -> r.onLeaveRoom(oldRoom, getCurrentStoryState()));
    }


    public AvTimeSpan onEnterRoom(final IHasStoringPlaceGO oldRoom,
                                  final IHasStoringPlaceGO newRoom,
                                  final List<? extends ILivingBeingGO> creaturesInNewRoom) {
        return doReactions(creaturesInNewRoom,
                r -> r.onEnterRoom(oldRoom, newRoom,
                        getCurrentStoryState()));
    }

    public AvTimeSpan onNehmen(final IHasStoringPlaceGO room,
                               final ILocatableGO taken) {
        return doReactions(getCreaturesInRoom(room),
                r -> r.onNehmen(room, taken, getCurrentStoryState()));
    }

    public AvTimeSpan onEssen(final IHasStoringPlaceGO room) {
        return doReactionsForAllCreatures(
                r -> r.onEssen(room, getCurrentStoryState()));
    }

    public AvTimeSpan onAblegen(final IHasStoringPlaceGO room,
                                final IGameObject abgelegt) {
        return doReactions(getCreaturesInRoom(room),
                r -> r.onAblegen(room, abgelegt,
                        getCurrentStoryState()));
    }

    public AvTimeSpan onHochwerfen(final IHasStoringPlaceGO room,
                                   final ILocatableGO objectData) {
        return doReactions(getCreaturesInRoom(room),
                r -> r.onHochwerfen(room, objectData,
                        getCurrentStoryState()));
    }

    public AvTimeSpan onTimePassed(final AvDateTime lastTime, final AvDateTime now) {
        return doReactionsForAllCreatures(
                r -> r.onTimePassed(lastTime, now, getCurrentStoryState()));
    }

    private StoryState getCurrentStoryState() {
        return n.getStoryState();
    }

    private <LIV extends ILocatableGO & IDescribableGO & ILivingBeingGO>
    ImmutableList<LIV> getCreaturesInRoom(final IHasStoringPlaceGO room) {
        return loadDescribableLivingInventory(db, room);
    }

    private AvTimeSpan doReactions(final List<? extends ILivingBeingGO> creatures,
                                   final Function<AbstractCreatureReactions,
                                           AvTimeSpan> reaction) {
        AvTimeSpan timeElapsed = noTime();

        for (final ILivingBeingGO creature : creatures) {
            final AbstractCreatureReactions reactions = allCreatureReactions.get(creature.getId());
            if (reactions != null) {
                timeElapsed = timeElapsed.plus(reaction.apply(reactions));
            }
        }

        return timeElapsed;
    }

    private AvTimeSpan doReactionsForAllCreatures(
            final Function<AbstractCreatureReactions, AvTimeSpan> reaction) {
        AvTimeSpan timeElapsed = noTime();

        for (final AbstractCreatureReactions reactions : allCreatureReactions.values()) {
            timeElapsed = timeElapsed.plus(reaction.apply(reactions));
        }

        return timeElapsed;
    }

}
