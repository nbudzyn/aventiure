package de.nb.aventiure2.german.adjektiv;

import androidx.annotation.NonNull;

/**
 * Ein Adjektiv, das keine Ergänzungen fordert.
 */
public enum AdjektivOhneErgaenzungen implements AdjPhrOhneLeerstellen {
    GESPANNT("gespannt"),
    GLUECKLICH("glücklich"),
    VERAERGERT("verärgert"),
    UEBERRASCHT("überrascht"),
    UEBERRUMPELT("überrumpelt"),
    VERSTIMMT("verstimmt"),
    VERWIRRT("verwirrt"),
    VERWUNDERT("verwundert");

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

    @Override
    public AdjPhrOhneErgaenzungenOhneLeerstellen mitGraduativerAngabe(
            final GraduativeAngabe graduativeAngabe) {
        return toAdjPhr().mitGraduativerAngabe(graduativeAngabe);
    }

    private AdjPhrOhneErgaenzungenOhneLeerstellen toAdjPhr() {
        return new AdjPhrOhneErgaenzungenOhneLeerstellen(
                adjektiv
        );
    }
}
