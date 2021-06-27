package de.nb.aventiure2.data.world.syscomp.state.impl;

import de.nb.aventiure2.data.database.AvDatabase;
import de.nb.aventiure2.data.time.TimeTaker;
import de.nb.aventiure2.data.world.gameobject.*;

import static de.nb.aventiure2.data.world.gameobject.World.*;
import static de.nb.aventiure2.data.world.syscomp.state.impl.FroschprinzState.UNAUFFAELLIG;

public class FroschprinzStateComp extends NonScModifiableStateComp<FroschprinzState> {
    public FroschprinzStateComp(final AvDatabase db, final TimeTaker timeTaker,
                                final World world) {
        super(FROSCHPRINZ, db, timeTaker, world, FroschprinzState.class, UNAUFFAELLIG);
    }
}
