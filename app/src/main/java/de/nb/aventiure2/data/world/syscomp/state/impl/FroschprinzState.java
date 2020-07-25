package de.nb.aventiure2.data.world.syscomp.state.impl;

import static de.nb.aventiure2.data.world.syscomp.state.impl.FroschprinzState.Gestalt.FROSCH;
import static de.nb.aventiure2.data.world.syscomp.state.impl.FroschprinzState.Gestalt.MENSCH;

/**
 * A state the Froschprinz can be in.
 */
public enum FroschprinzState {
    UNAUFFAELLIG(FROSCH),
    HAT_SC_HILFSBEREIT_ANGESPROCHEN(FROSCH),
    HAT_NACH_BELOHNUNG_GEFRAGT(FROSCH),
    HAT_FORDERUNG_GESTELLT(FROSCH),

    // STORY Oder unnötig? Zurzeit verlässt der
    //  Frosch den Brunnen nicht und lässt sich auch nicht mitnehmen,
    //  bis er nicht die Dinge herausgeholt hat
    AUF_DEM_WEG_ZUM_BRUNNEN_UM_DINGE_HERAUSZUHOLEN(FROSCH),

    ERWARTET_VON_SC_EINLOESUNG_SEINES_VERSPRECHENS(FROSCH),
    AUF_DEM_WEG_ZUM_SCHLOSSFEST(FROSCH),
    WARTET_AUF_SC_BEIM_SCHLOSSFEST(FROSCH),
    HAT_HOCHHEBEN_GEFORDERT(FROSCH),
    BEIM_SCHLOSSFEST_AUF_TISCH_WILL_ZUSAMMEN_ESSEN(FROSCH),
    ZURUECKVERWANDELT_IN_VORHALLE(MENSCH),
    ZURUECKVERWANDELT_SCHLOSS_VORHALLE_VERLASSEN(MENSCH);

    public enum Gestalt {
        FROSCH, MENSCH
    }

    private final Gestalt gestalt;

    FroschprinzState(final Gestalt gestalt) {
        this.gestalt = gestalt;
    }

    public boolean hasGestalt(final Gestalt gestalt) {
        return this.gestalt == gestalt;
    }

    public Gestalt getGestalt() {
        return gestalt;
    }
}
