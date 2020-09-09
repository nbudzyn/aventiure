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
     * Füllt die Objekt-Leerstelle mit dieser Woertlichen Rede.
     */
    PraedikatMitWoertlicherRedeOhneLeerstellen mitWoertlicherRede(
            final WoertlicheRede woertlicheRede);

}
