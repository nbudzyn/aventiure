package de.nb.aventiure2.german.praedikat;

public interface PraedikatNurMitLeerstelleFuerLexikalischenKern {
    /**
     * Füllt die Leerstelle für den lexikalischen Kern mit einem Prädikat mit einer
     * Objektleerstelle
     */
    default SemPraedikatMitEinerObjektleerstelle mitLexikalischemKern(
            final SemPraedikatMitEinerObjektleerstelle lexikalischerKern) {
        return substPhrasierbar -> mitLexikalischemKern(lexikalischerKern.mit(substPhrasierbar));
    }

    /**
     * Füllt die Leerstelle für den lexikalischen Kern.
     */
    SemPraedikatOhneLeerstellen mitLexikalischemKern(
            final SemPraedikatOhneLeerstellen lexikalischerKern);
}
