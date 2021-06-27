package de.nb.aventiure2.german.praedikat;

import de.nb.aventiure2.german.base.Numerus;
import de.nb.aventiure2.german.base.NumerusGenus;
import de.nb.aventiure2.german.base.SubstantivischePhrase;

import static de.nb.aventiure2.german.base.Person.P3;
import static java.util.Objects.requireNonNull;

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


    public static String istSind(final SubstantivischePhrase substantivischePhrase) {
        return istSind(substantivischePhrase.getNumerusGenus());
    }

    public static String istSind(final NumerusGenus numerusGenus) {
        return istSind(numerusGenus.getNumerus());
    }

    private static String istSind(final Numerus numerus) {
        return requireNonNull(VERB.getPraesensOhnePartikel(P3, numerus));
    }
}
