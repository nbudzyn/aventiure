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
    // "ein Gespräch mit Rapunzel beginnen"
    BEGINNEN("beginnen", "beginnst", PraepositionMitKasus.MIT_DAT),

    // "die Zauberin nach ihrem Ziel fragen"
    FRAGEN_NACH("fragen", "fragst", PraepositionMitKasus.NACH);

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
        return mitPraep(describablePraep).mitObj(describableAkk).getDuHauptsatz();
    }

    /**
     * Gibt eine Infinitivkonstruktion zurück mit diesem Verb und diesen Objekten.
     * ("den Frosch in die Hände nehmen")
     */
    public String getDescriptionInfinitiv(final Person person, final Numerus numerus,
                                          final SubstantivischePhrase describableAkk,
                                          final SubstantivischePhrase describablePraep) {
        return mitPraep(describablePraep).mitObj(describableAkk).getInfinitiv(person, numerus);
    }

    /**
     * Gibt eine zu-Infinitivkonstruktion zurück mit diesem Verb und diesen Objekten.
     * ("den Frosch in die Hände zu nehmen")
     */
    public String getDescriptionZuInfinitiv(final Person person, final Numerus numerus,
                                            final SubstantivischePhrase describableAkk,
                                            final SubstantivischePhrase describablePraep) {
        return mitPraep(describablePraep).mitObj(describableAkk).getInfinitiv(person, numerus);
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
