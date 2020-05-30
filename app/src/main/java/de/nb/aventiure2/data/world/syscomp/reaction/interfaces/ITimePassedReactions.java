package de.nb.aventiure2.data.world.syscomp.reaction.interfaces;

import de.nb.aventiure2.data.world.syscomp.reaction.IReactions;
import de.nb.aventiure2.data.world.time.AvDateTime;
import de.nb.aventiure2.data.world.time.AvTimeSpan;

/**
 * Reactions to time passing.
 */
public interface ITimePassedReactions extends IReactions {
    AvTimeSpan onTimePassed(final AvDateTime lastTime, final AvDateTime now);
}
