package de.nb.aventiure2.german.base;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Objects;

import javax.annotation.CheckReturnValue;

import de.nb.aventiure2.german.adjektiv.AdjPhrOhneLeerstellen;
import de.nb.aventiure2.german.adjektiv.AdjektivOhneErgaenzungen;
import de.nb.aventiure2.german.satz.Satz;

import static de.nb.aventiure2.german.base.Artikel.Typ.DEF;
import static de.nb.aventiure2.german.base.Artikel.Typ.INDEF;
import static de.nb.aventiure2.german.base.Flexionsreihe.fr;
import static de.nb.aventiure2.german.base.Kasus.AKK;
import static de.nb.aventiure2.german.base.Kasus.DAT;
import static de.nb.aventiure2.german.base.Kasus.NOM;
import static de.nb.aventiure2.german.base.Konstituentenfolge.joinToKonstituentenfolge;
import static de.nb.aventiure2.german.base.Konstituentenfolge.schliesseInKommaEin;
import static de.nb.aventiure2.german.base.NumerusGenus.F;
import static de.nb.aventiure2.german.base.NumerusGenus.M;
import static de.nb.aventiure2.german.base.NumerusGenus.N;
import static de.nb.aventiure2.german.base.NumerusGenus.PL_MFN;
import static de.nb.aventiure2.german.base.Person.P3;

/**
 * Eine Nominalphrase, z.B. "ein dicker, hässlicher Frosch".
 */
public class Nominalphrase
        extends EinzelneSubstantivischePhraseMitOptFokuspartikel
        implements IErlaubtAttribute {
    @Nullable
    private final Artikel.Typ artikelTyp;

    @Nullable
    private final AdjPhrOhneLeerstellen adjPhr;

    private final Flexionsreihe flexionsreiheArtikellos;

    // Allgemeine Nominalphrasen ohne Bezugsobjekt
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
            np(AdjektivOhneErgaenzungen.BLAU, NomenFlexionsspalte.HIMMEL);
    public static final Nominalphrase DEIN_HERZ =
            np(N, null, "dein Herz",
                    "deinem Herzen");
    public static final Nominalphrase DUESTERE_WOLKEN =
            np(PL_MFN, INDEF,
                    "düstere Wolken", "düsteren Wolken");
    public static final Nominalphrase EIN_GROBER_ABRISS =
            np(N, INDEF, "grober Abriss",
                    "groben Abriss", "groben Abriss");
    public static final Nominalphrase EINE_KLEINE_NOTLUEGE =
            np(N, INDEF, "kleine Notlüge",
                    "kleinen Notlüge");
    public static final Nominalphrase ERSTE_SONNENSTRAHLEN =
            np(PL_MFN, DEF, "ersten Sonnenstrahlen");
    public static final Nominalphrase GANZER_HIMMEL =
            np(M, DEF, "ganze Himmel",
                    "ganzen Himmel", "ganzen Himmel");
    public static final Nominalphrase GETRUEBTES_TAGESLICHT =
            np(N, DEF, "getrübte Tageslicht",
                    "getrübten Tageslicht");
    public static final Nominalphrase EIN_HEISSER_TAG =
            np(M, INDEF, "heißer Tag",
                    "heißen Tag", "heißen Tag");
    public static final Nominalphrase HEISSER_SONNENSCHEIN =
            np(M, DEF, "heiße Sonnenschein",
                    "heißen Sonnenschein", "heißen Sonnenschein");
    public static final Nominalphrase HELLES_TAGESLICHT =
            np(N, DEF, "helle Tageslicht",
                    "hellen Tageslicht", "helle Tageslicht");
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
    public static final Nominalphrase MORGENDLICHER_SONNENSCHEIN =
            np(M, DEF, "morgendliche Sonnenschein",
                    "morgendlichen Sonnenschein",
                    "morgendlichen Sonnenschein");
    public static final Nominalphrase KLIRRENDE_KAELTE_OHNE_ART =
            np(F, null,
                    "klirrende Kälte", "klirrenden Kälte");
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
    public static final Nominalphrase SENGENDE_SONNE =
            np(F, DEF, "sengende Sonne", "sengenden Sonne");
    public static final Nominalphrase STRAHLEND_BLAUER_HIMMEL =
            np(M, DEF, "strahlend blaue Himmel",
                    "strahlend blauen Himmel", "strahlend blauen Himmel");
    public static final Nominalphrase TRUEBES_DAEMMERLICHT =
            np(N, DEF, "trübe Dämmerlicht",
                    "trüben Dämmerlicht");
    public static final Nominalphrase TRUEBES_LICHT =
            np(N, DEF, "trübe Licht",
                    "trüben Licht");
    public static final Nominalphrase WARMES_WETTER_OHNE_ART =
            np(N, null,
                    "warmes Wetter", "warmem Wetter");
    public static final Nominalphrase WOLKENVERHANGENER_HIMMEL =
            np(M, DEF, "wolkenverhangene Himmel",
                    "wolkenverhangenen Himmel",
                    "wolkenverhangenen Himmel");

    /**
     * Erzeugt eine Nominalphrase ohne Bezugsobjekt.
     */
    @NonNull
    public static Nominalphrase np(final NumerusGenus numerusGenus,
                                   @Nullable final Artikel.Typ artikelTyp,
                                   final String nominalNominativDativUndAkkusativ) {
        return np(numerusGenus, artikelTyp, nominalNominativDativUndAkkusativ,
                (IBezugsobjekt) null);
    }

    @NonNull
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

    @NonNull
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
    @NonNull
    public static Nominalphrase np(final NumerusGenus numerusGenus,
                                   @Nullable final Artikel.Typ artikelTyp,
                                   final String nominalNominativ,
                                   final String nominalDativ,
                                   final String nominalAkkusativ) {
        return np(numerusGenus, artikelTyp, nominalNominativ, nominalDativ, nominalAkkusativ,
                null);
    }

    @NonNull
    public static Nominalphrase np(final NumerusGenus numerusGenus,
                                   @Nullable final Artikel.Typ artikelTyp,
                                   final String nominalNominativ,
                                   final String nominalDativ,
                                   final String nominalAkkusativ,
                                   @Nullable final IBezugsobjekt bezugsobjekt) {
        return np(numerusGenus, artikelTyp,
                fr(nominalNominativ, nominalDativ, nominalAkkusativ), bezugsobjekt);
    }

    @NonNull
    public static Nominalphrase np(final NomenFlexionsspalte nomenFlexionsspalte) {
        return np(nomenFlexionsspalte, null);
    }

    @NonNull
    public static Nominalphrase np(final NumerusGenus numerusGenus,
                                   @Nullable final Artikel.Typ artikelTyp,
                                   final Flexionsreihe flexionsreiheArtikellos) {
        return np(numerusGenus, artikelTyp, flexionsreiheArtikellos, null);
    }

    @NonNull
    public static Nominalphrase np(@Nullable final AdjPhrOhneLeerstellen adjPhr,
                                   final NomenFlexionsspalte nomenFlexionsspalte) {
        return np(adjPhr, nomenFlexionsspalte, null);
    }

    @NonNull
    public static Nominalphrase np(final NomenFlexionsspalte nomenFlexionsspalte,
                                   @Nullable final IBezugsobjekt bezugsobjekt) {
        return np(null, nomenFlexionsspalte, bezugsobjekt);
    }

    @NonNull
    public static Nominalphrase np(@Nullable final AdjPhrOhneLeerstellen adjPhr,
                                   final NomenFlexionsspalte nomenFlexionsspalte,
                                   @Nullable final IBezugsobjekt bezugsobjekt) {
        // Beispiel für eine voll ausgebaute Nominalphrase:
        // "sogar ein neues Buch dieses Autors mit vielen Bildern, das uns erstaunt"
        // Die Elemente sind Artikel, Adjektivattribut, Kern (Nomen), Genitivattribut,
        // Präpositionalattribut und Attributsatz (Eisenberg, Der Satz, S. 387ff)

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
                nomenFlexionsspalte.getArtikelTyp(),
                adjPhr,
                nomenFlexionsspalte.getFlexionsreiheArtikellos(),
                bezugsobjekt);
    }

    public static Nominalphrase np(final NumerusGenus numerusGenus,
                                   @Nullable final Artikel.Typ artikelTyp,
                                   final Flexionsreihe flexionsreiheArtikellos,
                                   @Nullable final IBezugsobjekt bezugsobjekt) {
        return new Nominalphrase(numerusGenus, artikelTyp, null, flexionsreiheArtikellos,
                bezugsobjekt);
    }

    private Nominalphrase(final NumerusGenus numerusGenus,
                          @Nullable final Artikel.Typ artikelTyp,
                          @Nullable final AdjPhrOhneLeerstellen adjPhr,
                          final Flexionsreihe flexionsreiheArtikellos,
                          @Nullable final IBezugsobjekt bezugsobjekt) {
        this(null, numerusGenus, artikelTyp, adjPhr, flexionsreiheArtikellos, bezugsobjekt);
    }

    private Nominalphrase(final @Nullable String fokuspartikel,
                          final NumerusGenus numerusGenus,
                          @Nullable final Artikel.Typ artikelTyp,
                          @Nullable final AdjPhrOhneLeerstellen adjPhr,
                          final Flexionsreihe flexionsreiheArtikellos,
                          @Nullable final IBezugsobjekt bezugsobjekt) {
        super(fokuspartikel, numerusGenus, bezugsobjekt);
        this.artikelTyp = artikelTyp;
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

        return new Nominalphrase(fokuspartikel, getNumerusGenus(), artikelTyp,
                adjPhr,
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
        return imK(DAT, false);
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
            // FIXME Präpositionalkasus mit "es" sind problematisch, da "es"
            //  nicht phrasenbildend ist.
            //  - "in es" etc. wird vertreten durch "hinein", "auf es" durch "darauf" etc.
            //  - Man bräuchte wohl eine neue Klasse adverbialer Angaben
            //   wie DARAUF, DARUNTER, HINEIN etc., und jede
            //   Präposition MIT AKKUSATIV müsste zwingend
            //   eines dieser Adverbien referenzieren, das als
            //   Ersatz verwendet wird.
            //  - Dabei ändert sich vielleicht teilweise sogar die Zusammenschreibung?!
            //  ("Du willst es hineinlegen" statt *"Du willst es in es legen"?!)
            //  - Das scheint aber nicht bei belebten Dingen möglich zu sein:
            //  ?"Das ist unser Kind. Wir haben viel Geld hineingesteckt"
            //  ?"Das ist unser Kind. Wir haben einen Nachtisch dafür gekauft."

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
        return imK(kasus, true);
    }

    private Konstituentenfolge imK(final Kasus kasus, final boolean mitArtikel) {
        @Nullable final Artikel artikel = getArtikel();

        final boolean artikelwortTraegtKasusendung =
                !mitArtikel || Artikel.traegtKasusendung(artikel, kasus);
        // wenn kein Artikel erzeugt werden soll, steht etwas wie "zum" oder
        // "zur" davor, dass eine Kasusendung trägt

        @Nullable final Satz attributivAnteilRelativsatz = adjPhr != null ?
                adjPhr.getAttributivAnteilRelativsatz(getPerson(), getNumerusGenus(),
                        kasus, getBezugsobjekt()) : null;

        @Nullable final AdjPhrOhneLeerstellen attributivAnteilLockererNachtrag = adjPhr != null ?
                adjPhr.getAttributivAnteilLockererNachtrag(kasus) : null;

        return joinToKonstituentenfolge(
                // Eine Konstituentenfolge mit nur einer Konstituente
                joinToKonstituentenfolge(
                        getFokuspartikel(), // "sogar"
                        mitArtikel && artikel != null ? artikel.imStr(kasus) : null, // "die"
                        adjPhr != null ?
                                adjPhr.getAttributivAnteilAdjektivattribut(getNumerusGenus(), kasus,
                                        artikelwortTraegtKasusendung) :
                                null, // "junge"
                        flexionsreiheArtikellos.im(kasus), // "Frau"
                        attributivAnteilRelativsatz != null ?
                                schliesseInKommaEin(attributivAnteilRelativsatz.getRelativsatz())
                                // , die sich fragt, ob du wohl kommst [,]
                                : null,
                        attributivAnteilLockererNachtrag != null ?
                                schliesseInKommaEin(attributivAnteilLockererNachtrag
                                        .getPraedikativ(getPerson(), getNumerus()))
                                // (Hier nichts aus dem Prädikativum ins Nachfeld auslagern,
                                // führt zu falscher Bedeutung)
                                : null
                        // ", gespannt, ob du etwas zu berichten hast[,]"
                ).joinToSingleKonstituente()
                        // Das Ganze soll eine Konstituente sein, die als Bezugsobejekt
                        // verstanden werden kann
                        .withBezugsobjektUndKannVerstandenWerdenAls(
                                getBezugsobjekt(), getNumerusGenus()));
    }

    @Override
    public String nomStr() {
        // FIXME Verwendungen suchen und bei
        //  denkbaren Problemen mit Nachkomma nomK() verwenden.
        return nomK().joinToSingleKonstituente().toTextOhneKontext();
    }

    @Override
    public String datStr() {
        // FIXME Verwendungen suchen und bei
        //  denkbaren Problemen mit Nachkomma nomK() verwenden.
        return datK().joinToSingleKonstituente().toTextOhneKontext();
    }

    @Override
    public String artikellosDatStr() {
        // FIXME Verwendungen suchen und bei
        //  denkbaren Problemen mit Nachkomma nomK() verwenden.
        return artikellosDatK().joinToSingleKonstituente().toTextOhneKontext();
    }

    @Override
    public String akkStr() {
        // FIXME Verwendungen suchen und bei
        //  denkbaren Problemen mit Nachkomma nomK() verwenden.
        return akkK().joinToSingleKonstituente().toTextOhneKontext();
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
    public boolean equals(final @Nullable Object o) {
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
