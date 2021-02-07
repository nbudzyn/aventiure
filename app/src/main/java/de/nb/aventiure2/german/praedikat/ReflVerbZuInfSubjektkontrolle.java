package de.nb.aventiure2.german.praedikat;

import androidx.annotation.NonNull;

import de.nb.aventiure2.german.base.Kasus;

import static de.nb.aventiure2.german.base.Kasus.AKK;

/**
 * Ein Verb wie "sich [Akk] freuen, zu ...". Dabei ist es das Subjekt, dass ... tut
 * ("Subjektkontrolle").
 */
public enum ReflVerbZuInfSubjektkontrolle implements VerbMitValenz {
    SICH_FREUEN_ZU("freuen", AKK,
            "freue", "freust", "freut",
            "freut", "gefreut");

    /**
     * Das Verb an sich, ohne Informationen zur Valenz, ohne Ergänzungen, ohne
     * Angaben
     */
    @NonNull
    private final Verb verb;

    /**
     * Der Kasus mit dem das Verb reflexiv steht - z.B. Akkusativ ("sich freuen") oder
     * Dativ ("sich vorstellen")
     */
    @NonNull
    private final Kasus kasus;

    ReflVerbZuInfSubjektkontrolle(final String infinitiv,
                                  final Kasus kasus,
                                  final String ichForm,
                                  final String duForm,
                                  final String erSieEsForm,
                                  final String ihrForm,
                                  final String partizipII) {
        verb = new Verb(infinitiv, ichForm, duForm, erSieEsForm, ihrForm,
                Perfektbildung.HABEN, partizipII);
        this.kasus = kasus;
    }

    /**
     * Füllt die Leerstelle für den lexikalischen Kern.
     */
    public PraedikatReflZuInfSubjektkontrollen mitLexikalischemKern(
            final PraedikatOhneLeerstellen lexikalischerKern) {
        return new PraedikatReflZuInfSubjektkontrollen(verb, kasus,
                lexikalischerKern);
    }

    @Override
    @NonNull
    public Verb getVerb() {
        return verb;
    }
}