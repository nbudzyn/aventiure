package de.nb.aventiure2.data.world.syscomp.state.impl;

import de.nb.aventiure2.data.database.AvDatabase;
import de.nb.aventiure2.data.narration.Narrator;
import de.nb.aventiure2.data.time.TimeTaker;
import de.nb.aventiure2.data.world.gameobject.*;
import de.nb.aventiure2.data.world.syscomp.state.AbstractStateComp;

import static de.nb.aventiure2.data.world.gameobject.World.*;
import static de.nb.aventiure2.data.world.syscomp.state.impl.FroschprinzState.UNAUFFAELLIG;

public class FroschprinzStateComp extends AbstractStateComp<FroschprinzState> {
    public FroschprinzStateComp(final AvDatabase db, final TimeTaker timeTaker,
                                final Narrator n, final World world) {
        super(FROSCHPRINZ, db, timeTaker, world, FroschprinzState.class, UNAUFFAELLIG);
    }
}
