package de.nb.aventiure2.data.world.syscomp.state.impl;

import de.nb.aventiure2.data.database.AvDatabase;
import de.nb.aventiure2.data.time.TimeTaker;
import de.nb.aventiure2.data.world.gameobject.*;

import static de.nb.aventiure2.data.world.gameobject.World.*;
import static de.nb.aventiure2.data.world.syscomp.state.impl.RapunzelState.UNAEUFFAELLIG;

public class RapunzelStateComp extends NonScModifiableStateComp<RapunzelState> {
    public RapunzelStateComp(final AvDatabase db, final TimeTaker timeTaker,
                             final World world) {
        super(RAPUNZEL, db, timeTaker, world, RapunzelState.class, UNAEUFFAELLIG);
    }
}
