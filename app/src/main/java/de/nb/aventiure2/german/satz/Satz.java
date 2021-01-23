package de.nb.aventiure2.german.satz;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.common.collect.ImmutableList;

import java.util.Collection;

import javax.annotation.CheckReturnValue;

import de.nb.aventiure2.german.base.Interrogativpronomen;
import de.nb.aventiure2.german.base.Konstituente;
import de.nb.aventiure2.german.base.Konstituentenfolge;
import de.nb.aventiure2.german.base.Personalpronomen;
import de.nb.aventiure2.german.base.SubstantivischePhrase;
import de.nb.aventiure2.german.praedikat.AdverbialeAngabeSkopusSatz;
import de.nb.aventiure2.german.praedikat.AdverbialeAngabeSkopusVerbAllg;
import de.nb.aventiure2.german.praedikat.AdverbialeAngabeSkopusVerbWohinWoher;
import de.nb.aventiure2.german.praedikat.Modalpartikel;
import de.nb.aventiure2.german.praedikat.PraedikatMitEinerObjektleerstelle;
import de.nb.aventiure2.german.praedikat.PraedikatOhneLeerstellen;

import static com.google.common.collect.ImmutableList.toImmutableList;
import static de.nb.aventiure2.german.base.Numerus.SG;
import static de.nb.aventiure2.german.base.Person.P2;

/**
 * Ein Satz.
 */
public class Satz {
    /**
     * Anschlusswörter sind vor allem "und", "denn" und "aber". Sie stehen vor dem
     * Vorfeld.
     */
    @Nullable
    private final String anschlusswort;

    /**
     * Das Subjekt des Satzes.
     */
    private final SubstantivischePhrase subjekt;

    /**
     * Das Prädikat des Satzes, im Sinne des Verbs mit all seinen Ergänzungen und
     * Angabe - ohne das Subjekt.
     */
    private final PraedikatOhneLeerstellen praedikat;

    /**
     * Ein dem Satz direkt untergeordneter (Neben-) Satz, der den Status einer <i>Angabe</i> hat
     * - der also nicht Subjekt o.Ä. ist.
     */
    @Nullable
    private final Konditionalsatz angabensatz;

    public static ImmutableList<Satz> altSubjObjSaetze(
            final SubstantivischePhrase subjekt,
            final PraedikatMitEinerObjektleerstelle praedikat, final SubstantivischePhrase objekt,
            final ImmutableList<AdverbialeAngabeSkopusVerbAllg> adverbialeAngaben) {
        return altSubjObjSaetze(subjekt, ImmutableList.of(praedikat), objekt, adverbialeAngaben);
    }

    public static ImmutableList<Satz> altSubjObjSaetze(
            final SubstantivischePhrase subjekt,
            final ImmutableList<? extends PraedikatMitEinerObjektleerstelle> praedikate,
            final SubstantivischePhrase objekt,
            final ImmutableList<AdverbialeAngabeSkopusVerbAllg> adverbialeAngaben) {
        return adverbialeAngaben.stream()
                .flatMap(aa -> praedikate.stream()
                        .map(v -> v.mit(objekt)
                                .mitAdverbialerAngabe(aa)
                                .alsSatzMitSubjekt(subjekt)))
                .collect(toImmutableList());
    }

    public Satz(final SubstantivischePhrase subjekt,
                final PraedikatOhneLeerstellen praedikat) {
        this(null, subjekt, praedikat);
    }

    public Satz(@Nullable final String anschlusswort,
                final SubstantivischePhrase subjekt,
                final PraedikatOhneLeerstellen praedikat) {
        this(anschlusswort, subjekt, praedikat, null);
    }

    public Satz(@Nullable final String anschlusswort,
                final SubstantivischePhrase subjekt,
                final PraedikatOhneLeerstellen praedikat,
                @Nullable final Konditionalsatz angabensatz) {
        this.anschlusswort = anschlusswort;
        this.subjekt = subjekt;
        this.praedikat = praedikat;
        this.angabensatz = angabensatz;
    }

    public Satz mitAnschlusswort(@Nullable final String anschlusswort) {
        return new Satz(anschlusswort, subjekt, praedikat, angabensatz);
    }

    /**
     * Fügt dem Subjekt etwas hinzu wie "auch", "allein", "ausgerechnet",
     * "wenigstens" etc. (sofern das Subjekt eine Fokuspartikel erlaubt, ansonsten
     * wird sie verworfen)
     */
    public Satz mitSubjektFokuspartikel(
            @Nullable final String subjektFokuspartikel) {
        return new Satz(anschlusswort, subjekt.mitFokuspartikel(subjektFokuspartikel),
                praedikat, angabensatz);
    }

    public Satz mitModalpartikeln(final Modalpartikel... modalpartikeln) {
        return new Satz(anschlusswort, subjekt, praedikat.mitModalpartikeln(modalpartikeln),
                angabensatz);
    }

    public Satz mitModalpartikeln(final Collection<Modalpartikel> modalpartikeln) {
        return new Satz(anschlusswort, subjekt, praedikat.mitModalpartikeln(modalpartikeln),
                angabensatz);
    }

    public Satz mitAdverbialerAngabe(@Nullable final AdverbialeAngabeSkopusSatz adverbialeAngabe) {
        return new Satz(anschlusswort, subjekt, praedikat.mitAdverbialerAngabe(adverbialeAngabe),
                angabensatz);
    }

    public Satz mitAdverbialerAngabe(
            @Nullable final AdverbialeAngabeSkopusVerbAllg adverbialeAngabe) {
        return new Satz(anschlusswort, subjekt, praedikat.mitAdverbialerAngabe(adverbialeAngabe),
                angabensatz);
    }

    public Satz mitAdverbialerAngabe(
            @Nullable final AdverbialeAngabeSkopusVerbWohinWoher adverbialeAngabe) {
        return new Satz(anschlusswort, subjekt, praedikat.mitAdverbialerAngabe(adverbialeAngabe),
                angabensatz);
    }

    public Satz mitAngabensatz(@Nullable final Konditionalsatz angabensatz) {
        if (angabensatz == null) {
            return this;
        }

        return new Satz(anschlusswort, subjekt, praedikat, angabensatz);
    }

    /**
     * Gibt eine indirekte Frage zurück: Etwas wie
     * <ul>
     * <li>ob du etwas zu berichten hast
     * <li>was du zu berichten hast
     * <li>wer etwas zu berichten hat
     * <li>wer was zu berichten hat
     * <li>mit wem sie sich treffen wird
     * <li>wann du etwas zu berichten hast
     * <li>wessen Heldentaten wer zu berichten hat
     * <li>was zu erzählen du beginnen wirst
     * <li>was du zu erzählen beginnen wirst
     * <li>was du zu erzählen beginnen wirst
     * </ul>
     */
    public Konstituentenfolge getIndirekteFrage() {
        // Zurzeit unterstützen wir nur Interrogativpronomen für die normalen Kasus 
        // wie "wer" oder "was".
        // Später sollten auch unterstützt werden:
        // - Interrogativpronomen mit Präposition ("mit wem")
        // - Interrogativpronomen für Angaben ("wann")
        // - "substantivische Interrogativphrasen" wie "wessen Heldentaten"
        // - "Infinitiv-Interrogativphrasen" wie "was zu erzählen"
        if (subjekt instanceof Interrogativpronomen) {
            // "wer etwas zu berichten hat", "wer was zu berichten hat"
            return getIndirekteFrageNachSubjekt();
        }

        @Nullable final Konstituente erstesInterrogativpronomenImPraedikat =
                praedikat.getErstesInterrogativpronomen();

        if (erstesInterrogativpronomenImPraedikat == null) {
            // "ob du etwas zu berichten hast"
            return getObFrage();
        }

        // "was du zu berichten hast", "wem er was gegeben hat"
        return Konstituentenfolge.joinToKonstituentenfolge(
                anschlusswort, // "und"
                erstesInterrogativpronomenImPraedikat, // "was" / "wem"
                getVerbletztsatz().cutFirst(
                        erstesInterrogativpronomenImPraedikat
                ) // "du zu berichten hast", "wer zu berichten hat"
        );
    }

    private Konstituentenfolge getIndirekteFrageNachSubjekt() {
        // "wer etwas zu berichten hat", "wer was zu berichten hat", "was er zu berichten hat"
        return getVerbletztsatz();
    }

    private Konstituentenfolge getObFrage() {
        return Konstituentenfolge.joinToKonstituentenfolge(
                anschlusswort, // "und"
                "ob",
                getVerbletztsatz() // "du etwas zu berichten hast"
        );
    }

    /**
     * Gibt den Satz als Verbzweitsatz aus, bei dem nach Möglichkeit ein "spezielles"
     * Vorfeld gewählt wird, z.B. eine adverbiale Bestimmung: "am Abend hast du etwas zu berichten"
     */
    public Konstituentenfolge getVerbzweitsatzMitSpeziellemVorfeldAlsWeitereOption() {
        @Nullable final Konstituente speziellesVorfeld =
                praedikat.getSpeziellesVorfeldAlsWeitereOption(subjekt.getPerson(),
                        subjekt.getNumerus());
        if (speziellesVorfeld == null) {
            // Angabensätze können / sollten nur unter gewissen Voraussetzungen
            // ins Vorfeld gesetzt werden.

            return getVerbzweitsatzStandard();
        }

        return getVerbzweitsatzMitVorfeldAusMittelOderNachfeld(speziellesVorfeld);
    }

    /**
     * Gibt den Satz als einige alternative Verbzweitsätze aus, z.B. "du hast
     * am Abend etwas zu berichten" oder "am Abend hast du etwas zu berichten"
     */
    @CheckReturnValue
    @NonNull
    public ImmutableList<Konstituentenfolge> altVerzweitsaetze() {
        final ImmutableList.Builder<Konstituentenfolge> res = ImmutableList.builder();

        res.add(getVerbzweitsatzStandard());

        @Nullable final Konstituente speziellesVorfeld =
                praedikat.getSpeziellesVorfeldAlsWeitereOption(
                        subjekt.getPerson(), subjekt.getNumerus());
        if (speziellesVorfeld == null) {
            res.add(getVerbzweitsatzMitSpeziellemVorfeldAlsWeitereOption());
        }

        return res.build();
    }

    /**
     * Gibt den Satz als Verbzweitsatz aus, bei dem das Subjekt im Vorfeld steht, z.B. "du hast
     * am Abend etwas zu berichten" oder "du nimmst den Ast"
     */
    public Konstituentenfolge getVerbzweitsatzStandard() {
        @Nullable final Konstituente speziellesVorfeld =
                praedikat.getSpeziellesVorfeldSehrErwuenscht(
                        subjekt.getPerson(), subjekt.getNumerus());
        if (speziellesVorfeld != null) {
            return getVerbzweitsatzMitVorfeldAusMittelOderNachfeld(speziellesVorfeld);
        }

        return Konstituentenfolge.joinToKonstituentenfolge(
                anschlusswort, // "und"
                subjekt.nomK(),
                praedikat.getVerbzweit(subjekt.getPerson(), subjekt.getNumerus()),
                angabensatz != null ?
                        Konstituentenfolge
                                .schliesseInKommaEin(angabensatz.getDescription()) :
                        null);
    }

    private Konstituentenfolge getVerbzweitsatzMitVorfeldAusMittelOderNachfeld(
            final Konstituente vorfeld) {
        return Konstituentenfolge.joinToKonstituentenfolge(
                anschlusswort, // "und"
                vorfeld,
                praedikat.getVerbzweitMitSubjektImMittelfeld(subjekt).cutFirst(vorfeld),
                angabensatz != null ?
                        Konstituentenfolge.schliesseInKommaEin(angabensatz.getDescription()) :
                        null);
    }

    public Konstituentenfolge getVerbzweitsatzMitVorfeld(final String vorfeld) {
        return Konstituentenfolge.joinToKonstituentenfolge(
                anschlusswort, // "und"
                vorfeld,
                praedikat.getVerbzweitMitSubjektImMittelfeld(subjekt),
                angabensatz != null ?
                        Konstituentenfolge.schliesseInKommaEin(angabensatz.getDescription()) :
                        null);
    }

    /**
     * Gibt den Satz als Verbletztsatz aus, z.B. "du etwas zu berichten hast"
     */
    Konstituentenfolge getVerbletztsatz() {
        return Konstituentenfolge.joinToKonstituentenfolge(
                anschlusswort, // "und"
                subjekt.nomK(),
                praedikat.getVerbletzt(subjekt.getPerson(), subjekt.getNumerus()),
                angabensatz != null ?
                        Konstituentenfolge.schliesseInKommaEin(angabensatz.getDescription()) :
                        null);
    }

    /**
     * Gibt den Satz in Verbzeitform aus, jedoch ohne Subjekt, also beginnend mit
     * dem Anschlusswort (z.B. "und") und dem Verb. Z.B. "und hast
     * am Abend etwas zu berichten" oder "und nimmst den Ast"
     */
    public Konstituentenfolge getSatzanschlussOhneSubjekt() {
        return Konstituentenfolge.joinToKonstituentenfolge(
                anschlusswort, // "und"
                praedikat.getVerbzweit(subjekt.getPerson(), subjekt.getNumerus()),
                angabensatz != null ?
                        Konstituentenfolge.schliesseInKommaEin(angabensatz.getDescription()) :
                        null);
    }

    public boolean hasSubjektDu() {
        return subjekt instanceof Personalpronomen
                && subjekt.getPerson() == P2
                && subjekt.getNumerus() == SG;
    }

    public SubstantivischePhrase getSubjekt() {
        return subjekt;
    }

    public PraedikatOhneLeerstellen getPraedikat() {
        return praedikat;
    }

    // equals() und hashCode() überschreiben wir extra nicht! Alle Satz-Objekte
    // sollen als "verschieden" gelten. Ansonsten müssten wir auch in allen
    // SubstantivischePhrase- und
    // PraedikatOhneLeerstellen-Implementierungen equals() und hashCode() überschreiben.
    // Das wäre inhaltlich richtig, aber viel Arbeit.
}