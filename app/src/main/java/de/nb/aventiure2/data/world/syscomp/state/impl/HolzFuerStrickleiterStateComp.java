package de.nb.aventiure2.data.world.syscomp.state.impl;

import de.nb.aventiure2.data.database.AvDatabase;
import de.nb.aventiure2.data.narration.Narrator;
import de.nb.aventiure2.data.time.TimeTaker;
import de.nb.aventiure2.data.world.gameobject.*;
import de.nb.aventiure2.data.world.syscomp.state.AbstractStateComp;

import static de.nb.aventiure2.data.world.gameobject.World.*;
import static de.nb.aventiure2.data.world.syscomp.state.impl.HolzFuerStrickleiterState.NOCH_NICHT_GEBROCHEN;

public class HolzFuerStrickleiterStateComp extends AbstractStateComp<HolzFuerStrickleiterState> {
    public HolzFuerStrickleiterStateComp(final AvDatabase db,
                                         final TimeTaker timeTaker, final Narrator n,
                                         final World world) {
        super(HOLZ_FUER_STRICKLEITER, db, timeTaker, n, world, HolzFuerStrickleiterState.class,
                NOCH_NICHT_GEBROCHEN);
    }
}
