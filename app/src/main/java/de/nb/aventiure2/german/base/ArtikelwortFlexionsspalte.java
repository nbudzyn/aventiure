package de.nb.aventiure2.german.base;

import static java.util.Objects.requireNonNull;
import static de.nb.aventiure2.german.base.ArtikelwortFlexionsspalte.Typ.DEF;
import static de.nb.aventiure2.german.base.ArtikelwortFlexionsspalte.Typ.DEIN;
import static de.nb.aventiure2.german.base.ArtikelwortFlexionsspalte.Typ.EINIGER;
import static de.nb.aventiure2.german.base.ArtikelwortFlexionsspalte.Typ.ETWAS;
import static de.nb.aventiure2.german.base.ArtikelwortFlexionsspalte.Typ.EUER;
import static de.nb.aventiure2.german.base.ArtikelwortFlexionsspalte.Typ.IHR;
import static de.nb.aventiure2.german.base.ArtikelwortFlexionsspalte.Typ.INDEF;
import static de.nb.aventiure2.german.base.ArtikelwortFlexionsspalte.Typ.MEIN;
import static de.nb.aventiure2.german.base.ArtikelwortFlexionsspalte.Typ.NEG_INDEF;
import static de.nb.aventiure2.german.base.ArtikelwortFlexionsspalte.Typ.SEIN;
import static de.nb.aventiure2.german.base.ArtikelwortFlexionsspalte.Typ.UNSER;
import static de.nb.aventiure2.german.base.ArtikelwortFlexionsspalte.Typ.VIEL_INDEF;
import static de.nb.aventiure2.german.base.Flexionsreihe.fr;
import static de.nb.aventiure2.german.base.NumerusGenus.F;
import static de.nb.aventiure2.german.base.NumerusGenus.M;
import static de.nb.aventiure2.german.base.NumerusGenus.N;
import static de.nb.aventiure2.german.base.NumerusGenus.PL_MFN;

import androidx.annotation.Nullable;

import com.google.common.collect.ImmutableMap;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Ein (attributiv gebrauchtes) Artikelwort (genauer gesagt: die Flexionsspalte
 * eines Artikelworts  für einen gewissen Numerus und ein gewisses Genus) - umfasst
 * neben den eigentlichen Artikeln auch, Wörter wie "kein", Possessivartikel ("mein", "unser"),
 * Indefinitartikel etc.
 */
public class ArtikelwortFlexionsspalte implements DeklinierbarePhrase {
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
        DEF(true, false, null),
        /**
         * Possessivartikel für ein Bezugsobjekt 1. Person Singular: "mein( Haus)", "meine( Häuser)"
         */
        MEIN(false, false, null),
        /**
         * Possessivartikel für ein Bezugsobjekt 2. Person Singular: "dein( Haus)", "deine( Häuser)"
         */
        DEIN(false, false, null),
        /**
         * Possessivartikel für ein Bezugsobjekt 3. Person Singular maskulinum oder neutrum:
         * "sein( Haus)", "seine( Häuser)"
         */
        SEIN(false, false, null),
        /**
         * Possessivartikel für ein Bezugsobjekt 3. Person Singular femininum oder Plural:
         * "ihr( Haus)" (Petras Hauser), "ihr( Häuser)"
         */
        IHR(false, false, null),
        /**
         * Possessivartikel für ein Bezugsobjekt 1. Person Plural: "unser( Haus)", "unsere( Häuser)"
         */
        UNSER(false, false, null),
        /**
         * Possessivartikel für ein Bezugsobjekt 2. Person Plural: "unser( Haus)", "unsere( Häuser)"
         */
        EUER(false, false, null),
        /**
         * Indefinitpronomen "etwas"
         */
        ETWAS(false, false,
                // "Nicht etwas Wassser"* -> "kein Wassser"
                // "Nicht einige Leute"* -> "keine Leute"
                NEG_INDEF),
        /**
         * Indefinitpronomen "einiges" / einige"
         */
        EINIGER(false, false,
                // "Nicht etwas Wassser"* -> "kein Wassser"
                // "Nicht einige Leute"* -> "keine Leute"
                NEG_INDEF),
        /**
         * Indefinitpronomen "viel" / "viele" (ohne vorangehenden Artikel):
         * "viel Soße" (nicht: "die viele Soße"!)
         */
        VIEL_INDEF(false, false,
                // "nicht viel" / "nicht viele"
                null);

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

        public ArtikelwortFlexionsspalte vor(final IErlaubtAttribute phraseDieAttributeErlaubt) {
            return get(this, phraseDieAttributeErlaubt.getNumerusGenus());
        }

        public ArtikelwortFlexionsspalte vor(final NumerusGenus numerusGenus) {
            return get(this, numerusGenus);
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

    private static final Map<Typ, Map<NumerusGenus, ArtikelwortFlexionsspalte>> ALL =
            new ConcurrentHashMap<>();

    private final NumerusGenus numerusGenus;
    private final Flexionsreihe flexionsreihe;

    static {
        // der, die, das, ...
        ALL.put(DEF, ImmutableMap.of(
                M, new ArtikelwortFlexionsspalte(M,
                        fr("der", "dem", "den")),
                F, new ArtikelwortFlexionsspalte(F,
                        fr("die", "der")),
                N, new ArtikelwortFlexionsspalte(N,
                        fr("das", "dem")),
                PL_MFN, new ArtikelwortFlexionsspalte(PL_MFN,
                        fr("die", "den"))));

        // ein, eine, ...
        ALL.put(INDEF, buildFlexionsspaltenTypEin("ein", false));

        // kein, keine, ...
        ALL.put(NEG_INDEF, buildFlexionsspaltenTypEin("kein", true));

        // Die Possessivartikel: mein, dein, sein, ihr, unser, euer, ...
        ALL.put(MEIN, buildFlexionsspaltenTypEin("mein", true));
        ALL.put(DEIN, buildFlexionsspaltenTypEin("dein", true));
        ALL.put(SEIN, buildFlexionsspaltenTypEin("sein", true));
        ALL.put(IHR, buildFlexionsspaltenTypEin("ihr", true));
        ALL.put(UNSER, buildFlexionsspaltenTypEin("unser", true));
        ALL.put(EUER, buildFlexionsspaltenTypEin("euer", true));

        // Indefinitpronomen etwas / einiges
        ALL.put(EINIGER, buildFlexionsspaltenTypDieser("einig", true));
        ALL.put(ETWAS, ImmutableMap.of(
                // "etwas Hirsch"
                M, new ArtikelwortFlexionsspalte(M, fr("etwas")),
                // "etwas Soße"
                F, new ArtikelwortFlexionsspalte(F, fr("etwas")),
                // "etwas Wasser"
                N, new ArtikelwortFlexionsspalte(N, fr("etwas")),
                // Ersatzkonstruktion: "einige Leute"
                PL_MFN, requireNonNull(requireNonNull(ALL.get(EINIGER)).get(PL_MFN))));

        // Indefinitpronomen viel, indefinit: "viel Soße", "viele Leute", ...
        ALL.put(VIEL_INDEF, buildFlexionsspaltenTypEin("viel", true));
    }

    /**
     * Baut die Artikel (genauer: die Artikel-Flexionsspalten) für allen
     * Genera im Singular, ggf. auch für den Plural, für einen Artikel
     * vom Typ "ein".
     */
    private static ImmutableMap<NumerusGenus, ArtikelwortFlexionsspalte> buildFlexionsspaltenTypEin(
            final String stamm, final boolean auchPlural) {
        final ImmutableMap.Builder<NumerusGenus, ArtikelwortFlexionsspalte> res =
                ImmutableMap.builder();
        res.put(M, new ArtikelwortFlexionsspalte(stamm, false, M));
        res.put(F, new ArtikelwortFlexionsspalte(stamm, false, F));
        res.put(N, new ArtikelwortFlexionsspalte(stamm, false, N));

        if (auchPlural) {
            res.put(PL_MFN, new ArtikelwortFlexionsspalte(stamm, false, PL_MFN));
        }

        return res.build();
    }

    /**
     * Baut die Artikel (genauer: die Artikel-Flexionsspalten) für allen
     * Genera im Singular, ggf. auch für den Plural, für einen Artikel
     * vom Typ "dieser".
     */
    private static ImmutableMap<NumerusGenus, ArtikelwortFlexionsspalte> buildFlexionsspaltenTypDieser(
            final String stamm, final boolean auchPlural) {
        final ImmutableMap.Builder<NumerusGenus, ArtikelwortFlexionsspalte> res =
                ImmutableMap.builder();
        res.put(M, new ArtikelwortFlexionsspalte(stamm, true, M));
        res.put(F, new ArtikelwortFlexionsspalte(stamm, true, F));
        res.put(N, new ArtikelwortFlexionsspalte(stamm, true, N));

        if (auchPlural) {
            res.put(PL_MFN, new ArtikelwortFlexionsspalte(stamm, true, PL_MFN));
        }

        return res.build();
    }

    static Typ getPossessiv(final Person person,
                            final NumerusGenus numerusGenusBezugsnomen) {
        switch (numerusGenusBezugsnomen.getNumerus()) {
            case SG:
                return getPossessivSg(person, numerusGenusBezugsnomen);
            case PL:
                return getPossessivPl(person);
            default:
                throw new IllegalArgumentException(
                        "Unerwarteter Numerus: " + numerusGenusBezugsnomen.getNumerus());
        }
    }

    private static Typ getPossessivSg(final Person person,
                                      final NumerusGenus numerusGenusBezugsnomen) {
        switch (person) {
            case P1:
                return MEIN;
            case P2:
                return DEIN;
            case P3:
                return getPossessivP3(numerusGenusBezugsnomen);
            default:
                throw new IllegalArgumentException("Unerwartete Person: " + person);
        }
    }

    private static Typ getPossessivP3(final NumerusGenus numerusGenusBezugsnomen) {
        switch (numerusGenusBezugsnomen) {
            case M:
            case N:
                return SEIN;
            case F:
            case PL_MFN:
                return IHR;
            default:
                throw new IllegalArgumentException(
                        "Unerwartete Numerus des Bezugsnomens: " + numerusGenusBezugsnomen);
        }
    }

    private static Typ getPossessivPl(final Person person) {
        switch (person) {
            case P1:
                return UNSER;
            case P2:
                return EUER;
            case P3:
                return IHR;
            default:
                throw new IllegalArgumentException(
                        "Unerwartete Person: " + person);
        }
    }

    static boolean traegtKasusendung(
            @Nullable final ArtikelwortFlexionsspalte artikelwortFlexionsspalte,
            final Kasus kasus) {
        if (artikelwortFlexionsspalte == null) {
            return false;
        }

        return artikelwortFlexionsspalte.traegtKasusendung(kasus);
    }

    public static @Nullable
    ArtikelwortFlexionsspalte get(@Nullable final Typ typ, final NumerusGenus numerusGenus) {
        if (typ == null) {
            return null;
        }
        return requireNonNull(ALL.get(typ)).get(numerusGenus);
    }

    private ArtikelwortFlexionsspalte(final String stammTypEin,
                                      final boolean stetsMitEndung,
                                      final NumerusGenus numerusGenus) {
        this(numerusGenus,
                requireNonNull(endungenEinDieser(stetsMitEndung).get(numerusGenus))
                        .buildFlexionsreihe(stammTypEin));
    }

    private static Map<NumerusGenus, Endungen> endungenEinDieser(final boolean stetsMitEdung) {
        return ImmutableMap.of(
                M, new Endungen(stetsMitEdung ? "er" : "",
                        "em", "en"),
                F, new Endungen("e", "er"),
                N, new Endungen(stetsMitEdung ? "es" : "", "em"),
                PL_MFN, new Endungen("e", "en"));
    }

    private ArtikelwortFlexionsspalte(final NumerusGenus numerusGenus,
                                      final Flexionsreihe flexionsreihe) {
        this.numerusGenus = numerusGenus;
        this.flexionsreihe = flexionsreihe;
    }

    private boolean traegtKasusendung(final Kasus kasus) {
        // "ein" ist der einzige "Artikel im engeren Sinne" ohne Kasusendung.
        // Aber auch andere Artikelwörter haben keine Kasusendung, etwa "kein", "mein" oder
        // "viel".
        final String artikelImKasus = imStr(kasus);

        return artikelImKasus.endsWith("er")
                || artikelImKasus.endsWith("en")
                || artikelImKasus.endsWith("e")
                || artikelImKasus.equals("die")
                || artikelImKasus.equals("das")
                || artikelImKasus.endsWith("em");
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
        final ArtikelwortFlexionsspalte artikelwortFlexionsspalte = (ArtikelwortFlexionsspalte) o;
        return numerusGenus == artikelwortFlexionsspalte.numerusGenus &&
                flexionsreihe.equals(artikelwortFlexionsspalte.flexionsreihe);
    }

    @Override
    public int hashCode() {
        return Objects.hash(numerusGenus);
    }
}
