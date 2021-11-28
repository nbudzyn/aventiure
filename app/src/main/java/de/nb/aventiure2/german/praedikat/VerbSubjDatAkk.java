package de.nb.aventiure2.german.praedikat;

import static de.nb.aventiure2.german.praedikat.VerbSubjAkkPraep.HALTEN;
import static de.nb.aventiure2.german.praedikat.VerbSubjAkkPraep.SCHUETTEN;
import static de.nb.aventiure2.german.praedikat.VerbSubjObj.BIETEN;
import static de.nb.aventiure2.german.praedikat.VerbSubjObj.SAGEN;

import androidx.annotation.NonNull;

import de.nb.aventiure2.german.base.SubstantivischePhrase;

/**
 * Ein Verb (ggf. mit Präfix), das genau mit einem Subjekt, einem Dativobjekt und
 * einem Akkusativ-Objekt steht.
 */
public enum VerbSubjDatAkk implements VerbMitValenz {
    // Verben ohne Präfix
    BERICHTEN("berichten",
            "berichte", "berichtest", "berichtet", "berichtet",
            Perfektbildung.HABEN, "berichtet"),
    ERKLAEREN("erklären",
            "erkläre", "erklärst", "erklärt", "erklärt",
            Perfektbildung.HABEN, "erklärt"),
    GEBEN("geben",
            "gebe", "gibst", "gibt", "gebt",
            Perfektbildung.HABEN, "gegeben"),
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

    // Präfixverben
    ANBIETEN(BIETEN, "an", Perfektbildung.HABEN),
    AUSSCHUETTEN(SCHUETTEN, "aus", Perfektbildung.HABEN),
    HINHALTEN(HALTEN, "hin", Perfektbildung.HABEN),
    ZUSAGEN(SAGEN, "zu", Perfektbildung.HABEN);

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

    VerbSubjDatAkk(final VerbMitValenz verbMitValenz,
                   final String partikel,
                   final Perfektbildung perfektbildung) {
        this(verbMitValenz.getVerb(), partikel, perfektbildung);
    }

    VerbSubjDatAkk(final Verb verbOhnePartikel,
                   final String partikel,
                   final Perfektbildung perfektbildung) {
        this(verbOhnePartikel.mitPartikel(partikel).mitPerfektbildung(perfektbildung));
    }

    VerbSubjDatAkk(final Verb verb) {
        this.verb = verb;
    }

    public SemPraedikatDatAkkMitEinerAkkLeerstelle mitDat(
            final SubstantivischePhrase substPhrDat) {
        return new SemPraedikatDatAkkMitEinerAkkLeerstelle(verb, substPhrDat);
    }

    public SemPraedikatDatAkkMitEinerDatLeerstelle mitAkk(
            final SubstantivischePhrase substPhrAkk) {
        return new SemPraedikatDatAkkMitEinerDatLeerstelle(verb, substPhrAkk);
    }

    @Override
    @NonNull
    public Verb getVerb() {
        return verb;
    }
}
