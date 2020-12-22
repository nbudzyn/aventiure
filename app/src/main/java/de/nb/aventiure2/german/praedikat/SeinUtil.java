package de.nb.aventiure2.german.praedikat;

import de.nb.aventiure2.german.base.Numerus;
import de.nb.aventiure2.german.base.NumerusGenus;
import de.nb.aventiure2.german.base.Person;

import static de.nb.aventiure2.german.base.Numerus.SG;

/**
 * Hilfsmethoden zum Verb "sein".
 */
public class SeinUtil {
    private SeinUtil() {
    }

    public static String seinVerbform(final Person person, final Numerus numerus) {
        switch (person) {
            case P1:
                return numerus == SG ? "bin" : "sind";
            case P2:
                return numerus == SG ? "bist" : "seid";
            case P3:
                return istSind(numerus);
            default:
                throw new IllegalStateException("Unexpected Person: " + person);
        }
    }

    public static String istSind(final NumerusGenus numerusGenus) {
        return istSind(numerusGenus.getNumerus());
    }

    public static String istSind(final Numerus numerus) {
        return numerus == SG ? "ist" : "sind";
    }
}
