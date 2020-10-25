package de.nb.aventiure2.data.world.syscomp.state.impl;

import de.nb.aventiure2.data.database.AvDatabase;
import de.nb.aventiure2.data.narration.Narrator;
import de.nb.aventiure2.data.world.gameobject.*;
import de.nb.aventiure2.data.world.syscomp.state.AbstractStateComp;

import static de.nb.aventiure2.data.world.gameobject.World.*;
import static de.nb.aventiure2.data.world.syscomp.state.impl.RapunzelsZauberinState.MACHT_ZURZEIT_KEINE_RAPUNZELBESUCHE;

public class RapunzelsZauberinStateComp extends AbstractStateComp<RapunzelsZauberinState> {
    public RapunzelsZauberinStateComp(final AvDatabase db, final Narrator n, final World world) {
        super(RAPUNZELS_ZAUBERIN, db, n, world, RapunzelsZauberinState.class,
                MACHT_ZURZEIT_KEINE_RAPUNZELBESUCHE);
    }
}
