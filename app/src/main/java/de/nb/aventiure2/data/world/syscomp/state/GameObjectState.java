package de.nb.aventiure2.data.world.syscomp.state;

/**
 * A state a game object can be in.
 */
public enum GameObjectState {
    // ALLGEMEIN
    NORMAL,
    UNAUFFAELLIG,

    // SCHLOSSWACHE
    AUFMERKSAM,

    // FROSCHPRINZ
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
    WILL_BEIM_SCHLOSSFEST_ZUSAMMEN_ESSEN,

    // SCHLOSSFEST
    NOCH_NICHT_BEGONNEN,
    BEGONNEN
}
