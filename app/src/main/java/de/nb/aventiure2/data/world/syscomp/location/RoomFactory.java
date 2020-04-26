package de.nb.aventiure2.data.world.syscomp.location;

import javax.annotation.Nonnull;

import de.nb.aventiure2.data.world.base.GameObject;
import de.nb.aventiure2.data.world.base.GameObjectId;
import de.nb.aventiure2.data.world.syscomp.spatialconnection.ISpatiallyConnectedGO;
import de.nb.aventiure2.data.world.syscomp.spatialconnection.SpatialConnectionComp;
import de.nb.aventiure2.data.world.syscomp.storingplace.IHasStoringPlaceGO;
import de.nb.aventiure2.data.world.syscomp.storingplace.StoringPlaceComp;
import de.nb.aventiure2.data.world.syscomp.storingplace.StoringPlaceType;

/**
 * A factory for special {@link GameObject}s: Rooms in the world.
 */
public class RoomFactory {
    public RoomFactory() {
    }

    public static GameObject create(final GameObjectId id) {
        return create(id, StoringPlaceType.BODEN);
    }

    public static GameObject create(final GameObjectId id, final StoringPlaceType locationMode) {
        return new Room(id, new StoringPlaceComp(id, locationMode));
    }

    private static class Room extends GameObject
            implements IHasStoringPlaceGO, ISpatiallyConnectedGO {
        private final StoringPlaceComp storingPlaceComp;
        private final SpatialConnectionComp spatialConnectionComp;

        public Room(final GameObjectId id, final StoringPlaceComp storingPlaceComp) {
            super(id);
            // Jede Komponente muss registiert werden!
            this.storingPlaceComp = addComponent(storingPlaceComp);
            spatialConnectionComp = addComponent(new SpatialConnectionComp(id));
        }

        @Nonnull
        @Override
        public StoringPlaceComp storingPlaceComp() {
            return storingPlaceComp;
        }

        @Nonnull
        @Override
        public SpatialConnectionComp spatialConnectionComp() {
            return spatialConnectionComp;
        }
    }
}
