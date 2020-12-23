package de.nb.aventiure2.german.praedikat;

import de.nb.aventiure2.german.base.Numerus;
import de.nb.aventiure2.german.base.NumerusGenus;
import de.nb.aventiure2.german.base.Person;

import static de.nb.aventiure2.german.base.Numerus.SG;

/**
 * Hilfsmethoden und Konstanten zum Verb "haben".
 */
public class HabenUtil {
    static final Verb VERB =
            new Verb("haben", "hast", Perfektbildung.HABEN, "gehabt");

    private HabenUtil() {
    }

    public static String habenVerbform(final Person person, final Numerus numerus) {
        switch (person) {
            case P1:
                return numerus == SG ? "habe" : "haben";
            case P2:
                return numerus == SG ? VERB.getDuForm() : "habt";
            case P3:
                return hatHaben(numerus);
            default:
                throw new IllegalStateException("Unexpected Person: " + person);
        }
    }

    public static String hatHaben(final NumerusGenus numerusGenus) {
        return hatHaben(numerusGenus.getNumerus());
    }

    private static String hatHaben(final Numerus numerus) {
        return numerus == SG ? "hat" : "haben";
    }
}
