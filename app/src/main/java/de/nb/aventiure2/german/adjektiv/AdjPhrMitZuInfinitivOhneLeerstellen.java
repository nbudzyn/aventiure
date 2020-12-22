package de.nb.aventiure2.german.adjektiv;


import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import de.nb.aventiure2.annotations.Argument;
import de.nb.aventiure2.annotations.Valenz;
import de.nb.aventiure2.german.praedikat.PraedikatOhneLeerstellen;

/**
 * Eine Adjektivphrase mit zu-Infinitiv, in der alle Leerstellen besetzt sind. Beispiel:
 * <ul>
 *     <li>"glücklich, Peter zu sehen"
 *     <li>"sehr glücklich, Peter zu sehen"
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
        this(null, adjektiv, lexikalischerKern);
    }

    private AdjPhrMitZuInfinitivOhneLeerstellen(
            @Nullable final GraduativeAngabe graduativeAngabe,
            final Adjektiv adjektiv,
            final PraedikatOhneLeerstellen lexikalischerKern) {
        super(graduativeAngabe, adjektiv);
        this.lexikalischerKern = lexikalischerKern;
    }

    @Override
    public AdjPhrMitZuInfinitivOhneLeerstellen mitGraduativerAngabe(
            @Nullable final GraduativeAngabe graduativeAngabe) {
        if (graduativeAngabe == null) {
            return this;
        }

        return new AdjPhrMitZuInfinitivOhneLeerstellen(
                graduativeAngabe,
                getAdjektiv(),
                lexikalischerKern
        );
    }

}
