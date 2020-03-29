package de.nb.aventiure2.data.world.entity.creature;

import de.nb.aventiure2.data.world.base.StateList;

/**
 * The states a creature can be in (one at a time).
 */
public class CreatureStateList extends StateList<CreatureState> {
    public static CreatureStateList sl(final CreatureState... states) {
        return new CreatureStateList(states);
    }

    public CreatureStateList(final CreatureState... states) {
        super(states);
    }
}
