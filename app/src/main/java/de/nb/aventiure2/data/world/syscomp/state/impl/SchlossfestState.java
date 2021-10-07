package de.nb.aventiure2.data.world.syscomp.state.impl;

import static de.nb.aventiure2.data.time.AvTime.oClock;

import de.nb.aventiure2.data.time.AvTime;

/**
 * A state the Froschprinz can be in.
 */
public enum SchlossfestState {
    NOCH_NICHT_BEGONNEN(false),
    BEGONNEN(true),
    VERWUESTET(true),
    NACH_VERWUESTUNG_WIEDER_GERICHTET_MARKTSTAENDE_OFFEN(true),
    NACH_VERWUESTUNG_WIEDER_GERICHTET_MARKTSTAENDE_GESCHLOSSEN(true);

    private static final AvTime BEGINN_MARKTZEIT = oClock(7, 15);
    private static final AvTime ENDE_MARKTZEIT = oClock(16, 30);

    private final boolean schlossfestLaeuft;

    public static SchlossfestState getMarkt(final AvTime time) {
        return time.isWithin(BEGINN_MARKTZEIT, ENDE_MARKTZEIT) ?
                NACH_VERWUESTUNG_WIEDER_GERICHTET_MARKTSTAENDE_OFFEN :
                NACH_VERWUESTUNG_WIEDER_GERICHTET_MARKTSTAENDE_GESCHLOSSEN;
    }

    SchlossfestState(final boolean schlossfestLaeuft) {
        this.schlossfestLaeuft = schlossfestLaeuft;
    }

    public boolean schlossfestLaeuft() {
        return schlossfestLaeuft;
    }
}
