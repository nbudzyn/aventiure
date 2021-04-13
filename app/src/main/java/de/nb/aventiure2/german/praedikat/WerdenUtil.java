package de.nb.aventiure2.german.praedikat;

/**
 * Hilfsmethoden und Konstanten zum Verb "werden".
 */
class WerdenUtil {
    static final Verb VERB =
            new Verb("werden",
                    "werde", "wirst",
                    "wird",
                    "werden", "werdet",
                    null, Perfektbildung.SEIN, "geworden");

    private WerdenUtil() {
    }
}
