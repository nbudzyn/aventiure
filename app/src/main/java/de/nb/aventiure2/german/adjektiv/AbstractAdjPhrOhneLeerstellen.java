package de.nb.aventiure2.german.adjektiv;

import androidx.annotation.NonNull;

/**
 * Eine Adjektivphrase, in der alle Leerstellen besetzt sind. Beispiele:
 * <ul>
 *     <li>"glücklich"
 *     <li>"glücklich, dich zu sehen!"
 * </ul>
 * <p>
 * Adverbiale Angaben ("aus Langeweile") können immer noch eingefügt werden.
 */
abstract class AbstractAdjPhrOhneLeerstellen implements AdjPhrOhneLeerstellen {
    /**
     * Das Adjektiv an sich, ohne Informationen zur Valenz
     */
    @NonNull
    private final Adjektiv adjektiv;

    AbstractAdjPhrOhneLeerstellen(final Adjektiv adjektiv) {
        this.adjektiv = adjektiv;
    }
}
