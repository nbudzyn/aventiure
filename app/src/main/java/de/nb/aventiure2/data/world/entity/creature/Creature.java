package de.nb.aventiure2.data.world.entity.creature;

import de.nb.aventiure2.data.world.base.GameObjectId;
import de.nb.aventiure2.data.world.entity.base.AbstractEntity;
import de.nb.aventiure2.data.world.room.AvRoom;
import de.nb.aventiure2.german.base.Nominalphrase;

/**
 * A creature in the world (not the player character)
 */
public class Creature extends AbstractEntity {
    public enum Key {
        SCHLOSSWACHE(20_000), FROSCHPRINZ(20_001);

        private final GameObjectId gameObjectId;

        private Key(final int gameObjectId) {
            this(new GameObjectId(gameObjectId));
        }

        Key(final GameObjectId gameObjectId) {
            this.gameObjectId = gameObjectId;
        }

        public GameObjectId getGameObjectId() {
            return gameObjectId;
        }
    }

    private final Creature.Key key;

    /**
     * The initial room where this creature can be found.
     */
    private final AvRoom initialRoom;
    private final CreatureStateList states;

    public static Creature get(final Creature.Key key) {
        for (final Creature creature : Creatures.ALL) {
            if (creature.key == key) {
                return creature;
            }
        }

        throw new IllegalStateException("Unexpected key: " + key);
    }

    /**
     * Constructor for a creature.
     *
     * @param states The first state is the initial state.
     */
    Creature(final Creature.Key key,
             final Nominalphrase descriptionAtFirstSight,
             final Nominalphrase descriptionWhenKnown,
             final AvRoom initialRoom,
             final CreatureStateList states) {
        super(key.getGameObjectId(), descriptionAtFirstSight, descriptionWhenKnown,
                descriptionWhenKnown);
        this.key = key;
        this.initialRoom = initialRoom;
        this.states = states;
    }

    /**
     * Constructor for a creature.
     *
     * @param states The first state is the initial state.
     */
    Creature(final Creature.Key key,
             final Nominalphrase descriptionAtFirstSight,
             final Nominalphrase normalDescriptionWhenKnown,
             final Nominalphrase shortDescriptionWhenKnown,
             final AvRoom.Key initialRoom,
             final CreatureStateList states) {
        this(key, descriptionAtFirstSight, normalDescriptionWhenKnown,
                shortDescriptionWhenKnown, AvRoom.get(initialRoom), states);
    }

    /**
     * Constructor for a creature.
     *
     * @param states The first state is the initial state.
     */
    Creature(final Creature.Key key,
             final Nominalphrase descriptionAtFirstSight,
             final Nominalphrase normalDescriptionWhenKnown,
             final Nominalphrase shortDescriptionWhenKnown,
             final AvRoom initialRoom,
             final CreatureStateList states) {
        super(key.getGameObjectId(), descriptionAtFirstSight, normalDescriptionWhenKnown,
                shortDescriptionWhenKnown);
        this.key = key;
        this.initialRoom = initialRoom;
        this.states = states;
    }

    public Creature.Key getKey() {
        return key;
    }

    // TODO Use or remove
    public AvRoom getInitialRoom() {
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

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final Creature creature = (Creature) o;
        return key == creature.key;
    }

    @Override
    public int hashCode() {
        return key.hashCode();
    }
}
