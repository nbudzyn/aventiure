package de.nb.aventiure2.german.base;

import com.google.common.collect.ImmutableMap;

import java.util.Map;

import static de.nb.aventiure2.german.base.NumerusGenus.F;
import static de.nb.aventiure2.german.base.NumerusGenus.M;
import static de.nb.aventiure2.german.base.NumerusGenus.N;
import static de.nb.aventiure2.german.base.NumerusGenus.PL;

public class Relativpronomen extends DeklinierbarePhrase {
    private static final Map<NumerusGenus, Relativpronomen> ALL = ImmutableMap.of(
            M, new Relativpronomen(M, "der", "dem", "den"),
            F, new Relativpronomen(F, "die", "der"),
            N, new Relativpronomen(N, "das", "dem"),
            PL, new Relativpronomen(N, "die", "denen"));

    public static Relativpronomen get(final NumerusGenus numerusGenus) {
        return ALL.get(numerusGenus);
    }

    private Relativpronomen(final NumerusGenus numerusGenus,
                            final String nominativAkkusativ, final String dativ) {
        this(numerusGenus, nominativAkkusativ, dativ, nominativAkkusativ);
    }

    private Relativpronomen(final NumerusGenus numerusGenus,
                            final String nominativ, final String dativ, final String akkusativ) {
        super(numerusGenus, nominativ, dativ, akkusativ);
    }
}
