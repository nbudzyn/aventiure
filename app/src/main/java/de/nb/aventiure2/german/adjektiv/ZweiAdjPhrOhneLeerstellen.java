package de.nb.aventiure2.german.adjektiv;

import javax.annotation.Nullable;

/**
 * Zwei Adjektivphrasen ohne Leerstellen, die mit <i>und</i>
 * verbunden werden
 */
public class ZweiAdjPhrOhneLeerstellen implements AdjPhrOhneLeerstellen {
    private final AdjPhrOhneLeerstellen ersteAdjPhr;
    private final AdjPhrOhneLeerstellen zweiteAdjPhr;

    private ZweiAdjPhrOhneLeerstellen(
            final AdjPhrOhneLeerstellen ersteAdjPhr,
            final AdjPhrOhneLeerstellen zweiteAdjPhr) {
        this.ersteAdjPhr = ersteAdjPhr;
        this.zweiteAdjPhr = zweiteAdjPhr;
    }

    @Override
    public AdjPhrOhneLeerstellen mitGraduativerAngabe(
            @Nullable final GraduativeAngabe graduativeAngabe) {
        return new ZweiAdjPhrOhneLeerstellen(
                ersteAdjPhr.mitGraduativerAngabe(graduativeAngabe),
                zweiteAdjPhr
        );
    }
}
