package de.nb.aventiure2.german.praedikat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import de.nb.aventiure2.german.base.WoertlicheRede;

/**
 * Ein Verb (ggf. mit Präfix), das genau mit einem Subjekt und wörtlicher Rede steht.
 */
public enum VerbSubjWoertlicheRede implements PraedikatMitEinerLeerstelleFuerWoertlicheRede {
    RUFEN("rufen", "rufst");

    /**
     * Das Verb an sich, ohne Ergänzungen, ohne Angaben
     */
    @NonNull
    private final Verb verb;

    VerbSubjWoertlicheRede(@NonNull final String infinitiv,
                           @NonNull final String duForm) {
        this(new Verb(infinitiv, duForm));
    }

    VerbSubjWoertlicheRede(@NonNull final String infinitiv,
                           @NonNull final String duForm,
                           @Nullable final String partikel) {
        this(new Verb(infinitiv, duForm, partikel));
    }

    VerbSubjWoertlicheRede(@NonNull final Verb verb) {
        this.verb = verb;
    }

    @Override
    public PraedikatMitWoertlicherRedeOhneLeerstellen mitWoertlicherRede(
            final WoertlicheRede woertlicheRede) {
        return new PraedikatMitWoertlicherRedeOhneLeerstellen(verb,
                woertlicheRede);
    }
}
