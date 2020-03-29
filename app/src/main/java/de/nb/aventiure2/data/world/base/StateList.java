package de.nb.aventiure2.data.world.base;

import androidx.annotation.NonNull;

import com.google.common.collect.ImmutableList;

/**
 * The states something can be in (one at a time).
 */
public abstract class StateList<S> {
    private final ImmutableList<S> states;

    @SafeVarargs
    public StateList(final S... states) {
        this.states = ImmutableList.copyOf(states);
    }

    public S getInitial() {
        return states.iterator().next();
    }

    public boolean contains(final S state) {
        return states.contains(state);
    }

    @NonNull
    @Override
    public String toString() {
        return states.toString();
    }
}
