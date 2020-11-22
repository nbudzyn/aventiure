package de.nb.aventiure2.data.world.syscomp.reaction.impl;

import androidx.annotation.Nullable;

import de.nb.aventiure2.data.database.AvDatabase;
import de.nb.aventiure2.data.narration.Narrator;
import de.nb.aventiure2.data.world.gameobject.*;
import de.nb.aventiure2.data.world.syscomp.feelings.FeelingsComp;
import de.nb.aventiure2.data.world.syscomp.location.ILocatableGO;
import de.nb.aventiure2.data.world.syscomp.reaction.AbstractReactionsComp;
import de.nb.aventiure2.data.world.syscomp.reaction.interfaces.IMovementReactions;
import de.nb.aventiure2.data.world.syscomp.reaction.interfaces.ISCActionReactions;
import de.nb.aventiure2.data.world.syscomp.reaction.interfaces.ITimePassedReactions;
import de.nb.aventiure2.data.world.syscomp.storingplace.ILocationGO;
import de.nb.aventiure2.data.world.syscomp.waiting.WaitingComp;
import de.nb.aventiure2.data.world.time.*;

import static de.nb.aventiure2.data.world.gameobject.World.*;
import static de.nb.aventiure2.data.world.time.AvTimeSpan.*;

/**
 * "Automatische" Reaktionen des Spielercharakters, z.B. darauf, dass Zeit vergeht.
 * (Z.B.: Spielercharakter wird hungrig.)
 */
public class ScAutomaticReactionsComp
        extends AbstractReactionsComp
        implements IMovementReactions, ITimePassedReactions, ISCActionReactions {
    private final WaitingComp waitingComp;
    private final FeelingsComp feelingsComp;

    public ScAutomaticReactionsComp(final AvDatabase db,
                                    final Narrator n,
                                    final World world,
                                    final WaitingComp waitingComp,
                                    final FeelingsComp feelingsComp) {
        super(SPIELER_CHARAKTER, db, n, world);
        this.waitingComp = waitingComp;
        this.feelingsComp = feelingsComp;
    }

    @Override
    public void onLeave(final ILocatableGO locatable, final ILocationGO from, @Nullable
    final ILocationGO to) {
        waitingComp.stopWaiting();
        // FIXME Problem: Die Beschreibung wurde bereits geschrieben? Wenn
        //  sie ausgeblendet wäre, ist sie wohl verloren?
    }

    @Override
    public void onEnter(final ILocatableGO locatable, @Nullable final ILocationGO from, final ILocationGO to) {
        waitingComp.stopWaiting();
        // FIXME Problem: Die Beschreibung wurde bereits geschrieben? Wenn
        //  sie ausgeblendet wäre, ist sie wohl verloren?
    }

    @Override
    public void onTimePassed(final AvDateTime startTime, final AvDateTime endTime) {
        feelingsComp.onTimePassed(startTime, endTime);

        final AvTimeSpan remainingWaitTime = waitingComp.getEndTime().minus(endTime);
        if (remainingWaitTime.longerThan(noTime())) {
            // Erzwingen, dass sich die Welt noch weitere 3 Minuten weiterdreht
            // (oder die remainingWaitTime - wenn die kleiner ist)
            db.nowDao().passTime(min(mins(3), remainingWaitTime));
        }
    }

    @Override
    public void afterScActionAndFirstWorldUpdate() {
        feelingsComp.narrateScMuedigkeitIfNecessary();
    }
}
