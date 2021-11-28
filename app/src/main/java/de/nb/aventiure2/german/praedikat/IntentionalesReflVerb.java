package de.nb.aventiure2.german.praedikat;

import static de.nb.aventiure2.german.base.Kasus.AKK;

import androidx.annotation.NonNull;

import de.nb.aventiure2.german.base.Kasus;

/**
 * Ein Verb wie <i>sich bemühen</i> oder </i>sich weigern</i> (etwas zu tun).
 * <p>
 * "Die vom Subjekt bezeichnete Person will oder will nicht die Handlung
 * ausführen, die im Komplement genannt ist", siehe Peter Eisenberg,
 * Der SemSatz, S. 356 (Kapitel 11.2) - wobei es sich um ein reflexives Verb handelt.
 *
 * @see IntentionalesVerb
 */
public enum IntentionalesReflVerb
        implements VerbMitValenz, PraedikatNurMitLeerstelleFuerLexikalischenKern {
    // "Rapunzel bemüht sich, die Haare herunterzulassen"
    SICH_BEMUEHEN("bemühen", AKK,
            "bemühe", "bemühst", "bemüht", "bemüht",
            Perfektbildung.HABEN, "bemüht");
    // Ein weiteres reflexives intentionales Verb ist "sich weigern".

    /**
     * Das Verb an sich, ohne Informationen zur Valenz, ohne Ergänzungen, ohne
     * Angaben
     */
    @NonNull
    private final Verb verb;

    /**
     * Der Kasus mit dem das Verb reflexiv steht - z.B. Akkusativ ("sich bemühen").
     */
    @NonNull
    private final Kasus kasus;

    IntentionalesReflVerb(final String infinitiv,
                          final Kasus kasus,
                          final String ichForm,
                          final String duForm,
                          final String erSieEsForm,
                          final String ihrForm,
                          final Perfektbildung perfektbildung, final String partizipII) {
        this(new Verb(infinitiv, ichForm, duForm, erSieEsForm, ihrForm, perfektbildung,
                partizipII), kasus);
    }

    IntentionalesReflVerb(final Verb verb, final Kasus kasus) {
        this.verb = verb;
        this.kasus = kasus;
    }

    /**
     * Füllt die Leerstelle für den lexikalischen Kern.
     */
    @Override
    public SemPraedikatReflIntentionalesVerbOhneLeerstellen mitLexikalischemKern(
            final SemPraedikatOhneLeerstellen lexikalischerKern) {
        return new SemPraedikatReflIntentionalesVerbOhneLeerstellen(verb, kasus,
                lexikalischerKern);
    }

    @Override
    @NonNull
    public Verb getVerb() {
        return verb;
    }
}
