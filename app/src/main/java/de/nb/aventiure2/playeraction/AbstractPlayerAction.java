package de.nb.aventiure2.playeraction;

import androidx.annotation.NonNull;

import de.nb.aventiure2.data.database.AvDatabase;
import de.nb.aventiure2.data.storystate.IPlayerAction;
import de.nb.aventiure2.data.storystate.StoryState;
import de.nb.aventiure2.data.storystate.StoryState.StartsNew;
import de.nb.aventiure2.data.storystate.StoryStateBuilder;
import de.nb.aventiure2.data.storystate.StoryStateDao;
import de.nb.aventiure2.data.world.creature.CreatureDataDao;
import de.nb.aventiure2.data.world.object.ObjectDataDao;
import de.nb.aventiure2.data.world.player.inventory.PlayerInventoryDao;
import de.nb.aventiure2.data.world.player.location.PlayerLocationDao;
import de.nb.aventiure2.data.world.player.stats.PlayerStatsDao;
import de.nb.aventiure2.data.world.room.RoomDao;

/**
 * An action the player could choose to advance the story.
 */
public abstract class AbstractPlayerAction implements IPlayerAction {
    private final AvDatabase db;
    protected final StoryStateDao n;
    protected final RoomDao roomDao;
    protected final ObjectDataDao objectDataDao;
    protected final CreatureDataDao creatureDataDao;
    protected final PlayerStatsDao playerStatsDao;
    protected final PlayerLocationDao playerLocationDao;
    protected final PlayerInventoryDao playerInventoryDao;

    protected AbstractPlayerAction(final AvDatabase db) {
        this.db = db;
        roomDao = db.roomDao();
        playerStatsDao = db.playerStatsDao();
        objectDataDao = db.objectDataDao();
        creatureDataDao = db.creatureDataDao();
        playerLocationDao = db.playerLocationDao();
        playerInventoryDao = db.playerInventoryDao();

        n = db.storyStateDao();
    }

    /**
     * Returns the name of the action as it is displayed to the player.
     */
    abstract public String getName();

    abstract public void narrateAndDo(StoryState currentStoryState);

    protected StoryStateBuilder t(
            final StartsNew startsNew,
            @NonNull final String text) {
        return StoryStateBuilder.t(this, startsNew, text)
                // Sensible default - caller may override this setting
                .letzterRaum(playerLocationDao.getPlayerLocation().getRoom());
    }
}