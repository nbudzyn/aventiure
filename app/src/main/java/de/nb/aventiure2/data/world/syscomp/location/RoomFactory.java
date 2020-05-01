package de.nb.aventiure2.data.world.syscomp.location;

import javax.annotation.Nonnull;

import de.nb.aventiure2.data.world.base.GameObject;
import de.nb.aventiure2.data.world.base.GameObjectId;
import de.nb.aventiure2.data.world.syscomp.spatialconnection.AbstractSpatialConnectionComp;
import de.nb.aventiure2.data.world.syscomp.spatialconnection.ISpatiallyConnectedGO;
import de.nb.aventiure2.data.world.syscomp.storingplace.IHasStoringPlaceGO;
import de.nb.aventiure2.data.world.syscomp.storingplace.StoringPlaceComp;
import de.nb.aventiure2.data.world.syscomp.storingplace.StoringPlaceType;

/**
 * A factory for special {@link GameObject}s: Rooms in the world.
 */
public class RoomFactory {
    public RoomFactory() {
    }

    public static GameObject create(final GameObjectId id,
                                    final AbstractSpatialConnectionComp spatialConnectionComp) {
        return create(id, StoringPlaceType.BODEN, spatialConnectionComp);
    }

    public static GameObject create(final GameObjectId id, final StoringPlaceType locationMode,
                                    final AbstractSpatialConnectionComp spatialConnectionComp) {
        return new Room(id, new StoringPlaceComp(id, locationMode),
                spatialConnectionComp);
    }

    private static class Room extends GameObject
            implements IHasStoringPlaceGO, ISpatiallyConnectedGO {
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
