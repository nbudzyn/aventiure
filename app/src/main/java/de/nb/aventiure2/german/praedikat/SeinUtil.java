package de.nb.aventiure2.german.praedikat;

import de.nb.aventiure2.german.base.Numerus;
import de.nb.aventiure2.german.base.NumerusGenus;

import static de.nb.aventiure2.german.base.Person.P3;

/**
 * Hilfsmethoden und Konstanten zum Verb "sein".
 */
public class SeinUtil {
    public static final Verb VERB =
            new Verb("sein",
                    "bin", "bist", "ist",
                    "sind", "seid",
                    null, Perfektbildung.SEIN, "gewesen");

    private SeinUtil() {
    }

    public static String istSind(final NumerusGenus numerusGenus) {
        return istSind(numerusGenus.getNumerus());
    }

    public static String istSind(final Numerus numerus) {
        return VERB.getPraesensOhnePartikel(P3, numerus);
    }
}
