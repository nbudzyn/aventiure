package de.nb.aventiure2.german.praedikat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import de.nb.aventiure2.german.base.Numerus;
import de.nb.aventiure2.german.base.Person;
import de.nb.aventiure2.german.base.PraepositionMitKasus;
import de.nb.aventiure2.german.base.SubstantivischePhrase;

/**
 * Ein Verb (ggf. mit Präfix), das genau mit einem Subjekt, einem Präpositionalobjekt und
 * einem Akkusativ-Objekt steht.
 */
public enum VerbSubjAkkPraep implements Praedikat {
    // "die Zauberin nach ihrem Ziel fragen"
    FRAGEN_NACH("fragen", "fragst", PraepositionMitKasus.NACH),
    // "den Frosch in die Hände nehmen"
    NEHMEN_IN("nehmen", "nimmst", PraepositionMitKasus.IN_AKK);

    /**
     * Das Verb an sich, ohne Informationen zur Valenz, ohne Ergänzungen, ohne
     * Angaben
     */
    @NonNull
    private final Verb verb;

    @NonNull
    private final PraepositionMitKasus praepositionMitKasus;

    VerbSubjAkkPraep(@NonNull final String infinitiv,
                     @NonNull final String duForm,
                     @Nullable final PraepositionMitKasus praepositionMitKasus) {
        this(new Verb(infinitiv, duForm), praepositionMitKasus);
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
        return mitPraep(describablePraep).getDescriptionDuHauptsatz(describableAkk);
    }

    /**
     * Gibt eine Infinitivkonstruktion zurück mit diesem Verb und diesen Objekten.
     * ("den Frosch in die Hände nehmen")
     */
    public String getDescriptionInfinitiv(final Person person, final Numerus numerus,
                                          final SubstantivischePhrase describableAkk,
                                          final SubstantivischePhrase describablePraep) {
        return mitPraep(describablePraep).getDescriptionInfinitiv(
                person, numerus, describableAkk);
    }

    /**
     * Gibt eine zu-Infinitivkonstruktion zurück mit diesem Verb und diesen Objekten.
     * ("den Frosch in die Hände zu nehmen")
     */
    public String getDescriptionZuInfinitiv(final Person person, final Numerus numerus,
                                            final SubstantivischePhrase describableAkk,
                                            final SubstantivischePhrase describablePraep) {
        return mitPraep(describablePraep).getDescriptionZuInfinitiv(
                person, numerus, describableAkk);
    }

    public PraedikatMitEinerObjektleerstelle mitPraep(
            final SubstantivischePhrase describablePraep) {
        return new PraedikatPraepMitEinerAkkLeerstelle(verb,
                praepositionMitKasus,
                describablePraep);
    }

    public PraedikatMitEinerObjektleerstelle mitAkk(
            final SubstantivischePhrase describableAkk) {
        return new PraedikatAkkMitEinerPraepLeerstelle(verb,
                praepositionMitKasus,
                describableAkk);
    }
}
