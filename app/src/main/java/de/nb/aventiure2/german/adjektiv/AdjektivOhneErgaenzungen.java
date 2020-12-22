package de.nb.aventiure2.german.adjektiv;

import androidx.annotation.NonNull;

/**
 * Ein Adjektiv, das keine Ergänzungen fordert.
 */
public enum AdjektivOhneErgaenzungen implements AdjPhrOhneLeerstellen {
    GLUECKLICH("glücklich");

    /**
     * Das Adjektiv an sich, ohne Informationen zur Valenz
     */
    @NonNull
    private final Adjektiv adjektiv;

    AdjektivOhneErgaenzungen(@NonNull final String praedikativ) {
        this(new Adjektiv(praedikativ));
    }

    AdjektivOhneErgaenzungen(@NonNull final Adjektiv adjektiv) {
        this.adjektiv = adjektiv;
    }
}
