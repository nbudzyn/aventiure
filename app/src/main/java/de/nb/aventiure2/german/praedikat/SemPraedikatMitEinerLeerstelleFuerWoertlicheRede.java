package de.nb.aventiure2.german.praedikat;

import de.nb.aventiure2.german.base.WoertlicheRede;

/**
 * Ein "semantisches Prädikat", in dem für wörtliche Rede eine Leerstelle besteht. Beispiele:
 * <ul>
 *     <li>"... rufen"
 *     <li>"... sagen"
 * </ul>
 */
public interface SemPraedikatMitEinerLeerstelleFuerWoertlicheRede extends SemPraedikat {
    /**
     * Füllt die Leerstelle mit dieser Woertlichen Rede.
     */
    default SemPraedikatOhneLeerstellen mitWoertlicheRede(
            final String woertlicheRede) {
        return mitWoertlicheRede(new WoertlicheRede(woertlicheRede));
    }

    /**
     * Füllt die Leerstelle mit dieser Woertlichen Rede.
     */
    SemPraedikatOhneLeerstellen mitWoertlicheRede(final WoertlicheRede woertlicheRede);
}


