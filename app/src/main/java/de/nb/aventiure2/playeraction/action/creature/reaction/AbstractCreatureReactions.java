package de.nb.aventiure2.playeraction.action.creature.reaction;

import androidx.annotation.NonNull;

import de.nb.aventiure2.data.database.AvDatabase;
import de.nb.aventiure2.data.storystate.IPlayerAction;
import de.nb.aventiure2.data.storystate.StoryState;
import de.nb.aventiure2.data.storystate.StoryStateBuilder;
import de.nb.aventiure2.data.storystate.StoryStateDao;
import de.nb.aventiure2.data.world.creature.CreatureData;
import de.nb.aventiure2.data.world.room.AvRoom;

abstract class AbstractCreatureReactions {
    protected final AvDatabase db;
    protected final StoryStateDao n;
    protected final Class<? extends IPlayerAction> playerActionClass;

    public AbstractCreatureReactions(final AvDatabase db,
                                     final Class<? extends IPlayerAction> playerActionClass) {
        this.db = db;
        n = db.storyStateDao();
        this.playerActionClass = playerActionClass;
    }

    /**
     * Called after the PC has left the <code>oldRoom</code>.
     * <i>Make sure you alwasy set <code>letzterRaum</code> to <code>oldRoom</code> when creating
     * <code>StoryState</code>s</i>.
     */
    public abstract void onLeaveRoom(final AvRoom oldRoom, final CreatureData creature);

    protected StoryStateBuilder t(
            @NonNull final StoryState.StartsNew startsNew,
            @NonNull final String text) {

        return StoryStateBuilder.t(playerActionClass, startsNew, text)
                // Sensible default - caller may override this setting
                .letzterRaum(db.playerLocationDao().getPlayerLocation().getRoom());
    }
}
