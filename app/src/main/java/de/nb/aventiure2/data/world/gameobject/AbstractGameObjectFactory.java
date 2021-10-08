package de.nb.aventiure2.data.world.gameobject;

import de.nb.aventiure2.data.database.AvDatabase;
import de.nb.aventiure2.data.time.TimeTaker;

public class AbstractGameObjectFactory
        implements IWorldLoaderMixin {
    protected final AvDatabase db;
    protected final TimeTaker timeTaker;
    protected final World world;

    AbstractGameObjectFactory(
            final AvDatabase db, final TimeTaker timeTaker, final World world) {
        this.db = db;
        this.timeTaker = timeTaker;
        this.world = world;
    }

    @Override
    public World getWorld() {
        return world;
    }
}
