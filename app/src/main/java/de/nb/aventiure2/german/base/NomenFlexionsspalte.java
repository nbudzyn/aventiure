package de.nb.aventiure2.german.base;

import static com.google.common.base.Preconditions.checkArgument;
import static de.nb.aventiure2.german.base.Flexionsreihe.fr;
import static de.nb.aventiure2.german.base.Nominalphrase.np;
import static de.nb.aventiure2.german.base.NumerusGenus.F;
import static de.nb.aventiure2.german.base.NumerusGenus.M;
import static de.nb.aventiure2.german.base.NumerusGenus.N;
import static de.nb.aventiure2.german.base.NumerusGenus.PL_MFN;
import static de.nb.aventiure2.german.base.Person.P3;
import static de.nb.aventiure2.german.string.GermanStringUtil.capitalizeFirstLetter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Locale;
import java.util.regex.Pattern;

import javax.annotation.Nonnull;

import de.nb.aventiure2.german.adjektiv.AdjPhrOhneLeerstellen;
import de.nb.aventiure2.german.string.NoLetterException;

/**
 * Die Flexionsspalte eines Nomens, d.h. die verschiedenen Kasus-Formen für ein Nomen
 * bei festgelegtem Artikeltyp und Numerus. Beispiele: "der Abend" ("dem Abend", "den Abend"),
 * "eine Nacht", "Schlafen", "ein Eingeweihter", "die Eingeweihte", "die Eingeweihten" etc.
 */
public enum NomenFlexionsspalte implements EinzelneSubstantivischePhrase {
    // Flexionsspalten allgemeines Nomen ohne Bezugsobjekt

    ABEND(M),
    ABENDDAEMMERUNG(F, "Abenddämmerung"),
    ABENDHIMMEL(M),
    ABENDHITZE(F),
    ABENDLICHT(N),
    ABENDSONNE(F),
    ABENDSONNENSCHEIN(M),
    ABRISS(M),
    AESTE(PL_MFN, "Äste", "Ästen"),
    ANGEBOTE(PL_MFN, "Angebote", "Angeboten"),
    ASTGABEL(F),
    BANK(F),
    BAEUERIN(F, "Bäuerin"),
    BAUERNMARKT(M),
    BAUERSFRAU(F),
    BAUM(M),
    BAEUME(PL_MFN, "Bäume", "Bäumen"),
    BETT(N),
    BETTGESTELL(N),
    BINSEN(PL_MFN),
    BRETTERTISCH(M),
    DINGE(PL_MFN, "Dinge", "Dingen"),
    DAEMMERLICHT(N, "Dämmerlicht"),
    DUESTERNIS(F, "Düsternis"),
    DUNKEL(N),
    DUNKELHEIT(F),
    ELEFANTEN(PL_MFN),
    EISESKAELTE(F, "Eiseskälte"),
    FIRMAMENT(N),
    FRAU(F),
    FREUDE(F),
    FROSCH(M),
    FROST(M),
    FUSS(M, "Fuß"),
    GARTEN(M),
    GESCHIRR(N),
    GESPRAECH(N, "Gespräch"),
    HAAR(N),
    HAARE(PL_MFN, "Haare", "Haaren"),
    HAENDE(PL_MFN, "Hände", "Händen"),
    HALBDUNKEL(N),
    HASEN(PL_MFN),
    HELLE(N, "Helle", "Hellen"),
    HERZ(N, "Herz", "Herzen"),
    HELLEBARDE(F),
    HIMMEL(M),
    HITZE(F),
    HOEHE(F, "Höhe"),
    HOLZ(N),
    HORIZONT(M),
    INSEKTEN(PL_MFN),
    KAELTE(F, "Kälte"),
    KINN(N),
    KLAUBHOLZ(N),
    KOENIGSSOHN(M, "Königssohn"),
    KOERBE(PL_MFN, "Körbe", "Körben"),
    KORBFLECHTERIN(F),
    KRAFT(F),
    KUEHLE(N, "Kühle", "Kühlen"),
    KUGEL(F),
    LEIB(M, "Leib", "Leibe"),
    LICHT(N),
    LUFT(F),
    LUFTHAUCH(M),
    LUFTZUG(M),
    MANN(M),
    MARKT(M),
    MARKTSTAENDE(PL_MFN, "Marktstände", "Marktständen"),
    MUS(N),
    MITTAG(M),
    MITTAGSHITZE(F),
    MITTAGSSONNE(F),
    MITTE(F),
    MITTERNACHT(F),
    MARMORTREPPE(F),
    MOND(M),
    MONDLICHT(N),
    MONDSCHEIN(M),
    MONDSCHIMMER(M),
    MORAST(M),
    MORGEN(M),
    MORGENLICHT(N),
    MORGENSONNE(F),
    NACHT(F),
    NACHTHIMMEL(M),
    NAME(M, "Name", "Namen", "Namen"),
    NASE(F),
    NOTLUEGE(F, "Notlüge"),
    OSTEN(M),
    PLATZ(M),
    RABEN(PL_MFN),
    RAPUNZEL(F, true),
    RETTUNG(F),
    ROTWEINE(PL_MFN, "Rotweine", "Rotweinen"),
    RUNDHOELZER(PL_MFN, "Rundhölzer", "Rundhölzern"),
    SCHATTEN(M),
    SCHLOSSGARTEN(M),
    SCHLOSSWACHE(F),
    SCHUMMERLICHT(N),
    SONNE(F),
    SONNENHITZE(F),
    SONNENSCHEIN(M),
    SONNENSTRAHLEN(PL_MFN),
    SONNENUNTERGANG(M),
    STUECKE(PL_MFN, "Stücke", "Stücken"),
    TISCH(M),
    // FIXME "scheint der Mond *von dem* Sternenhimmel herab",
    //  "steigt die sonne *an dem* Firmament empor" (-> vom, am)
    STERNENHIMMEL(M),
    STERNENLICHT(N),
    STERNENZELT(N),
    STURM(M),
    STURMWIND(M),
    TAG(M),
    TAGESLICHT(N),
    TASCHE(F),
    TOEPFE(PL_MFN, "Töpfe", "Töpfen"),
    TUPFEN(PL_MFN),
    VOLLMOND(M),
    UNWETTER(N),
    WAERME(F, "Wärme"),
    WAHRHEIT(F),
    WETTER(N),
    WIND(M),
    WINDHAUCH(M),
    WOLKE(F),
    WOLKEN(PL_MFN),
    WOLKENFETZEN(PL_MFN),
    WOLKENDECKE(F),
    WOLKENFRONT(F),
    WUT(F),
    ZAUBERIN(F),
    ZEIT(F),
    ZIEL(N),
    ZWIELICHT(N);

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

    /**
     * Die Flexionsreihe, artikellos, - Achtung, kann auch {@code null}
     * sein - dann wird sie on the fly berechnet, vgl. {@link #getFlexionsreiheArtikellos()}.
     */
    @Nullable
    private final Flexionsreihe flexionsreiheArtikellos;

    /**
     * Erzeugt ein Nomen ohne Bezugsobjekt.
     */
    NomenFlexionsspalte(final NumerusGenus numerusGenus) {
        this(numerusGenus, false);
    }

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
                        final boolean grundsaetzlichArtikellos) {
        this(numerusGenus, grundsaetzlichArtikellos, (Flexionsreihe) null);
    }

    /**
     * Erzeugt ein Nomen ohne Bezugsobjekt.
     */
    @SuppressWarnings("unused")
    NomenFlexionsspalte(final NumerusGenus numerusGenus,
                        final boolean grundsaetzlichArtikellos,
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
                        final boolean grundsaetzlichArtikellos,
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
                "Nomen enthält Whitespace: %s", kasusform);
        return kasusform;
    }

    NomenFlexionsspalte(final NumerusGenus numerusGenus,
                        final boolean grundsaetzlichArtikellos,
                        @Nullable final Flexionsreihe flexionsreiheArtikellos) {
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
    public SubstantivischePhrase ohneNegationspartikelphrase() {
        return this;
    }

    @Nullable
    @Override
    public Negationspartikelphrase getNegationspartikelphrase() {
        return null;
    }

    @Override
    public SubstantivischePhrase neg(final Negationspartikelphrase negationspartikelphrase,
                                     final boolean moeglichstNegativIndefiniteWoerterVerwenden) {
        return toNominalphrase()
                .neg(negationspartikelphrase, moeglichstNegativIndefiniteWoerterVerwenden);
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
    public ArtikelwortFlexionsspalte.Typ possArt() {
        return ArtikelwortFlexionsspalte.getPossessiv(P3, getNumerusGenus());
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

    @Nonnull
    public Flexionsreihe getFlexionsreiheArtikellos() {
        if (flexionsreiheArtikellos != null) {
            return flexionsreiheArtikellos;
        }

        try {
            final String nominalNominativDativUndAkkusativ =
                    checkNoWhitespace(
                            capitalizeFirstLetter(
                                    name().toLowerCase(Locale.GERMANY)));
            return fr(nominalNominativDativUndAkkusativ);
        } catch (final NoLetterException e) {
            throw new IllegalStateException(e);
        }
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
