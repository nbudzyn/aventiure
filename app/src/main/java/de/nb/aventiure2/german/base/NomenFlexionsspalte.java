package de.nb.aventiure2.german.base;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.regex.Pattern;

import de.nb.aventiure2.german.adjektiv.AdjPhrOhneLeerstellen;

import static com.google.common.base.Preconditions.checkArgument;
import static de.nb.aventiure2.german.base.Artikel.Typ.DEF;
import static de.nb.aventiure2.german.base.Artikel.Typ.INDEF;
import static de.nb.aventiure2.german.base.Flexionsreihe.fr;
import static de.nb.aventiure2.german.base.Nominalphrase.np;
import static de.nb.aventiure2.german.base.NumerusGenus.F;
import static de.nb.aventiure2.german.base.NumerusGenus.M;
import static de.nb.aventiure2.german.base.NumerusGenus.N;
import static de.nb.aventiure2.german.base.NumerusGenus.PL_MFN;
import static de.nb.aventiure2.german.base.Person.P3;

/**
 * Die Flexionsspalte eines Nomens, d.h. die verschiedenen Kasus-Formen für ein Nomen
 * bei festgelegtem Artikeltyp und Numerus. Beispiele: "der Abend" ("dem Abend", "den Abend"),
 * "eine Nacht", "Schlafen", "ein Eingeweihter", "die Eingeweihte", "die Eingeweihten" etc.
 */
public enum NomenFlexionsspalte implements EinzelneSubstantivischePhrase {
    // Flexionsspalten allgemeines Nomen ohne Bezugsobjekt

    ABEND(M, DEF, "Abend"),
    ABEND_EIN(M, INDEF, "Abend"),
    ABENDHIMMEL(M, DEF, "Abendhimmel"),
    ABENDLICHT(N, DEF, "Abendlicht"),
    ABENDSONNE(F, DEF, "Abendsonne"),
    ABENDSONNENSCHEIN(M, DEF, "Abendsonnenschein"),
    ABRISS_EIN(M, INDEF, "Abriss"),
    ANGEBOTE_OHNE_ART(PL_MFN, INDEF, "Angebote", "Angeboten"),
    ASTGABEL(F, DEF, "Astgabel"),
    BANK(F, DEF, "Bank"),
    BAUM(M, DEF, "Baum"),
    BAUM_EIN(M, INDEF, "Baum"),
    BAEUME(PL_MFN, DEF, "Bäume", "Bäumen"),
    BETT(N, DEF, "Bett"),
    BETT_EIN(N, INDEF, "Bett"),
    BETTGESTELL(N, DEF, "Bettgestell"),
    BETTGESTELL_EIN(N, INDEF, "Bettgestell"),
    BRETTERTISCH(M, DEF, "Brettertisch"),
    DINGE(PL_MFN, DEF, "Dinge", "Dingen"),
    DAEMMERLICHT(N, DEF, "Dämmerlicht"),
    DUESTERNIS(F, DEF, "Düsternis"),
    DUNKEL(N, DEF, "Dunkel"),
    DUNKELHEIT(F, DEF, "Dunkelheit"),
    ELEFANTEN_INDEF(PL_MFN, INDEF, "Elefanten"),
    FRAU(F, DEF, "Frau"),
    FREUDE_OHNE_ART(F, null, "Freude"),
    FROSCH(M, DEF, "Frosch"),
    FUSS(M, DEF, "Fuß"),
    GARTEN(M, DEF, "Garten"),
    GESPRAECH(N, DEF, "Gespräch"),
    GESPRAECH_EIN(N, INDEF, "Gespräch"),
    HAARE(PL_MFN, DEF, "Haare", "Haaren"),
    HAENDE(PL_MFN, DEF, "Hände", "Händen"),
    HALBDUNKEL(N, DEF, "Halbdunkel"),
    HERZ(N, DEF, "Herz"),
    HELLEBARDE(F, DEF, "Hellebarde"),
    HIMMEL(M, DEF, "Himmel"),
    HOEHE(F, DEF, "Höhe"),
    KAELTE(F, DEF, "Kälte"),
    KAELTE_OHNE_ART(F, null, "Kälte"),
    KINN(N, DEF, "Kinn"),
    KOENIGSSOHN(M, DEF, "Königssohn"),
    KUGEL(F, DEF, "Kugel"),
    LEIB(M, DEF, "Leib", "Leibe"),
    LICHT(N, DEF, "Licht"),
    LICHT_OHNE_ART(N, null, "Licht"),
    MITTAGSHITZE(F, DEF, "Mittagshitze"),
    MITTAGSSONNE(F, DEF, "Mittagssonne"),
    MITTE(F, DEF, "Mitte"),
    MOND(M, DEF, "Mond"),
    MONDLICHT(N, DEF, "Mondlicht"),
    MONDSCHEIN(M, DEF, "Mondschein"),
    MORGEN(M, DEF, "Morgen"),
    MORGENLICHT(N, DEF, "Morgenlicht"),
    MORGENSONNE(F, DEF, "Morgensonne"),
    NACHT(F, DEF, "Nacht"),
    NACHTHIMMEL(M, DEF, "Nachthimmel"),
    NAME(M, DEF, "Name", "Namen", "Namen"),
    NASE(F, DEF, "Nase"),
    NOTLUEGE_EINE(F, INDEF, "Notlüge"),
    PLATZ(M, DEF, "Platz"),
    RAPUNZEL(F, null, "Rapunzel"),
    RETTUNG_OHNE_ART(F, null, "Rettung"),
    ROTWEINE_INDEF(PL_MFN, INDEF, "Rotweine", "Rotweinen"),
    SCHATTEN(M, DEF, "Schatten"),
    SCHLOSSWACHE(F, DEF, "Schlosswache"),
    SCHUMMERLICHT(N, DEF, "Schummerlicht"),
    SONNE(F, DEF, "Sonne"),
    SONNENHITZE(F, DEF, "Sonnenhitze"),
    SONNENSCHEIN(M, DEF, "Sonnenschein"),
    SONNENSTRAHLEN(PL_MFN, DEF, "Sonnenstrahlen"),
    TISCH(M, DEF, "Tisch"),
    STERNENHIMMEL(M, DEF, "Sternenhimmel"),
    STERNENLICHT(N, DEF, "Sternenlicht"),
    TAG(M, DEF, "Tag"),
    TAG_EIN(M, INDEF, "Tag"),
    TAGESLICHT(N, DEF, "Tageslicht"),
    TAGESLICHT_OHNE_ART(N, null, "Tageslicht"),
    TASCHE_EINE(F, INDEF, "Tasche"),
    VOLLMOND(M, DEF, "Vollmond"),
    WAHRHEIT(F, DEF, "Wahrheit"),
    WETTER_EIN(N, INDEF, "Wetter"),
    WETTER_OHNE_ART(N, null, "Wetter"),
    WOLKEN_OHNE_ART(PL_MFN, INDEF, "Wolken"),
    WUT_OHNE_ART(F, null, "Wut"),
    ZAUBERIN(F, DEF, "Zauberin"),
    ZAUBERIN_EINE(F, INDEF, "Zauberin"),
    ZIEL(N, DEF, "Ziel"),
    ZWIELICHT(N, DEF, "Zwielicht");

    private final NumerusGenus numerusGenus;

    @Nullable
    private final Artikel.Typ artikelTyp;

    private final Flexionsreihe flexionsreiheArtikellos;

    /**
     * Erzeugt ein Nomen ohne Bezugsobjekt.
     */
    NomenFlexionsspalte(final NumerusGenus numerusGenus,
                        @Nullable final Artikel.Typ artikelTyp,
                        final String nominalNominativDativUndAkkusativ) {
        this(numerusGenus, artikelTyp,
                nominalNominativDativUndAkkusativ, nominalNominativDativUndAkkusativ);
    }

    /**
     * Erzeugt ein Nomen ohne Bezugsobjekt.
     */
    NomenFlexionsspalte(final NumerusGenus numerusGenus,
                        @Nullable final Artikel.Typ artikelTyp,
                        final String nominalNominativUndAkkusativ,
                        final String nominalDativ) {
        this(numerusGenus, artikelTyp,
                nominalNominativUndAkkusativ, nominalDativ, nominalNominativUndAkkusativ);
    }

    /**
     * Erzeugt ein Nomen ohne Bezugsobjekt.
     */
    NomenFlexionsspalte(final NumerusGenus numerusGenus,
                        @Nullable final Artikel.Typ artikelTyp,
                        final String nominalNominativ,
                        final String nominalDativ,
                        final String nominalAkkusativ) {
        this(numerusGenus, artikelTyp,
                fr(checkNoWhitespace(nominalNominativ),
                        checkNoWhitespace(nominalDativ),
                        checkNoWhitespace(nominalAkkusativ)));
    }

    private static String checkNoWhitespace(final String kasusform) {
        checkArgument(!Pattern.compile(".*\\s.*").matcher(kasusform).matches(),
                "Nomen enthält Whitespace: " + kasusform);
        return kasusform;
    }

    NomenFlexionsspalte(final NumerusGenus numerusGenus,
                        @Nullable final Artikel.Typ artikelTyp,
                        final Flexionsreihe flexionsreiheArtikellos) {
        this.numerusGenus = numerusGenus;
        this.artikelTyp = artikelTyp;
        this.flexionsreiheArtikellos = flexionsreiheArtikellos;
    }

    @Override
    public SubstantivischePhrase ohneFokuspartikel() {
        return this;
    }

    @Nullable
    @Override
    public String getFokuspartikel() {
        return null;
    }

    /**
     * Fügt der substantivischen Phrase etwas hinzu wie "auch", "allein", "ausgerechnet",
     * "wenigstens" etc.
     */
    @Override
    public Nominalphrase mitFokuspartikel(@Nullable final String fokuspartikel) {
        return toNominalphrase().mitFokuspartikel(fokuspartikel);
    }

    @Override
    public boolean erlaubtVerschmelzungVonPraepositionMitArtikel() {
        return toNominalphrase().erlaubtVerschmelzungVonPraepositionMitArtikel();
    }

    @Override
    public String nomStr() {
        return toNominalphrase().nomStr();
    }

    @Override
    public String datStr() {
        return toNominalphrase().datStr();
    }

    @Override
    public String artikellosDatStr() {
        return toNominalphrase().artikellosDatStr();
    }

    @Override
    public String akkStr() {
        return toNominalphrase().akkStr();
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

    @Nullable
    public Artikel.Typ getArtikelTyp() {
        return artikelTyp;
    }

    public Flexionsreihe getFlexionsreiheArtikellos() {
        return flexionsreiheArtikellos;
    }

    @Override
    @Nullable
    public IBezugsobjekt getBezugsobjekt() {
        return null;
    }

    @Override
    public NumerusGenus getNumerusGenus() {
        return numerusGenus;
    }

    public Nominalphrase mit(final AdjPhrOhneLeerstellen adjPhr) {
        return np(adjPhr, this);
    }

    @NonNull
    private Nominalphrase toNominalphrase() {
        return np(this);
    }
}
