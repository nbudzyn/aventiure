package de.nb.aventiure2.data.world.syscomp.state.impl;

import de.nb.aventiure2.data.database.AvDatabase;
import de.nb.aventiure2.data.world.syscomp.state.AbstractStateComp;

import static de.nb.aventiure2.data.world.gameobject.World.RAPUNZELS_ZAUBERIN;
import static de.nb.aventiure2.data.world.syscomp.state.impl.RapunzelsZauberinState.BESUCHT_RAPUNZEL_REGELMAESSIG;

public class RapunzelsZauberinStateComp extends AbstractStateComp<RapunzelsZauberinState> {
    public RapunzelsZauberinStateComp(final AvDatabase db) {
        super(RAPUNZELS_ZAUBERIN, db, RapunzelsZauberinState.class,
                BESUCHT_RAPUNZEL_REGELMAESSIG);
    }
}
