package de.nb.aventiure2.german.praedikat;

import androidx.annotation.NonNull;

import de.nb.aventiure2.german.base.DeklinierbarePhrase;

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
    private final DeklinierbarePhrase describableDat;

    public PraedikatDatMitEinerAkkLeerstelle(final Verb verb,
                                             final DeklinierbarePhrase describableDat) {
        this.verb = verb;
        this.describableDat = describableDat;
    }

    @Override
    public PraedikatOhneLeerstellen mitObj(final DeklinierbarePhrase describable) {
        return mitAkk(describable);
    }

    public PraedikatOhneLeerstellen mitAkk(
            final DeklinierbarePhrase describableAkk) {
        return new PraedikatDatAkkOhneLeerstellen(verb,
                describableDat, describableAkk);
    }

    @Override
    public boolean duHauptsatzLaesstSichMitNachfolgendemDuHauptsatzZusammenziehen() {
        return true;
    }
}
