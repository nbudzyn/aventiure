package de.nb.aventiure2.data.world.syscomp.storingplace;

/**
 * Wie sehr ein {@link ILocationGO} drinnen oder draußen ist. Davon hängt z.B. ab, wie sehr der SC
 * der Witterung ausgesetzt ist.
 */
public enum DrinnenDraussen {
    /**
     * Draußen und unter offenem Himmel.Nach oben weit offen und ungeschützt.
     * <p>
     * Z.B. auf einer Wiese, auf einem kahlen Hügel,
     * auf einem Tisch, der vor einem Haus steht etc.
     */
    DRAUSSEN_UNTER_OFFENEM_HIMMEL(true),
    /**
     * Draußen, aber nicht unter offenem Himmel, sondern etwas geschützt, dass der
     * offene Himmel nicht oder kaum zu sehen ist.
     * <p>
     * Z.B. im Wald, unter einem Felsvorsprung, auf einem Tisch im Wald o.Ä.
     */
    DRAUSSEN_GESCHUETZT(true),
    /**
     * Drinnen in einem Raum oder im wesentlichen geschlossenen Behältnis.
     * <p>
     * Z.B. in einem Innenraum, in einem Raum mit Fenstern, in einem geschlossenen Behältnisse
     * unter einer Bett, auf einem Tisch in einem Innenraum etc.
     */
    DRINNEN(false);

    /**
     * Draußen oder nicht (dann also drinnen, zumindest im Sinne von
     * "in etwas geschlossenem")
     */
    private final boolean draussen;

    DrinnenDraussen(final boolean draussen) {
        this.draussen = draussen;
    }

    public boolean isDraussen() {
        return draussen;
    }
}
