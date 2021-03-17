package de.nb.aventiure2.german.praedikat;

import de.nb.aventiure2.german.base.Praedikativum;

/**
 * Ein Prädikat, in dem (noch) für genau ein Prädikativum eine Leerstelle besteht. Beispiele:
 * <ul>
 *     <li>"mir ist..." (z.B. "mir ist kalt", "mir ist was?")
 * </ul>
 */
public interface PraedikatMitEinerPraedikativumLeerstelle extends Praedikat {
    /**
     * Füllt die Leerstelle mit diesem Prädikativum
     */
    PraedikatOhneLeerstellen mit(final Praedikativum praedikativum);
}
