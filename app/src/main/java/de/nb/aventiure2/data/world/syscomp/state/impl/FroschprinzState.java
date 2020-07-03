package de.nb.aventiure2.data.world.syscomp.state.impl;

/**
 * A state the Froschprinz can be in.
 */
public enum FroschprinzState {
    UNAUFFAELLIG,
    HAT_SC_HILFSBEREIT_ANGESPROCHEN,
    HAT_NACH_BELOHNUNG_GEFRAGT,
    HAT_FORDERUNG_GESTELLT,

    // STORY Oder unnötig? Zurzeit verlässt der
    //  Frosch den Brunnen nicht und lässt sich auch nicht mitnehmen,
    //  bis er nicht die Dinge herausgeholt hat
    AUF_DEM_WEG_ZUM_BRUNNEN_UM_DINGE_HERAUSZUHOLEN,

    ERWARTET_VON_SC_EINLOESUNG_SEINES_VERSPRECHENS,
    AUF_DEM_WEG_ZUM_SCHLOSSFEST,
    WARTET_AUF_SC_BEIM_SCHLOSSFEST,
    HAT_HOCHHEBEN_GEFORDERT,
    BEIM_SCHLOSSFEST_AUF_TISCH_WILL_ZUSAMMEN_ESSEN,
    ZURUECKVERWANDELT_IN_VORHALLE,
    ZURUECKVERWANDELT_SCHLOSS_VORHALLE_VERLASSEN
}
