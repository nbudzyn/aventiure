package de.nb.aventiure2.data.world.gameobject.wetter;

import de.nb.aventiure2.data.database.AvDatabase;
import de.nb.aventiure2.data.narration.Narrator;
import de.nb.aventiure2.data.time.TimeTaker;
import de.nb.aventiure2.data.world.base.GameObject;
import de.nb.aventiure2.data.world.gameobject.*;

/**
 * Factory für das {@link Wetter}-Game-Object.
 */
public class WetterFactory {
    private final AvDatabase db;
    private final TimeTaker timeTaker;
    private final Narrator n;
    private final World world;

    public WetterFactory(final AvDatabase db,
                         final TimeTaker timeTaker,
                         final Narrator n,
                         final World world) {
        this.db = db;
        this.timeTaker = timeTaker;
        this.n = n;
        this.world = world;
    }

    public GameObject create() {
        return new Wetter(db, n, timeTaker, world);
    }
}
