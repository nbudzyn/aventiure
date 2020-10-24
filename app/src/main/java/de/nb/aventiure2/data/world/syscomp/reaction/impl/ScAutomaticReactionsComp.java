package de.nb.aventiure2.data.world.syscomp.reaction.impl;

import de.nb.aventiure2.data.database.AvDatabase;
import de.nb.aventiure2.data.world.gameobject.*;
import de.nb.aventiure2.data.world.syscomp.feelings.FeelingsComp;
import de.nb.aventiure2.data.world.syscomp.reaction.AbstractReactionsComp;
import de.nb.aventiure2.data.world.syscomp.reaction.interfaces.ITimePassedReactions;
import de.nb.aventiure2.data.world.time.*;

import static de.nb.aventiure2.data.world.gameobject.World.*;

/**
 * "Automatische" Reaktionen des Spielercharakters, z.B. darauf, dass Zeit vergeht.
 * (Z.B.: Spielercharakter wird hungrig.)
 */
public class ScAutomaticReactionsComp
        extends AbstractReactionsComp
        implements ITimePassedReactions {
    private final FeelingsComp feelingsComp;

    public ScAutomaticReactionsComp(final AvDatabase db, final World world,
                                    final FeelingsComp feelingsComp) {
        super(SPIELER_CHARAKTER, db, world);
        this.feelingsComp = feelingsComp;
    }

    @Override
    public void onTimePassed(final AvDateTime startTime, final AvDateTime endTime) {
        feelingsComp.onTimePassed(startTime, endTime);
    }
}
