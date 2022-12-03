package de.nb.aventiure2.german.base;

import static java.util.Objects.requireNonNull;
import static de.nb.aventiure2.german.base.Belebtheit.UNBELEBT;
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

import androidx.annotation.Nullable;

import com.google.common.collect.ImmutableMap;

import java.util.Map;
import java.util.Objects;

public class Personalpronomen extends
        SubstantivischesPronomenMitVollerFlexionsreiheEinzelneKomplexe {
    private static final Map<Person, Map<NumerusGenus, Personalpronomen>> ALL_UNBELEBT =
            ImmutableMap.of(
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

    public static final Personalpronomen EXPLETIVES_ES = get(Person.P3, NumerusGenus.N, UNBELEBT);

    private final Person person;

    public static boolean isPersonalpronomenEs(
            final SubstPhrOderReflexivpronomen substPhrOderReflexivpronomen,
            final KasusOderPraepositionalkasus kasusOderPraepositionalkasus) {
        if (!(substPhrOderReflexivpronomen instanceof Personalpronomen)) {
            return false;
        }

        return isEs((Personalpronomen) substPhrOderReflexivpronomen, kasusOderPraepositionalkasus);
    }

    private static boolean isEs(final Personalpronomen personalpronomen,
                                final KasusOderPraepositionalkasus kasusOderPraepositionalkasus) {
        return personalpronomen.getPerson() == P3
                && personalpronomen.getNumerusGenus() == N
                && (kasusOderPraepositionalkasus == NOM || kasusOderPraepositionalkasus == AKK)
                && personalpronomen.getFokuspartikel() == null
                && personalpronomen.getNegationspartikelphrase() == null;
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
    public static Personalpronomen get(final Person person, final NumerusGenus numerusGenus,
                                       final Belebtheit belebtheit) {
        return get(person, numerusGenus, belebtheit, null);
    }

    public static Personalpronomen get(final Person person, final NumerusGenus numerusGenus,
                                       final Belebtheit belebtheit,
                                       @Nullable final IBezugsobjekt bezugsobjekt) {
        final Personalpronomen unbelebtOhneBezugsobjekt =
                requireNonNull(ALL_UNBELEBT.get(person)).get(numerusGenus);

        return requireNonNull(unbelebtOhneBezugsobjekt)
                .mitBelebtheit(belebtheit)
                .mitBezugsobjekt(bezugsobjekt);
    }

    public boolean isP2SgBelebt() {
        return person == P2 && getNumerus() == SG && isBelebt();
    }

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

        return new Personalpronomen(fokuspartikel, getNegationspartikelphrase(), person,
                getNumerusGenus(),
                getFlexionsreihe(), getBelebtheit(), getBezugsobjekt());
    }

    @Override
    public Personalpronomen ohneNegationspartikelphrase() {
        if (getNegationspartikelphrase() == null) {
            return this;
        }

        return new Personalpronomen(getFokuspartikel(), null, person,
                getNumerusGenus(),
                getFlexionsreihe(), getBelebtheit(), getBezugsobjekt());
    }

    @Override
    public Personalpronomen neg(@Nullable final Negationspartikelphrase negationspartikelphrase,
                                final boolean moeglichstNegativIndefiniteWoerterVerwenden) {
        return new Personalpronomen(getFokuspartikel(), negationspartikelphrase, person,
                getNumerusGenus(),
                getFlexionsreihe(), getBelebtheit(), getBezugsobjekt());
    }

    private Personalpronomen mitBelebtheit(final Belebtheit belebtheit) {
        if (Objects.equals(getBelebtheit(), belebtheit)) {
            return this;
        }

        return new Personalpronomen(getFokuspartikel(), getNegationspartikelphrase(), person,
                getNumerusGenus(),
                getFlexionsreihe(), belebtheit, getBezugsobjekt());
    }

    private Personalpronomen mitBezugsobjekt(@Nullable final IBezugsobjekt bezugsobjekt) {
        if (Objects.equals(getBezugsobjekt(), bezugsobjekt)) {
            return this;
        }

        return new Personalpronomen(getFokuspartikel(), getNegationspartikelphrase(), person,
                getNumerusGenus(),
                getFlexionsreihe(), getBelebtheit(), bezugsobjekt);
    }

    private Personalpronomen(final Person person,
                             final NumerusGenus numerusGenus,
                             final Flexionsreihe flexionsreihe) {
        this(person, numerusGenus, flexionsreihe, UNBELEBT);
    }

    private Personalpronomen(final Person person,
                             final NumerusGenus numerusGenus,
                             final Flexionsreihe flexionsreihe,
                             final Belebtheit belebtheit) {
        this(null, person, numerusGenus, flexionsreihe, belebtheit, null);
    }

    private Personalpronomen(@Nullable final String fokuspartikel,
                             final Person person,
                             final NumerusGenus numerusGenus,
                             final Flexionsreihe flexionsreihe,
                             final Belebtheit belebtheit,
                             @Nullable final IBezugsobjekt bezugsobjekt) {
        this(fokuspartikel, null, person, numerusGenus, flexionsreihe,
                belebtheit, bezugsobjekt);
    }


    private Personalpronomen(@Nullable final String fokuspartikel,
                             @Nullable final Negationspartikelphrase negationspartikelphrase,
                             final Person person,
                             final NumerusGenus numerusGenus,
                             final Flexionsreihe flexionsreihe,
                             final Belebtheit belebtheit,
                             @Nullable final IBezugsobjekt bezugsobjekt) {
        super(fokuspartikel, negationspartikelphrase, numerusGenus, flexionsreihe,
                belebtheit, person == P3 ? bezugsobjekt : null);
        this.person = person;
    }

    @Override
    public final String imStr(final Kasus kasus) {
        // "es" mit Fokuspartikel sollte der Aufrufer vermeiden.
        // Auch "nicht es" sollte der Aufrufer vermeiden.

        return super.imStr(kasus);
    }

    @Override
    public Personalpronomen persPron() {
        return Personalpronomen.get(person, getNumerusGenus(), getBelebtheit(), getBezugsobjekt());
    }

    @Override
    public Reflexivpronomen reflPron() {
        // P1 und P2 sind hier noch nicht vorgesehen
        return Reflexivpronomen.get(
                new PraedRegMerkmale(person, getNumerusGenus().getNumerus(), getBelebtheit()));
    }

    /**
     * "er, der..."
     */
    @Override
    public Relativpronomen relPron() {
        return Relativpronomen.get(person, getNumerusGenus(), getBelebtheit(), getBezugsobjekt());
    }

    /**
     * "Er... sein..."
     */
    @Override
    public IArtikelworttypOderVorangestelltesGenitivattribut possArt() {
        return ArtikelwortFlexionsspalte.getPossessiv(person, getNumerusGenus());
    }

    @Override
    public boolean isUnbetontesPronomen() {
        return getFokuspartikel() == null;
    }

    @Override
    public Person getPerson() {
        return person;
    }

    public static void checkExpletivesEs(final SubstantivischePhrase substPhr) {
        if (!(substPhr instanceof Personalpronomen)) {
            throw new IllegalStateException("Kein expletives es: " + substPhr);
        }

        substPhr.getPraedRegMerkmale().checkExpletivesEs();
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
