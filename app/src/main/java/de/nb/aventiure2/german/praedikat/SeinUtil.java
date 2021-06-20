package de.nb.aventiure2.german.praedikat;

import java.util.Collection;

import de.nb.aventiure2.german.base.Numerus;
import de.nb.aventiure2.german.base.NumerusGenus;

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

    public static String istSind(final NumerusGenus numerusGenus) {
        return istSind(numerusGenus.getNumerus());
    }

    public static String istSind(final Collection<?> subjektEntitaeten) {
        return istSind(subjektEntitaeten.size());
    }

    private static String istSind(final int number) {
        return istSind(Numerus.forNumber(number));
    }

    private static String istSind(final Numerus numerus) {
        // FIXME Denkfehler: Auch bei nur einem Objekt kann es Pural sein ("die Äste")!
        //  Vgl.  world.getDescriptionSingleOrCollective(objectsInDenBrunnenGefallen)

        return requireNonNull(VERB.getPraesensOhnePartikel(P3, numerus));
    }
}
