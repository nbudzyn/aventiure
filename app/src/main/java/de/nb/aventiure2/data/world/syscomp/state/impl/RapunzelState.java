package de.nb.aventiure2.data.world.syscomp.state.impl;

/**
 * A state Rapunzel can be in.
 */
public enum RapunzelState {
    /**
     * Hat noch nicht mit dem Singen angefangen
     */
    UNAEUFFAELLIG,
    /**
     * Singt gerade nicht
     */
    NORMAL,
    SINGEND,
    HAARE_VOM_TURM_HERUNTERGELASSEN,
    HAT_NACH_KUGEL_GEFRAGT,
    HAT_NACH_LIEBSTER_JAHRESZEIT_GEFRAGT;
}
