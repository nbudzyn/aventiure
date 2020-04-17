package de.nb.aventiure2.data.world.invisible;

import de.nb.aventiure2.data.world.base.AbstractGameObject;
import de.nb.aventiure2.data.world.base.GameObjectId;

/**
 * An invisible concept, idea, event or the like, that has a state.
 *
 * @see de.nb.aventiure2.data.world.entity.base.AbstractEntity
 */
public class Invisible extends AbstractGameObject {
    public static GameObjectId SCHLOSSFEST = new GameObjectId(40_000);
    public static GameObjectId TAGESZEIT = new GameObjectId(40_001);

    private final InvisibleStateList states;

    public static Invisible get(final GameObjectId id) {
        for (final Invisible invisible : Invisibles.ALL) {
            if (invisible.is(id)) {
                return invisible;
            }
        }

        throw new IllegalStateException("Unexpected game object id: " + id);
    }

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
