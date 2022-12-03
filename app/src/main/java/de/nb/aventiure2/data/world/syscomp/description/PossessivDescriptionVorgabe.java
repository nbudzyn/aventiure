package de.nb.aventiure2.data.world.syscomp.description;

/**
 * Gibt vor, in wieweit eine <i>Possessiv-Beschreibung</i> gewünscht ist.
 */
public enum PossessivDescriptionVorgabe {
    /**
     * Es ist <i>keine</i> Possessivbeschreibung gewünscht, sondern nur etwas wie "die Haare".
     */
    NICHT_POSSESSIV(false),
    /**
     * Es ist nach Möglichkeite eine anphorische Beschreibung mit Possessivpronomen
     * (Possessivartikel) gewünscht ("ihre Haare"), sonst <i>keine</i> Possessivbeschreibung
     * (also <i>nicht</i> "Rapunzels Haare"), sondern nur etwas wie "die Haare".
     * Hiermit kann man Sätze vermeiden wie ?"Rapunzel kämmt Rapunzels Haare" -
     * wo sich z.B. der possessive Bezug auf das Subjekt bezieht.
     */
    GENITIVATTRIBUT_VERBOTEN(true),
    /**
     * Es ist nach Möglichkeit eine anaphorische Possessivbeschreibung mit Possessivpronomen
     * gewünscht ("ihre Haare"), sonst möglichst eine Possessivbeschreibung mit Genitivattribut
     * ("Rapunzels Haare"), und wenn auch das nicht möglich ist, eine Beschreibung wie
     * "die Haare".
     */
    ALLES_ERLAUBT(true);

    /**
     * Ob eine der gewünschten Alternativen eine anphorische Beschreibung mit Possessivpronomen
     * (Possessivartikel) ist ("ihre Haare"). Relevant für Optimierungen.
     */
    private final boolean evtlPossessivartikel;

    PossessivDescriptionVorgabe(final boolean evtlPossessivartikel) {
        this.evtlPossessivartikel = evtlPossessivartikel;
    }

    public boolean evtlPossessivartikel() {
        return evtlPossessivartikel;
    }
}
