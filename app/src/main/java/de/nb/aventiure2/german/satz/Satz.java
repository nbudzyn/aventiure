package de.nb.aventiure2.german.satz;

import de.nb.aventiure2.german.base.SubstantivischePhrase;
import de.nb.aventiure2.german.praedikat.PraedikatOhneLeerstellen;

/**
 * Ein Satz.
 */
public class Satz {
    /**
     * Das Subjekt des Satzes.
     */
    private final SubstantivischePhrase subjekt;

    /**
     * Das Prädikat des Satzes, im Sinne des Verbs mit all seinen Ergänzungen und
     * Angabe - ohne das Subjekt.
     */
    private final PraedikatOhneLeerstellen praedikat;

    public Satz(final SubstantivischePhrase subjekt,
                final PraedikatOhneLeerstellen praedikat) {
        this.subjekt = subjekt;
        this.praedikat = praedikat;
    }
}
