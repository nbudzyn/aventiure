package de.nb.aventiure2.german;

import com.google.common.collect.ImmutableMap;

import java.util.Map;

import static de.nb.aventiure2.german.Genus.F;
import static de.nb.aventiure2.german.Genus.M;
import static de.nb.aventiure2.german.Genus.N;

public class Relativpronomen extends DekliniertePhrase {
    private static final Map<Genus, Relativpronomen> ALL = ImmutableMap.of(
            M, new Relativpronomen(M, "der", "dem", "den"),
            F, new Relativpronomen(F, "die", "der"),
            N, new Relativpronomen(N, "das", "dem"));

    public static Relativpronomen get(final Genus genus) {
        return ALL.get(genus);
    }

    private Relativpronomen(final Genus genus,
                            final String nominativAkkusativ, final String dativ) {
        this(genus, nominativAkkusativ, dativ, nominativAkkusativ);
    }

    private Relativpronomen(final Genus genus,
                            final String nominativ, final String dativ, final String akkusativ) {
        super(genus, nominativ, dativ, akkusativ);
    }
}
