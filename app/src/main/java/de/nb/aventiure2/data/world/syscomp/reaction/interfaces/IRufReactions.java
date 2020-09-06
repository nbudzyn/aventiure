package de.nb.aventiure2.data.world.syscomp.reaction.interfaces;

import de.nb.aventiure2.data.world.syscomp.location.ILocatableGO;
import de.nb.aventiure2.data.world.syscomp.reaction.IReactions;
import de.nb.aventiure2.data.world.time.AvTimeSpan;

/**
 * Reaktionen darauf, dass jemand etwas gerufen
 * hat.
 */
public interface IRufReactions extends IReactions {
    AvTimeSpan onRuf(ILocatableGO rufer, Ruftyp ruftyp);
}
