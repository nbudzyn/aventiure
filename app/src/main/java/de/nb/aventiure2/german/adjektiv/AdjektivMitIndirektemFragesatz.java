package de.nb.aventiure2.german.adjektiv;

import androidx.annotation.NonNull;

import de.nb.aventiure2.german.satz.Satz;

/**
 * Ein Adjektiv, das einen ob- oder w-Fragesatz (eine indirekte Frage) fordert:
 * "gespannt, ob du etwas zu berichten hast / was du zu berichten hast / wessen Heldentaten
 * wer zu berichten hat"
 */
public enum AdjektivMitIndirektemFragesatz implements Adjektivphrase {
    // "gespannt, ob du etwas zu berichten hast / was du zu berichten hast / wessen Heldentaten wer zu berichten hat"
    GESPANNT("gespannt");

    /**
     * Das Adjektiv an sich, ohne Informationen zur Valenz
     */
    @NonNull
    private final Adjektiv adjektiv;

    AdjektivMitIndirektemFragesatz(@NonNull final String praedikativ) {
        this(new Adjektiv(praedikativ));
    }


    AdjektivMitIndirektemFragesatz(@NonNull final Adjektiv adjektiv) {
        this.adjektiv = adjektiv;
    }

    public AdjPhrMitIndirektemFragesatzOhneLeerstellen mitIndirektemFragesatz(
            final Satz indirekterFragesatz) {
        return new AdjPhrMitIndirektemFragesatzOhneLeerstellen(adjektiv, indirekterFragesatz);
    }
}
