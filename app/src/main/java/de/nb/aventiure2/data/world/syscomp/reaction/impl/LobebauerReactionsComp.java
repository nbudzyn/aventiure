package de.nb.aventiure2.data.world.syscomp.reaction.impl;

import static de.nb.aventiure2.data.world.gameobject.World.*;
import static de.nb.aventiure2.data.world.syscomp.state.impl.FroschprinzState.ZURUECKVERWANDELT_SCHLOSS_VORHALLE_VERLASSEN;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import de.nb.aventiure2.data.narration.Narrator;
import de.nb.aventiure2.data.world.counter.CounterDao;
import de.nb.aventiure2.data.world.gameobject.*;
import de.nb.aventiure2.data.world.syscomp.location.ILocatableGO;
import de.nb.aventiure2.data.world.syscomp.reaction.AbstractDescribableReactionsComp;
import de.nb.aventiure2.data.world.syscomp.reaction.interfaces.IMovementReactions;
import de.nb.aventiure2.data.world.syscomp.state.IHasStateGO;
import de.nb.aventiure2.data.world.syscomp.state.impl.FroschprinzState;
import de.nb.aventiure2.data.world.syscomp.storingplace.ILocationGO;
import de.nb.aventiure2.data.world.syscomp.talking.impl.LobebauerTalkingComp;

/**
 * Reaktionen des Lobebauern - z.B. darauf, dass er neben dem SC vor dem Schloss steht,
 * wenn der Prinz wegfährt.
 */
public class LobebauerReactionsComp extends AbstractDescribableReactionsComp
        implements IMovementReactions {
    private final LobebauerTalkingComp talkingComp;

    public LobebauerReactionsComp(final CounterDao counterDao,
                                  final Narrator n,
                                  final World world,
                                  final LobebauerTalkingComp talkingComp) {
        super(LOBEBAUER, counterDao, n, world);
        this.talkingComp = talkingComp;
    }

    @Override
    public void onLeave(final ILocatableGO locatable, final ILocationGO from,
                        @Nullable final ILocationGO to) {
        if (locatable.is(FROSCHPRINZ)) {
            onLeaveFroschprinz(from, to);
        }
    }

    private <F extends IHasStateGO<FroschprinzState> & ILocatableGO>
    void onLeaveFroschprinz(final ILocationGO from, @Nullable final ILocationGO to) {
        if (to != null
                || !world.isOrHasVisiblyRecursiveLocation(from, DRAUSSEN_VOR_DEM_SCHLOSS)) {
            return;
        }

        final F froschprinz = loadFroschprinz();
        if (!froschprinz.stateComp().hasState(ZURUECKVERWANDELT_SCHLOSS_VORHALLE_VERLASSEN)
                || !world.isOrHasVisiblyRecursiveLocation(
                SPIELER_CHARAKTER, DRAUSSEN_VOR_DEM_SCHLOSS)) {
            return;
        }

        // Der SC hat gerade gesehen, wie der Prinz weggefahren ist.
        talkingComp.sprichtScAnUndHaeltSiebenJahreAnsprache();
    }

    @Override
    public boolean isVorScVerborgen() {
        return false;
    }

    @Override
    public void onEnter(final ILocatableGO locatable, @Nullable final ILocationGO from,
                        final ILocationGO to) {
    }

    @NonNull
    private <F extends IHasStateGO<FroschprinzState> & ILocatableGO> F loadFroschprinz() {
        return loadRequired(FROSCHPRINZ);
    }
}
