package de.nb.aventiure2.german.adjektiv;

import androidx.annotation.NonNull;

import de.nb.aventiure2.german.praedikat.PraedikatOhneLeerstellen;

/**
 * Ein Adjektiv, das einen zu-Infinitiv fordert: "glücklich, Peter zu sehen".
 */
public enum AdjektivMitZuInfinitiv implements Adjektivphrase {
    // "glücklich, Peter zu sehen"
    GLUECKLICH("glücklich"),
    UEBERGLUECKLICH("überglücklich"),
    UEBERRASCHT("überrascht")
    // Z.B. gewillt
    ;

    /**
     * Das Adjektiv an sich, ohne Informationen zur Valenz
     */
    @NonNull
    private final Adjektiv adjektiv;

    AdjektivMitZuInfinitiv(@NonNull final String praedikativ) {
        this(new Adjektiv(praedikativ));
    }


    AdjektivMitZuInfinitiv(@NonNull final Adjektiv adjektiv) {
        this.adjektiv = adjektiv;
    }

    public AdjPhrMitZuInfinitivOhneLeerstellen mitLexikalischerKern(
            final PraedikatOhneLeerstellen lexikalischerKern) {
        return new AdjPhrMitZuInfinitivOhneLeerstellen(adjektiv, lexikalischerKern);
    }
}
