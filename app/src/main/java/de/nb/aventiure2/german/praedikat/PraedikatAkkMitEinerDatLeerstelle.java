package de.nb.aventiure2.german.praedikat;

import androidx.annotation.NonNull;

import de.nb.aventiure2.german.base.DeklinierbarePhrase;

/**
 * Ein Prädikat, in dem ein Akkusative schon gesetzt ist und genau nur noch für
 * ein Dativ-Objekt eine Leerstelle besteht. Beispiel:
 * "... Angebote machen" (z.B. "dem Frosch Angebote machen)
 */
class PraedikatAkkMitEinerDatLeerstelle implements PraedikatMitEinerObjektleerstelle {
    /**
     * Das Verb an sich, ohne Informationen zur Valenz, ohne Ergänzungen, ohne
     * Angaben
     */
    @NonNull
    private final Verb verb;

    /**
     * Das (Objekt / Wesen / Konzept für das) Akkusativobjekt (z.B. "Angebote")
     */
    @NonNull
    private final DeklinierbarePhrase describableAkk;

    public PraedikatAkkMitEinerDatLeerstelle(final Verb verb,
                                             final DeklinierbarePhrase describableAkk) {
        this.verb = verb;
        this.describableAkk = describableAkk;
    }

    @Override
    public PraedikatOhneLeerstellen mitObj(final DeklinierbarePhrase describable) {
        return mitDat(describable);
    }

    public PraedikatOhneLeerstellen mitDat(
            final DeklinierbarePhrase describableDat) {
        return new PraedikatDatAkkOhneLeerstellen(verb,
                describableDat, describableAkk);
    }

    @Override
    public boolean duHauptsatzLaesstSichMitNachfolgendemDuHauptsatzZusammenziehen() {
        return true;
    }
}
