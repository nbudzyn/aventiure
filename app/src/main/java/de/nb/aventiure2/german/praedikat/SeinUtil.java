package de.nb.aventiure2.german.praedikat;

import de.nb.aventiure2.german.base.NumerusGenus;

import static de.nb.aventiure2.german.base.NumerusGenus.PL_MFN;

/**
 * Hilfsmethoden zum Verb "sein".
 */
public class SeinUtil {
    private SeinUtil() {
    }

    public static String istSind(final NumerusGenus numerusGenus) {
        if (numerusGenus == PL_MFN) {
            return "sein";
        }

        return "ist";
    }
}
