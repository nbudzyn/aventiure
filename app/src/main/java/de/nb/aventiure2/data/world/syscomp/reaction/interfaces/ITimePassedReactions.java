package de.nb.aventiure2.data.world.syscomp.reaction.interfaces;

import de.nb.aventiure2.data.time.AvDateTime;
import de.nb.aventiure2.data.world.syscomp.reaction.IReactions;

/**
 * Reactions to time passing.
 */
public interface ITimePassedReactions extends IReactions {
    void onTimePassed(final AvDateTime startTime, final AvDateTime endTime);

    // IDEA Worker Placement: Man tut bestimmte Dinge oder bringt NSCs an einen
    //  Ort oder bringt sie auch in einen bestimmten Zustand - die arbeiten
    //  dann autonom und nach einer Weile geschieht... XYZ ist erzeugt,
    //  sie haben den Zustand... jemand Neues ist erschienen etc.
}
