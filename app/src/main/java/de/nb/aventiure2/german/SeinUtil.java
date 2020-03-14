package de.nb.aventiure2.german;

import static de.nb.aventiure2.german.NumerusGenus.PL;

/**
 * Hilfsmethoden zum Verb "sein".
 */
public class SeinUtil {
    private SeinUtil() {
    }

    public static String istSind(final NumerusGenus numerusGenus) {
        if (numerusGenus == PL) {
            return "sein";
        }

        return "ist";
    }
}
