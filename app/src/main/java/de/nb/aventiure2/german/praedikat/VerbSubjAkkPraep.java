package de.nb.aventiure2.german.praedikat;

import static de.nb.aventiure2.german.base.PraepositionMitKasus.FUER;
import static de.nb.aventiure2.german.praedikat.VerbSubjObj.SETZEN;

import androidx.annotation.NonNull;

import de.nb.aventiure2.german.base.PraepositionMitKasus;
import de.nb.aventiure2.german.base.SubstantivischePhrase;

/**
 * Ein Verb (ggf. mit Präfix), das genau mit einem Subjekt, einem Präpositionalobjekt und
 * einem Akkusativ-Objekt steht.
 */
public enum VerbSubjAkkPraep implements VerbMitValenz {
    // Verben ohne Partikel
    // "ein Gespräch mit Rapunzel beginnen"
    BEGINNEN("beginnen",
            "beginne", "beginnst", "beginnt", "beginnt",
            PraepositionMitKasus.MIT_DAT,
            Perfektbildung.HABEN, "begonnen"),
    // "die Zauberin nach ihrem Ziel fragen"
    FRAGEN_NACH("fragen",
            "frage", "fragst", "fragt", "fragt",
            PraepositionMitKasus.NACH,
            Perfektbildung.HABEN, "gefragt"),
    HALTEN("halten",
            "halte", "hältst", "hält", "haltet",
            FUER,
            Perfektbildung.HABEN, "gehalten"),
    SCHUETTEN("schütten",
            "schütte", "schüttest", "schüttet", "schüttet",
            PraepositionMitKasus.IN_AKK,
            Perfektbildung.HABEN, "geschüttet"),

    // Partikelverben
    // "das Gespräch mit Rapunzel beginnen"
    FORTSETZEN(SETZEN, PraepositionMitKasus.MIT_DAT, "fort",
            Perfektbildung.HABEN);

    /**
     * Das Verb an sich, ohne Informationen zur Valenz, ohne Ergänzungen, ohne
     * Angaben
     */
    @NonNull
    private final Verb verb;

    @NonNull
    private final PraepositionMitKasus praepositionMitKasus;

    VerbSubjAkkPraep(final String infinitiv,
                     @NonNull final String ichForm,
                     @NonNull final String duForm,
                     @NonNull final String erSieEsForm,
                     @NonNull final String ihrForm,
                     final PraepositionMitKasus praepositionMitKasus,
                     final Perfektbildung perfektbildung, final String partizipII) {
        this(new Verb(infinitiv, ichForm, duForm, erSieEsForm, ihrForm, perfektbildung, partizipII),
                praepositionMitKasus);
    }

    VerbSubjAkkPraep(final VerbMitValenz verbMitValenz,
                     final PraepositionMitKasus praepositionMitKasus,
                     final String partikel,
                     final Perfektbildung perfektbildung) {
        this(verbMitValenz.getVerb(), praepositionMitKasus, partikel, perfektbildung);
    }

    VerbSubjAkkPraep(final Verb verbOhnePartikel,
                     final PraepositionMitKasus praepositionMitKasus,
                     final String partikel,
                     final Perfektbildung perfektbildung) {
        this(verbOhnePartikel.mitPartikel(partikel).mitPerfektbildung(perfektbildung),
                praepositionMitKasus);
    }

    VerbSubjAkkPraep(final Verb verb,
                     final PraepositionMitKasus praepositionMitKasus) {
        this.verb = verb;
        this.praepositionMitKasus = praepositionMitKasus;
    }

    public SemPraedikatMitEinerObjektleerstelle mitPraep(
            final SubstantivischePhrase substPhrPraep) {
        return new SemPraedikatAkkPraepMitEinerAkkLeerstelle(
                verb, praepositionMitKasus, substPhrPraep);
    }

    public SemPraedikatMitEinerObjektleerstelle mitAkk(
            final SubstantivischePhrase substPhrAkk) {
        return new SemPraedikatAkkPraepMitEinerPraepLeerstelle(
                verb, praepositionMitKasus, substPhrAkk);
    }

    @Override
    @NonNull
    public Verb getVerb() {
        return verb;
    }
}
