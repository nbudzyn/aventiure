package de.nb.aventiure2.german.adjektiv;


import javax.annotation.Nonnull;

import de.nb.aventiure2.annotations.Argument;
import de.nb.aventiure2.annotations.Valenz;
import de.nb.aventiure2.german.praedikat.PraedikatOhneLeerstellen;

/**
 * Eine Adjektivphrase mit zu-Infinitiv, in der alle Leerstellen besetzt sind. Beispiel:
 * <ul>
 *     <li>"glücklich, Peter zu sehen"
 * </ul>
 */
class AdjPhrMitZuInfinitivOhneLeerstellen extends AbstractAdjPhrOhneLeerstellen {
    /**
     * "(...glücklich,) Peter zu sehen"
     */
    @Nonnull
    @Argument
    private final PraedikatOhneLeerstellen lexikalischerKern;

    @Valenz
    AdjPhrMitZuInfinitivOhneLeerstellen(
            final Adjektiv adjektiv,
            final PraedikatOhneLeerstellen lexikalischerKern) {
        super(adjektiv);
        this.lexikalischerKern = lexikalischerKern;
    }
}
