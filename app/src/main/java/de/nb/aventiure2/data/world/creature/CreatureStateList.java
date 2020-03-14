package de.nb.aventiure2.data.world.creature;

import androidx.annotation.NonNull;

import com.google.common.collect.ImmutableList;

/**
 * The states a creature can be in (one at a time).
 */
public class CreatureStateList {
    private final ImmutableList<CreatureState> states;

    public static CreatureStateList sm(final CreatureState... states) {
        return new CreatureStateList(states);
    }

    public CreatureStateList(final CreatureState... states) {
        this.states = ImmutableList.copyOf(states);
    }

    public CreatureState getInitial() {
        return states.iterator().next();
    }

    public boolean contains(final CreatureState state) {
        return states.contains(state);
    }

    @NonNull
    @Override
    public String toString() {
        return states.toString();
    }
}
