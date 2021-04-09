package de.nb.aventiure2.data.world.syscomp.storingplace;

/**
 * In wieweit ein {@link ILocationGO} offen oder geschlossen ist. Besonders relevant für die
 * Ermittlung der Lichtverhältnisse, wie sehr der SC der Witterung ausgesetzt ist
 * (ist der SC drinnen oder draußen?) etc.
 */
public enum Geschlossenheit {
    /**
     * Nach oben weit offen und ungeschützt.
     * <p>
     * Z.B. auf einer Wiese, auf einem kahlen Hügel, eine oben offene Kiste, auf einem Tisch etc.
     */
    NACH_OBEN_WEITGEHEND_OFFEN_UND_UNGESCHUETZT(true),
    /**
     * Grundsätzlich offen, aber nicht weit offen direkt nach oben.
     * <p>
     * Z.B. im Wald, unter einem Felsvorsprung oder in einem Regal
     */
    MAN_KANN_HINEINSEHEN_UND_LICHT_SCHEINT_HINEIN_UND_HINAUS(true),
    /**
     * Im Wesentlichen geschlossen: Man kann nicht direkt hineinsehen (außer man kniet sich hin,
     * öffnet etwas o.Ä.) und Licht scheint nicht hinein (außer man leuchtet ganz gezieht hinein)
     * oder hinaus.
     * <p>
     * Z.B. Innenräume, auch Räume mit Fenstern, geschlossene Behältnisse, unter dem Bett etc.
     */
    MAN_KANN_NICHT_DIREKT_HINEINSEHEN_UND_LICHT_SCHEINT_NICHT_HINEIN_ODER_HINAUS(false);

    private final boolean manKannHineinsehenUndLichtScheintHineinUndHinaus;

    Geschlossenheit(final boolean manKannHineinsehenUndLichtScheintHineinUndHinaus) {
        this.manKannHineinsehenUndLichtScheintHineinUndHinaus =
                manKannHineinsehenUndLichtScheintHineinUndHinaus;
    }

    public boolean manKannHineinsehenUndLichtScheintHineinUndHinaus() {
        return manKannHineinsehenUndLichtScheintHineinUndHinaus;
    }
}
