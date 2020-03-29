package de.nb.aventiure2.data.world.invisible;

/**
 * An invisible concept, idea, event or the like, that has a state.
 *
 * @see de.nb.aventiure2.data.world.entity.base.AbstractEntity
 */
public class Invisible {
    public enum Key {
        SCHLOSSFEST
    }

    private final Invisible.Key key;

    private final InvisibleStateList states;

    public static Invisible get(final Invisible.Key key) {
        for (final Invisible invisible : Invisibles.ALL) {
            if (invisible.key == key) {
                return invisible;
            }
        }

        throw new IllegalStateException("Unexpected key: " + key);
    }

    /**
     * Constructor for an invisible.
     *
     * @param states The first state is the initial state.
     */
    Invisible(final Invisible.Key key,
              final InvisibleStateList states) {
        this.key = key;
        this.states = states;
    }

    public Invisible.Key getKey() {
        return key;
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

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final Invisible invisible = (Invisible) o;
        return key == invisible.key;
    }

    @Override
    public int hashCode() {
        return key.hashCode();
    }
}
