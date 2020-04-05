package de.nb.aventiure2.data.world.room;

public enum AvRoom {
    SCHLOSS_VORHALLE(ObjectLocationMode.EIN_TISCH),
    DRAUSSEN_VOR_DEM_SCHLOSS,
    IM_WALD_NAHE_DEM_SCHLOSS(ObjectLocationMode.WALDWEG),
    ABZWEIG_IM_WALD(ObjectLocationMode.WALDWEG),
    VOR_DER_HUETTE_IM_WALD(ObjectLocationMode.VOR_DER_HUETTE),
    HUETTE_IM_WALD(ObjectLocationMode.HOLZTISCH),
    BETT_IN_DER_HUETTE_IM_WALD(ObjectLocationMode.NEBEN_DIR_IM_BETT),
    HINTER_DER_HUETTE(ObjectLocationMode.UNTER_DEM_BAUM),
    IM_WALD_BEIM_BRUNNEN(ObjectLocationMode.GRAS_NEBEN_DEM_BRUNNEN),
    UNTEN_IM_BRUNNEN(ObjectLocationMode.AM_GRUNDE_DES_BRUNNENS),
    WALDWILDNIS_HINTER_DEM_BRUNNEN(ObjectLocationMode.MATSCHIGER_WALDBODENN);

    private final ObjectLocationMode locationMode;

    AvRoom() {
        this(ObjectLocationMode.BODEN);
    }

    AvRoom(final ObjectLocationMode locationMode) {
        this.locationMode = locationMode;
    }

    public ObjectLocationMode getLocationMode() {
        return locationMode;
    }
}
