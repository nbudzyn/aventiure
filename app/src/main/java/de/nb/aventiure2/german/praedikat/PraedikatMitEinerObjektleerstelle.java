package de.nb.aventiure2.german.praedikat;

import de.nb.aventiure2.german.base.SubstantivischePhrase;

/**
 * Ein Prädikat, in dem (noch) für genau ein Objekt eine Leerstelle besteht. Beispiele:
 * <ul>
 *     <li>"... nehmen" (z.B. "das Buch nehmen")
 *     <li>"... Angebote machen" (z.B. "dem Frosch Angebote machen)
 * </ul>
 */
public interface PraedikatMitEinerObjektleerstelle extends Praedikat {
    /**
     * Füllt die Objekt-Leerstelle mit diesem Objekt.
     */
    PraedikatOhneLeerstellen mitObj(final SubstantivischePhrase describable);
}
