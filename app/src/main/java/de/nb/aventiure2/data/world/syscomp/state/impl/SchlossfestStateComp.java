package de.nb.aventiure2.data.world.syscomp.state.impl;

import de.nb.aventiure2.data.database.AvDatabase;
import de.nb.aventiure2.data.narration.Narrator;
import de.nb.aventiure2.data.world.gameobject.*;
import de.nb.aventiure2.data.world.syscomp.state.AbstractStateComp;

import static de.nb.aventiure2.data.world.gameobject.World.*;
import static de.nb.aventiure2.data.world.syscomp.state.impl.SchlossfestState.NOCH_NICHT_BEGONNEN;

public class SchlossfestStateComp extends AbstractStateComp<SchlossfestState> {
    public SchlossfestStateComp(final AvDatabase db,
                                final Narrator n, final World world) {
        super(SCHLOSSFEST, db, n, world, SchlossfestState.class, NOCH_NICHT_BEGONNEN);
    }
}
