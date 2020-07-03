package de.nb.aventiure2.data.world.syscomp.state;

import de.nb.aventiure2.data.world.base.StateList;

public class GameObjectStateList extends StateList<GameObjectState> {
    // TODO Separates Status-Enum für jedes IHasState

    public static GameObjectStateList sl(final GameObjectState... states) {
        return new GameObjectStateList(states);
    }

    private GameObjectStateList(final GameObjectState... states) {
        super(states);
    }
}
