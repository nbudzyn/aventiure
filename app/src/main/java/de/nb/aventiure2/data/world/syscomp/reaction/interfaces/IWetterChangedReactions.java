package de.nb.aventiure2.data.world.syscomp.reaction.interfaces;

import com.google.common.collect.ImmutableList;

import de.nb.aventiure2.data.world.syscomp.reaction.IReactions;
import de.nb.aventiure2.data.world.syscomp.wetter.WetterData;

/**
 * Reactions to weather change.
 */
public interface IWetterChangedReactions extends IReactions {
    /**
     * Aufgerufen, wenn sich das Wetter verändert hat.
     *
     * @param wetterSteps Die einzelnen "Schritte", in denen sich das Wetter verändert hat.
     *                    Der erste Wert ist das Wetter vor den Änderungen, der letzte Wert ist
     *                    das Wetter nach den Änderungen. Es kann Zwischenwerte geben, z.B.
     *                    wenn der SC eine lange Aktion durchgeführt hat, während der sich das
     *                    Wetter mehrfach geändert hat. Der Aufrufer muss berücksichtigen, dass
     *                    bereits der letzte Stand vorliegt.
     */
    void onWetterChanged(final ImmutableList<WetterData> wetterSteps);
}
