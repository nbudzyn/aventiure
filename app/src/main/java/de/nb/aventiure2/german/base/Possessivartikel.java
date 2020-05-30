package de.nb.aventiure2.german.base;

import com.google.common.collect.ImmutableMap;

import java.util.Map;

import static de.nb.aventiure2.german.base.NumerusGenus.F;
import static de.nb.aventiure2.german.base.NumerusGenus.M;
import static de.nb.aventiure2.german.base.NumerusGenus.N;
import static de.nb.aventiure2.german.base.NumerusGenus.PL_MFN;

public class Possessivartikel {
    /**
     * Numerus und Genus des Bezugsnomens (also des Worts, auf das sich dieser
     * Possessivartikel bezieht).
     */
    private final NumerusGenus numerusGenusBezugsnomen;

    private final String stamm;

    private static final Map<NumerusGenus, Possessivartikel> ALL = ImmutableMap.of(
            M, new Possessivartikel(M, "sein"),
            F, new Possessivartikel(F, "ihr"),
            N, new Possessivartikel(N, "sein"),
            PL_MFN, new Possessivartikel(PL_MFN, "ihr"));

    public Possessivartikel(final NumerusGenus numerusGenusBezugsnomen, final String stamm) {
        this.numerusGenusBezugsnomen = numerusGenusBezugsnomen;
        this.stamm = stamm;
    }

    public static Possessivartikel get(final NumerusGenus numerusGenusBezugsnomen) {
        return ALL.get(numerusGenusBezugsnomen);
    }

    public NumerusGenus getNumerusGenusBezugsnomen() {
        return numerusGenusBezugsnomen;
    }

    /**
     * Gibt die {@link FlexionsSpalte} zurück, die vor dieser substantivischen Phrase
     * (attributiv) notwendig ist.
     */
    public FlexionsSpalte vor(final SubstantivischePhrase substantivischePhrase) {
        return new FlexionsSpalte(stamm, substantivischePhrase.getNumerusGenus());
    }

    /**
     * Gibt die {@link FlexionsSpalte} zurück, die vor einem Nomen in diesem Numerus und
     * Genus (attributiv) notwendig ist.
     */
    public FlexionsSpalte vor(final NumerusGenus numerusGenus) {
        return new FlexionsSpalte(stamm, numerusGenus);
    }

    public static class FlexionsSpalte extends EinreihigDeklinierbarePhrase {
        /**
         * Numerus und Genus des Nomens, <i>vor</i> dem der Possessivartikel
         * (attributiv) steht.
         */
        private final NumerusGenus numerusGenus;

        FlexionsSpalte(final String stamm, final NumerusGenus numerusGenus) {
            super(numerusGenus,
                    stamm + getEndungen(numerusGenus).nominativ,
                    stamm + getEndungen(numerusGenus).dativ,
                    stamm + getEndungen(numerusGenus).akkusativ);

            this.numerusGenus =
                    getEndungen(numerusGenus).numerusGenus;
        }

        private static Endungen getEndungen(final NumerusGenus numerusGenusBezugsnomen) {
            return Endungen.get(numerusGenusBezugsnomen);
        }
    }

    private static class Endungen {
        private static final Map<NumerusGenus, Endungen> ALL = ImmutableMap.of(
                M, new Endungen(M, "", "em", "en"),
                F, new Endungen(F, "e", "er"),
                N, new Endungen(N, "", "em"),
                PL_MFN, new Endungen(PL_MFN, "e", "en"));


        private final NumerusGenus numerusGenus;
        private final String nominativ;
        private final String dativ;
        private final String akkusativ;

        private static Endungen get(final NumerusGenus numerusGenus) {
            return ALL.get(numerusGenus);
        }

        private Endungen(final NumerusGenus numerusGenus,
                         final String nominativAkkusativ, final String dativ) {
            this(numerusGenus, nominativAkkusativ, dativ, nominativAkkusativ);
        }

        private Endungen(final NumerusGenus numerusGenus,
                         final String nominativ, final String dativ, final String akkusativ) {
            this.numerusGenus = numerusGenus;
            this.nominativ = nominativ;
            this.dativ = dativ;
            this.akkusativ = akkusativ;
        }
    }
}
