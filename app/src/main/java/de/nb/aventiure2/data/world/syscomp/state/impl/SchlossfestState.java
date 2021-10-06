package de.nb.aventiure2.data.world.syscomp.state.impl;

/**
 * A state the Froschprinz can be in.
 */
public enum SchlossfestState {
    NOCH_NICHT_BEGONNEN(false),
    BEGONNEN(true),
    VERWUESTET(true),
    MARKT_AUFGEBAUT(true);

    private final boolean schlossfestLaeuft;

    SchlossfestState(final boolean schlossfestLaeuft) {
        this.schlossfestLaeuft = schlossfestLaeuft;
    }

    public boolean schlossfestLaeuft() {
        return schlossfestLaeuft;
    }
}
