package de.nb.aventiure2.data.world.syscomp.location;

import java.util.function.Supplier;

import javax.annotation.Nonnull;

import de.nb.aventiure2.data.database.AvDatabase;
import de.nb.aventiure2.data.narration.Narrator;
import de.nb.aventiure2.data.time.TimeTaker;
import de.nb.aventiure2.data.world.base.EnumRange;
import de.nb.aventiure2.data.world.base.GameObject;
import de.nb.aventiure2.data.world.base.GameObjectId;
import de.nb.aventiure2.data.world.base.Temperatur;
import de.nb.aventiure2.data.world.gameobject.*;
import de.nb.aventiure2.data.world.syscomp.spatialconnection.AbstractSpatialConnectionComp;
import de.nb.aventiure2.data.world.syscomp.spatialconnection.ISpatiallyConnectedGO;
import de.nb.aventiure2.data.world.syscomp.spatialconnection.impl.ImWaldBeimBrunnenConnectionComp;
import de.nb.aventiure2.data.world.syscomp.storingplace.Geschlossenheit;
import de.nb.aventiure2.data.world.syscomp.storingplace.ILocationGO;
import de.nb.aventiure2.data.world.syscomp.storingplace.StoringPlaceComp;
import de.nb.aventiure2.data.world.syscomp.storingplace.StoringPlaceType;

import static de.nb.aventiure2.data.world.gameobject.World.*;
import static de.nb.aventiure2.data.world.syscomp.storingplace.Geschlossenheit.MAN_KANN_HINEINSEHEN_UND_LICHT_SCHEINT_HINEIN_UND_HINAUS;

/**
 * A factory for special {@link GameObject}s: Rooms in the world.
 */
public class RoomFactory {
    private final AvDatabase db;
    private final TimeTaker timeTaker;
    private final Narrator n;
    private final World world;

    public RoomFactory(final AvDatabase db,
                       final TimeTaker timeTaker,
                       final Narrator n,
                       final World world) {
        this.db = db;
        this.timeTaker = timeTaker;
        this.n = n;
        this.world = world;
    }

    public GameObject createImWaldBeimBrunnen() {
        final StoringPlaceComp storingPlaceComp = new StoringPlaceComp(IM_WALD_BEIM_BRUNNEN,
                timeTaker, world, null, StoringPlaceType.NEBEN_DEM_BRUNNEN, false,
                MAN_KANN_HINEINSEHEN_UND_LICHT_SCHEINT_HINEIN_UND_HINAUS,
                StoringPlaceComp.LEUCHTET_NIE,
                EnumRange.of(Temperatur.KLIRREND_KALT, Temperatur.KUEHL));

        return new Room(IM_WALD_BEIM_BRUNNEN, storingPlaceComp,
                new ImWaldBeimBrunnenConnectionComp(db, timeTaker, n, world, storingPlaceComp));
    }

    /**
     * Erzeugt ein Game-Objekt, das etwas enthalten kann, aber selbst nirgendwo enthalten sein
     * kann.
     */
    public GameObject create(final GameObjectId id,
                             final StoringPlaceType locationMode,
                             final boolean niedrig,
                             final Geschlossenheit geschlossenheit,
                             final Supplier<Boolean> leuchtetErmittler,
                             final AbstractSpatialConnectionComp spatialConnectionComp) {
        return create(id, locationMode, niedrig, geschlossenheit, leuchtetErmittler,
                EnumRange.all(Temperatur.class),
                spatialConnectionComp);
    }


    /**
     * Erzeugt ein Game-Objekt, das etwas enthalten kann, aber selbst nirgendwo enthalten sein
     * kann.
     */
    public GameObject create(final GameObjectId id,
                             final StoringPlaceType locationMode,
                             final boolean niedrig,
                             final Geschlossenheit geschlossenheit,
                             final Supplier<Boolean> leuchtetErmittler,
                             final EnumRange<Temperatur> temperaturRange,
                             final AbstractSpatialConnectionComp spatialConnectionComp) {
        return new Room(id,
                new StoringPlaceComp(id, timeTaker, world, null, locationMode,
                        niedrig, geschlossenheit,
                        leuchtetErmittler, temperaturRange),
                spatialConnectionComp);
    }


    /**
     * Ein Game-Objekt, das etwas enthalten kann, aber selbst nirgendwo enthalten sein
     * kann.
     */
    private static class Room extends GameObject
            implements ILocationGO, ISpatiallyConnectedGO {
        private final StoringPlaceComp storingPlaceComp;
        private final AbstractSpatialConnectionComp spatialConnectionComp;

        Room(final GameObjectId id, final StoringPlaceComp storingPlaceComp,
             final AbstractSpatialConnectionComp spatialConnectionComp) {
            super(id);
            // Jede Komponente muss registiert werden!
            this.storingPlaceComp = addComponent(storingPlaceComp);
            this.spatialConnectionComp = addComponent(spatialConnectionComp);
        }

        @Nonnull
        @Override
        public StoringPlaceComp storingPlaceComp() {
            return storingPlaceComp;
        }

        @Nonnull
        @Override
        public AbstractSpatialConnectionComp spatialConnectionComp() {
            return spatialConnectionComp;
        }
    }
}
