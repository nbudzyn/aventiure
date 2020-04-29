package de.nb.aventiure2.german.base;

import com.google.common.collect.ImmutableMap;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static de.nb.aventiure2.german.base.NumerusGenus.F;
import static de.nb.aventiure2.german.base.NumerusGenus.M;
import static de.nb.aventiure2.german.base.NumerusGenus.N;
import static de.nb.aventiure2.german.base.NumerusGenus.PL_MFN;
import static de.nb.aventiure2.german.base.Relativpronomen.Typ.REGEL;
import static de.nb.aventiure2.german.base.Relativpronomen.Typ.WERWAS;

public class Relativpronomen extends DeklinierbarePhrase {
    public enum Typ {
        // "das Kind, das"
        REGEL,
        // "alles, was"
        WERWAS
    }

    private static final Map<Typ, Map<NumerusGenus, Relativpronomen>> ALL =
            new ConcurrentHashMap<>();

    static {
        // "das Kind, das"
        ALL.put(REGEL, ImmutableMap.of(
                M, new Relativpronomen(M, "der", "dem", "den"),
                F, new Relativpronomen(F, "die", "der"),
                N, new Relativpronomen(N, "das", "dem"),
                PL_MFN, new Relativpronomen(PL_MFN, "die", "denen")));

        // "alles, was"
        ALL.put(WERWAS, ImmutableMap.of(
                M, new Relativpronomen(M, "wer", "wem", "wen"),
                F,
                // Ersatz
                ALL.get(REGEL).get(F),
                N, new Relativpronomen(N, "was",
                        // Ersatz
                        ALL.get(REGEL).get(N).dat()),
                PL_MFN, ALL.get(REGEL).get(PL_MFN)));
    }

    public static Relativpronomen get(final NumerusGenus numerusGenus) {
        return get(REGEL, numerusGenus);
    }

    public static Relativpronomen getWerWas(final NumerusGenus numerusGenus) {
        return get(WERWAS, numerusGenus);
    }

    public static Relativpronomen get(final Typ typ, final NumerusGenus numerusGenus) {
        return ALL.get(typ).get(numerusGenus);
    }

    private Relativpronomen(final NumerusGenus numerusGenus,
                            final String nominativAkkusativ, final String dativ) {
        this(numerusGenus, nominativAkkusativ, dativ, nominativAkkusativ);
    }

    private Relativpronomen(final NumerusGenus numerusGenus,
                            final String nominativ, final String dativ, final String akkusativ) {
        super(numerusGenus, nominativ, dativ, akkusativ);
    }

    @Override
    public Personalpronomen persPron() {
        // "Das Haus, das ich gesehen habe, - es ist ein schönes Haus."
        return Personalpronomen.get(getNumerusGenus());
    }

    @Override
    public Relativpronomen relPron() {
        // "das Haus, das ich gesehen habe, DAS mir gleich aufgefallen ist"
        return this;
    }
}
