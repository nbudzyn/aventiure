package de.nb.aventiure2.german.praedikat;

import androidx.annotation.NonNull;

import de.nb.aventiure2.german.base.PraepositionMitKasus;
import de.nb.aventiure2.german.base.SubstantivischePhrase;

/**
 * Ein Prädikat, in dem ein Präpositionalobjekt schon gesetzt ist und genau nur noch für
 * ein Akkusativ-Objekt eine Leerstelle besteht.  Beispiel:
 * * "... in die Hände nehmen" (z.B. "den Frosch in die Hände nehmen")
 */
class PraedikatPraepMitEinerAkkLeerstelle
        implements PraedikatMitEinerObjektleerstelle {
    /**
     * Das Verb an sich, ohne Informationen zur Valenz, ohne Ergänzungen, ohne
     * Angaben
     */
    @NonNull
    private final Verb verb;

    @NonNull
    private final PraepositionMitKasus praepositionMitKasus;

    /**
     * Das Präpositionalobjekt
     */
    @NonNull
    private final SubstantivischePhrase describablePraep;

    public PraedikatPraepMitEinerAkkLeerstelle(final Verb verb,
                                               @NonNull final
                                               PraepositionMitKasus praepositionMitKasus,
                                               final SubstantivischePhrase describablePraep) {
        this.verb = verb;
        this.praepositionMitKasus = praepositionMitKasus;
        this.describablePraep = describablePraep;
    }

    @Override
    public PraedikatOhneLeerstellen mitObj(final SubstantivischePhrase describable) {
        return mitAkk(describable);
    }

    public PraedikatOhneLeerstellen mitAkk(final SubstantivischePhrase describableAkk) {
        return new PraedikatAkkPraepOhneLeerstellen(
                verb, praepositionMitKasus, describableAkk, describablePraep);
    }

    @Override
    public boolean duHauptsatzLaesstSichMitNachfolgendemDuHauptsatzZusammenziehen() {
        return true;
    }
}
