package de.nb.aventiure2.german.praedikat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Ein Verb wie versuchen, beabsichtigen, sich weigern, versäumen,
 * zögern etc. (etwas zu tun)
 * <p>
 * "Die vom Subjekt bezeichnete Person will oder will nicht die Handlung
 * ausführen, die im Komplement genannt ist", siehe Peter Eisenberg,
 * Der Satz, S. 356 (Kapitel 11.2)
 */
public enum IntentionalesVerb implements Praedikat {
    // "Rapunzel versucht, die Haare herunterzulassen"
    VERSUCHEN("versuchen", "versuchst", Perfektbildung.HABEN, "versucht");
    // Weitere direktive Verben sind beabsichtigen, sich weigern, versäumen,
    // zögern

    /**
     * Das Verb an sich, ohne Informationen zur Valenz, ohne Ergänzungen, ohne
     * Angaben
     */
    @NonNull
    private final Verb verb;

    IntentionalesVerb(@NonNull final String infinitiv,
                      @NonNull final String duForm,
                      final Perfektbildung perfektbildung, final String partizipII) {
        this(new Verb(infinitiv, duForm, perfektbildung, partizipII));
    }

    IntentionalesVerb(@NonNull final String infinitiv,
                      @NonNull final String duForm,
                      @Nullable final String partikel,
                      final Perfektbildung perfektbildung, final String partizipII) {
        this(new Verb(infinitiv, duForm, partikel, perfektbildung, partizipII));
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
}
