package de.nb.aventiure2.playeraction.action.creature.reaction;

import de.nb.aventiure2.data.database.AvDatabase;
import de.nb.aventiure2.data.storystate.IPlayerAction;
import de.nb.aventiure2.data.storystate.StoryState;
import de.nb.aventiure2.data.world.entity.base.AbstractEntityData;
import de.nb.aventiure2.data.world.entity.creature.CreatureData;
import de.nb.aventiure2.data.world.entity.object.ObjectData;
import de.nb.aventiure2.data.world.room.AvRoom;
import de.nb.aventiure2.data.world.time.AvDateTime;
import de.nb.aventiure2.data.world.time.AvTimeSpan;

import static de.nb.aventiure2.data.world.time.AvTimeSpan.noTime;

class NullCreatureReactions extends AbstractCreatureReactions {
    public NullCreatureReactions(final AvDatabase db,
                                 final Class<? extends IPlayerAction> playerActionClass) {
        super(db, playerActionClass);
    }

    @Override
    public AvTimeSpan onLeaveRoom(final AvRoom oldRoom, final CreatureData creature,
                                  final StoryState currentStoryState) {
        return noTime();
    }

    @Override
    public AvTimeSpan onEnterRoom(final AvRoom oldRoom, final AvRoom newRoom,
                                  final CreatureData creatureInNewRoom,
                                  final StoryState currentStoryState) {
        return noTime();
    }

    @Override
    public AvTimeSpan onNehmen(final AvRoom room, final CreatureData creatureInRoom,
                               final AbstractEntityData genommenData,
                               final StoryState currentStoryState) {
        return noTime();
    }

    @Override
    public AvTimeSpan onAblegen(final AvRoom room, final CreatureData creatureInRoom,
                                final AbstractEntityData abgelegtData,
                                final StoryState currentStoryState) {
        return noTime();
    }

    @Override
    public AvTimeSpan onHochwerfen(final AvRoom room, final CreatureData creatureInRoom,
                                   final ObjectData objectData,
                                   final StoryState currentStoryState) {
        return noTime();
    }

    @Override
    public AvTimeSpan onTimePassed(final AvDateTime lastTime, final AvDateTime now) {
        return noTime();
    }
}
