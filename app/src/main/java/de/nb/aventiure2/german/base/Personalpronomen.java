package de.nb.aventiure2.german.base;

import com.google.common.collect.ImmutableMap;

import java.util.Map;

import static de.nb.aventiure2.german.base.Flexionsreihe.fr;
import static de.nb.aventiure2.german.base.NumerusGenus.F;
import static de.nb.aventiure2.german.base.NumerusGenus.M;
import static de.nb.aventiure2.german.base.NumerusGenus.N;
import static de.nb.aventiure2.german.base.NumerusGenus.PL_MFN;
import static de.nb.aventiure2.german.base.Person.P3;

public class Personalpronomen extends SubstantivischesPronomenMitVollerFlexionsreihe {
    private static final Map<NumerusGenus, Personalpronomen> ALL = ImmutableMap.of(
            M, new Personalpronomen(M,
                    fr("er", "ihm", "ihn")),
            F, new Personalpronomen(F,
                    fr("sie", "ihr")),
            N, new Personalpronomen(N,
                    fr("es", "ihm")),
            PL_MFN, new Personalpronomen(PL_MFN,
                    fr("sie", "ihnen")));

    public static boolean isPersonalpronomen(final String string) {
        return ALL.values().stream()
                .anyMatch(p -> p.isWortform(string));
    }

    public static Personalpronomen get(final NumerusGenus numerusGenus) {
        return ALL.get(numerusGenus);
    }

    private Personalpronomen(final NumerusGenus numerusGenus,
                             final Flexionsreihe flexionsreihe) {
        super(numerusGenus, flexionsreihe);
    }

    @Override
    public Personalpronomen persPron() {
        return this;
    }

    @Override
    public Reflexivpronomen reflPron() {
        // P1 und P2 sind hier noch nicht vorgesehen
        return Reflexivpronomen.get(P3, getNumerusGenus().getNumerus());
    }

    /**
     * "er, der..."
     */
    @Override
    public Relativpronomen relPron() {
        return Relativpronomen.get(getNumerusGenus());
    }

    /**
     * "Er... sein..."
     */
    @Override
    public Possessivartikel possArt() {
        return Possessivartikel.get(getNumerusGenus());
    }

    @Override
    public Person getPerson() {
        // P1 und P2 sind hier noch nicht vorgesehen
        return P3;
    }
}
