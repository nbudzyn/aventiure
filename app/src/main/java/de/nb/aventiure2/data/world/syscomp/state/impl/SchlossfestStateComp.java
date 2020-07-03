package de.nb.aventiure2.data.world.syscomp.state.impl;

import de.nb.aventiure2.data.database.AvDatabase;
import de.nb.aventiure2.data.world.base.GameObjectId;
import de.nb.aventiure2.data.world.syscomp.state.AbstractStateComp;

import static de.nb.aventiure2.data.world.syscomp.state.impl.SchlossfestState.NOCH_NICHT_BEGONNEN;

public class SchlossfestStateComp extends AbstractStateComp<SchlossfestState> {
    public SchlossfestStateComp(final GameObjectId gameObjectId,
                                final AvDatabase db) {
        super(gameObjectId, db, SchlossfestState.class, NOCH_NICHT_BEGONNEN);
    }
}
