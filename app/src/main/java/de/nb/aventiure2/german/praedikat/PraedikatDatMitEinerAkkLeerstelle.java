package de.nb.aventiure2.german.praedikat;

import androidx.annotation.NonNull;

import de.nb.aventiure2.german.base.SubstantivischePhrase;

/**
 * Ein Prädikat, in dem ein Dativobjekt schon gesetzt ist und genau nur noch für
 * ein Akkusativ-Objekt eine Leerstelle besteht.
 */
class PraedikatDatMitEinerAkkLeerstelle implements PraedikatMitEinerObjektleerstelle {
    /**
     * Das Verb an sich, ohne Informationen zur Valenz, ohne Ergänzungen, ohne
     * Angaben
     */
    @NonNull
    private final Verb verb;

    /**
     * Das Dativobjekt
     */
    @NonNull
    private final SubstantivischePhrase describableDat;

    public PraedikatDatMitEinerAkkLeerstelle(final Verb verb,
                                             final SubstantivischePhrase describableDat) {
        this.verb = verb;
        this.describableDat = describableDat;
    }

    @Override
    public PraedikatOhneLeerstellen mitObj(final SubstantivischePhrase describable) {
        return mitAkk(describable);
    }

    public PraedikatOhneLeerstellen mitAkk(
            final SubstantivischePhrase describableAkk) {
        return new PraedikatDatAkkOhneLeerstellen(verb,
                describableDat, describableAkk);
    }

    @Override
    public boolean duHauptsatzLaesstSichMitNachfolgendemDuHauptsatzZusammenziehen() {
        return true;
    }
}
