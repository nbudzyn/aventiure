package de.nb.aventiure2.data.world.syscomp.state.impl;

import de.nb.aventiure2.data.database.AvDatabase;
import de.nb.aventiure2.data.world.base.GameObjectId;
import de.nb.aventiure2.data.world.syscomp.state.AbstractStateComp;

import static de.nb.aventiure2.data.world.syscomp.state.impl.SchlosswacheState.UNAUFFAELLIG;

public class SchlosswacheStateComp extends AbstractStateComp<SchlosswacheState> {
    public SchlosswacheStateComp(final GameObjectId gameObjectId,
                                 final AvDatabase db) {
        super(gameObjectId, db, SchlosswacheState.class, UNAUFFAELLIG);
    }
}
