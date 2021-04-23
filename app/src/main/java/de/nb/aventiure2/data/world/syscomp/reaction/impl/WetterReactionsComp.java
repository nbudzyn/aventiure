package de.nb.aventiure2.data.world.syscomp.reaction.impl;

import de.nb.aventiure2.data.narration.Narrator;
import de.nb.aventiure2.data.time.AvDateTime;
import de.nb.aventiure2.data.world.gameobject.*;
import de.nb.aventiure2.data.world.syscomp.reaction.AbstractReactionsComp;
import de.nb.aventiure2.data.world.syscomp.reaction.interfaces.ISCActionReactions;
import de.nb.aventiure2.data.world.syscomp.reaction.interfaces.ITimePassedReactions;
import de.nb.aventiure2.data.world.syscomp.wetter.WetterComp;

import static de.nb.aventiure2.data.world.gameobject.World.*;

/**
 * Reaktionen des Wetters - insbesondere darauf, dass die Zeit vergeht
 */
public class WetterReactionsComp extends AbstractReactionsComp
        implements ITimePassedReactions,
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
}
