package de.nb.aventiure2.german.adjektiv;


import javax.annotation.Nullable;

import de.nb.aventiure2.annotations.Valenz;

/**
 * Eine Adjektivphrase die keiner Ergänzungen fordert. Beispiele:
 * <ul>
 *     <li>"glücklich"
 *     <li>"sehr glücklich"
 * </ul>
 */
class AdjPhrOhneErgaenzungenOhneLeerstellen extends AbstractAdjPhrOhneLeerstellen {
    @Valenz
    AdjPhrOhneErgaenzungenOhneLeerstellen(
            final Adjektiv adjektiv) {
        this(null, adjektiv);
    }

    private AdjPhrOhneErgaenzungenOhneLeerstellen(
            @Nullable final GraduativeAngabe graduativeAngabe,
            final Adjektiv adjektiv) {
        super(graduativeAngabe, adjektiv);
    }

    @Override
    public AdjPhrOhneErgaenzungenOhneLeerstellen mitGraduativerAngabe(
            @Nullable final GraduativeAngabe graduativeAngabe) {
        if (graduativeAngabe == null) {
            return this;
        }

        return new AdjPhrOhneErgaenzungenOhneLeerstellen(
                graduativeAngabe,
                getAdjektiv()
        );
    }
}
