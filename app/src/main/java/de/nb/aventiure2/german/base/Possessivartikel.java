package de.nb.aventiure2.german.base;

import com.google.common.collect.ImmutableMap;

import java.util.Map;
import java.util.Objects;

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
            alleGenera("mein", "unser"),
            P2,
            alleGenera("dein", "euer"),
            P3,
            ImmutableMap.of(
                    M, new Possessivartikel(M, "sein"),
                    F, new Possessivartikel(F, "ihr"),
                    N, new Possessivartikel(N, "sein"),
                    PL_MFN, new Possessivartikel(PL_MFN, "ihr"))
    );

    private static final Map<NumerusGenus, Endungen> ALL_ENDUNGEN = ImmutableMap.of(
            M, new Endungen("", "em", "en"),
            F, new Endungen("e", "er"),
            N, new Endungen("", "em"),
            PL_MFN, new Endungen("e", "en"));


    /**
     * Numerus und Genus des Bezugsnomens (also des Worts, auf das sich dieser
     * Possessivartikel bezieht).
     */
    private final NumerusGenus numerusGenusBezugsnomen;

    private final String stamm;

    private static Map<NumerusGenus, Possessivartikel>
    alleGenera(final String stammSg, final String stammPl) {
        return ImmutableMap.of(
                // "mein" hat eigentlich kein Genus.
                M, new Possessivartikel(M, stammSg),
                F, new Possessivartikel(F, stammSg),
                N, new Possessivartikel(N, stammSg),
                PL_MFN, new Possessivartikel(PL_MFN, stammPl));
    }

    private Possessivartikel(final NumerusGenus numerusGenusBezugsnomen, final String stamm) {
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

    private static Endungen getEndungen(final NumerusGenus numerusGenusBezugsnomen) {
        return ALL_ENDUNGEN.get(numerusGenusBezugsnomen);
    }

    public static class FlexionsSpalte implements DeklinierbarePhrase {
        private final Flexionsreihe flexionsreihe;

        FlexionsSpalte(final String stamm, final NumerusGenus numerusGenus) {
            flexionsreihe = getEndungen(numerusGenus).buildFlexionsreihe(stamm);
        }

        @Override
        public String nomStr() {
            return flexionsreihe.nom();
        }

        @Override
        public String datStr() {
            return flexionsreihe.dat();
        }

        @Override
        public String akkStr() {
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

}
