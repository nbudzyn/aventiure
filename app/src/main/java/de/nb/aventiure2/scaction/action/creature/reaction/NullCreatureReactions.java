package de.nb.aventiure2.scaction.action.creature.reaction;

import de.nb.aventiure2.data.database.AvDatabase;
import de.nb.aventiure2.data.storystate.IPlayerAction;
import de.nb.aventiure2.data.storystate.StoryState;
import de.nb.aventiure2.data.world.base.GameObject;
import de.nb.aventiure2.data.world.entity.base.AbstractEntityData;
import de.nb.aventiure2.data.world.entity.creature.CreatureData;
import de.nb.aventiure2.data.world.entity.object.ObjectData;
import de.nb.aventiure2.data.world.time.AvDateTime;
import de.nb.aventiure2.data.world.time.AvTimeSpan;

import static de.nb.aventiure2.data.world.time.AvTimeSpan.noTime;

class NullCreatureReactions extends AbstractCreatureReactions {
    public NullCreatureReactions(final AvDatabase db,
                                 final Class<? extends IPlayerAction> playerActionClass) {
        super(db, playerActionClass);
    }

    @Override
    public AvTimeSpan onLeaveRoom(final GameObject oldRoom, final CreatureData creature,
                                  final StoryState currentStoryState) {
        return noTime();
    }

    @Override
    public AvTimeSpan onEnterRoom(final GameObject oldRoom, final GameObject newRoom,
                                  final CreatureData creatureInNewRoom,
                                  final StoryState currentStoryState) {
        return noTime();
    }

    @Override
    public AvTimeSpan onNehmen(final GameObject room, final CreatureData creatureInRoom,
                               final AbstractEntityData genommenData,
                               final StoryState currentStoryState) {
        return noTime();
    }

    @Override
    public AvTimeSpan onEssen(final GameObject room, final CreatureData creature,
                              final StoryState currentStoryState) {
        return noTime();
    }

    @Override
    public AvTimeSpan onAblegen(final GameObject room, final CreatureData creatureInRoom,
                                final AbstractEntityData abgelegtData,
                                final StoryState currentStoryState) {
        return noTime();
    }

    @Override
    public AvTimeSpan onHochwerfen(final GameObject room, final CreatureData creatureInRoom,
                                   final ObjectData objectData,
                                   final StoryState currentStoryState) {
        return noTime();
    }

    @Override
    public AvTimeSpan onTimePassed(final AvDateTime lastTime, final AvDateTime now,
                                   final StoryState currentStoryState) {
        return noTime();
    }
}
