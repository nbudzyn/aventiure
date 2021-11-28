package de.nb.aventiure2.german.praedikat;

import de.nb.aventiure2.german.base.Praedikativum;

/**
 * Ein "semantisches Prädikat", in dem (noch) für genau ein Prädikativum eine Leerstelle besteht.
 * Beispiele:
 * <ul>
 *     <li>"mir ist..." (z.B. "mir ist kalt", "mir ist was?")
 * </ul>
 */
public interface SemPraedikatMitEinerPraedikativumLeerstelle extends SemPraedikat {
    /**
     * Füllt die Leerstelle mit diesem Prädikativum
     */
    SemPraedikatOhneLeerstellen mit(final Praedikativum praedikativum);
}
