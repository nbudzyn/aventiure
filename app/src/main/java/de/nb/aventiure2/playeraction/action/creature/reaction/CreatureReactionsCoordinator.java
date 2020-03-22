package de.nb.aventiure2.playeraction.action.creature.reaction;

import com.google.common.collect.ImmutableMap;

import java.util.List;
import java.util.Map;

import de.nb.aventiure2.data.database.AvDatabase;
import de.nb.aventiure2.data.storystate.IPlayerAction;
import de.nb.aventiure2.data.storystate.StoryStateDao;
import de.nb.aventiure2.data.world.creature.Creature;
import de.nb.aventiure2.data.world.creature.CreatureData;
import de.nb.aventiure2.data.world.room.AvRoom;

import static de.nb.aventiure2.data.world.creature.Creature.Key.FROSCHPRINZ;

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
                .build();
    }

    public void onLeaveRoom(final AvRoom oldRoom,
                            final List<CreatureData> creaturesInOldRoom) {
        for (final CreatureData creatureInOldRoom : creaturesInOldRoom) {
            allCreatureReactions.get(creatureInOldRoom.getCreature().getKey())
                    .onLeaveRoom(oldRoom, creatureInOldRoom);
        }
    }
}
