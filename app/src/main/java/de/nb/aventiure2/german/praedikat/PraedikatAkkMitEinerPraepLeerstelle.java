package de.nb.aventiure2.german.praedikat;

import androidx.annotation.NonNull;

import de.nb.aventiure2.german.base.PraepositionMitKasus;
import de.nb.aventiure2.german.base.SubstantivischePhrase;

/**
 * Ein Prädikat, in dem ein Akkusativ schon gesetzt ist und genau nur noch für
 * ein Präpositionalobjekt eine Leerstelle besteht.
 */
class PraedikatAkkMitEinerPraepLeerstelle implements PraedikatMitEinerObjektleerstelle {
    /**
     * Das Verb an sich, ohne Informationen zur Valenz, ohne Ergänzungen, ohne
     * Angaben
     */
    @NonNull
    private final Verb verb;

    @NonNull
    private final PraepositionMitKasus praepositionMitKasus;

    /**
     * Das Akkusativobjekt
     */
    @NonNull
    private final SubstantivischePhrase describableAkk;

    public PraedikatAkkMitEinerPraepLeerstelle(final Verb verb,
                                               final PraepositionMitKasus praepositionMitKasus,
                                               final SubstantivischePhrase describableAkk) {
        this.verb = verb;
        this.praepositionMitKasus = praepositionMitKasus;
        this.describableAkk = describableAkk;
    }

    @Override
    public PraedikatOhneLeerstellen mitObj(final SubstantivischePhrase describable) {
        return mitPraep(describable);
    }

    public PraedikatOhneLeerstellen mitPraep(
            final SubstantivischePhrase describablePraep) {
        return new PraedikatAkkPraepOhneLeerstellen(
                verb, praepositionMitKasus, describableAkk, describablePraep);
    }

    @Override
    public boolean duHauptsatzLaesstSichMitNachfolgendemDuHauptsatzZusammenziehen() {
        return true;
    }
}
