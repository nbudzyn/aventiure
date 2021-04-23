package de.nb.aventiure2.data.world.syscomp.reaction.impl;

import androidx.annotation.Nullable;

import de.nb.aventiure2.data.narration.Narrator;
import de.nb.aventiure2.data.time.AvDateTime;
import de.nb.aventiure2.data.world.gameobject.*;
import de.nb.aventiure2.data.world.syscomp.location.ILocatableGO;
import de.nb.aventiure2.data.world.syscomp.reaction.AbstractReactionsComp;
import de.nb.aventiure2.data.world.syscomp.reaction.interfaces.IMovementReactions;
import de.nb.aventiure2.data.world.syscomp.reaction.interfaces.ISCActionReactions;
import de.nb.aventiure2.data.world.syscomp.reaction.interfaces.ITimePassedReactions;
import de.nb.aventiure2.data.world.syscomp.storingplace.ILocationGO;
import de.nb.aventiure2.data.world.syscomp.wetter.WetterComp;

import static de.nb.aventiure2.data.world.gameobject.World.*;

/**
 * Reaktionen des Wetters - insbesondere darauf, dass die Zeit vergeht
 */
public class WetterReactionsComp extends AbstractReactionsComp
        implements IMovementReactions, ITimePassedReactions,
        ISCActionReactions {
    private final WetterComp wetterComp;

    public WetterReactionsComp(final Narrator n,
                               final World world,
                               final WetterComp wetterComp) {
        super(WETTER, n, world);
        this.wetterComp = wetterComp;
    }

    @Override
    public void onTimePassed(final AvDateTime startTime, final AvDateTime endTime) {
        wetterComp.onTimePassed(startTime, endTime);
    }

    @Override
    public void afterScActionAndFirstWorldUpdate() {
        wetterComp.narrateWetterhinweisWennNoetig();
    }

    @Override
    public boolean verbirgtSichVorEintreffendemSC() {
        return false;
    }

    @Override
    public void onEnter(final ILocatableGO locatable, @Nullable final ILocationGO from,
                        final ILocationGO to) {
        if (locatable.is(SPIELER_CHARAKTER)) {
            wetterComp.onScEnter(from, to);
        }
    }

    @Override
    public void onLeave(final ILocatableGO locatable, final ILocationGO from,
                        @Nullable final ILocationGO to) {
    }
}
