package de.nb.aventiure2.german.base;

import androidx.annotation.Nullable;

import com.google.common.collect.ImmutableMap;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

import static de.nb.aventiure2.german.base.Artikel.Typ.DEF;
import static de.nb.aventiure2.german.base.Artikel.Typ.INDEF;
import static de.nb.aventiure2.german.base.Flexionsreihe.fr;
import static de.nb.aventiure2.german.base.NumerusGenus.F;
import static de.nb.aventiure2.german.base.NumerusGenus.M;
import static de.nb.aventiure2.german.base.NumerusGenus.N;
import static de.nb.aventiure2.german.base.NumerusGenus.PL_MFN;

public class Artikel implements DeklinierbarePhrase {
    static boolean traegtKasusendung(@Nullable final Artikel artikel, final Kasus kasus) {
        if (artikel == null) {
            return false;
        }

        // "ein" ist der einzige Artikel ohne Kasusendung.
        // (Aber auch andere Artikelwörter haben keine Kasusendung, etwa "kein", "mein" oder
        // "viel".)
        return !artikel.imStr(kasus).equals("ein");
    }

    public enum Typ {
        /**
         * "ein( Haus)" , "(Häuser)"
         */
        INDEF(false),
        /**
         * "das( Haus)", "die( Häuser)"
         */
        DEF(true);

        private final boolean erlaubtVerschmelzungMitPraeposition;

        Typ(final boolean erlaubtVerschmelzungMitPraeposition) {
            this.erlaubtVerschmelzungMitPraeposition = erlaubtVerschmelzungMitPraeposition;
        }

        public boolean erlaubtVerschmelzungMitPraeposition() {
            return erlaubtVerschmelzungMitPraeposition;
        }
    }

    private static final Map<Typ, Map<NumerusGenus, Artikel>> ALL =
            new ConcurrentHashMap<>();

    private final NumerusGenus numerusGenus;
    private final Flexionsreihe flexionsreihe;

    static {
        ALL.put(DEF, ImmutableMap.of(
                M, new Artikel(M,
                        fr("der", "dem", "den")),
                F, new Artikel(F,
                        fr("die", "der")),
                N, new Artikel(N,
                        fr("das", "dem")),
                PL_MFN, new Artikel(PL_MFN,
                        fr("die", "den"))));

        ALL.put(INDEF, ImmutableMap.of(
                M, new Artikel(M,
                        fr("ein", "einem", "einen")),
                F, new Artikel(F,
                        fr("eine", "einer")),
                N, new Artikel(N,
                        fr("ein", "einem"))));
    }

    public static @Nullable
    Artikel get(@Nullable final Typ typ, final NumerusGenus numerusGenus) {
        if (typ == null) {
            return null;
        }
        return ALL.get(typ).get(numerusGenus);
    }

    private Artikel(final NumerusGenus numerusGenus,
                    final Flexionsreihe flexionsreihe) {
        this.numerusGenus = numerusGenus;
        this.flexionsreihe = flexionsreihe;
    }

    @Override
    public String nomStr() {
        return flexionsreihe.nom();
    }

    @Override
    public String datStr() {
        return flexionsreihe.dat();
    }

    @Override
    public String akkStr() {
        return flexionsreihe.akk();
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final Artikel artikel = (Artikel) o;
        return numerusGenus == artikel.numerusGenus &&
                flexionsreihe.equals(artikel.flexionsreihe);
    }

    @Override
    public int hashCode() {
        return Objects.hash(numerusGenus);
    }
}
