package de.nb.aventiure2.scaction.action.creature.reaction;

import de.nb.aventiure2.data.database.AvDatabase;
import de.nb.aventiure2.data.storystate.IPlayerAction;
import de.nb.aventiure2.data.storystate.StoryState;
import de.nb.aventiure2.data.world.entity.base.AbstractEntityData;
import de.nb.aventiure2.data.world.entity.creature.CreatureData;
import de.nb.aventiure2.data.world.entity.object.ObjectData;
import de.nb.aventiure2.data.world.room.AvRoom;
import de.nb.aventiure2.data.world.time.AvDateTime;
import de.nb.aventiure2.data.world.time.AvTimeSpan;
import de.nb.aventiure2.scaction.action.base.reaction.AbstractReactions;

abstract class AbstractCreatureReactions extends AbstractReactions {
    public AbstractCreatureReactions(final AvDatabase db,
                                     final Class<? extends IPlayerAction> scActionClass) {
        super(db, scActionClass);
    }

    /**
     * Called after the PC has left the <code>oldRoom</code>.
     * <i>Make sure you alwasy set <code>letzterRaum</code> to <code>oldRoom</code> when creating
     * <code>StoryState</code>s</i>.
     */
    public abstract AvTimeSpan onLeaveRoom(final AvRoom oldRoom, final CreatureData creature,
                                           StoryState currentStoryState);

    /**
     * Called after the PC has entered the <code>newRoom</code>.
     * <i>Make sure you alwasy set <code>letzterRaum</code> to <code>oldRoom</code> when creating
     * <code>StoryState</code>s</i>.
     */
    public abstract AvTimeSpan onEnterRoom(final AvRoom oldRoom, AvRoom newRoom,
                                           CreatureData creatureInNewRoom,
                                           StoryState currentStoryState);

    public abstract AvTimeSpan onNehmen(AvRoom room, CreatureData creatureInRoom,
                                        AbstractEntityData genommenData,
                                        StoryState currentStoryState);

    public abstract AvTimeSpan onEssen(AvRoom room, CreatureData creatureInRoom,
                                       StoryState currentStoryState);

    public abstract AvTimeSpan onAblegen(AvRoom room, CreatureData creatureInRoom,
                                         AbstractEntityData abgelegtData,
                                         StoryState currentStoryState);

    public abstract AvTimeSpan onHochwerfen(AvRoom room, CreatureData creatureInRoom,
                                            ObjectData objectData, StoryState currentStoryState);

    public abstract AvTimeSpan onTimePassed(AvDateTime lastTime, AvDateTime now,
                                            final StoryState currentStoryState);
}
