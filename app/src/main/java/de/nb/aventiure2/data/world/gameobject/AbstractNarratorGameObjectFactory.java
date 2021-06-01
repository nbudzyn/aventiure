package de.nb.aventiure2.data.world.gameobject;

import de.nb.aventiure2.data.database.AvDatabase;
import de.nb.aventiure2.data.narration.Narrator;
import de.nb.aventiure2.data.time.TimeTaker;

public abstract class AbstractNarratorGameObjectFactory extends AbstractGameObjectFactory {
    protected final Narrator n;

    AbstractNarratorGameObjectFactory(
            final AvDatabase db, final TimeTaker timeTaker, final Narrator n, final World world) {
        super(db, timeTaker, world);
        this.n = n;
    }
}
