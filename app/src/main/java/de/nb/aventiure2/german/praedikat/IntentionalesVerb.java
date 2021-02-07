package de.nb.aventiure2.german.praedikat;

import androidx.annotation.NonNull;

/**
 * Ein Verb wie versuchen, beabsichtigen, sich weigern, versäumen,
 * zögern etc. (etwas zu tun)
 * <p>
 * "Die vom Subjekt bezeichnete Person will oder will nicht die Handlung
 * ausführen, die im Komplement genannt ist", siehe Peter Eisenberg,
 * Der Satz, S. 356 (Kapitel 11.2)
 */
public enum IntentionalesVerb implements VerbMitValenz {
    // "Rapunzel versucht, die Haare herunterzulassen"
    VERSUCHEN("versuchen",
            "versuche", "versuchst", "versucht", "versucht",
            Perfektbildung.HABEN, "versucht");
    // Weitere direktive Verben sind beabsichtigen, sich weigern, versäumen,
    // zögern

    /**
     * Das Verb an sich, ohne Informationen zur Valenz, ohne Ergänzungen, ohne
     * Angaben
     */
    @NonNull
    private final Verb verb;

    IntentionalesVerb(@NonNull final String infinitiv,
                      @NonNull final String ichForm,
                      @NonNull final String duForm,
                      @NonNull final String erSieEsForm,
                      @NonNull final String ihrForm,
                      final Perfektbildung perfektbildung, final String partizipII) {
        this(new Verb(infinitiv, ichForm, duForm, erSieEsForm, ihrForm, perfektbildung,
                partizipII));
    }

    IntentionalesVerb(@NonNull final Verb verb) {
        this.verb = verb;
    }

    /**
     * Füllt die Leerstelle für den lexikalischen Kern.
     */
    public PraedikatIntentionalesVerbOhneLeerstellen mitLexikalischemKern(
            final PraedikatOhneLeerstellen lexikalischerKern) {
        return new PraedikatIntentionalesVerbOhneLeerstellen(verb,
                lexikalischerKern);
    }

    @Override
    @NonNull
    public Verb getVerb() {
        return verb;
    }
}
