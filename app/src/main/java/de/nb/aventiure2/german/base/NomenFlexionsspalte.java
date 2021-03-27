package de.nb.aventiure2.german.base;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.regex.Pattern;

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
    ABENDHIMMEL(M, DEF, "Abendhimmel"),
    ABENDLICHT(N, DEF, "Abendlicht"),
    ABENDSONNE(F, DEF, "Abendsonne"),
    ABENDSONNENSCHEIN(M, DEF, "Abendsonnenschein"),
    ANGEBOTE_OHNE_ART(PL_MFN, INDEF, "Angebote", "Angeboten"),
    ASTGABEL(F, DEF, "Astgabel"),
    BAUM(M, DEF, "Baum"),
    BETT(N, DEF, "Bett"),
    BETTGESTELL(N, DEF, "Bettgestell"),
    DINGE(PL_MFN, DEF, "Dinge", "Dingen"),
    DAEMMERLICHT(N, DEF, "Dämmerlicht"),
    DUESTERNIS(F, DEF, "Düsternis"),
    DUNKEL(N, DEF, "Dunkel"),
    DUNKELHEIT(F, DEF, "Dunkelheit"),
    EIN_BETT(N, INDEF, "Bett"),
    EIN_BETTGESTELL(N, INDEF, "Bettgestell"),
    EIN_GESPRAECH(N, INDEF, "Gespräch"),
    EINE_TASCHE(F, INDEF, "Tasche"),
    FRAU(F, DEF, "Frau"),
    FREUDE_OHNE_ART(F, null, "Freude"),
    FROSCH(M, DEF, "Frosch"),
    FUSS(M, DEF, "Fuß"),
    GESPRAECH(N, DEF, "Gespräch"),
    HAENDE(PL_MFN, DEF, "Hände", "Händen"),
    HALBDUNKEL(N, DEF, "Halbdunkel"),
    HIMMEL(M, DEF, "Himmel"),
    HOEHE(F, DEF, "Höhe"),
    KUGEL(F, DEF, "Kugel"),
    LEIB(M, DEF, "Leib", "Leibe"),
    LICHT_OHNE_ART(N, null, "Licht"),
    MITTAGSSONNE(F, DEF, "Mittagssonne"),
    MOND(M, DEF, "Mond"),
    MONDLICHT(N, DEF, "Mondlicht"),
    MONDSCHEIN(M, DEF, "Mondschein"),
    MORGEN(M, DEF, "Morgen"),
    MORGENLICHT(N, DEF, "Morgenlicht"),
    MORGENSONNE(F, DEF, "Morgensonne"),
    NACHT(F, DEF, "Nacht"),
    NACHTHIMMEL(M, DEF, "Nachthimmel"),
    RAPUNZEL(F, null, "Rapunzel"),
    RETTUNG_OHNE_ART(F, null, "Rettung"),
    SCHATTEN(M, DEF, "Schatten"),
    SCHLOSSWACHE(F, DEF, "Schlosswache"),
    SCHUMMERLICHT(N, DEF, "Schummerlicht"),
    SONNE(F, DEF, "Sonne"),
    SONNENHITZE(F, DEF, "Sonnenhitze"),
    SONNENSCHEIN(M, DEF, "Sonnenschein"),
    TAG(M, DEF, "Tag"),
    STERNENHIMMEL(M, DEF, "Sternenhimmel"),
    STERNENLICHT(N, DEF, "Sternenlicht"),
    TAGESLICHT(N, DEF, "Tageslicht"),
    TAGESLICHT_OHNE_ART(N, null, "Tageslicht"),
    TISCH(M, DEF, "Tisch"),
    VOLLMOND(M, DEF, "Vollmond"),
    WAHRHEIT(F, DEF, "Wahrheit"),
    WUT_OHNE_ART(F, null, "Wut"),
    ZAUBERIN(F, DEF, "Zauberin"),
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

    // FIXME mit(AdjPhr)

    @NonNull
    private Nominalphrase toNominalphrase() {
        return np(this);
    }
}
