package de.nb.aventiure2.german.base;

import androidx.annotation.Nullable;

import com.google.common.collect.ImmutableMap;

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

public class Relativpronomen extends SubstantivischesPronomenMitVollerFlexionsreihe {
    public enum Typ {
        // "das Kind, das"
        REGEL,
        // "alles, was"
        WERWAS;
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
        all.put(WERWAS, ImmutableMap.of(P3, buildWerWas(regel.get(P3))));

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
                regelP3.get(F),
                N, new Relativpronomen(P3, N,
                        fr("was",
                                // Ersatz
                                regelP3.get(N).dat())),
                // Ersatz
                PL_MFN, regelP3.get(PL_MFN));
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
                        regelP3.get(M), nachgestelltesPersonalpronomenNomSg),
                F, new Relativpronomen(person, F,
                        regelP3.get(F), nachgestelltesPersonalpronomenNomSg),
                N, new Relativpronomen(person, N,
                        regelP3.get(M), nachgestelltesPersonalpronomenNomSg),
                PL_MFN, new Relativpronomen(person, PL_MFN,
                        regelP3.get(PL_MFN), nachgestelltesPersonalpronomenNomPl)
        );
    }

    public static Relativpronomen getWerWas(final Person person,
                                            final NumerusGenus numerusGenus) {
        return get(WERWAS, person, numerusGenus);
    }

    public static Relativpronomen get(final Person person, final NumerusGenus numerusGenus) {
        return get(REGEL, person, numerusGenus);
    }

    public static Relativpronomen get(final Typ typ, final Person person,
                                      final NumerusGenus numerusGenus) {
        @Nullable final Relativpronomen res = ALL.get(REGEL).get(person).get(numerusGenus);
        if (res == null) {
            // P1 und P2 sind immer zählbar, also kein *"ich, was" oder "*"ich, der was"
            // Ersatz
            return get(REGEL, person, numerusGenus);
        }

        return res;
    }

    private Relativpronomen(final Person person, final NumerusGenus numerusGenus,
                            final Relativpronomen basis,
                            final String nachgestelltesPersonalpronomenNomPl) {
        this(person, numerusGenus, fr(
                // "[ich, ]der ich", "[wir, ]die wir"
                basis.nom() + " " + nachgestelltesPersonalpronomenNomPl,
                // "[ich, ]dem"
                basis.dat(),
                // "[ich, ]den"
                basis.akk()));
    }

    private Relativpronomen(final Person person,
                            final NumerusGenus numerusGenus,
                            final Flexionsreihe flexionsreihe) {
        super(numerusGenus, flexionsreihe);
        this.person = person;
    }

    @Override
    public Personalpronomen persPron() {
        // "Das Haus, das ich gesehen habe, - es ist ein schönes Haus."
        return Personalpronomen.get(person, getNumerusGenus());
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
    public Person getPerson() {
        return person;
    }
}
