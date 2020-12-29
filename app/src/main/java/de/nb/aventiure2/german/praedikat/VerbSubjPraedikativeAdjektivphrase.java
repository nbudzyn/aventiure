package de.nb.aventiure2.german.praedikat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import de.nb.aventiure2.german.adjektiv.AdjPhrOhneLeerstellen;
import de.nb.aventiure2.german.base.Numerus;
import de.nb.aventiure2.german.base.Person;
import de.nb.aventiure2.german.description.AllgDescription;

/**
 * Ein Verb, das mit einer prädikativen Adjektivphrase steht. (Z.B: "(glücklich) wirken").
 * <p>
 * Zum Verb sein mit Prädikativum siehe
 * {@link PraedikativumPraedikatOhneLeerstellen#praedikativumPraedikatMit(AllgDescription)}.
 */
public enum VerbSubjPraedikativeAdjektivphrase implements Praedikat {
    SCHEINEN("scheinen",
            "scheine", "scheinst", "scheint", "scheint",
            Perfektbildung.HABEN, "geschienen"),
    WIRKEN("wirken",
            "wirke", "wirkst", "wirkt", "wirkt",
            Perfektbildung.HABEN, "gewirkt");

    // FIXME ""sieht ... aus"! "Sie sieht gluecklich aus, dich zu treffen."
    // FIXME Vielleicht Dinge vermeiden wie "Sie sieht glücklich aus, dich zu sehen".

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

    VerbSubjPraedikativeAdjektivphrase(@NonNull final String infinitiv,
                                       @NonNull final String ichForm,
                                       @NonNull final String duForm,
                                       @NonNull final String erSieEsForm,
                                       @NonNull final String ihrForm,
                                       @Nullable final String partikel,
                                       @NonNull final Perfektbildung perfektbildung,
                                       final String partizipII) {
        this(new Verb(infinitiv, ichForm, duForm, erSieEsForm, ihrForm,
                partikel, perfektbildung, partizipII));
    }

    VerbSubjPraedikativeAdjektivphrase(@NonNull final Verb verb) {
        this.verb = verb;
    }

    public PraedikatMitPraedikativerAdjektivphraseOhneLeerstellen mit(
            final AdjPhrOhneLeerstellen adjektivphrase) {
        return new PraedikatMitPraedikativerAdjektivphraseOhneLeerstellen(verb,
                adjektivphrase);
    }

    public String getPraesensOhnePartikel(final Person person, final Numerus numerus) {
        return verb.getPraesensOhnePartikel(person, numerus);
    }
}
