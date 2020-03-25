package de.nb.aventiure2.playeraction.action.creature.reaction;

import androidx.annotation.NonNull;

import com.google.common.collect.ImmutableCollection;

import java.util.Collection;

import de.nb.aventiure2.data.database.AvDatabase;
import de.nb.aventiure2.data.storystate.IPlayerAction;
import de.nb.aventiure2.data.storystate.StoryState;
import de.nb.aventiure2.data.storystate.StoryStateBuilder;
import de.nb.aventiure2.data.storystate.StoryStateDao;
import de.nb.aventiure2.data.world.creature.CreatureData;
import de.nb.aventiure2.data.world.entity.AbstractEntityData;
import de.nb.aventiure2.data.world.object.ObjectData;
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
    public abstract void onLeaveRoom(final AvRoom oldRoom, final CreatureData creature,
                                     StoryState currentStoryState);

    /**
     * Called after the PC has entered the <code>newRoom</code>.
     * <i>Make sure you alwasy set <code>letzterRaum</code> to <code>oldRoom</code> when creating
     * <code>StoryState</code>s</i>.
     */
    public abstract void onEnterRoom(final AvRoom oldRoom, AvRoom newRoom,
                                     CreatureData creatureInNewRoom,
                                     StoryState currentStoryState);

    public abstract void onNehmen(AvRoom room, CreatureData creatureInRoom,
                                  AbstractEntityData genommenData,
                                  StoryState currentStoryState);

    public abstract void onAblegen(AvRoom room, CreatureData creatureInRoom,
                                   AbstractEntityData abgelegtData,
                                   StoryState currentStoryState);

    public abstract void onHochwerfen(AvRoom room, CreatureData creatureInRoom,
                                      ObjectData objectData, StoryState currentStoryState);

    protected StoryStateBuilder alt(
            final ImmutableCollection.Builder<StoryStateBuilder> alternatives) {
        return alt(alternatives.build());
    }

    private StoryStateBuilder alt(final Collection<StoryStateBuilder> alternatives) {
        return alt(alternatives.toArray(new StoryStateBuilder[alternatives.size()]));
    }

    protected StoryStateBuilder alt(final StoryStateBuilder... alternatives) {
        return n.chooseNextFrom(alternatives);
    }

    protected StoryStateBuilder t(
            @NonNull final StoryState.StructuralElement startsNew,
            @NonNull final String text) {

        return StoryStateBuilder.t(playerActionClass, startsNew, text)
                // Sensible default - caller may override this setting
                .letzterRaum(db.playerLocationDao().getPlayerLocation().getRoom());
    }
}
