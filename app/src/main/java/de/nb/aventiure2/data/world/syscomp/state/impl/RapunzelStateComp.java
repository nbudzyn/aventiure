package de.nb.aventiure2.data.world.syscomp.state.impl;

import de.nb.aventiure2.data.database.AvDatabase;
import de.nb.aventiure2.data.narration.Narrator;
import de.nb.aventiure2.data.time.TimeTaker;
import de.nb.aventiure2.data.world.gameobject.*;
import de.nb.aventiure2.data.world.syscomp.state.AbstractStateComp;

import static de.nb.aventiure2.data.world.gameobject.World.*;
import static de.nb.aventiure2.data.world.syscomp.state.impl.RapunzelState.UNAEUFFAELLIG;

public class RapunzelStateComp extends AbstractStateComp<RapunzelState> {
    public RapunzelStateComp(final AvDatabase db, final TimeTaker timeTaker,
                             final Narrator n, final World world) {
        super(RAPUNZEL, db, timeTaker, n, world, RapunzelState.class, UNAEUFFAELLIG);
    }
}
