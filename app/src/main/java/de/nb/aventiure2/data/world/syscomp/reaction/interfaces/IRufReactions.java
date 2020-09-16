package de.nb.aventiure2.data.world.syscomp.reaction.interfaces;

import de.nb.aventiure2.data.world.syscomp.location.ILocatableGO;
import de.nb.aventiure2.data.world.syscomp.reaction.IReactions;

/**
 * Reaktionen darauf, dass jemand etwas gerufen
 * hat.
 */
public interface IRufReactions extends IReactions {
    void onRuf(ILocatableGO rufer, Ruftyp ruftyp);
}
