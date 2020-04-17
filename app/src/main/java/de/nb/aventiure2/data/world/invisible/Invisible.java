package de.nb.aventiure2.data.world.invisible;

import de.nb.aventiure2.data.world.base.GameObject;
import de.nb.aventiure2.data.world.base.GameObjectId;
import de.nb.aventiure2.data.world.entity.base.Entity;

/**
 * An invisible concept, idea, event or the like, that has a state.
 *
 * @see Entity
 */
public class Invisible extends GameObject {
    private final InvisibleStateList states;

    /**
     * Constructor for an invisible.
     *
     * @param states The first state is the initial state.
     */
    Invisible(final GameObjectId id,
              final InvisibleStateList states) {
        super(id);
        this.states = states;
    }

    public boolean isStateAllowed(final InvisibleState state) {
        return states.contains(state);
    }

    public InvisibleStateList getAllowedStates() {
        return states;
    }

    public InvisibleState getInitialState() {
        return states.getInitial();
    }
}
