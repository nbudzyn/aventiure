package de.nb.aventiure2.german.praedikat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import de.nb.aventiure2.german.base.WoertlicheRede;

/**
 * Ein Verb (ggf. mit Präfix), das genau mit einem Subjekt und wörtlicher Rede steht.
 */
public enum VerbSubjWoertlicheRede implements PraedikatMitEinerLeerstelleFuerWoertlicheRede {
    RUFEN("rufen", "rufe", "rufst", "ruft", "ruft",
            Perfektbildung.HABEN, "gerufen");

    /**
     * Das Verb an sich, ohne Ergänzungen, ohne Angaben
     */
    @NonNull
    private final Verb verb;

    VerbSubjWoertlicheRede(final String infinitiv,
                           final String ichForm,
                           final String duForm,
                           final String erSieEsForm,
                           final String ihrForm,
                           final Perfektbildung perfektbildung, final String partizipII) {
        this(new Verb(infinitiv, ichForm, duForm, erSieEsForm, ihrForm, perfektbildung,
                partizipII));
    }

    VerbSubjWoertlicheRede(final String infinitiv,
                           final String ichForm,
                           final String duForm,
                           final String erSieEsForm,
                           final String ihrForm,
                           @Nullable final String partikel,
                           final Perfektbildung perfektbildung, final String partizipII) {
        this(new Verb(infinitiv, ichForm, duForm, erSieEsForm, ihrForm, partikel, perfektbildung,
                partizipII));
    }

    VerbSubjWoertlicheRede(final Verb verb) {
        this.verb = verb;
    }

    @Override
    public PraedikatMitWoertlicherRedeOhneLeerstellen mitWoertlicherRede(
            final WoertlicheRede woertlicheRede) {
        return new PraedikatMitWoertlicherRedeOhneLeerstellen(verb,
                woertlicheRede);
    }
}
