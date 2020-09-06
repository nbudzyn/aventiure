package de.nb.aventiure2.german.base;

import com.google.common.collect.ImmutableMap;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static de.nb.aventiure2.german.base.Flexionsreihe.fr;
import static de.nb.aventiure2.german.base.NumerusGenus.F;
import static de.nb.aventiure2.german.base.NumerusGenus.M;
import static de.nb.aventiure2.german.base.NumerusGenus.N;
import static de.nb.aventiure2.german.base.NumerusGenus.PL_MFN;
import static de.nb.aventiure2.german.base.Relativpronomen.Typ.REGEL;
import static de.nb.aventiure2.german.base.Relativpronomen.Typ.WERWAS;

public class Relativpronomen extends PronomenMitVollerFlexionsreihe {
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
                M, new Relativpronomen(M,
                        fr("der", "dem", "den")),
                F, new Relativpronomen(F,
                        fr("die", "der")),
                N, new Relativpronomen(N,
                        fr("das", "dem")),
                PL_MFN, new Relativpronomen(PL_MFN,
                        fr("die", "denen"))));

        // "alles, was"
        ALL.put(WERWAS, ImmutableMap.of(
                M, new Relativpronomen(M,
                        fr("wer", "wem", "wen")),
                F,
                // Ersatz
                ALL.get(REGEL).get(F),
                N, new Relativpronomen(N,
                        fr("was",
                                // Ersatz
                                ALL.get(REGEL).get(N).dat())),
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
                            final Flexionsreihe flexionsreihe) {
        super(numerusGenus, flexionsreihe);
    }

    @Override
    public Personalpronomen persPron() {
        // "Das Haus, das ich gesehen habe, - es ist ein schönes Haus."
        return Personalpronomen.get(getNumerusGenus());
    }

    @Override
    public Possessivartikel possArt() {
        return Possessivartikel.get(getNumerusGenus());
    }

    @Override
    public Relativpronomen relPron() {
        // "das Haus, das ich gesehen habe, DAS mir gleich aufgefallen ist"
        return this;
    }
}
