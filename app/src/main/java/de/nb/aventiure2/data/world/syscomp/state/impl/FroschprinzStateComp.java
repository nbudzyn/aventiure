package de.nb.aventiure2.data.world.syscomp.state.impl;

import de.nb.aventiure2.data.database.AvDatabase;
import de.nb.aventiure2.data.world.gameobject.World;
import de.nb.aventiure2.data.world.syscomp.state.AbstractStateComp;

import static de.nb.aventiure2.data.world.gameobject.World.FROSCHPRINZ;
import static de.nb.aventiure2.data.world.syscomp.state.impl.FroschprinzState.UNAUFFAELLIG;

public class FroschprinzStateComp extends AbstractStateComp<FroschprinzState> {
    public FroschprinzStateComp(final AvDatabase db, final World world) {
        super(FROSCHPRINZ, db, world, FroschprinzState.class, UNAUFFAELLIG);
    }
}
