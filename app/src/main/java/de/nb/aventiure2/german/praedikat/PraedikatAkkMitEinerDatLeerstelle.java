package de.nb.aventiure2.german.praedikat;

import androidx.annotation.NonNull;

import de.nb.aventiure2.german.base.SubstantivischePhrase;

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
    private final SubstantivischePhrase describableAkk;

    public PraedikatAkkMitEinerDatLeerstelle(final Verb verb,
                                             final SubstantivischePhrase describableAkk) {
        this.verb = verb;
        this.describableAkk = describableAkk;
    }

    @Override
    public AbstractPraedikatOhneLeerstellen mitObj(final SubstantivischePhrase describable) {
        return mitDat(describable);
    }

    public AbstractPraedikatOhneLeerstellen mitDat(
            final SubstantivischePhrase describableDat) {
        return new PraedikatDatAkkOhneLeerstellen(verb,
                describableDat, describableAkk);
    }
}
