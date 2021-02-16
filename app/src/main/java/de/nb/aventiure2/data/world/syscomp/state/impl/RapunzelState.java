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
    PAUSED_BEFORE_HAARE_VOM_TURM_HERUNTERGELASSEN,
    DO_START_HAARE_VOM_TURM_HERUNTERLASSEN,
    HAARE_VOM_TURM_HERUNTERGELASSEN,
    HAT_NACH_KUGEL_GEFRAGT,
    HAT_NACH_LIEBSTER_JAHRESZEIT_GEFRAGT,
    HAT_NACH_HERKUNFT_DER_GOLDENEN_KUGEL_GEFRAGT,
    HAT_NACH_BIENEN_UND_BLUMEN_GEFRAGT;
}
