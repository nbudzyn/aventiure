package de.nb.aventiure2.german.praedikat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import de.nb.aventiure2.german.description.AllgDescription;

/**
 * Ein Verb, das mit einer prädikativen Adjektivphrase steht. (Z.B: "(glücklich) wirken").
 * <p>
 * Zum Verb sein mit Prädikativum siehe
 * {@link PraedikativumPraedikatOhneLeerstellen#praedikativumPraedikatMit(AllgDescription)}.
 */
public enum VerbSubjPraedikativeAdjektivphrase implements Praedikat {
    SCHEINEN("scheinen", "scheinst", Perfektbildung.HABEN, "geschienen"),
    WIRKEN("wirken", "wirkst", Perfektbildung.HABEN, "gewirkt");

    // FIXME ""sieht ... aus"! Problem: Dann muss die prädikative Adjektivphrase
    //  diskontinuierlich aufgeteilt werden: Sie sieht gluecklich aus, dich zu treffen.
    //  Außerdem: Dinge verhindern wie "Sie sieht glücklich aus, dich zu sehen".

    /**
     * Das Verb an sich, ohne Informationen zur Valenz, ohne Ergänzungen, ohne
     * Angaben
     */
    @NonNull
    private final Verb verb;

    VerbSubjPraedikativeAdjektivphrase(@NonNull final String infinitiv,
                                       @NonNull final String duForm,
                                       @NonNull final Perfektbildung perfektbildung,
                                       final String partizipII) {
        this(new Verb(infinitiv, duForm, perfektbildung, partizipII));
    }

    VerbSubjPraedikativeAdjektivphrase(@NonNull final String infinitiv,
                                       @NonNull final String duForm,
                                       @Nullable final String partikel,
                                       @NonNull final Perfektbildung perfektbildung,
                                       final String partizipII) {
        this(new Verb(infinitiv, duForm, partikel, perfektbildung, partizipII));
    }

    VerbSubjPraedikativeAdjektivphrase(@NonNull final Verb verb) {
        this.verb = verb;
    }

    public PraedikatMitPraedikativerAdjektivphraseOhneLeerstellen mit(
            final AllgDescription praedikativeAdjektivphrase) {
        return new PraedikatMitPraedikativerAdjektivphraseOhneLeerstellen(verb,
                praedikativeAdjektivphrase);
    }

    public String getDuForm() {
        return verb.getDuFormOhnePartikel();
    }
}
