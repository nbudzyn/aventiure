package de.nb.aventiure2.german.satz;

import androidx.annotation.Nullable;

import de.nb.aventiure2.german.base.Interrogativpronomen;
import de.nb.aventiure2.german.base.Konstituente;
import de.nb.aventiure2.german.base.SubstantivischePhrase;
import de.nb.aventiure2.german.praedikat.AdverbialeAngabeSkopusSatz;
import de.nb.aventiure2.german.praedikat.AdverbialeAngabeSkopusVerbAllg;
import de.nb.aventiure2.german.praedikat.AdverbialeAngabeSkopusVerbWohinWoher;
import de.nb.aventiure2.german.praedikat.PraedikatOhneLeerstellen;

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
    public Iterable<Konstituente> getIndirekteFrage() {
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
        return Konstituente.joinToKonstituenten(
                anschlusswort, // "und"
                erstesInterrogativpronomenImPraedikat, // "was" / "wem"
                Konstituente.cutFirst(
                        getVerbletztsatz(),
                        erstesInterrogativpronomenImPraedikat
                ) // "du zu berichten hast", "wer zu berichten hat"
        );
    }

    private Iterable<Konstituente> getIndirekteFrageNachSubjekt() {
        // "wer etwas zu berichten hat", "wer was zu berichten hat", "was er zu berichten hat"
        return getVerbletztsatz();
    }

    private Iterable<Konstituente> getObFrage() {
        return Konstituente.joinToKonstituenten(
                anschlusswort, // "und"
                "ob",
                getVerbletztsatz() // "du etwas zu berichten hast"
        );
    }

    /**
     * Gibt den Satz als Verbzweitsatz aus, bei dem nach Möglichkeit ein "spezielles"
     * Vorfeld gewählt wird, z.B. eine adverbiale Bestimmung: "Am Abend hast du etwas zu berichten"
     */
    public Iterable<Konstituente> getVerbzweitsatzMitSpeziellemVorfeldAlsWeitereOption() {
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
     * Gibt den Satz als Verbzweitsatz aus, bei dem das Subjekt im Vorfeld steht, z.B. "Du hast
     * am Abend etwas zu berichten" oder "Du nimmst den Ast"
     */
    public Iterable<Konstituente> getVerbzweitsatzStandard() {
        @Nullable final Konstituente speziellesVorfeld =
                praedikat.getSpeziellesVorfeldSehrErwuenscht(
                        subjekt.getPerson(), subjekt.getNumerus());
        if (speziellesVorfeld != null) {
            return getVerbzweitsatzMitVorfeldAusMittelOderNachfeld(speziellesVorfeld);
        }

        return Konstituente.capitalize(
                Konstituente.joinToKonstituenten(
                        anschlusswort, // "und"
                        subjekt.nom(),
                        praedikat.getVerbzweit(subjekt.getPerson(), subjekt.getNumerus()),
                        angabensatz != null ?
                                Konstituente.schliesseInKommaEin(angabensatz.getDescription()) :
                                null));
    }

    private Iterable<Konstituente> getVerbzweitsatzMitVorfeldAusMittelOderNachfeld(
            final Konstituente vorfeld) {
        return Konstituente.capitalize(
                Konstituente.joinToKonstituenten(
                        anschlusswort, // "und"
                        vorfeld,
                        Konstituente.cutFirst(
                                praedikat.getVerbzweitMitSubjektImMittelfeld(subjekt),
                                vorfeld),
                        angabensatz != null ?
                                Konstituente.schliesseInKommaEin(angabensatz.getDescription()) :
                                null));
    }

    public Iterable<Konstituente> getVerbzweitsatzMitVorfeld(final String vorfeld) {
        return Konstituente.capitalize(
                Konstituente.joinToKonstituenten(
                        anschlusswort, // "und"
                        vorfeld,
                        praedikat.getVerbzweitMitSubjektImMittelfeld(subjekt),
                        angabensatz != null ?
                                Konstituente.schliesseInKommaEin(angabensatz.getDescription()) :
                                null));
    }

    /**
     * Gibt den Satz als Verbletztsatz aus, z.B. "du etwas zu berichten hast"
     */
    Iterable<Konstituente> getVerbletztsatz() {
        return Konstituente.joinToKonstituenten(
                anschlusswort, // "und"
                subjekt.nom(),
                praedikat.getVerbletzt(subjekt.getPerson(), subjekt.getNumerus()),
                angabensatz != null ?
                        Konstituente.schliesseInKommaEin(angabensatz.getDescription()) :
                        null);
    }

    public SubstantivischePhrase getSubjekt() {
        return subjekt;
    }

    public PraedikatOhneLeerstellen getPraedikat() {
        return praedikat;
    }
}