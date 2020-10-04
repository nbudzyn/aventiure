package de.nb.aventiure2.german.description;

public enum Kohaerenzrelation {
    // Vgl. Averintsvea-Klisch, "Textkonhärenz" S. 18ff
    /**
     * Die Relation ist entweder explizit als Text schon ausgedrückt oder
     * sie wird implizit verstanden.
     */
    VERSTEHT_SICH_VON_SELBST,
    /**
     * Es besteht eine Diskontinuität.
     */
    DISKONTINUITAET,
    /**
     * Es liegt eine Wiederholung vor.
     */
    WIEDERHOLUNG
}
