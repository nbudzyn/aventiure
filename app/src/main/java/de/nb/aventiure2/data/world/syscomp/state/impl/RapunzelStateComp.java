package de.nb.aventiure2.data.world.syscomp.state.impl;

import de.nb.aventiure2.data.database.AvDatabase;
import de.nb.aventiure2.data.world.syscomp.state.AbstractStateComp;

import static de.nb.aventiure2.data.world.gameobject.World.RAPUNZEL;
import static de.nb.aventiure2.data.world.syscomp.state.impl.RapunzelState.STILL;

public class RapunzelStateComp extends AbstractStateComp<RapunzelState> {
    public RapunzelStateComp(final AvDatabase db) {
        super(RAPUNZEL, db, RapunzelState.class, STILL);
    }
}
