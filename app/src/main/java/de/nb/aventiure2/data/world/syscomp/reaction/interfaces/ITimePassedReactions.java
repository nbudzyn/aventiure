package de.nb.aventiure2.data.world.syscomp.reaction.interfaces;

import de.nb.aventiure2.data.time.AvDateTime;
import de.nb.aventiure2.data.world.syscomp.reaction.IReactions;

/**
 * Reactions to time passing.
 */
public interface ITimePassedReactions extends IReactions {
    void onTimePassed(final AvDateTime startTime, final AvDateTime endTime);
}
