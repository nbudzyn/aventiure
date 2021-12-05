package de.nb.aventiure2.german.praedikat;

import static de.nb.aventiure2.german.praedikat.VerbSubjObj.SEHEN;

import androidx.annotation.NonNull;

import de.nb.aventiure2.german.adjektiv.AdjPhrOhneLeerstellen;

/**
 * Ein Verb, das mit einer prädikativen Adjektivphrase steht. (Z.B: "(glücklich) wirken").
 * <p>
 * Zum Verb <i>sein</i> mit Prädikativum siehe
 * {@link PraedikativumSemPraedikatOhneLeerstellen}.
 */
public enum VerbSubjPraedikativeAdjektivphrase implements VerbMitValenz {
    // Verben ohne Präfix
    GUCKEN("gucken",
            "gucke", "guckst", "guckt", "guckt",
            Perfektbildung.HABEN, "geguckt"),
    SCHAUEN("schauen",
            "schaue", "schaust", "schaut", "schaut",
            Perfektbildung.HABEN, "geschaut"),
    SCHEINEN("scheinen",
            "scheine", "scheinst", "scheint", "scheint",
            Perfektbildung.HABEN, "geschienen"),
    WIRKEN("wirken",
            "wirke", "wirkst",
            "wirkt", "wirkt",
            Perfektbildung.HABEN, "gewirkt"),

    //Präfixverben
    AUSSEHEN(SEHEN, "aus", Perfektbildung.HABEN),
    DREINSCHAUEN(VerbSubjObj.SCHAUEN, "drein", Perfektbildung.HABEN);

    /**
     * Das Verb an sich, ohne Informationen zur Valenz, ohne Ergänzungen, ohne
     * Angaben
     */
    @NonNull
    private final Verb verb;

    VerbSubjPraedikativeAdjektivphrase(@NonNull final String infinitiv,
                                       @NonNull final String ichForm,
                                       @NonNull final String duForm,
                                       @NonNull final String erSieEsForm,
                                       @NonNull final String ihrForm,
                                       @NonNull final Perfektbildung perfektbildung,
                                       final String partizipII) {
        this(new Verb(infinitiv, ichForm, duForm, erSieEsForm, ihrForm, perfektbildung,
                partizipII));
    }

    VerbSubjPraedikativeAdjektivphrase(final VerbMitValenz verbMitValenz,
                                       final String partikel,
                                       final Perfektbildung perfektbildung) {
        this(verbMitValenz.getVerb(), partikel, perfektbildung);
    }

    VerbSubjPraedikativeAdjektivphrase(final Verb verbOhnePartikel,
                                       final String partikel,
                                       final Perfektbildung perfektbildung) {
        this(verbOhnePartikel.mitPartikel(partikel).mitPerfektbildung(perfektbildung));
    }

    VerbSubjPraedikativeAdjektivphrase(@NonNull final Verb verb) {
        this.verb = verb;
    }

    public SemPraedikatMitPraedikativerAdjektivphraseOhneLeerstellen mit(
            final AdjPhrOhneLeerstellen adjektivphrase) {
        return new SemPraedikatMitPraedikativerAdjektivphraseOhneLeerstellen(verb,
                adjektivphrase);
    }

    @Override
    @NonNull
    public Verb getVerb() {
        return verb;
    }
}
