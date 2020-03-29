package de.nb.aventiure2.data.world.invisible;

import de.nb.aventiure2.data.world.base.StateList;

public class InvisibleStateList extends StateList<InvisibleState> {
    public static InvisibleStateList sl(final InvisibleState... states) {
        return new InvisibleStateList(states);
    }

    public InvisibleStateList(final InvisibleState... states) {
        super(states);
    }
}
