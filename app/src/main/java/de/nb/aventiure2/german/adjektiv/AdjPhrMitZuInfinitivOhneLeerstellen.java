package de.nb.aventiure2.german.adjektiv;


import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import de.nb.aventiure2.annotations.Argument;
import de.nb.aventiure2.annotations.Valenz;
import de.nb.aventiure2.german.base.GermanUtil;
import de.nb.aventiure2.german.base.Numerus;
import de.nb.aventiure2.german.base.Person;
import de.nb.aventiure2.german.base.Wortfolge;
import de.nb.aventiure2.german.praedikat.PraedikatOhneLeerstellen;

import static de.nb.aventiure2.german.base.Wortfolge.w;

/**
 * Eine Adjektivphrase mit zu-Infinitiv, in der alle Leerstellen besetzt sind. Beispiel:
 * <ul>
 *     <li>"glücklich, Peter zu sehen"
 *     <li>"sehr glücklich, Peter zu sehen"
 * </ul>
 */
public class AdjPhrMitZuInfinitivOhneLeerstellen extends AbstractAdjPhrOhneLeerstellen {
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

    @Override
    public Wortfolge getPraedikativ(final Person person, final Numerus numerus) {
        return w(
                GermanUtil.joinToNull(
                        getGraduativeAngabe(), // "sehr"
                        getAdjektiv().getPraedikativ(), // "glücklich"
                        ", ",
                        lexikalischerKern.getZuInfinitiv(person, numerus)
                        // "sich erheben zu dürfen"
                ).getString(),
                true // Komma steht definitiv aus
        );
    }
}
