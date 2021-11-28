package de.nb.aventiure2.german.praedikat;

import androidx.annotation.NonNull;

import de.nb.aventiure2.german.base.WoertlicheRede;

/**
 * Ein Verb (ggf. mit Präfix), das genau mit einem Subjekt und wörtlicher Rede steht.
 */
public enum VerbSubjWoertlicheRede
        implements VerbMitValenz, SemPraedikatMitEinerLeerstelleFuerWoertlicheRede {
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

    VerbSubjWoertlicheRede(final Verb verb) {
        this.verb = verb;
    }

    @Override
    public SemPraedikatWoertlicheRedeOhneLeerstellen mitWoertlicheRede(
            final WoertlicheRede woertlicheRede) {
        return new SemPraedikatWoertlicheRedeOhneLeerstellen(verb,
                woertlicheRede);
    }

    @Override
    @NonNull
    public Verb getVerb() {
        return verb;
    }
}
