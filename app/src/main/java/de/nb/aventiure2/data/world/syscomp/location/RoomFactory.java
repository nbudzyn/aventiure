package de.nb.aventiure2.data.world.syscomp.location;

import javax.annotation.Nonnull;

import de.nb.aventiure2.data.database.AvDatabase;
import de.nb.aventiure2.data.world.base.GameObject;
import de.nb.aventiure2.data.world.base.GameObjectId;
import de.nb.aventiure2.data.world.gameobject.World;
import de.nb.aventiure2.data.world.syscomp.spatialconnection.AbstractSpatialConnectionComp;
import de.nb.aventiure2.data.world.syscomp.spatialconnection.ISpatiallyConnectedGO;
import de.nb.aventiure2.data.world.syscomp.spatialconnection.impl.ImWaldBeimBrunnenConnectionComp;
import de.nb.aventiure2.data.world.syscomp.storingplace.ILocationGO;
import de.nb.aventiure2.data.world.syscomp.storingplace.StoringPlaceComp;
import de.nb.aventiure2.data.world.syscomp.storingplace.StoringPlaceType;

import static de.nb.aventiure2.data.world.gameobject.World.IM_WALD_BEIM_BRUNNEN;

/**
 * A factory for special {@link GameObject}s: Rooms in the world.
 */
public class RoomFactory {
    private final AvDatabase db;
    private final World world;

    public RoomFactory(final AvDatabase db,
                       final World world) {
        this.db = db;
        this.world = world;
    }

    public GameObject createImWaldBeimBrunnen() {
        final StoringPlaceComp storingPlaceComp = new StoringPlaceComp(IM_WALD_BEIM_BRUNNEN, db,
                world,
                StoringPlaceType.GRAS_NEBEN_DEM_BRUNNEN,
                false);

        return new Room(IM_WALD_BEIM_BRUNNEN, storingPlaceComp,
                new ImWaldBeimBrunnenConnectionComp(db, world, storingPlaceComp));
    }

    public GameObject create(final GameObjectId id,
                             final boolean dauerhaftBeleuchtet,
                             final AbstractSpatialConnectionComp spatialConnectionComp) {
        return create(id, StoringPlaceType.BODEN, dauerhaftBeleuchtet, spatialConnectionComp);
    }

    public GameObject create(final GameObjectId id, final StoringPlaceType locationMode,
                             final boolean dauerhaftBeleuchtet,
                             final AbstractSpatialConnectionComp spatialConnectionComp) {
        return new Room(id, new StoringPlaceComp(id, db, world, locationMode, dauerhaftBeleuchtet),
                spatialConnectionComp);
    }

    private static class Room extends GameObject
            implements ILocationGO, ISpatiallyConnectedGO {
        private final StoringPlaceComp storingPlaceComp;
        private final AbstractSpatialConnectionComp spatialConnectionComp;

        public Room(final GameObjectId id, final StoringPlaceComp storingPlaceComp,
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
