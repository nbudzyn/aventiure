package de.nb.aventiure2.data.world.syscomp.state.impl;

import de.nb.aventiure2.data.database.AvDatabase;
import de.nb.aventiure2.data.time.TimeTaker;
import de.nb.aventiure2.data.world.gameobject.*;

import static de.nb.aventiure2.data.world.gameobject.World.*;
import static de.nb.aventiure2.data.world.syscomp.state.impl.RapunzelsZauberinState.MACHT_ZURZEIT_KEINE_RAPUNZELBESUCHE;

public class RapunzelsZauberinStateComp extends NonScModifiableStateComp<RapunzelsZauberinState> {
    public RapunzelsZauberinStateComp(final AvDatabase db, final TimeTaker timeTaker,
                                      final World world) {
        super(RAPUNZELS_ZAUBERIN, db, timeTaker, world, RapunzelsZauberinState.class,
                MACHT_ZURZEIT_KEINE_RAPUNZELBESUCHE);
    }
}
