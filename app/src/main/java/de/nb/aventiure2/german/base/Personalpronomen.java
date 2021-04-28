package de.nb.aventiure2.german.base;

import androidx.annotation.Nullable;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;

import java.util.Map;
import java.util.Objects;

import static de.nb.aventiure2.german.base.Flexionsreihe.fr;
import static de.nb.aventiure2.german.base.Kasus.AKK;
import static de.nb.aventiure2.german.base.Kasus.NOM;
import static de.nb.aventiure2.german.base.Numerus.SG;
import static de.nb.aventiure2.german.base.NumerusGenus.F;
import static de.nb.aventiure2.german.base.NumerusGenus.M;
import static de.nb.aventiure2.german.base.NumerusGenus.N;
import static de.nb.aventiure2.german.base.NumerusGenus.PL_MFN;
import static de.nb.aventiure2.german.base.Person.P1;
import static de.nb.aventiure2.german.base.Person.P2;
import static de.nb.aventiure2.german.base.Person.P3;
import static java.util.Objects.requireNonNull;

public class Personalpronomen extends SubstantivischesPronomenMitVollerFlexionsreiheEinzelne {
    private static final Map<Person, Map<NumerusGenus, Personalpronomen>> ALL = ImmutableMap.of(
            P1,
            alleGenera(P1,
                    "ich", "mir", "mich", "wir", "uns"),
            P2,
            alleGenera(P2,
                    "du", "dir", "dich", "ihr", "euch"),
            P3,
            ImmutableMap.of(
                    M, new Personalpronomen(P3, M,
                            fr("er", "ihm", "ihn")),
                    F, new Personalpronomen(P3, F,
                            fr("sie", "ihr")),
                    N, new Personalpronomen(P3, N,
                            fr("es", "ihm")),
                    PL_MFN, new Personalpronomen(P3, PL_MFN,
                            fr("sie", "ihnen")))
    );

    public static final Personalpronomen EXPLETIVES_ES = get(Person.P3, NumerusGenus.N);

    private final Person person;

    public static boolean isPersonalpronomenEs(
            final SubstPhrOderReflexivpronomen substPhrOderReflexivpronomen,
            final KasusOderPraepositionalkasus kasusOderPraepositionalkasus) {
        if (!(substPhrOderReflexivpronomen instanceof Personalpronomen)) {
            return false;
        }

        final Personalpronomen personalpronomen = (Personalpronomen) substPhrOderReflexivpronomen;

        return personalpronomen.getPerson() == P3
                && personalpronomen.getNumerusGenus() == N
                && (kasusOderPraepositionalkasus == NOM || kasusOderPraepositionalkasus == AKK);
    }

    private static Map<NumerusGenus, Personalpronomen>
    alleGenera(final Person person,
               final String nomSg, final String datSg, final String akkSg,
               final String nomPl, final String datAkkPl) {
        return ImmutableMap.of(
                // Auch "ich" hat ein Genus, es ist allerdings nicht sichtbar (nicht overt):
                // - "ich" (m) hat das "Relativpronomen" "der ich"
                // - "ich" (f) hat das "Relativpronomen" "die ich"
                M, new Personalpronomen(person, M, fr(nomSg, datSg, akkSg)),
                F, new Personalpronomen(person, F, fr(nomSg, datSg, akkSg)),
                N, new Personalpronomen(person, N, fr(nomSg, datSg, akkSg)),
                PL_MFN, new Personalpronomen(person, PL_MFN, fr(nomPl, datAkkPl))
        );
    }

    /**
     * Gibt das passende Personalpronomen zurück - ohne Bezugsobjekt
     */
    public static Personalpronomen get(final Person person, final NumerusGenus numerusGenus) {
        return get(person, numerusGenus, null);
    }

    public static Personalpronomen get(final Person person, final NumerusGenus numerusGenus,
                                       @Nullable final IBezugsobjekt bezugsobjekt) {
        final Personalpronomen ohneBezugsobjekt = requireNonNull(ALL.get(person)).get(numerusGenus);

        return requireNonNull(ohneBezugsobjekt).mitBezugsobjekt(bezugsobjekt);
    }

    /**
     * Fügt der substantivischen Phrase etwas hinzu wie "auch", "allein", "ausgerechnet",
     * "wenigstens" etc.
     */
    @Override
    public Personalpronomen ohneFokuspartikel() {
        return (Personalpronomen) super.ohneFokuspartikel();
    }

    /**
     * Fügt dem Personalpronomen etwas hinzu wie "auch", "allein", "ausgerechnet",
     * "wenigstens" etc. Es ist zu beachten, das "es" nicht phrasenfähig ist - statt
     * *"auch es" zu erzeugen, wird das "auch" in dem Fall verworfen (während "auch ihm"
     * erzeugt wird).
     */
    @Override
    public Personalpronomen mitFokuspartikel(@Nullable final String fokuspartikel) {
        if (Objects.equals(getFokuspartikel(), fokuspartikel)) {
            return this;
        }

        return new Personalpronomen(fokuspartikel, person, getNumerusGenus(),
                getFlexionsreihe(), getBezugsobjekt());
    }

    private Personalpronomen mitBezugsobjekt(@Nullable final IBezugsobjekt bezugsobjekt) {
        if (Objects.equals(getBezugsobjekt(), bezugsobjekt)) {
            return this;
        }

        return new Personalpronomen(getFokuspartikel(), person, getNumerusGenus(),
                getFlexionsreihe(), bezugsobjekt);
    }

    private Personalpronomen(final Person person,
                             final NumerusGenus numerusGenus,
                             final Flexionsreihe flexionsreihe) {
        this(null, person, numerusGenus, flexionsreihe, null);
    }

    private Personalpronomen(@Nullable final String fokuspartikel,
                             final Person person,
                             final NumerusGenus numerusGenus,
                             final Flexionsreihe flexionsreihe,
                             @Nullable final IBezugsobjekt bezugsobjekt) {
        super(fokuspartikel, numerusGenus, flexionsreihe,
                person == P3 ? bezugsobjekt : null);
        this.person = person;
    }

    @Override
    public final String imStr(final Kasus kasus) {
        if (getFokuspartikel() != null
                && isPersonalpronomenEs(this, kasus)) {
            return ohneFokuspartikel().imStr(kasus);
        }

        return super.imStr(kasus);
    }


    @Override
    public Personalpronomen persPron() {
        return ohneFokuspartikel();
    }

    @Override
    public Reflexivpronomen reflPron() {
        // P1 und P2 sind hier noch nicht vorgesehen
        return Reflexivpronomen.get(person, getNumerusGenus().getNumerus());
    }

    /**
     * "er, der..."
     */
    @Override
    public Relativpronomen relPron() {
        return Relativpronomen.get(person, getNumerusGenus(), getBezugsobjekt());
    }

    /**
     * "Er... sein..."
     */
    @Override
    public Possessivartikel possArt() {
        return Possessivartikel.get(person, getNumerusGenus());
    }

    @Override
    public boolean isUnbetontesPronomen() {
        return getFokuspartikel() == null;
    }

    @Override
    public Person getPerson() {
        return person;
    }

    public static void checkExpletivesEs(final SubstantivischePhrase subjekt) {
        checkExpletivesEs(subjekt.getPerson(), subjekt.getNumerus());

        Preconditions.checkArgument(subjekt.getNumerusGenus() == NumerusGenus.N,
                "Subjekt nicht Neutrum - ungültig für Wetterverben - keine expletives "
                        + "es: %s", subjekt.getNumerusGenus());

        Preconditions.checkArgument(subjekt.getBezugsobjekt() == null,
                "Subjekt hat ein Bezugsobjekt - ungültig für Wetterverben - keine expletives "
                        + "es: %s", subjekt.getBezugsobjekt());
    }

    public static void checkExpletivesEs(final Person person, final Numerus numerus) {
        if (person != P3) {
            throw new IllegalStateException(
                    "Ungültige Person für Wetterverben, keine expletives es: " + person);
        }

        if (numerus != SG) {
            throw new IllegalStateException(
                    "Ungültiger Numerus für Wetterverben, keine expletives es: " + numerus);
        }
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        final Personalpronomen that = (Personalpronomen) o;
        return person == that.person;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), person);
    }
}
