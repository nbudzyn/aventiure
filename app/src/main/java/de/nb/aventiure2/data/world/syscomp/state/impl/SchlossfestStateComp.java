package de.nb.aventiure2.data.world.syscomp.state.impl;

import de.nb.aventiure2.data.database.AvDatabase;
import de.nb.aventiure2.data.time.TimeTaker;
import de.nb.aventiure2.data.world.gameobject.*;

import static de.nb.aventiure2.data.world.gameobject.World.*;
import static de.nb.aventiure2.data.world.syscomp.state.impl.SchlossfestState.NOCH_NICHT_BEGONNEN;

public class SchlossfestStateComp extends NonScModifiableStateComp<SchlossfestState> {
    public SchlossfestStateComp(final AvDatabase db,
                                final TimeTaker timeTaker,
                                final World world) {
        super(SCHLOSSFEST, db, timeTaker, world, NOCH_NICHT_BEGONNEN);
    }
}
