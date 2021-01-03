package de.nb.aventiure2.german.adjektiv;

import androidx.annotation.NonNull;

import java.util.Objects;

/**
 * Repräsentiert ein Adjektiv als Lexem, von dem Wortformen gebildet werden können - jedoch <i>ohne
 * Informationen zur Valenz</i>.
 */
public class Adjektiv {
    /**
     * Prädikative Form des Verbs ("glücklich", "hoch")
     */
    @NonNull
    private final String praedikativ;

    public Adjektiv(final String praedikativ) {
        this.praedikativ = praedikativ;
    }

    /**
     * Gibt die prädikative Form des Verbs zurück ("glücklich", "hoch").
     */
    @NonNull
    public String getPraedikativ() {
        return praedikativ;
    }

    public AdjPhrOhneErgaenzungenOhneLeerstellen toAdjPhr() {
        return new AdjPhrOhneErgaenzungenOhneLeerstellen(this);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final Adjektiv verb = (Adjektiv) o;
        return praedikativ.equals(verb.praedikativ);
    }

    @Override
    public int hashCode() {
        return Objects.hash(praedikativ);
    }

    @NonNull
    @Override
    public String toString() {
        return "Verb{" +
                "prädikative Form='" + praedikativ + '\'' +
                '}';
    }
}
