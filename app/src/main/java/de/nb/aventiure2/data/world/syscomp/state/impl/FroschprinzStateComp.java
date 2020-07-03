package de.nb.aventiure2.data.world.syscomp.state.impl;

import de.nb.aventiure2.data.database.AvDatabase;
import de.nb.aventiure2.data.world.base.GameObjectId;
import de.nb.aventiure2.data.world.syscomp.state.AbstractStateComp;

import static de.nb.aventiure2.data.world.syscomp.state.impl.FroschprinzState.UNAUFFAELLIG;

public class FroschprinzStateComp extends AbstractStateComp<FroschprinzState> {
    public FroschprinzStateComp(final GameObjectId gameObjectId,
                                final AvDatabase db) {
        super(gameObjectId, db, FroschprinzState.class, UNAUFFAELLIG);
    }
}
