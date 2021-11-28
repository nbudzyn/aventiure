package de.nb.aventiure2.german.praedikat;

import de.nb.aventiure2.german.base.SubstantivischPhrasierbar;

/**
 * Ein "semantisches Prädikat", in dem (noch) für genau ein Objekt eine Leerstelle besteht.
 * Beispiele:
 * <ul>
 *     <li>"... nehmen" (z.B. "das Buch nehmen")
 *     <li>"... Angebote machen" (z.B. "dem Frosch Angebote machen)
 * </ul>
 */
public interface SemPraedikatMitEinerObjektleerstelle extends SemPraedikat {
    /**
     * Füllt die Objekt-Leerstelle mit diesem Objekt.
     */
    SemPraedikatOhneLeerstellen mit(final SubstantivischPhrasierbar substPhrasierbar);
}
