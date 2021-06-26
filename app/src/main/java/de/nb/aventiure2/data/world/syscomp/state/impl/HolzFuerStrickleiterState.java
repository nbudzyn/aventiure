package de.nb.aventiure2.data.world.syscomp.state.impl;

public enum HolzFuerStrickleiterState {
    /**
     * Der Sturm hat das Holz noch nicht von den Bäumen gebrochen
     */
    AM_BAUM,
    /**
     * Der Sturm hat das Holz von den Bäumen gebrochen - es wurde noch nicht gesammelt.
     */
    AUF_DEM_BODEN,
    /**
     * Das Holz wurde gesammelt.
     */
    GESAMMELT,
    /**
     * Das Holz wurde in handliche Stücke gebrochen
     */
    IN_STUECKE_GEBROCHEN
}
