package de.nb.aventiure2.data.world.room;

import de.nb.aventiure2.data.world.base.AbstractGameObject;
import de.nb.aventiure2.data.world.base.GameObjectId;

/**
 * A room in the world
 */
public class AvRoom extends AbstractGameObject {
    private final ObjectLocationMode locationMode;

    AvRoom(final GameObjectId id) {
        this(id, ObjectLocationMode.BODEN);

    }

    AvRoom(final GameObjectId id, final ObjectLocationMode locationMode) {
        super(id);
        this.locationMode = locationMode;
    }

    public ObjectLocationMode getLocationMode() {
        return locationMode;
    }
}
