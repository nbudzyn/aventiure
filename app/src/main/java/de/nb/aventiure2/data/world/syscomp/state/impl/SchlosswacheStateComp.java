package de.nb.aventiure2.data.world.syscomp.state.impl;

import de.nb.aventiure2.data.database.AvDatabase;
import de.nb.aventiure2.data.narration.Narrator;
import de.nb.aventiure2.data.time.TimeTaker;
import de.nb.aventiure2.data.world.base.GameObjectId;
import de.nb.aventiure2.data.world.gameobject.*;
import de.nb.aventiure2.data.world.syscomp.state.AbstractStateComp;

import static de.nb.aventiure2.data.world.syscomp.state.impl.SchlosswacheState.UNAUFFAELLIG;

public class SchlosswacheStateComp extends AbstractStateComp<SchlosswacheState> {
    public SchlosswacheStateComp(final GameObjectId gameObjectId,
                                 final AvDatabase db,
                                 final TimeTaker timeTaker,
                                 final Narrator n,
                                 final World world) {
        super(gameObjectId, db, timeTaker, world, SchlosswacheState.class, UNAUFFAELLIG);
    }
}
