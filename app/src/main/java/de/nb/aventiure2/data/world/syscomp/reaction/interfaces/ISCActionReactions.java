package de.nb.aventiure2.data.world.syscomp.reaction.interfaces;

import de.nb.aventiure2.data.world.syscomp.reaction.IReactions;
import de.nb.aventiure2.data.world.time.AvTimeSpan;

/**
 * Reactions to any kind of SC action.
 */
public interface ISCActionReactions extends IReactions {
    AvTimeSpan afterScActionAndFirstWorldUpdate();
}
