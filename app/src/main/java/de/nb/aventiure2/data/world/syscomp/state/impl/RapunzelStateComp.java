package de.nb.aventiure2.data.world.syscomp.state.impl;

import androidx.annotation.NonNull;

import de.nb.aventiure2.data.database.AvDatabase;
import de.nb.aventiure2.data.narration.Narrator;
import de.nb.aventiure2.data.time.TimeTaker;
import de.nb.aventiure2.data.world.gameobject.*;
import de.nb.aventiure2.data.world.gameobject.player.*;
import de.nb.aventiure2.data.world.syscomp.state.AbstractStateComp;
import de.nb.aventiure2.german.base.Nominalphrase;

import static de.nb.aventiure2.data.world.gameobject.World.*;
import static de.nb.aventiure2.data.world.syscomp.state.impl.RapunzelState.UNAEUFFAELLIG;

public class RapunzelStateComp extends AbstractStateComp<RapunzelState> {
    public RapunzelStateComp(final AvDatabase db, final TimeTaker timeTaker,
                             final Narrator n, final World world) {
        super(RAPUNZEL, db, timeTaker, n, world, RapunzelState.class, UNAEUFFAELLIG);
    }


    @NonNull
    protected SpielerCharakter loadSC() {
        return world.loadSC();
    }

    /**
     * Gibt eine Nominalphrase zurück, die das Game Object beschreibt.
     * Die Phrase kann unterschiedlich sein, je nachdem,
     * ob der Spieler das Game Object schon kennt oder nicht.
     *
     * @param shortIfKnown <i>Falls der Spieler(-charakter)</i> das
     *                     Game Object schon kennt, wird eher eine
     *                     kürzere Beschreibung gewählt
     */
    private Nominalphrase getDescription(final boolean shortIfKnown) {
        return world.getDescription(getGameObjectId(), shortIfKnown);
    }
}
