package de.nb.aventiure2.german.base;

import static de.nb.aventiure2.german.base.ArtikelwortFlexionsspalte.Typ.DEF;
import static de.nb.aventiure2.german.base.Belebtheit.UNBELEBT;
import static de.nb.aventiure2.german.base.Flexionsreihe.fr;
import static de.nb.aventiure2.german.base.IArtikelworttypOderVorangestelltesGenitivattribut.getNegativeForm;
import static de.nb.aventiure2.german.base.IArtikelworttypOderVorangestelltesGenitivattribut.isNegativ;
import static de.nb.aventiure2.german.base.Kasus.AKK;
import static de.nb.aventiure2.german.base.Kasus.DAT;
import static de.nb.aventiure2.german.base.Kasus.NOM;
import static de.nb.aventiure2.german.base.Konstituentenfolge.joinToKonstituentenfolge;
import static de.nb.aventiure2.german.base.Konstituentenfolge.schliesseInKommaEin;
import static de.nb.aventiure2.german.base.NumerusGenus.F;
import static de.nb.aventiure2.german.base.NumerusGenus.M;
import static de.nb.aventiure2.german.base.NumerusGenus.PL_MFN;
import static de.nb.aventiure2.german.base.Person.P3;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Objects;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;

import de.nb.aventiure2.german.adjektiv.AdjPhrOhneLeerstellen;
import de.nb.aventiure2.german.satz.SyntSatz;

/**
 * Eine Nominalphrase, z.B. "ein dicker, hässlicher Frosch".
 */
// FIXME SemNominalphrase (wegen Relativsatz)? Oder irgendwie "generisch-agnostisch"?
public class Nominalphrase
        extends EinzelneKomplexeSubstantivischePhrase
        implements IErlaubtAttribute {
    @Nullable
    private final IArtikelworttypOderVorangestelltesGenitivattribut
            artikelworttypOderVorangestelltesGenitivattribut;

    @Nullable
    private final AdjPhrOhneLeerstellen adjPhr;

    private final Flexionsreihe flexionsreiheArtikellos;

    // Allgemeine Nominalphrasen ohne Bezugsobjekt
    public static final Nominalphrase BLICK_AUF_DEN_STERNENHIMMEL =
            np(M, DEF, "Blick auf den Sternenhimmel");
    public static final Nominalphrase BRUETENDE_HITZE_DER_MITTAGSSONNE =
            np(F, DEF, "brütende Hitze der Mittagssonne",
                    "brütenden Hitze der Mittagssonne");
    public static final Nominalphrase BRUETENDE_HITZE_DER_SONNE =
            np(F, DEF, "brütende Hitze der Sonne",
                    "brütenden Hitze der Sonne");
    public static final Nominalphrase DRUECKENDE_HITZE_DER_MITTAGSSONNE =
            np(F, DEF, "drückende Hitze der Mittagssonne",
                    "drückenden Hitze der Mittagssonne");
    public static final Nominalphrase DRUECKENDE_HITZE_DER_SONNE =
            np(F, DEF, "drückende Hitze der Sonne",
                    "drückenden Hitze der Sonne");
    // Problem hier: "erst"* ist offenbar ein Adjektiv, kann aber nur
    // attributiv verwendet werden. Das ist bisher nicht vorgesetzen.
    public static final Nominalphrase ERSTER_SONNENSTRAHL =
            np(M, DEF, "erste Sonnenstrahl",
                    "ersten Sonnenstrahl", "ersten Sonnenstrahl");
    public static final Nominalphrase ERSTER_STRAHL_DER_AUFGEHENDEN_SONNE =
            np(M, DEF, "erste Strahl der aufgehenden Sonne",
                    "ersten Strahl der aufgehenden Sonne",
                    "ersten Strahl der aufgehenden Sonne");
    // Problem hier: "erst"* ist anscheinend ein Adjektiv, kann aber nur
    // attributiv verwendet werden. Das ist bisher nicht vorgesetzen.
    public static final Nominalphrase ERSTE_SONNENSTRAHLEN =
            np(PL_MFN, DEF, "ersten Sonnenstrahlen");
    public static final Nominalphrase ERSTE_STRAHLEN_DER_AUFGEHENDEN_SONNE =
            np(PL_MFN, DEF, "ersten Strahlen der aufgehenden Sonne");
    // Problem hier: "letzt"* ist anscheinend ein Adjektiv, kann aber nur
    // attributiv verwendet werden. Das ist bisher nicht vorgesetzen.
    public static final Nominalphrase LETZTE_ZIRREN =
            np(PL_MFN, DEF, "letzten Zirren");
    public static final Nominalphrase SCHUTZ_VOR_DEM_AERGSTEN_STURM =
            np(M, null, "Schutz vor dem ärgsten Sturm");
    public static final Nominalphrase VON_DER_SONNE_AUFGEHEIZTE_STEHENDE_LUFT =
            np(F, DEF, "von der Sonne aufgeheizte stehende Luft",
                    "von der Sonne aufgeheizten stehenden Luft");

    /**
     * Erzeugt eine Nominalphrase (unbelebt) ohne Bezugsobjekt.
     */
    @NonNull
    private static Nominalphrase np(final NumerusGenus numerusGenus,
                                    @Nullable
                                    final IArtikelworttypOderVorangestelltesGenitivattribut artikelworttypOderVorangestelltesGenitivattribut,
                                    final String nominalNominativDativUndAkkusativ) {
        return np(numerusGenus, artikelworttypOderVorangestelltesGenitivattribut,
                nominalNominativDativUndAkkusativ,
                UNBELEBT);
    }

    /**
     * Erzeugt eine Nominalphrase ohne Bezugsobjekt.
     */
    @NonNull
    private static Nominalphrase np(final NumerusGenus numerusGenus,
                                    @Nullable
                                    final IArtikelworttypOderVorangestelltesGenitivattribut artikelworttypOderVorangestelltesGenitivattribut,
                                    final String nominalNominativDativUndAkkusativ,
                                    final Belebtheit belebtheit) {
        return np(numerusGenus, artikelworttypOderVorangestelltesGenitivattribut,
                nominalNominativDativUndAkkusativ,
                belebtheit, null);
    }

    @NonNull
    public static Nominalphrase np(final NumerusGenus numerusGenus,
                                   @Nullable
                                   final IArtikelworttypOderVorangestelltesGenitivattribut artikelworttypOderVorangestelltesGenitivattribut,
                                   final String nominalNominativDativUndAkkusativ,
                                   final Belebtheit belebtheit,
                                   @Nullable final IBezugsobjekt bezugsobjekt) {
        return np(numerusGenus, artikelworttypOderVorangestelltesGenitivattribut,
                nominalNominativDativUndAkkusativ, nominalNominativDativUndAkkusativ, belebtheit,
                bezugsobjekt
        );
    }

    /**
     * Erzeugt eine Nominalphrase (unbelebt) ohne Bezugsobjekt.
     */
    public static Nominalphrase np(final NumerusGenus numerusGenus,
                                   @Nullable
                                   final IArtikelworttypOderVorangestelltesGenitivattribut artikelworttypOderVorangestelltesGenitivattribut,
                                   final String nominalNominativUndAkkusativ,
                                   final String nominalDativ) {
        return np(numerusGenus, artikelworttypOderVorangestelltesGenitivattribut,
                nominalNominativUndAkkusativ, nominalDativ, UNBELEBT);
    }

    /**
     * Erzeugt eine Nominalphrase ohne Bezugsobjekt.
     */
    public static Nominalphrase np(final NumerusGenus numerusGenus,
                                   @Nullable
                                   final IArtikelworttypOderVorangestelltesGenitivattribut artikelworttypOderVorangestelltesGenitivattribut,
                                   final String nominalNominativUndAkkusativ,
                                   final String nominalDativ,
                                   final Belebtheit belebtheit) {
        return np(numerusGenus, artikelworttypOderVorangestelltesGenitivattribut,
                nominalNominativUndAkkusativ, nominalDativ,
                belebtheit, null);
    }

    @NonNull
    public static Nominalphrase np(final NumerusGenus numerusGenus,
                                   @Nullable
                                   final IArtikelworttypOderVorangestelltesGenitivattribut artikelworttypOderVorangestelltesGenitivattribut,
                                   final String nominalNominativUndAkkusativ,
                                   final String nominalDativ,
                                   final Belebtheit belebtheit,
                                   @Nullable final IBezugsobjekt bezugsobjekt) {
        return np(numerusGenus, artikelworttypOderVorangestelltesGenitivattribut,
                nominalNominativUndAkkusativ, nominalDativ, nominalNominativUndAkkusativ,
                belebtheit, bezugsobjekt);
    }

    /**
     * Erzeugt eine Nominalphrase (unbelebt) ohne Bezugsobjekt.
     */
    @NonNull
    private static Nominalphrase np(final NumerusGenus numerusGenus,
                                    @Nullable
                                    final IArtikelworttypOderVorangestelltesGenitivattribut artikelworttypOderVorangestelltesGenitivattribut,
                                    final String nominalNominativ,
                                    final String nominalDativ,
                                    final String nominalAkkusativ) {
        return np(numerusGenus, artikelworttypOderVorangestelltesGenitivattribut, nominalNominativ,
                nominalDativ, nominalAkkusativ,
                UNBELEBT);
    }

    /**
     * Erzeugt eine Nominalphrase ohne Bezugsobjekt.
     */
    @NonNull
    private static Nominalphrase np(final NumerusGenus numerusGenus,
                                    @Nullable
                                    final IArtikelworttypOderVorangestelltesGenitivattribut artikelworttypOderVorangestelltesGenitivattribut,
                                    final String nominalNominativ,
                                    final String nominalDativ,
                                    final String nominalAkkusativ,
                                    final Belebtheit belebtheit) {
        return np(numerusGenus, artikelworttypOderVorangestelltesGenitivattribut, nominalNominativ,
                nominalDativ, nominalAkkusativ,
                belebtheit, null);
    }

    @NonNull
    public static Nominalphrase np(final NumerusGenus numerusGenus,
                                   @Nullable
                                   final IArtikelworttypOderVorangestelltesGenitivattribut artikelworttypOderVorangestelltesGenitivattribut,
                                   final String nominalNominativ,
                                   final String nominalDativ,
                                   final String nominalAkkusativ,
                                   @Nullable final IBezugsobjekt bezugsobjekt) {
        return np(numerusGenus, artikelworttypOderVorangestelltesGenitivattribut,
                nominalNominativ, nominalDativ, nominalAkkusativ, UNBELEBT, bezugsobjekt
        );
    }

    @NonNull
    public static Nominalphrase np(final NumerusGenus numerusGenus,
                                   @Nullable
                                   final IArtikelworttypOderVorangestelltesGenitivattribut artikelworttypOderVorangestelltesGenitivattribut,
                                   final String nominalNominativ,
                                   final String nominalDativ,
                                   final String nominalAkkusativ,
                                   final Belebtheit belebtheit,
                                   @Nullable final IBezugsobjekt bezugsobjekt) {
        return np(numerusGenus, artikelworttypOderVorangestelltesGenitivattribut,
                fr(nominalNominativ, nominalDativ, nominalAkkusativ), belebtheit, bezugsobjekt
        );
    }

    /**
     * Erzeugt eine Nominalphrase mit definitem Artikel - oder ohne Artikel,
     * falls das Nomen grundsätzlich artikellos ist.
     */
    @NonNull
    public static Nominalphrase np(final NomenFlexionsspalte nomenFlexionsspalte) {
        return np(nomenFlexionsspalte.isGrundsaetzlichArtikellos() ?
                        null : DEF,
                nomenFlexionsspalte, null);
    }

    @NonNull
    public static Nominalphrase npArtikellos(final NomenFlexionsspalte nomenFlexionsspalte) {
        return np(null, null, nomenFlexionsspalte, null);
    }

    @NonNull
    public static Nominalphrase np(
            final IArtikelworttypOderVorangestelltesGenitivattribut artikelworttypOderVorangestelltesGenitivattribut,
            final NomenFlexionsspalte nomenFlexionsspalte) {
        return np(artikelworttypOderVorangestelltesGenitivattribut, nomenFlexionsspalte, null);
    }

    @NonNull
    public static Nominalphrase np(final NumerusGenus numerusGenus,
                                   @Nullable
                                   final IArtikelworttypOderVorangestelltesGenitivattribut artikelworttypOderVorangestelltesGenitivattribut,
                                   final Flexionsreihe flexionsreiheArtikellos) {
        return np(numerusGenus, artikelworttypOderVorangestelltesGenitivattribut,
                flexionsreiheArtikellos, UNBELEBT);
    }

    @NonNull
    public static Nominalphrase np(final NumerusGenus numerusGenus,
                                   @Nullable
                                   final IArtikelworttypOderVorangestelltesGenitivattribut artikelworttypOderVorangestelltesGenitivattribut,
                                   final Flexionsreihe flexionsreiheArtikellos,
                                   final Belebtheit belebtheit) {
        return np(numerusGenus, artikelworttypOderVorangestelltesGenitivattribut,
                flexionsreiheArtikellos, belebtheit, null
        );
    }

    @NonNull
    public static Nominalphrase np(@Nullable final AdjPhrOhneLeerstellen adjPhr,
                                   final NomenFlexionsspalte nomenFlexionsspalte) {
        return np(adjPhr, nomenFlexionsspalte, null);
    }

    /**
     * Erzeugt eine Nominalphrase mit definitem Artikel - oder ohne Artikel,
     * falls das Nomen grundsätzlich artikellos ist.
     */
    @NonNull
    public static Nominalphrase np(final NomenFlexionsspalte nomenFlexionsspalte,
                                   @Nullable final IBezugsobjekt bezugsobjekt) {
        return np(nomenFlexionsspalte.isGrundsaetzlichArtikellos() ?
                        null : DEF,
                nomenFlexionsspalte, bezugsobjekt);
    }

    @NonNull
    public static Nominalphrase npArtikellos(@Nullable final AdjPhrOhneLeerstellen adjPhr,
                                             final NomenFlexionsspalte nomenFlexionsspalte) {
        return np(null, adjPhr, nomenFlexionsspalte, null);
    }

    @NonNull
    public static Nominalphrase np(
            @Nullable final IArtikelworttypOderVorangestelltesGenitivattribut artikeltyp,
            @Nullable final AdjPhrOhneLeerstellen adjPhr,
            final NomenFlexionsspalte nomenFlexionsspalte) {
        return np(artikeltyp, adjPhr, nomenFlexionsspalte, null);
    }

    @NonNull
    public static Nominalphrase npArtikellos(final NomenFlexionsspalte nomenFlexionsspalte,
                                             @Nullable final IBezugsobjekt bezugsobjekt) {
        return np(null, null, nomenFlexionsspalte, bezugsobjekt);
    }

    @NonNull
    public static Nominalphrase np(
            @Nullable final IArtikelworttypOderVorangestelltesGenitivattribut artikeltyp,
            final NomenFlexionsspalte nomenFlexionsspalte,
            @Nullable final IBezugsobjekt bezugsobjekt) {
        return np(artikeltyp, null, nomenFlexionsspalte, bezugsobjekt);
    }

    /**
     * Erzeugt eine artikellose Nominalphrase
     */
    @NonNull
    public static Nominalphrase npArtikellos(@Nullable final AdjPhrOhneLeerstellen adjPhr,
                                             final NomenFlexionsspalte nomenFlexionsspalte,
                                             @Nullable final IBezugsobjekt bezugsobjekt) {
        return np(null, adjPhr, nomenFlexionsspalte, bezugsobjekt);
    }

    /**
     * Erzeugt eine Nominalphrase mit definitem Artikel - oder ohne Artikel,
     * falls das Nomen grundsätzlich artikellos ist.
     */
    @NonNull
    public static Nominalphrase np(@Nullable final AdjPhrOhneLeerstellen adjPhr,
                                   final NomenFlexionsspalte nomenFlexionsspalte,
                                   @Nullable final IBezugsobjekt bezugsobjekt) {
        return np(nomenFlexionsspalte.isGrundsaetzlichArtikellos() ?
                        null : DEF,
                adjPhr, nomenFlexionsspalte, bezugsobjekt);
    }

    @NonNull
    public static Nominalphrase np(
            @Nullable final IArtikelworttypOderVorangestelltesGenitivattribut artikeltyp,
            @Nullable final AdjPhrOhneLeerstellen adjPhr,
            final NomenFlexionsspalte nomenFlexionsspalte,
            @Nullable final IBezugsobjekt bezugsobjekt) {
        // Beispiel für eine voll ausgebaute Nominalphrase:
        // "sogar ein neues Buch dieses Autors mit vielen Bildern, das uns erstaunt"
        // Die Elemente sind Artikel, Adjektivattribut, Kern (Nomen), Genitivattribut,
        // Präpositionalattribut und Attributsatz (Eisenberg, Der SemSatz, S. 387ff)

        // Komplexe Nominalphrasen in Kombination mit komplexen Adjektivphrasen:
        // "sogar ein Kritiker von Anfang an wirklich begeisterndes und Eingeweihte überraschende
        // Buch dieses Autors
        // mit vielen Bildern, das uns erstaunt",
        // "die junge Frau des Herzogs, die dich überrascht, gespannt, ob du etwas zu berichten
        // hast,"
        // "die Frau des Herzogs, zufrieden, dich zu sehen, und gespannt, ob du etwas zu
        // berichten hast,"

        return new Nominalphrase(
                nomenFlexionsspalte.getNumerusGenus(),
                artikeltyp,
                adjPhr,
                nomenFlexionsspalte.getFlexionsreiheArtikellos(),
                nomenFlexionsspalte.getBelebtheit(), bezugsobjekt
        );
    }


    private static Nominalphrase np(final NumerusGenus numerusGenus,
                                    @Nullable
                                    final IArtikelworttypOderVorangestelltesGenitivattribut artikelworttypOderVorangestelltesGenitivattribut,
                                    final Flexionsreihe flexionsreiheArtikellos,
                                    final Belebtheit belebtheit,
                                    @Nullable final IBezugsobjekt bezugsobjekt) {
        return new Nominalphrase(numerusGenus, artikelworttypOderVorangestelltesGenitivattribut,
                null, flexionsreiheArtikellos,
                belebtheit, bezugsobjekt);
    }

    private Nominalphrase(final NumerusGenus numerusGenus,
                          @Nullable
                          final IArtikelworttypOderVorangestelltesGenitivattribut artikelworttypOderVorangestelltesGenitivattribut,
                          @Nullable final AdjPhrOhneLeerstellen adjPhr,
                          final Flexionsreihe flexionsreiheArtikellos,
                          final Belebtheit belebtheit, @Nullable final IBezugsobjekt bezugsobjekt) {
        this(null, null,
                numerusGenus, artikelworttypOderVorangestelltesGenitivattribut, adjPhr,
                flexionsreiheArtikellos, belebtheit, bezugsobjekt
        );
    }

    private Nominalphrase(final @Nullable String fokuspartikel,
                          final @Nullable Negationspartikelphrase negationspartikelphrase,
                          final NumerusGenus numerusGenus,
                          @Nullable
                          final IArtikelworttypOderVorangestelltesGenitivattribut artikelworttypOderVorangestelltesGenitivattribut,
                          @Nullable final AdjPhrOhneLeerstellen adjPhr,
                          final Flexionsreihe flexionsreiheArtikellos,
                          final Belebtheit belebtheit, @Nullable final IBezugsobjekt bezugsobjekt) {
        super(fokuspartikel, negationspartikelphrase, numerusGenus, belebtheit, bezugsobjekt);
        this.artikelworttypOderVorangestelltesGenitivattribut =
                artikelworttypOderVorangestelltesGenitivattribut;
        this.adjPhr = adjPhr;
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

        return new Nominalphrase(fokuspartikel, getNegationspartikelphrase(),
                getNumerusGenus(), artikelworttypOderVorangestelltesGenitivattribut,
                adjPhr,
                flexionsreiheArtikellos, getBelebtheit(), getBezugsobjekt());
    }

    public Nominalphrase mitBelebheit(final Belebtheit belebtheit) {
        if (Objects.equals(getBelebtheit(), belebtheit)) {
            return this;
        }

        return new Nominalphrase(getFokuspartikel(), getNegationspartikelphrase(),
                getNumerusGenus(), artikelworttypOderVorangestelltesGenitivattribut,
                adjPhr,
                flexionsreiheArtikellos, belebtheit, getBezugsobjekt());
    }

    /**
     * Gibt diese Nominalphrase zurück, allerdings mit diesem Artikelwort oder dieser
     * Genitivphrase vorangestellt - <i>Achtung!</i>, eventuelle Adjektivphrasen
     * werden zwar passend dekliniert, aber {@link #flexionsreiheArtikellos}
     * wird unverändert übernommen! Das kann zu Fehlern führen,
     * wenn {@link #flexionsreiheArtikellos} nicht nur ein übliches Nomen ist,
     * sondern z.B. ein adjektivisch dekliniertes Nomen (setzt man bei "ein Studierender"
     * ein definites Artikelwort ergibt sich *"der Studierender") oder
     * wenn {@link #flexionsreiheArtikellos} in anderer Weise bereits deklinierte
     * Elemente enthält (setzt man bei "ein silberner Mond gegen Abend"
     * ein definites Artikelwort ergibt sich *"der silberner Mond gegen Abend").
     */
    @Nonnull
    @CheckReturnValue
    public Nominalphrase mitArtikelworttypOderVorangestelltemGenitivattribut(
            final IArtikelworttypOderVorangestelltesGenitivattribut
                    artikelworttypOderVorangestelltesGenitivattribut) {
        if (Objects.equals(this.artikelworttypOderVorangestelltesGenitivattribut,
                artikelworttypOderVorangestelltesGenitivattribut)) {
            return this;
        }

        return new Nominalphrase(getFokuspartikel(), getNegationspartikelphrase(),
                getNumerusGenus(), artikelworttypOderVorangestelltesGenitivattribut,
                adjPhr,
                flexionsreiheArtikellos, getBelebtheit(), getBezugsobjekt());
    }

    @Override
    public Nominalphrase ohneNegationspartikelphrase() {
        return this;
    }

    @Override
    public Nominalphrase neg(final Negationspartikelphrase negationspartikelphrase,
                             final boolean moeglichstNegativIndefiniteWoerterVerwenden) {
        @Nullable final IArtikelworttypOderVorangestelltesGenitivattribut negativerArtikeltyp =
                moeglichstNegativIndefiniteWoerterVerwenden ?
                        getNegativeForm(artikelworttypOderVorangestelltesGenitivattribut) : null;

        // Wir speichern die Negationspartikelphrase in jedem Fall - weil sie auch
        // Dinge wie "noch (nicht)" oder "(nicht) mehr" könnte.

        return new Nominalphrase(getFokuspartikel(),
                // Hier wird ggf. eine bestehende negationspartikelphrase überschrieben.
                negationspartikelphrase,
                getNumerusGenus(), negativerArtikeltyp,
                adjPhr,
                flexionsreiheArtikellos, getBelebtheit(), getBezugsobjekt());
    }

    @Override
    public boolean erlaubtVerschmelzungVonPraepositionMitArtikel() {
        if (artikelworttypOderVorangestelltesGenitivattribut == null
                || getFokuspartikel() != null) {
            return false;
        }

        // FIXME Steht dabei ein Relativsatz, der etwas näher definiert ("zu dem Zahnarzt,
        //  der ihr gestern empfohlen wurde") ist die Verschmelzung verboten.

        return artikelworttypOderVorangestelltesGenitivattribut
                .erlaubtVerschmelzungMitPraeposition();
    }

    @Override
    public Konstituentenfolge nomK() {
        return imK(NOM);
    }

    @Override
    public Konstituentenfolge datK() {
        return imK(DAT);
    }

    @Override
    @CheckReturnValue
    public Konstituentenfolge artikellosDatK() {
        return imK(DAT, true);
    }

    @Override
    @CheckReturnValue
    public Konstituentenfolge artikellosAkkK() {
        return imK(AKK, true);
    }

    @Override
    public Konstituentenfolge akkK() {
        return imK(AKK);
    }

    @Override
    public Konstituentenfolge imK(final KasusOderPraepositionalkasus kasusOderPraepositionalkasus) {
        if (kasusOderPraepositionalkasus instanceof Kasus) {
            return imK((Kasus) kasusOderPraepositionalkasus);
        }

        if (kasusOderPraepositionalkasus instanceof PraepositionMitKasus) {
            final PraepositionMitKasus praepositionMitKasus =
                    (PraepositionMitKasus) kasusOderPraepositionalkasus;

            return joinToKonstituentenfolge(
                    praepositionMitKasus.getDescription(this));
        }

        throw new IllegalArgumentException("Unexpected Kasus or Präpositionalkasus: " +
                kasusOderPraepositionalkasus);
    }

    @Override
    public Konstituentenfolge imK(final Kasus kasus) {
        return imK(kasus, false);
    }

    private Konstituentenfolge imK(final Kasus kasus, final boolean artikelWeglassen) {
        @Nullable final IFlexionsspalteArtikelwortOderVorangestelltesGenitivattribut
                flexionsspalteArtikelwortOderVorangestelltesGenitivattribut =
                IArtikelworttypOderVorangestelltesGenitivattribut
                        .vorNumerusGenus(artikelworttypOderVorangestelltesGenitivattribut,
                                getNumerusGenus());

        final boolean artikelwortOderGenitvattributTraegtKasusendung =
                artikelWeglassen || IFlexionsspalteArtikelwortOderVorangestelltesGenitivattribut
                        .traegtKasusendungFuerNominalphrasenkern(
                                flexionsspalteArtikelwortOderVorangestelltesGenitivattribut, kasus);
        // "artikelWeglassen" bedeutet, dass etwas wie "zum" oder "zur" davor steht, dass eine
        // Kasusendung trägt. (Ein vorangestelltes Genitivattribut gilt nicht als
        // "artikelWeglassen"!)

        @Nullable final SyntSatz attributivAnteilRelativsatz =
                // FIXME Sollte sich der textContext vielleicht über die Generierung
                //  schrittweise ändern? Wie kann das passieren?
                //  Denkbar wäre ein ein mutable textContext mit Visitor-Pattern...
                getAttributivAnteilRelativsatz(kasus);

        @Nullable final AdjPhrOhneLeerstellen attributivAnteilLockererNachtrag = adjPhr != null ?
                adjPhr.getAttributivAnteilLockererNachtrag(kasus) : null;

        @Nullable final String anteilNegationspartikelphraseVorArtikelUndNominalphrasenkern =
                getNegationspartikelphrase() != null ?
                        (isNegativ(artikelworttypOderVorangestelltesGenitivattribut) ?
                                getNegationspartikelphrase().getVorangestellteWoerter() :
                                // "länger( kein ... mehr)"
                                getNegationspartikelphrase().getDescription()) :
                        // "länger nicht mehr"
                        null;

        @Nullable final String anteilNegationspartikelphraseNachNominalphrasenkern =
                (getNegationspartikelphrase() != null && isNegativ(
                        artikelworttypOderVorangestelltesGenitivattribut)) ?
                        getNegationspartikelphrase().getNachgestellteWoerter() :
                        // "(länger kein ... )mehr"
                        null;

        return joinToKonstituentenfolge(
                // Eine Konstituentenfolge mit nur einer Konstituente
                joinToKonstituentenfolge(
                        getFokuspartikel(), // "sogar"
                        !isNegativ(artikelworttypOderVorangestelltesGenitivattribut) ?
                                getNegationspartikelphrase() : null,
                        anteilNegationspartikelphraseVorArtikelUndNominalphrasenkern,
                        // "länger" (vor "kein") / "länger nicht mehr" (nicht vor "kein")
                        !artikelWeglassen
                                && flexionsspalteArtikelwortOderVorangestelltesGenitivattribut
                                != null ?
                                flexionsspalteArtikelwortOderVorangestelltesGenitivattribut
                                        .imStr(kasus) : null,
                        // "die" / "eine" / "keine"
                        adjPhr != null ?
                                adjPhr.getAttributivAnteilAdjektivattribut(getNumerusGenus(),
                                        getBelebtheit(),
                                        kasus,
                                        artikelwortOderGenitvattributTraegtKasusendung) :
                                null, // "junge"
                        flexionsreiheArtikellos.im(kasus), // "Frau"
                        anteilNegationspartikelphraseNachNominalphrasenkern, // "mehr" (nach "kein")
                        attributivAnteilRelativsatz != null ?
                                schliesseInKommaEin(attributivAnteilRelativsatz.getRelativsatz())
                                // , die sich fragt, ob du wohl kommst [,]
                                : null,
                        attributivAnteilLockererNachtrag != null ?
                                schliesseInKommaEin(attributivAnteilLockererNachtrag
                                        .getPraedikativ(this))
                                // (Hier nichts aus dem Prädikativum ins Nachfeld auslagern,
                                // führt zu falscher Bedeutung)
                                : null
                        // ", gespannt, ob du etwas zu berichten hast[,]"
                ).joinToSingleKonstituente()
                        // Das Ganze soll eine Konstituente sein, die als Bezugsobejekt
                        // verstanden werden kann
                        .withBezugsobjektUndKannVerstandenWerdenAls(
                                getBezugsobjekt(),
                                getBezugsobjekt() != null ? getBelebtheit() : null,
                                getNumerusGenus()));

        // TODO Vermeiden von "Du / ich (Personalpronomen), glücklich Rapunzel zu sehen, tust dies
        //  und das" - besser "Glücklich, Rapunzel zu sehen, tust du ...".
        //  Dasselbe auch bei normalen Nominalphrasen: "Rapunzel, glücklich, dich zu sehen,
        //  tut dies und das" ist vielleicht nicht so schön, wie "Glücklich, dich zu sehen,
        //  tut Rapunzel dies und das".
        //  (Vermutlich ist "glücklich, dich zu sehen" eine Umformulierung als "depiktives
        //  Prädikativ", vgl. Duden 1205.) - Problem an der Sache:
        //  Funktioniert nicht bei allen Adjektivphrasen sinnvoll:
        //  "Die grüne Eidechse läuft über den Boden" - ?"Grün läuft die Eidechse über den Boden"
        //  Aber vielleicht bei praktisch allen mit w-Fragesatz oder anderweitig komplexen,
        //  für die das überhaupt nur relevant wäre? Letztlich müsste das
        //  Prädikat dem SemSatz mitteilen, dass "Glücklich, Rapunzel zu sehen" ins
        //  Vorfeld gerückt werden sollte (getSpeziellesVorfeldSehrErwuenscht(),
        //  getSpeziellesVorfeldAlsWeitereOption()).
        //  Der attributivAnteilLockererNachtrag muss eine eigene Konstituente sein,
        //  damit sie von getSpeziellesVorfeld...() auch zurückgegeben werden kann.
        //  Sätze mit "sein / werden" und Adjektivphrase können anscheinend kein so ein
        //  "depiktives Prädikativ" tragen? ?"Glücklich bist du hilfsbereit"

        // TODO Idee: Neue Stelle für eine "prädikative Angabe" ("depiktives Prädikativ", vgl.
        //  Duden 1205? )  wie "Peter geht fröhlich durch
        //  den Wald". Zur Stellung vergleiche: "Heute schlägt Peter /fröhlich/ heftig auf das Holz
        //  ein",
        //  also vor der Verb-Allgemein-Adverbialen Angabe - am liebsten jedoch im Vorfeld:
        //  Fröhlich gibt Peter dem Mann das Buch". Vgl. auch: "Das Buch gibt Peter fröhlich dem 
        //  Mann" -
        //  also auf jeden Fall vor dem Dativ-Objekt.
        //  Geht aber anscheinend nicht bei Prädikativum-Prädikaten: ?"Fröhlich ist Peter dumm"
        //  (aber: "Fröhlich ist Peter ein Esel" - andere Bedeutung?)

        // TODO Idee: Automatische Zusammenfassungen in der Art "Rapunzel ist vom Wandern müde .
        //  Rapunzel tut dies und das" zu "Rapunzel, vom Wandern müde, tut dies und das" (oder
        //  "Vom Wandern müde tut Rapunzel dies und das") "Glücklich, Rapunzel zu sehen, tust du 
        //  dies  und das" (neue "adverbiale Angabe" - eigentlich wohl "depiktives Prädikativ" /
        //  neues
        //  Vorfeld)
        //  Analog: "Du bist ganz zerknirscht. Du gehst ...." ->  "Ganz zerknirscht gehst du..."??
        //     ("depiktives Prädikativ"?, vgl. Nominalphrase!)
        //  - Oder so? "Du wirst ganz zerknirscht. Du gehst ...." ->  "Ganz zerknirscht gehst
        //  du..."??
        //  - Konrete Fälle suchen und dann einbauen - oder alternativ erst einmal verwerfen.
    }

    @Nullable
    private SyntSatz getAttributivAnteilRelativsatz(final Kasus kasus) {
        @Nullable SyntSatz attributivAnteilSemRelativsatz = null;
        if (adjPhr != null) {
            @Nullable final Praedikativum praedikativumFuerRelativsatz =
                    adjPhr.getAttributivAnteilRelativsatz(kasus);

            if (praedikativumFuerRelativsatz != null) {
                // "die"
                final Relativpronomen relativpronomen = Relativpronomen
                        .get(getPerson(), getNumerusGenus(), getBelebtheit(), getBezugsobjekt());

                // "die gespannt ist, was wer zu berichten hat"
                attributivAnteilSemRelativsatz =
                        praedikativumFuerRelativsatz.alsPraedikativumPraedikat()
                                .alsSatzMitSubjekt(relativpronomen);
            }
        }
        return attributivAnteilSemRelativsatz;
    }

    @Override
    public String nomStr() {
        return nomK().joinToSingleKonstituente().toTextOhneKontext();
    }

    @Override
    public String datStr() {
        return datK().joinToSingleKonstituente().toTextOhneKontext();
    }

    @Override
    public String artikellosDatStr() {
        return artikellosDatK().joinToSingleKonstituente().toTextOhneKontext();
    }

    @Override
    public String artikellosAkkStr() {
        return artikellosAkkK().joinToSingleKonstituente().toTextOhneKontext();
    }

    @Override
    public String akkStr() {
        return akkK().joinToSingleKonstituente().toTextOhneKontext();
    }

    @Override
    public Personalpronomen persPron() {
        return Personalpronomen.get(P3, getNumerusGenus(), getBelebtheit(), getBezugsobjekt());
    }

    @Override
    public Reflexivpronomen reflPron() {
        return Reflexivpronomen.get(
                new PraedRegMerkmale(P3, getNumerusGenus().getNumerus(), getBelebtheit()));
    }

    @Override
    public IArtikelworttypOderVorangestelltesGenitivattribut possArt() {
        return ArtikelwortFlexionsspalte.getPossessiv(P3, getNumerusGenus());
    }

    @Override
    public Relativpronomen relPron() {
        return Relativpronomen.get(P3, getNumerusGenus(), getBelebtheit(), getBezugsobjekt());
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
    public boolean equals(@Nullable final Object o) {
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
        return artikelworttypOderVorangestelltesGenitivattribut
                == that.artikelworttypOderVorangestelltesGenitivattribut &&
                Objects.equals(adjPhr, that.adjPhr) &&
                flexionsreiheArtikellos.equals(that.flexionsreiheArtikellos);
    }

    @Override
    public int hashCode() {
        return Objects
                .hash(super.hashCode(), artikelworttypOderVorangestelltesGenitivattribut, adjPhr,
                        flexionsreiheArtikellos);
    }
}
