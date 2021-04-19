package de.nb.aventiure2.german.base;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.regex.Pattern;

import de.nb.aventiure2.german.adjektiv.AdjPhrOhneLeerstellen;

import static com.google.common.base.Preconditions.checkArgument;
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

    ABEND(M, "Abend"),
    ABENDDAEMMERUNG(F, "Abenddämmerung"),
    ABENDHIMMEL(M, "Abendhimmel"),
    ABENDHITZE(F, "Abendhitze"),
    ABENDLICHT(N, "Abendlicht"),
    ABENDSONNE(F, "Abendsonne"),
    ABENDSONNENSCHEIN(M, "Abendsonnenschein"),
    ABRISS(M, "Abriss"),
    ANGEBOTE(PL_MFN, "Angebote", "Angeboten"),
    ASTGABEL(F, "Astgabel"),
    BANK(F, "Bank"),
    BAUM(M, "Baum"),
    BAEUME(PL_MFN, "Bäume", "Bäumen"),
    BETT(N, "Bett"),
    BETTGESTELL(N, "Bettgestell"),
    BRETTERTISCH(M, "Brettertisch"),
    DINGE(PL_MFN, "Dinge", "Dingen"),
    DAEMMERLICHT(N, "Dämmerlicht"),
    DUESTERNIS(F, "Düsternis"),
    DUNKEL(N, "Dunkel"),
    DUNKELHEIT(F, "Dunkelheit"),
    ELEFANTEN(PL_MFN, "Elefanten"),
    EISESKAELTE(F, "Eiseskälte"),
    FIRMAMENT(N, "Firmament"),
    FRAU(F, "Frau"),
    FREUDE(F, "Freude"),
    FROSCH(M, "Frosch"),
    FUSS(M, "Fuß"),
    GARTEN(M, "Garten"),
    GESPRAECH(N, "Gespräch"),
    HAARE(PL_MFN, "Haare", "Haaren"),
    HAENDE(PL_MFN, "Hände", "Händen"),
    HALBDUNKEL(N, "Halbdunkel"),
    HELLE(N, "Helle", "Hellen"),
    HERZ(N, "Herz"),
    HELLEBARDE(F, "Hellebarde"),
    HIMMEL(M, "Himmel"),
    HITZE(F, "Hitze"),
    HOEHE(F, "Höhe"),
    KAELTE(F, "Kälte"),
    KINN(N, "Kinn"),
    KOENIGSSOHN(M, "Königssohn"),
    KUEHLE(N, "Kühle", "Kühlen"),
    KUGEL(F, "Kugel"),
    LEIB(M, "Leib", "Leibe"),
    LICHT(N, "Licht"),
    LUFT(F, "Luft"),
    MITTAGSHITZE(F, "Mittagshitze"),
    MITTAGSSONNE(F, "Mittagssonne"),
    MITTE(F, "Mitte"),
    MARMORTREPPE(F, "Marmortreppe"),
    MOND(M, "Mond"),
    MONDLICHT(N, "Mondlicht"),
    MONDSCHEIN(M, "Mondschein"),
    MONDSCHIMMER(M, "Mondschimmer"),
    MORGEN(M, "Morgen"),
    MORGENLICHT(N, "Morgenlicht"),
    MORGENSONNE(F, "Morgensonne"),
    NACHT(F, "Nacht"),
    NACHTHIMMEL(M, "Nachthimmel"),
    NAME(M, "Name", "Namen", "Namen"),
    NASE(F, "Nase"),
    NOTLUEGE(F, "Notlüge"),
    OSTEN(M, "Osten"),
    PLATZ(M, "Platz"),
    RAPUNZEL(F, true, "Rapunzel"),
    RETTUNG(F, "Rettung"),
    ROTWEINE(PL_MFN, "Rotweine", "Rotweinen"),
    SCHATTEN(M, "Schatten"),
    SCHLOSSWACHE(F, "Schlosswache"),
    SCHUMMERLICHT(N, "Schummerlicht"),
    SONNE(F, "Sonne"),
    SONNENHITZE(F, "Sonnenhitze"),
    SONNENSCHEIN(M, "Sonnenschein"),
    SONNENSTRAHLEN(PL_MFN, "Sonnenstrahlen"),
    SONNENUNTERGANG(M, "Sonnenuntergang"),
    TISCH(M, "Tisch"),
    STERNENHIMMEL(M, "Sternenhimmel"),
    STERNENLICHT(N, "Sternenlicht"),
    TAG(M, "Tag"),
    TAGESLICHT(N, "Tageslicht"),
    TASCHE(F, "Tasche"),
    VOLLMOND(M, "Vollmond"),
    WAERME(F, "Wärme"),
    WAHRHEIT(F, "Wahrheit"),
    WETTER(N, "Wetter"),
    WOLKEN(PL_MFN, "Wolken"),
    WUT(F, "Wut"),
    ZAUBERIN(F, "Zauberin"),
    ZIEL(N, "Ziel"),
    ZWIELICHT(N, "Zwielicht");

    private final NumerusGenus numerusGenus;

    /**
     * Ob das Nomen <i>grundsätzlich artikellos</i> ist, also
     * in aller Regel ohne Artikel steht (z.B. Eigennamen: "Rapunzel").
     * <p>
     * Auch Nomen, die nicht "grundsätzlich artikellos sind",
     * können allerdings artikellos auftreten, z.B. "ich erhoffe mir Retttung".
     * Außerdem können grundsätzlich artikellose Nomen auch Artikel erhalten:
     * "eine Anna".
     */
    private final boolean grundsaetzlichArtikellos;

    // Es gibt außerdem seltene Fälle, wo die Deklination ausfallen kann und statt
    // eines z.b. Dativs die Nominativ-Form steht. (Man könnte das
    // "Ersatznominativ" nennen: "für den Arzt und den Patienten" / "für Arzt und Patient").
    // Im einfachen Fall eines definiten oder indefinitiven Artikels scheint immer
    // die volle Deklination zulässig zu sein - vielleicht sogar in jedem Fall.

    private final Flexionsreihe flexionsreiheArtikellos;

    /**
     * Erzeugt ein Nomen ohne Bezugsobjekt.
     */
    NomenFlexionsspalte(final NumerusGenus numerusGenus,
                        final String nominalNominativDativUndAkkusativ) {
        this(numerusGenus, nominalNominativDativUndAkkusativ, nominalNominativDativUndAkkusativ);
    }

    /**
     * Erzeugt ein Nomen ohne Bezugsobjekt.
     */
    NomenFlexionsspalte(final NumerusGenus numerusGenus,
                        @Nullable final boolean grundsaetzlichArtikellos,
                        final String nominalNominativDativUndAkkusativ) {
        this(numerusGenus, grundsaetzlichArtikellos,
                nominalNominativDativUndAkkusativ, nominalNominativDativUndAkkusativ);
    }

    /**
     * Erzeugt ein Nomen ohne Bezugsobjekt.
     */
    NomenFlexionsspalte(final NumerusGenus numerusGenus,
                        final String nominalNominativUndAkkusativ,
                        final String nominalDativ) {
        this(numerusGenus, nominalNominativUndAkkusativ, nominalDativ,
                nominalNominativUndAkkusativ);
    }


    /**
     * Erzeugt ein Nomen ohne Bezugsobjekt.
     */
    NomenFlexionsspalte(final NumerusGenus numerusGenus,
                        @Nullable final boolean grundsaetzlichArtikellos,
                        final String nominalNominativUndAkkusativ,
                        final String nominalDativ) {
        this(numerusGenus, grundsaetzlichArtikellos,
                nominalNominativUndAkkusativ, nominalDativ, nominalNominativUndAkkusativ);
    }


    /**
     * Erzeugt ein Nomen ohne Bezugsobjekt.
     */
    NomenFlexionsspalte(final NumerusGenus numerusGenus,
                        final String nominalNominativ,
                        final String nominalDativ,
                        final String nominalAkkusativ) {
        this(numerusGenus, false,
                nominalNominativ,
                nominalDativ,
                nominalAkkusativ);
    }


    /**
     * Erzeugt ein Nomen ohne Bezugsobjekt.
     */
    NomenFlexionsspalte(final NumerusGenus numerusGenus,
                        final boolean grundsaetzlichArtikellos,
                        final String nominalNominativ,
                        final String nominalDativ,
                        final String nominalAkkusativ) {
        this(numerusGenus, grundsaetzlichArtikellos,
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
                        final boolean grundsaetzlichArtikellos,
                        final Flexionsreihe flexionsreiheArtikellos) {
        this.numerusGenus = numerusGenus;
        this.grundsaetzlichArtikellos = grundsaetzlichArtikellos;
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
    public String artikellosAkkStr() {
        return toNominalphrase().artikellosAkkStr();
    }

    @Override
    public String akkStr() {
        return toNominalphrase().akkStr();
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

    public boolean isGrundsaetzlichArtikellos() {
        return grundsaetzlichArtikellos;
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
