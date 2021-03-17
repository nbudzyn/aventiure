package de.nb.aventiure2.german.praedikat;

import de.nb.aventiure2.german.base.Numerus;
import de.nb.aventiure2.german.base.Person;

import static java.util.Objects.requireNonNull;

/**
 * Hilfsmethoden und Konstanten zum Verb "haben".
 */
class HabenUtil {
    static final Verb VERB =
            new Verb("haben",
                    "habe", "hast", "hat",
                    "habt",
                    Perfektbildung.HABEN, "gehabt");

    private HabenUtil() {
    }

    private static String hatHaben(final Numerus numerus) {
        return requireNonNull(VERB.getPraesensOhnePartikel(Person.P3, numerus));
    }
}
