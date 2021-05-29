package de.nb.aventiure2.data.world.syscomp.reaction.interfaces;

import de.nb.aventiure2.data.world.syscomp.reaction.IReactions;
import de.nb.aventiure2.data.world.syscomp.wetter.WetterData;

/**
 * Reactions to weather change.
 */
public interface IWetterChangedReactions extends IReactions {
    void onWetterChanged(final WetterData oldWetter, WetterData newWetter);
}
