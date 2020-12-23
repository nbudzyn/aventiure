package de.nb.aventiure2.german.praedikat;

import androidx.annotation.NonNull;

import de.nb.aventiure2.german.base.Numerus;
import de.nb.aventiure2.german.base.Person;
import de.nb.aventiure2.german.base.PraepositionMitKasus;
import de.nb.aventiure2.german.base.SubstantivischePhrase;

/**
 * Ein Verb (ggf. mit Präfix), das genau mit einem Subjekt, einem Präpositionalobjekt und
 * einem Akkusativ-Objekt steht.
 */
public enum VerbSubjAkkPraep implements Praedikat {
    // "ein Gespräch mit Rapunzel beginnen"
    BEGINNEN("beginnen",
            "beginne", "beginnst", "beginnt", "beginnt",
            PraepositionMitKasus.MIT_DAT,
            Perfektbildung.HABEN, "begonnen"),

    // "die Zauberin nach ihrem Ziel fragen"
    FRAGEN_NACH("fragen",
            "frage", "fragst", "fragt", "fragt",
            PraepositionMitKasus.NACH,
            Perfektbildung.HABEN, "gefragt");

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

    VerbSubjAkkPraep(final Verb verb,
                     final PraepositionMitKasus praepositionMitKasus) {
        this.verb = verb;
        this.praepositionMitKasus = praepositionMitKasus;
    }

    /**
     * Gibt einen Satz zurück mit diesem Verb und diesen Objekten zurück.
     * ("Du nimmst den Frosch in die Hände")
     */
    public String getDescriptionHauptsatz(
            final SubstantivischePhrase describableAkk,
            final SubstantivischePhrase describablePraep) {
        return mitPraep(describablePraep).mit(describableAkk).getDuHauptsatz();
    }

    /**
     * Gibt eine Infinitivkonstruktion zurück mit diesem Verb und diesen Objekten.
     * ("den Frosch in die Hände nehmen")
     */
    public String getDescriptionInfinitiv(final Person person, final Numerus numerus,
                                          final SubstantivischePhrase describableAkk,
                                          final SubstantivischePhrase describablePraep) {
        return mitPraep(describablePraep).mit(describableAkk).getInfinitiv(person, numerus);
    }

    /**
     * Gibt eine zu-Infinitivkonstruktion zurück mit diesem Verb und diesen Objekten.
     * ("den Frosch in die Hände zu nehmen")
     */
    public String getDescriptionZuInfinitiv(final Person person, final Numerus numerus,
                                            final SubstantivischePhrase describableAkk,
                                            final SubstantivischePhrase describablePraep) {
        return mitPraep(describablePraep).mit(describableAkk).getInfinitiv(person, numerus);
    }

    public PraedikatMitEinerObjektleerstelle mitPraep(
            final SubstantivischePhrase describablePraep) {
        return new PraedikatAkkPraepMitEinerAkkLeerstelle(verb,
                praepositionMitKasus,
                describablePraep);
    }

    public PraedikatMitEinerObjektleerstelle mitAkk(
            final SubstantivischePhrase describableAkk) {
        return new PraedikatAkkPraepMitEinerPraepLeerstelle(verb,
                praepositionMitKasus,
                describableAkk);
    }
}
