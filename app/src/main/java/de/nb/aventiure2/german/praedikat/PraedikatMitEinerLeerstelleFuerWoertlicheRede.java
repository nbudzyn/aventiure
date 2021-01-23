package de.nb.aventiure2.german.praedikat;

import de.nb.aventiure2.german.base.WoertlicheRede;

/**
 * Ein Prädikat, in dem für wörtliche Rede eine Leerstelle besteht. Beispiele:
 * <ul>
 *     <li>"... rufen"
 *     <li>"... sagen"
 * </ul>
 */
public interface PraedikatMitEinerLeerstelleFuerWoertlicheRede extends Praedikat {
    /**
     * Füllt die Leerstelle mit dieser Woertlichen Rede.
     */
    default PraedikatOhneLeerstellen mitWoertlicheRede(final String woertlicheRede) {
        return mitWoertlicheRede(new WoertlicheRede(woertlicheRede));
    }

    /**
     * Füllt die Leerstelle mit dieser Woertlichen Rede.
     */
    PraedikatOhneLeerstellen mitWoertlicheRede(final WoertlicheRede woertlicheRede);
}


