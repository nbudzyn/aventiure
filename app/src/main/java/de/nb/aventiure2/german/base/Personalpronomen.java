package de.nb.aventiure2.german.base;

import com.google.common.collect.ImmutableMap;

import java.util.Map;

import static de.nb.aventiure2.german.base.NumerusGenus.F;
import static de.nb.aventiure2.german.base.NumerusGenus.M;
import static de.nb.aventiure2.german.base.NumerusGenus.N;
import static de.nb.aventiure2.german.base.NumerusGenus.PL_MFN;

public class Personalpronomen extends DeklinierbarePhrase {
    private static final Map<NumerusGenus, Personalpronomen> ALL = ImmutableMap.of(
            M, new Personalpronomen(M, "er", "ihm", "ihn"),
            F, new Personalpronomen(F, "sie", "ihr"),
            N, new Personalpronomen(N, "es", "ihm"),
            PL_MFN, new Personalpronomen(PL_MFN, "sie", "ihnen"));

    public static Personalpronomen get(final NumerusGenus numerusGenus) {
        return ALL.get(numerusGenus);
    }

    private Personalpronomen(final NumerusGenus numerusGenus,
                             final String nominativAkkusativ, final String dativ) {
        this(numerusGenus, nominativAkkusativ, dativ, nominativAkkusativ);
    }

    private Personalpronomen(final NumerusGenus numerusGenus,
                             final String nominativ, final String dativ, final String akkusativ) {
        super(numerusGenus, nominativ, dativ, akkusativ);
    }

    @Override
    public Personalpronomen persPron() {
        return this;
    }

    /**
     * "er, der..."
     */
    @Override
    public Relativpronomen relPron() {
        return Relativpronomen.get(getNumerusGenus());
    }
}
