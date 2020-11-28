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
     * Es liegt eine Wiederholung vor. Der SC macht eine Handlung noch einmal /
     * versucht sie zum zweiten (dritten...) Mal. (Z.B.: Der SC hält jemandem, der etwas
     * nicht angenommen hat, erneut etwas).
     */
    WIEDERHOLUNG,
    /**
     * Es liegt eine Fortsetzung vor. Der SC setzt eine bereits begonnene Handlung
     * fort. (Z.B.: Er wartet weiter.)
     */
    FORTSETZUNG
}
