package de.nb.aventiure2.german.base;

import com.google.common.collect.ImmutableMap;

import java.util.Map;
import java.util.Objects;

import static de.nb.aventiure2.german.base.Flexionsreihe.fr;
import static de.nb.aventiure2.german.base.NumerusGenus.F;
import static de.nb.aventiure2.german.base.NumerusGenus.M;
import static de.nb.aventiure2.german.base.NumerusGenus.N;
import static de.nb.aventiure2.german.base.NumerusGenus.PL_MFN;
import static de.nb.aventiure2.german.base.Person.P1;
import static de.nb.aventiure2.german.base.Person.P2;
import static de.nb.aventiure2.german.base.Person.P3;

public class Possessivartikel {
    private static final Map<Person, Map<NumerusGenus, Possessivartikel>> ALL = ImmutableMap.of(
            P1,
            alleGenera(P1, "mein", "unser"),
            P2,
            alleGenera(P2, "dein", "euer"),
            P3,
            ImmutableMap.of(
                    M, new Possessivartikel(P3, M, "sein"),
                    F, new Possessivartikel(P3, F, "ihr"),
                    N, new Possessivartikel(P3, N, "sein"),
                    PL_MFN, new Possessivartikel(P3, PL_MFN, "ihr"))
    );

    private final Person person;

    /**
     * Numerus und Genus des Bezugsnomens (also des Worts, auf das sich dieser
     * Possessivartikel bezieht).
     */
    private final NumerusGenus numerusGenusBezugsnomen;

    private final String stamm;

    private static Map<NumerusGenus, Possessivartikel>
    alleGenera(final Person person,
               final String stammSg, final String stammPl) {
        return ImmutableMap.of(
                // "mein" hat eigentlich kein Genus.
                M, new Possessivartikel(person, M, stammSg),
                F, new Possessivartikel(person, F, stammSg),
                N, new Possessivartikel(person, N, stammSg),
                PL_MFN, new Possessivartikel(person, PL_MFN, stammPl));
    }

    private Possessivartikel(final Person person,
                             final NumerusGenus numerusGenusBezugsnomen, final String stamm) {
        this.person = person;
        this.numerusGenusBezugsnomen = numerusGenusBezugsnomen;
        this.stamm = stamm;
    }

    public static Possessivartikel get(final Person person,
                                       final NumerusGenus numerusGenusBezugsnomen) {
        return ALL.get(person).get(numerusGenusBezugsnomen);
    }

    public NumerusGenus getNumerusGenusBezugsnomen() {
        return numerusGenusBezugsnomen;
    }

    /**
     * Gibt die {@link FlexionsSpalte} zurück, die vor dieser substantivischen Phrase
     * (attributiv) notwendig ist.
     */
    public FlexionsSpalte vor(final IErlaubtAttribute substantivischePhrase) {
        return new FlexionsSpalte(stamm, substantivischePhrase.getNumerusGenus());
    }

    /**
     * Gibt die {@link FlexionsSpalte} zurück, die vor einem Nomen in diesem Numerus und
     * Genus (attributiv) notwendig ist.
     */
    public FlexionsSpalte vor(final NumerusGenus numerusGenus) {
        return new FlexionsSpalte(stamm, numerusGenus);
    }

    public static class FlexionsSpalte implements DeklinierbarePhrase {
        private final Flexionsreihe flexionsreihe;

        FlexionsSpalte(final String stamm, final NumerusGenus numerusGenus) {
            flexionsreihe = getEndungen(numerusGenus).buildFlexionsreihe(stamm);
        }

        private static Endungen getEndungen(final NumerusGenus numerusGenusBezugsnomen) {
            return Endungen.get(numerusGenusBezugsnomen);
        }

        @Override
        public String nom() {
            return flexionsreihe.nom();
        }

        @Override
        public String dat() {
            return flexionsreihe.dat();
        }

        @Override
        public String akk() {
            return flexionsreihe.akk();
        }

        @Override
        public boolean equals(final Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            final FlexionsSpalte that = (FlexionsSpalte) o;
            return flexionsreihe.equals(that.flexionsreihe);
        }

        @Override
        public int hashCode() {
            return Objects.hash(flexionsreihe);
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

        private Flexionsreihe buildFlexionsreihe(final String stamm) {
            return fr(
                    stamm + nominativ,
                    stamm + dativ,
                    stamm + akkusativ
            );
        }
    }
}
