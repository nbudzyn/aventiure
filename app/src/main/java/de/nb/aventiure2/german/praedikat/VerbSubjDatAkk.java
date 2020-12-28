package de.nb.aventiure2.german.praedikat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import de.nb.aventiure2.german.base.SubstantivischePhrase;

/**
 * Ein Verb (ggf. mit Präfix), das genau mit einem Subjekt, einem Dativobjekt und
 * einem Akkusativ-Objekt steht.
 */
public enum VerbSubjDatAkk implements Praedikat {
    ANBIETEN("anbieten",
            "biete", "bietest", "bietet", "bietet",
            "an",
            Perfektbildung.HABEN, "angeboten"),
    AUSSCHUETTEN("ausschütten",
            "schütte", "schüttest", "schüttet", "schüttet",
            "aus",
            Perfektbildung.HABEN, "ausgeschüttet"),
    BERICHTEN("berichten",
            "berichte", "berichtest", "berichtet", "berichtet",
            Perfektbildung.HABEN, "berichtet"),
    GEBEN("geben",
            "gebe", "gibst", "gibt", "gebt",
            Perfektbildung.HABEN, "gegeben"),
    HINHALTEN("hinhalten",
            "halte", "hältst", "hält", "haltet",
            "hin",
            Perfektbildung.HABEN, "hingehalten"),
    // "dem Frosch Angebote machen"
    MACHEN("machen",
            "mache", "machst", "macht", "macht",
            Perfektbildung.HABEN, "gemacht"),
    REICHEN("reichen",
            "reiche", "reichst", "reicht", "reicht",
            Perfektbildung.HABEN, "gereicht"),
    VERSPRECHEN("versprechen",
            "verspreche", "versprichst", "verspricht",
            "versprecht",
            Perfektbildung.HABEN, "versprochen"),
    ZEIGEN("zeigen",
            "zeige", "zeigst", "zeigt", "zeigt",
            Perfektbildung.HABEN, "gezeigt"),
    ;

    /**
     * Das Verb an sich, ohne Informationen zur Valenz, ohne Ergänzungen, ohne
     * Angaben
     */
    @NonNull
    private final Verb verb;

    VerbSubjDatAkk(@NonNull final String infinitiv,
                   @NonNull final String ichForm,
                   @NonNull final String duForm,
                   @NonNull final String erSieEsForm,
                   @NonNull final String ihrForm,
                   final Perfektbildung perfektbildung, final String partizipII) {
        this(new Verb(infinitiv, ichForm, duForm, erSieEsForm, ihrForm, perfektbildung,
                partizipII));
    }

    VerbSubjDatAkk(@NonNull final String infinitiv,
                   @NonNull final String ichForm,
                   @NonNull final String duForm,
                   @NonNull final String erSieEsForm,
                   @NonNull final String ihrForm,
                   @Nullable final String partikel,
                   final Perfektbildung perfektbildung, final String partizipII) {
        this(new Verb(infinitiv, ichForm, duForm, erSieEsForm, ihrForm, partikel, perfektbildung,
                partizipII));
    }

    VerbSubjDatAkk(final Verb verb) {
        this.verb = verb;
    }

    public PraedikatDatAkkMitEinerAkkLeerstelle mitDat(
            final SubstantivischePhrase describableDat) {
        return new PraedikatDatAkkMitEinerAkkLeerstelle(verb,
                describableDat);
    }

    public PraedikatDatAkkMitEinerDatLeerstelle mitAkk(
            final SubstantivischePhrase describableAkk) {
        return new PraedikatDatAkkMitEinerDatLeerstelle(verb,
                describableAkk);
    }
}
