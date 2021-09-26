package de.nb.aventiure2.german.praedikat;

public interface PraedikatNurMitLeerstelleFuerLexikalischenKern {
    /**
     * Füllt die Leerstelle für den lexikalischen Kern mit einem Prädikat mit einer
     * Objektleerstelle
     */
    default PraedikatMitEinerObjektleerstelle mitLexikalischemKern(
            final PraedikatMitEinerObjektleerstelle lexikalischerKern) {
        return substPhr -> mitLexikalischemKern(lexikalischerKern.mit(substPhr));
    }

    /**
     * Füllt die Leerstelle für den lexikalischen Kern.
     */
    PraedikatOhneLeerstellen mitLexikalischemKern(final PraedikatOhneLeerstellen lexikalischerKern);
}
