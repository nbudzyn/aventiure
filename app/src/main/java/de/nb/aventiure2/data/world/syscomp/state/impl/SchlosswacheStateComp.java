package de.nb.aventiure2.data.world.syscomp.state.impl;

import de.nb.aventiure2.data.database.AvDatabase;
import de.nb.aventiure2.data.time.TimeTaker;
import de.nb.aventiure2.data.world.base.GameObjectId;
import de.nb.aventiure2.data.world.gameobject.*;

import static de.nb.aventiure2.data.world.syscomp.state.impl.SchlosswacheState.UNAUFFAELLIG;

public class SchlosswacheStateComp extends NonScModifiableStateComp<SchlosswacheState> {
    public SchlosswacheStateComp(final GameObjectId gameObjectId,
                                 final AvDatabase db,
                                 final TimeTaker timeTaker,
                                 final World world) {
        super(gameObjectId, db, timeTaker, world, UNAUFFAELLIG);
    }
}
