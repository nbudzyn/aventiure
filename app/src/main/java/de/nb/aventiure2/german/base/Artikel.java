package de.nb.aventiure2.german.base;

import androidx.annotation.Nullable;

import com.google.common.collect.ImmutableMap;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

import static de.nb.aventiure2.german.base.Artikel.Typ.DEF;
import static de.nb.aventiure2.german.base.Artikel.Typ.INDEF;
import static de.nb.aventiure2.german.base.Artikel.Typ.NEG_INDEF;
import static de.nb.aventiure2.german.base.Flexionsreihe.fr;
import static de.nb.aventiure2.german.base.NumerusGenus.F;
import static de.nb.aventiure2.german.base.NumerusGenus.M;
import static de.nb.aventiure2.german.base.NumerusGenus.N;
import static de.nb.aventiure2.german.base.NumerusGenus.PL_MFN;
import static java.util.Objects.requireNonNull;

public class Artikel implements DeklinierbarePhrase {
    static boolean traegtKasusendung(@Nullable final Artikel artikel, final Kasus kasus) {
        if (artikel == null) {
            return false;
        }

        // "ein" ist der einzige Artikel ohne Kasusendung.
        // (Aber auch andere Artikelwörter haben keine Kasusendung, etwa "kein", "mein" oder
        // "viel".)
        final String artikelImKasus = artikel.imStr(kasus);
        return !artikelImKasus.equals("ein") && !artikelImKasus.equals("kein");
    }

    public enum Typ {
        /**
         * "kein( Haus)", "keine(Häuser)"
         */
        NEG_INDEF(false, true,
                // Die negative Form von NEG_INDEF ist NEG_INDEF - wird im Konstruktor gesetzt!
                // Das hat zur Folge, dass wir keine doppelten Verneinungen in der Art
                // "nicht kein Essen" erzeugen. Die "Negation" von "kein Essen" ist immer noch
                // "kein Essen".
                null),
        /**
         * "ein( Haus)" , "(Häuser)"
         */
        INDEF(false, false, NEG_INDEF),
        /**
         * "das( Haus)", "die( Häuser)"
         */
        DEF(true, false, null);

        private final boolean erlaubtVerschmelzungMitPraeposition;

        /**
         * Ob der Artikel negativ ist. "kein" ist negativ, "ein" und "der" nicht.
         */
        private final boolean negativ;

        /**
         * Die negative Form des Artikels - sofern es eine gibt; bei negativen Artikeln
         * der Artikel selbst.
         */
        @Nullable
        private final Typ negativeForm;

        Typ(final boolean erlaubtVerschmelzungMitPraeposition, final boolean negativ,
            @Nullable final Typ negativeForm) {
            this.erlaubtVerschmelzungMitPraeposition = erlaubtVerschmelzungMitPraeposition;
            this.negativ = negativ;
            this.negativeForm = negativ ? this : negativeForm;
        }

        public boolean erlaubtVerschmelzungMitPraeposition() {
            return erlaubtVerschmelzungMitPraeposition;
        }

        public static boolean isNegativ(@Nullable final Typ typ) {
            if (typ == null) {
                return false;
            }

            return typ.isNegativ();
        }

        public boolean isNegativ() {
            return negativ;
        }

        @Nullable
        public static Typ getNegativeForm(@Nullable final Typ typ) {
            if (typ == null) {
                return null;
            }

            return typ.getNegativeForm();
        }

        @Nullable
        public Typ getNegativeForm() {
            return negativeForm;
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

        ALL.put(NEG_INDEF, ImmutableMap.of(
                M, new Artikel(M,
                        fr("kein", "keinem", "keinen")),
                F, new Artikel(F,
                        fr("keine", "keiner")),
                N, new Artikel(N,
                        fr("kein", "keinem")),
                PL_MFN, new Artikel(PL_MFN,
                        fr("keine", "keinen"))));
    }

    public static @Nullable
    Artikel get(@Nullable final Typ typ, final NumerusGenus numerusGenus) {
        if (typ == null) {
            return null;
        }
        return requireNonNull(ALL.get(typ)).get(numerusGenus);
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
