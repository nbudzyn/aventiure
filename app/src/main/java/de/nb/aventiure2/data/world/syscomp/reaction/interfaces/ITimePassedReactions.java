package de.nb.aventiure2.data.world.syscomp.reaction.interfaces;

import de.nb.aventiure2.data.world.syscomp.reaction.IReactions;
import de.nb.aventiure2.data.world.time.*;

/**
 * Reactions to time passing.
 */
public interface ITimePassedReactions extends IReactions {
    void onTimePassed(final AvDateTime startTime, final AvDateTime endTime);
}
