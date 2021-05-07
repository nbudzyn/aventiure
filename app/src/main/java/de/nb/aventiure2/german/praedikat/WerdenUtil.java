package de.nb.aventiure2.german.praedikat;

/**
 * Hilfsmethoden und Konstanten zum Verb "werden".
 */
public class WerdenUtil {
    public static final Verb VERB =
            new Verb("werden",
                    "werde", "wirst",
                    "wird",
                    "werden", "werdet",
                    null, Perfektbildung.SEIN, "geworden");

    private WerdenUtil() {
    }
}
