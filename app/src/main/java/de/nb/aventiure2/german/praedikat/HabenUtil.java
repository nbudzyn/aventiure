package de.nb.aventiure2.german.praedikat;

import de.nb.aventiure2.german.base.Numerus;
import de.nb.aventiure2.german.base.Person;

/**
 * Hilfsmethoden und Konstanten zum Verb "haben".
 */
public class HabenUtil {
    public static final Verb VERB =
            new Verb("haben",
                    "habe", "hast", "hat",
                    "habt",
                    Perfektbildung.HABEN, "gehabt");

    private HabenUtil() {
    }

    private static String hatHaben(final Numerus numerus) {
        return VERB.getPraesensOhnePartikel(Person.P3, numerus);
    }
}
