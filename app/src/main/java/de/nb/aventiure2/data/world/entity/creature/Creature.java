package de.nb.aventiure2.data.world.entity.creature;

import de.nb.aventiure2.data.world.base.GameObject;
import de.nb.aventiure2.data.world.base.GameObjectId;
import de.nb.aventiure2.data.world.entity.base.Entity;
import de.nb.aventiure2.data.world.room.Rooms;
import de.nb.aventiure2.german.base.Nominalphrase;

/**
 * A creature in the world (not the player character)
 */
public class Creature extends Entity {
    /**
     * The initial room where this creature can be found.
     */
    private final GameObject initialRoom;
    private final CreatureStateList states;

    /**
     * Constructor for a creature.
     *
     * @param states The first state is the initial state.
     */
    Creature(final GameObjectId id,
             final Nominalphrase descriptionAtFirstSight,
             final Nominalphrase descriptionWhenKnown,
             final GameObject initialRoom,
             final CreatureStateList states) {
        super(id, descriptionAtFirstSight, descriptionWhenKnown,
                descriptionWhenKnown);
        this.initialRoom = initialRoom;
        this.states = states;
    }

    /**
     * Constructor for a creature.
     *
     * @param states The first state is the initial state.
     */
    Creature(final GameObjectId id,
             final Nominalphrase descriptionAtFirstSight,
             final Nominalphrase normalDescriptionWhenKnown,
             final Nominalphrase shortDescriptionWhenKnown,
             final GameObjectId initialRoom,
             final CreatureStateList states) {
        this(id, descriptionAtFirstSight, normalDescriptionWhenKnown,
                shortDescriptionWhenKnown, Rooms.get(initialRoom), states);
    }

    /**
     * Constructor for a creature.
     *
     * @param states The first state is the initial state.
     */
    Creature(final GameObjectId id,
             final Nominalphrase descriptionAtFirstSight,
             final Nominalphrase normalDescriptionWhenKnown,
             final Nominalphrase shortDescriptionWhenKnown,
             final GameObject initialRoom,
             final CreatureStateList states) {
        super(id, descriptionAtFirstSight, normalDescriptionWhenKnown,
                shortDescriptionWhenKnown);
        this.initialRoom = initialRoom;
        this.states = states;
    }

    public GameObject getInitialRoom() {
        return initialRoom;
    }

    public boolean isStateAllowed(final CreatureState state) {
        return states.contains(state);
    }

    public CreatureStateList getAllowedStates() {
        return states;
    }

    public CreatureState getInitialState() {
        return states.getInitial();
    }
}
