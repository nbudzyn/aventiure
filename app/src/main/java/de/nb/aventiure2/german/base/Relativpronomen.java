package de.nb.aventiure2.german.base;

import androidx.annotation.Nullable;

import com.google.common.collect.ImmutableMap;

import java.util.Objects;

import static de.nb.aventiure2.german.base.Flexionsreihe.fr;
import static de.nb.aventiure2.german.base.NumerusGenus.F;
import static de.nb.aventiure2.german.base.NumerusGenus.M;
import static de.nb.aventiure2.german.base.NumerusGenus.N;
import static de.nb.aventiure2.german.base.NumerusGenus.PL_MFN;
import static de.nb.aventiure2.german.base.Person.P1;
import static de.nb.aventiure2.german.base.Person.P2;
import static de.nb.aventiure2.german.base.Person.P3;
import static de.nb.aventiure2.german.base.Relativpronomen.Typ.REGEL;
import static de.nb.aventiure2.german.base.Relativpronomen.Typ.WERWAS;
import static java.util.Objects.requireNonNull;

public class Relativpronomen extends
        SubstantivischesPronomenMitVollerFlexionsreiheEinzelneKomplexe {
    public enum Typ {
        // "das Kind, das"
        REGEL,
        // "alles, was"
        WERWAS
    }

    private static final ImmutableMap<Typ,
            ImmutableMap<Person, ImmutableMap<NumerusGenus, Relativpronomen>>>
            ALL;

    static {
        final ImmutableMap.Builder<Typ,
                ImmutableMap<Person, ImmutableMap<NumerusGenus, Relativpronomen>>> all =
                ImmutableMap.builder();

        final ImmutableMap<Person, ImmutableMap<NumerusGenus, Relativpronomen>> regel =
                buildRegel();
        all.put(REGEL, regel);

        // "alles, was"
        all.put(WERWAS, ImmutableMap.of(P3, buildWerWas(requireNonNull(regel.get(P3)))));

        ALL = all.build();
    }

    private static ImmutableMap<Person, ImmutableMap<NumerusGenus, Relativpronomen>> buildRegel() {
        final ImmutableMap.Builder<Person, ImmutableMap<NumerusGenus, Relativpronomen>> regel =
                ImmutableMap.builder();

        // "das Kind, das"
        final ImmutableMap<NumerusGenus, Relativpronomen> regelP3 = ImmutableMap.of(
                M, new Relativpronomen(P3, M,
                        fr("der", "dem", "den")),
                F, new Relativpronomen(P3, F,
                        fr("die", "der")),
                N, new Relativpronomen(P3, N,
                        fr("das", "dem")),
                PL_MFN, new Relativpronomen(P3, PL_MFN,
                        fr("die", "denen")));

        regel.put(P3, regelP3);

        // "ich, der ich", "ich, dem"
        regel.put(P1, mitNachgestelltemPersonalpronomenImNom(regelP3, P1,
                "ich", "wir"));

        // "ihr, die ihr", "ihr, denen"
        regel.put(P2, mitNachgestelltemPersonalpronomenImNom(regelP3, P2,
                "du", "ihr"));

        return regel.build();
    }

    private static ImmutableMap<NumerusGenus, Relativpronomen>
    buildWerWas(final ImmutableMap<NumerusGenus, Relativpronomen> regelP3) {
        return ImmutableMap.of(
                M, new Relativpronomen(P3, M,
                        fr("wer", "wem", "wen")),
                F,
                // Ersatz
                requireNonNull(regelP3.get(F)),
                N, new Relativpronomen(P3, N,
                        fr("was",
                                // Ersatz
                                requireNonNull(regelP3.get(N)).datStr())),
                // Ersatz
                PL_MFN, requireNonNull(regelP3.get(PL_MFN)));
    }

    private final Person person;

    private static ImmutableMap<NumerusGenus, Relativpronomen>
    mitNachgestelltemPersonalpronomenImNom(
            final ImmutableMap<NumerusGenus, Relativpronomen> regelP3,
            final Person person,
            final String nachgestelltesPersonalpronomenNomSg,
            final String nachgestelltesPersonalpronomenNomPl) {
        return ImmutableMap.of(
                M, new Relativpronomen(person, M,
                        requireNonNull(regelP3.get(M)), nachgestelltesPersonalpronomenNomSg),
                F, new Relativpronomen(person, F,
                        requireNonNull(regelP3.get(F)), nachgestelltesPersonalpronomenNomSg),
                N, new Relativpronomen(person, N,
                        requireNonNull(regelP3.get(M)), nachgestelltesPersonalpronomenNomSg),
                PL_MFN, new Relativpronomen(person, PL_MFN,
                        requireNonNull(regelP3.get(PL_MFN)), nachgestelltesPersonalpronomenNomPl)
        );
    }

    public static Relativpronomen getWerWas(final Person person,
                                            final NumerusGenus numerusGenus) {
        return get(WERWAS, person, numerusGenus);
    }

    /**
     * Gibt ein Relativpronomen ohne Bezugsobjekt zurück.
     */
    public static Relativpronomen get(final Person person, final NumerusGenus numerusGenus) {
        return get(person, numerusGenus, null);
    }

    public static Relativpronomen get(final Person person, final NumerusGenus numerusGenus,
                                      @Nullable final IBezugsobjekt bezugsobjekt) {
        return get(REGEL, person, numerusGenus, bezugsobjekt);
    }

    public static Relativpronomen get(final Typ typ, final Person person,
                                      final NumerusGenus numerusGenus) {
        return get(typ, person, numerusGenus, null);
    }

    private static Relativpronomen get(final Typ typ, final Person person,
                                       final NumerusGenus numerusGenus,
                                       @Nullable final IBezugsobjekt bezugsobjekt) {
        @Nullable Relativpronomen ohneBezugsobjekt = requireNonNull(
                requireNonNull(ALL.get(typ)).get(person)).get(numerusGenus);
        if (ohneBezugsobjekt == null) {
            // P1 und P2 sind immer zählbar, also kein *"ich, was" oder "*"ich, der was"
            // Ersatz
            ohneBezugsobjekt = get(REGEL, person, numerusGenus, bezugsobjekt);
        }


        return requireNonNull(ohneBezugsobjekt).mitBezugsobjekt(bezugsobjekt);
    }

    private Relativpronomen(final Person person, final NumerusGenus numerusGenus,
                            final Relativpronomen basis,
                            final String nachgestelltesPersonalpronomenNomPl) {
        this(person, numerusGenus, fr(
                // "[ich, ]der ich", "[wir, ]die wir"
                basis.nomStr() + " " + nachgestelltesPersonalpronomenNomPl,
                // "[ich, ]dem"
                basis.datStr(),
                // "[ich, ]den"
                basis.akkStr()));
    }

    /**
     * Erzeugt ein Relativpronomen ohne Bezugsobjekt.
     */
    private Relativpronomen(final Person person,
                            final NumerusGenus numerusGenus,
                            final Flexionsreihe flexionsreihe) {
        this(person, numerusGenus, flexionsreihe, null);
    }

    private Relativpronomen(final Person person,
                            final NumerusGenus numerusGenus,
                            final Flexionsreihe flexionsreihe,
                            @Nullable final IBezugsobjekt bezugsobjekt) {
        super(numerusGenus,
                // Relativpronommen können keine Negationspartikelphrasen haben:
                // *"das Kind, nicht das..."
                null, flexionsreihe, bezugsobjekt);
        this.person = person;
    }

    /**
     * Die Fokuspartikel wird verworfen. Relativpronomen können wohl keine
     * Fokuspartikeln haben.
     */
    @Override
    public Relativpronomen mitFokuspartikel(@Nullable final String fokuspartikel) {
        return this;
    }

    private Relativpronomen mitBezugsobjekt(@Nullable final IBezugsobjekt bezugsobjekt) {
        if (bezugsobjekt == null) {
            return this;
        }

        return new Relativpronomen(person, getNumerusGenus(),
                getFlexionsreihe(), bezugsobjekt);
    }

    @Override
    public SubstantivischePhrase ohneNegationspartikelphrase() {
        // Relativpronommen können keine Negationspartikelphrasen haben:
        // *"das Kind, nicht das..."
        return this;
    }

    @Override
    public Relativpronomen neg(final Negationspartikelphrase negationspartikelphrase,
                               final boolean moeglichstNegativIndefiniteWoerterVerwenden) {
        // Relativpronommen können keine Negationspartikelphrasen haben:
        // *"das Kind, nicht das..."
        return this;
    }

    @Override
    public Personalpronomen persPron() {
        // "Das Haus, das ich gesehen habe, - es ist ein schönes Haus."
        return Personalpronomen.get(person, getNumerusGenus(), getBezugsobjekt());
    }

    @Override
    public Reflexivpronomen reflPron() {
        // "Die Sache, die ich erlebt habe, - sie fühlt sich für mich im Nachhinein immer
        // noch seltsam an."
        return Reflexivpronomen.get(person, getNumerusGenus().getNumerus());
    }

    @Override
    public Possessivartikel possArt() {
        return Possessivartikel.get(person, getNumerusGenus());
    }

    @Override
    public Relativpronomen relPron() {
        // "das Haus, das ich gesehen habe, DAS mir gleich aufgefallen ist"
        return this;
    }

    @Override
    public boolean isUnbetontesPronomen() {
        return true;
    }

    @Override
    public Person getPerson() {
        return person;
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
        final Relativpronomen that = (Relativpronomen) o;
        return person == that.person;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), person);
    }
}
