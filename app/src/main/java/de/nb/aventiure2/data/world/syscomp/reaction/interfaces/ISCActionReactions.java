package de.nb.aventiure2.data.world.syscomp.reaction.interfaces;

import de.nb.aventiure2.data.world.syscomp.reaction.IReactions;

/**
 * Reactions to any kind of SC action.
 */
public interface ISCActionReactions extends IReactions {
    void afterScActionAndFirstWorldUpdate();
}
