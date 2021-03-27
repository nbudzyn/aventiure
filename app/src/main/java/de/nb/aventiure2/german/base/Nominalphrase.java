package de.nb.aventiure2.german.base;

import androidx.annotation.Nullable;

import java.util.Objects;

import static de.nb.aventiure2.german.base.Artikel.Typ.DEF;
import static de.nb.aventiure2.german.base.Artikel.Typ.INDEF;
import static de.nb.aventiure2.german.base.Flexionsreihe.fr;
import static de.nb.aventiure2.german.base.GermanUtil.joinToString;
import static de.nb.aventiure2.german.base.NumerusGenus.F;
import static de.nb.aventiure2.german.base.NumerusGenus.M;
import static de.nb.aventiure2.german.base.NumerusGenus.N;
import static de.nb.aventiure2.german.base.NumerusGenus.PL_MFN;
import static de.nb.aventiure2.german.base.Person.P3;

/**
 * Eine Nominalphrase, z.B. "ein dicker, hässlicher Frosch".
 */
public class Nominalphrase
        extends EinzelneSubstantivischePhrase
        implements IErlaubtAttribute {
    @Nullable
    private final Artikel.Typ artikelTyp;

    private final Flexionsreihe flexionsreiheArtikellos;

    // Allgemeine Nominalphrasen, die sich nicht auf ein
    // AvObject oder eine AbstractEntity beziehen.
    public static final Nominalphrase ABEND =
            np(M, DEF, "Abend");
    public static final Nominalphrase ABENDHIMMEL =
            np(M, DEF, "Abendhimmel");
    public static final Nominalphrase ABENDLICHT =
            np(N, DEF, "Abendlicht");
    public static final Nominalphrase ABENDSONNE =
            np(F, DEF, "Abendsonne");
    public static final Nominalphrase ABENDSONNENSCHEIN =
            np(M, DEF, "Abendsonnenschein");
    public static final Nominalphrase ANGEBOTE =
            np(PL_MFN, INDEF, "Angebote", "Angeboten");
    public static final Nominalphrase ASTGABEL =
            np(F, DEF, "Astgabel");
    public static final Nominalphrase BEDECKTER_HIMMEL =
            np(M, DEF, "bedeckter Himmel", "bedeckten Himmel",
                    "bedeckten Himmel");
    public static final Nominalphrase BEWOELKTER_HIMMEL =
            np(M, DEF, "bewölkte Himmel", "bewölkten Himmel",
                    "bewölkten Himmel");
    public static final Nominalphrase BEZOGENER_HIMMEL =
            np(M, DEF, "bezogener Himmel", "bezogenen Himmel",
                    "bezogenen Himmel");
    public static final Nominalphrase BLAUER_HIMMEL =
            np(M, DEF, "blaue Himmel", "blauen Himmel",
                    "blauen Himmel");
    public static final Nominalphrase DAEMMERLICHT =
            np(N, DEF, "Dämmerlicht");
    public static final Nominalphrase DEIN_HERZ =
            np(N, null, "dein Herz",
                    "deinem Herzen");
    public static final Nominalphrase DINGE =
            np(PL_MFN, DEF, "Dinge", "Dingen");
    public static final Nominalphrase DUESTERE_WOLKEN =
            np(PL_MFN, INDEF,
                    "düstere Wolken", "düsteren Wolken");
    public static final Nominalphrase DUESTERNIS =
            np(F, DEF, "Düsternis");
    public static final Nominalphrase DUNKEL =
            np(N, DEF, "Dunkel");
    public static final Nominalphrase DUNKELHEIT =
            np(F, DEF, "Dunkelheit");
    public static final Nominalphrase EIN_GESPRAECH =
            np(N, INDEF, "Gespräch");
    public static final Nominalphrase EIN_GROBER_ABRISS =
            np(N, INDEF, "grober Abriss",
                    "groben Abriss", "groben Abriss");
    public static final Nominalphrase EINE_KLEINE_NOTLUEGE =
            np(N, INDEF, "kleine Notlüge",
                    "kleinen Notlüge");
    public static final Nominalphrase ERSTE_SONNENSTRAHLEN =
            np(PL_MFN, DEF, "ersten Sonnenstrahlen");
    public static final Nominalphrase FREUDE_OHNE_ART =
            np(F, null, "Freude");
    public static final Nominalphrase FUSS =
            np(M, DEF, "Fuß");
    public static final Nominalphrase GANZER_HIMMEL =
            np(M, DEF, "ganze Himmel",
                    "ganzen Himmel", "ganzen Himmel");
    public static final Nominalphrase GESPRAECH =
            np(N, DEF, "Gespräch");
    public static final Nominalphrase GETRUEBTES_TAGESLICHT =
            np(N, DEF, "getrübte Tageslicht",
                    "getrübten Tageslicht");
    public static final Nominalphrase EIN_HEISSER_TAG =
            np(M, INDEF, "heißer Tag",
                    "heißen Tag", "heißen Tag");
    public static final Nominalphrase HALBDUNKEL =
            np(N, DEF, "Halbdunkel");
    public static final Nominalphrase HEISSER_SONNENSCHEIN =
            np(M, DEF, "heiße Sonnenschein",
                    "heißen Sonnenschein", "heißen Sonnenschein");
    public static final Nominalphrase HELLES_TAGESLICHT =
            np(N, DEF, "helle Tageslicht",
                    "hellen Tageslicht", "helle Tageslicht");
    public static final Nominalphrase HIMMEL =
            np(M, DEF, "Himmel");
    public static final Nominalphrase HOEHE =
            np(F, DEF, "Höhe");
    public static final Nominalphrase IHRE_HAARE =
            np(PL_MFN, null, "ihre Haare",
                    "ihren Haaren");
    public static final Nominalphrase IHR_NAME =
            np(M, null, "ihr Name",
                    "ihrem Namen", "ihren Namen");
    public static final Nominalphrase IHR_ZIEL =
            np(N, null, "ihr Ziel", "ihrem Ziel");
    public static final Nominalphrase KLARER_HIMMEL =
            np(M, DEF, "klare Himmel", "klaren Himmel",
                    "klaren Himmel");
    public static final Nominalphrase LEIB =
            np(M, DEF, "Leib", "Leibe");
    public static final Nominalphrase LICHT_OHNE_ART =
            np(N, null, "Licht");
    public static final Nominalphrase MITTAGSSONNE =
            np(F, DEF, "Mittagssonne");
    public static final Nominalphrase MORGENDLICHER_SONNENSCHEIN =
            np(M, DEF, "morgendliche Sonnenschein",
                    "morgendlichen Sonnenschein",
                    "morgendlichen Sonnenschein");
    public static final Nominalphrase KLIRRENDE_KAELTE_OHNE_ART =
            np(F, null,
                    "klirrende Kälte", "klirrenden Kälte");
    public static final Nominalphrase MOND =
            np(M, DEF, "Mond");
    public static final Nominalphrase MONDLICHT =
            np(N, DEF, "Mondlicht");
    public static final Nominalphrase MONDSCHEIN =
            np(M, DEF, "Mondschein");
    public static final Nominalphrase MORGEN =
            np(M, DEF, "Morgen");
    public static final Nominalphrase MORGENLICHT =
            np(N, DEF, "Morgenlicht");
    public static final Nominalphrase MORGENSONNE =
            np(F, DEF, "Morgensonne");
    public static final Nominalphrase NACHT =
            np(F, DEF, "Nacht");
    public static final Nominalphrase NACHTHIMMEL =
            np(M, DEF, "Nachthimmel");
    public static final Nominalphrase RETTUNG_OHNE_ART =
            np(F, null, "Rettung");
    public static final Nominalphrase EIN_SCHOENER_ABEND =
            np(M, INDEF, "schöner Abend",
                    "schönen Abend", "schönen Abend");
    public static final Nominalphrase ROETLICHER_ABENDHIMMEL =
            np(M, DEF, "rötliche Abendhimmel", "rötlichen Abendhimmel",
                    "rötlichen Abendhimmel");
    public static final Nominalphrase SANFTES_MONDLICHT =
            np(N, DEF, "sanfte Mondlicht",
                    "sanften Mondlicht");
    public static final Nominalphrase SANFTES_MORGENLICHT =
            np(N, DEF, "sanfte Morgenlicht",
                    "sanften Morgenlicht");
    public static final Nominalphrase SANFTES_TAGESLICHT =
            np(N, DEF, "sanfte Tageslicht",
                    "sanften Tageslicht");
    public static final Nominalphrase SCHUMMERLICHT =
            np(N, DEF, "Schummerlicht");
    public static final Nominalphrase SENGENDE_SONNE =
            np(F, DEF, "sengende Sonne", "sengenden Sonne");
    public static final Nominalphrase SONNE =
            np(F, DEF, "Sonne");
    public static final Nominalphrase SONNENHITZE =
            np(F, DEF, "Sonnenhitze");
    public static final Nominalphrase SONNENSCHEIN =
            np(M, DEF, "Sonnenschein");
    public static final Nominalphrase STRAHLEND_BLAUER_HIMMEL =
            np(M, DEF, "strahlend blaue Himmel",
                    "strahlend blauen Himmel", "strahlend blauen Himmel");
    public static final Nominalphrase STERNENHIMMEL =
            np(M, DEF, "Sternenhimmel");
    public static final Nominalphrase STERNENLICHT =
            np(N, DEF, "Sternenlicht");
    public static final Nominalphrase TAG =
            np(M, DEF, "Tag");
    public static final Nominalphrase TAGESLICHT =
            np(N, DEF, "Tageslicht");
    public static final Nominalphrase TAGESLICHT_OHNE_ART =
            np(N, null, "Tageslicht");
    public static final Nominalphrase TRUEBES_DAEMMERLICHT =
            np(N, DEF, "trübe Dämmerlicht",
                    "trüben Dämmerlicht");
    public static final Nominalphrase TRUEBES_LICHT =
            np(N, DEF, "trübe Licht",
                    "trüben Licht");
    public static final Nominalphrase VOLLMOND =
            np(M, DEF, "Vollmond");
    public static final Nominalphrase WAHRHEIT =
            np(F, DEF, "Wahrheit");
    public static final Nominalphrase WARMES_WETTER_OHNE_ART =
            np(N, null,
                    "warmes Wetter", "warmem Wetter");
    public static final Nominalphrase WOLKENVERHANGENER_HIMMEL =
            np(M, DEF, "wolkenverhangene Himmel",
                    "wolkenverhangenen Himmel",
                    "wolkenverhangenen Himmel");
    public static final Nominalphrase WUT_OHNE_ART =
            np(F, null, "Wut");
    public static final Nominalphrase ZWIELICHT =
            np(N, DEF, "Zwielicht");

    /**
     * Erzeugt eine Nominalphrase ohne Bezugsobjekt.
     */
    public static Nominalphrase np(final NumerusGenus numerusGenus,
                                   @Nullable final Artikel.Typ artikelTyp,
                                   final String nominalNominativDativUndAkkusativ) {
        return np(numerusGenus, artikelTyp, nominalNominativDativUndAkkusativ,
                (IBezugsobjekt) null);
    }

    public static Nominalphrase np(final NumerusGenus numerusGenus,
                                   @Nullable final Artikel.Typ artikelTyp,
                                   final String nominalNominativDativUndAkkusativ,
                                   @Nullable final IBezugsobjekt bezugsobjekt) {
        return np(numerusGenus, artikelTyp,
                nominalNominativDativUndAkkusativ, nominalNominativDativUndAkkusativ, bezugsobjekt);
    }

    /**
     * Erzeugt eine Nominalphrase ohne Bezugsobjekt.
     */
    public static Nominalphrase np(final NumerusGenus numerusGenus,
                                   @Nullable final Artikel.Typ artikelTyp,
                                   final String nominalNominativUndAkkusativ,
                                   final String nominalDativ) {
        return np(numerusGenus, artikelTyp, nominalNominativUndAkkusativ, nominalDativ,
                (IBezugsobjekt) null);
    }

    public static Nominalphrase np(final NumerusGenus numerusGenus,
                                   @Nullable final Artikel.Typ artikelTyp,
                                   final String nominalNominativUndAkkusativ,
                                   final String nominalDativ,
                                   @Nullable final IBezugsobjekt bezugsobjekt) {
        return np(numerusGenus, artikelTyp,
                nominalNominativUndAkkusativ, nominalDativ, nominalNominativUndAkkusativ,
                bezugsobjekt);
    }

    /**
     * Erzeugt eine Nominalphrase ohne Bezugsobjekt.
     */
    public static Nominalphrase np(final NumerusGenus numerusGenus,
                                   @Nullable final Artikel.Typ artikelTyp,
                                   final String nominalNominativ,
                                   final String nominalDativ,
                                   final String nominalAkkusativ) {
        return np(numerusGenus, artikelTyp, nominalNominativ, nominalDativ, nominalAkkusativ,
                null);
    }

    public static Nominalphrase np(final NumerusGenus numerusGenus,
                                   @Nullable final Artikel.Typ artikelTyp,
                                   final String nominalNominativ,
                                   final String nominalDativ,
                                   final String nominalAkkusativ,
                                   @Nullable final IBezugsobjekt bezugsobjekt) {
        return np(numerusGenus, artikelTyp,
                fr(nominalNominativ, nominalDativ, nominalAkkusativ), bezugsobjekt);
    }

    public static Nominalphrase np(final NumerusGenus numerusGenus,
                                   @Nullable final Artikel.Typ artikelTyp,
                                   final Flexionsreihe flexionsreiheArtikellos) {
        return np(numerusGenus, artikelTyp, flexionsreiheArtikellos, null);
    }

    public static Nominalphrase np(final NumerusGenus numerusGenus,
                                   @Nullable final Artikel.Typ artikelTyp,
                                   final Flexionsreihe flexionsreiheArtikellos,
                                   @Nullable final IBezugsobjekt bezugsobjekt) {
        return new Nominalphrase(numerusGenus, artikelTyp, flexionsreiheArtikellos, bezugsobjekt);
    }

    public Nominalphrase(final NumerusGenus numerusGenus,
                         @Nullable final Artikel.Typ artikelTyp,
                         final Flexionsreihe flexionsreiheArtikellos,
                         @Nullable final IBezugsobjekt bezugsobjekt) {
        this(null, numerusGenus, artikelTyp, flexionsreiheArtikellos, bezugsobjekt);
    }

    private Nominalphrase(final @Nullable String fokuspartikel,
                          final NumerusGenus numerusGenus,
                          @Nullable final Artikel.Typ artikelTyp,
                          final Flexionsreihe flexionsreiheArtikellos,
                          @Nullable final IBezugsobjekt bezugsobjekt) {
        super(fokuspartikel, numerusGenus, bezugsobjekt);
        this.artikelTyp = artikelTyp;
        this.flexionsreiheArtikellos = flexionsreiheArtikellos;
    }

    /**
     * Fügt der substantivischen Phrase etwas hinzu wie "auch", "allein", "ausgerechnet",
     * "wenigstens" etc.
     */
    @Override
    public Nominalphrase mitFokuspartikel(@Nullable final String fokuspartikel) {
        if (Objects.equals(getFokuspartikel(), fokuspartikel)) {
            return this;
        }

        return new Nominalphrase(fokuspartikel, getNumerusGenus(), artikelTyp,
                flexionsreiheArtikellos, getBezugsobjekt());
    }

    @Override
    public boolean erlaubtVerschmelzungVonPraepositionMitArtikel() {
        if (artikelTyp == null || getFokuspartikel() != null) {
            return false;
        }

        return artikelTyp.erlaubtVerschmelzungMitPraeposition();
    }

    @Override
    public String nomStr() {
        @Nullable final Artikel artikel = getArtikel();

        return joinToString(
                getFokuspartikel(),
                artikel != null ? artikel.nomStr() : null,
                flexionsreiheArtikellos.nom());
    }

    @Override
    public String datStr() {
        @Nullable final Artikel artikel = getArtikel();

        return joinToString(
                getFokuspartikel(),
                artikel != null ? artikel.datStr() : null,
                flexionsreiheArtikellos.dat());
    }

    @Override
    public String artikellosDatStr() {
        return joinToString(
                getFokuspartikel(),
                flexionsreiheArtikellos.dat());
    }

    @Override
    public String akkStr() {
        @Nullable final Artikel artikel = getArtikel();

        return joinToString(
                getFokuspartikel(),
                artikel != null ? artikel.akkStr() : null,
                flexionsreiheArtikellos.akk());
    }

    @Nullable
    public Artikel getArtikel() {
        return Artikel.get(artikelTyp, getNumerusGenus());
    }

    @Override
    public Personalpronomen persPron() {
        return Personalpronomen.get(P3, getNumerusGenus(), getBezugsobjekt());
    }

    @Override
    public Reflexivpronomen reflPron() {
        return Reflexivpronomen.get(P3, getNumerusGenus().getNumerus());
    }

    @Override
    public Possessivartikel possArt() {
        return Possessivartikel.get(P3, getNumerusGenus());
    }

    @Override
    public Relativpronomen relPron() {
        return Relativpronomen.get(P3, getNumerusGenus(), getBezugsobjekt());
    }

    @Override
    public boolean isUnbetontesPronomen() {
        return false;
    }

    @Override
    public Person getPerson() {
        return P3;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        final Nominalphrase that = (Nominalphrase) o;
        return artikelTyp == that.artikelTyp &&
                flexionsreiheArtikellos.equals(that.flexionsreiheArtikellos);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), artikelTyp, flexionsreiheArtikellos);
    }
}
