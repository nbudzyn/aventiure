package de.nb.aventiure2.german.satz;

import androidx.annotation.Nullable;

import de.nb.aventiure2.german.base.Interrogativpronomen;
import de.nb.aventiure2.german.base.Konstituente;
import de.nb.aventiure2.german.base.SubstantivischePhrase;
import de.nb.aventiure2.german.praedikat.PraedikatOhneLeerstellen;

/**
 * Ein Satz.
 */
public class Satz {
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
        this(subjekt, praedikat, null);
    }

    public Satz(final SubstantivischePhrase subjekt,
                final PraedikatOhneLeerstellen praedikat,
                @Nullable final Konditionalsatz angabensatz) {
        this.subjekt = subjekt;
        this.praedikat = praedikat;
        this.angabensatz = angabensatz;
    }

    public Satz mitAngabensatz(@Nullable final Konditionalsatz angabensatz) {
        if (angabensatz == null) {
            return this;
        }

        // STORY Mehrere Kondigionalsätze erlauben?

        return new Satz(subjekt, praedikat, angabensatz);
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
                "ob",
                getVerbletztsatz() // "du etwas zu berichten hast"
        );
    }

    /**
     * Gibt den Satz als Verbzweitsatz aus, z.B. "Du hast etwas zu berichten"
     */
    public Iterable<Konstituente> getVerbzweitsatz() {
        if (angabensatz == null) {
            return Konstituente.joinToKonstituenten(
                    subjekt.nom(),
                    praedikat.getVerbzweit(subjekt.getPerson(), subjekt.getNumerus())
            );
        }

        return Konstituente.joinToKonstituenten(
                subjekt.nom(),
                praedikat.getVerbzweit(subjekt.getPerson(), subjekt.getNumerus()),
                ",",
                Konstituente.withKommaStehtAus(angabensatz.getDescription())
                // es steht ein Komma aus
        );
    }

    /**
     * Gibt den Satz als Verbletztsatz aus, z.B. "du etwas zu berichten hast"
     */
    Iterable<Konstituente> getVerbletztsatz() {
        if (angabensatz == null) {
            return Konstituente.joinToKonstituenten(
                    subjekt.nom(),
                    praedikat.getVerbletzt(subjekt.getPerson(), subjekt.getNumerus())
            );
        }

        return Konstituente.joinToKonstituenten(
                subjekt.nom(),
                praedikat.getVerbletzt(subjekt.getPerson(), subjekt.getNumerus()),
                ",",
                Konstituente.withKommaStehtAus(angabensatz.getDescription())
                // es steht ein Komma aus
        );
    }

    public SubstantivischePhrase getSubjekt() {
        return subjekt;
    }

    public PraedikatOhneLeerstellen getPraedikat() {
        return praedikat;
    }
}