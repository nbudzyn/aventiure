package de.nb.aventiure2.data.world.invisible;

import de.nb.aventiure2.data.world.base.AbstractGameObject;
import de.nb.aventiure2.data.world.base.GameObjectId;

/**
 * An invisible concept, idea, event or the like, that has a state.
 *
 * @see de.nb.aventiure2.data.world.entity.base.AbstractEntity
 */
public class Invisible extends AbstractGameObject {
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
