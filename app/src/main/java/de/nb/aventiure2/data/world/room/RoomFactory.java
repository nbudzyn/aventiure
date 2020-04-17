package de.nb.aventiure2.data.world.room;

import de.nb.aventiure2.data.world.base.GameObject;
import de.nb.aventiure2.data.world.base.GameObjectId;

/**
 * A factory for special {@link GameObject}s: Rooms in the world.
 */
public class RoomFactory {
    RoomFactory() {
    }

    static GameObject create(final GameObjectId id) {
        return create(id, ObjectLocationMode.BODEN);
    }

    static GameObject create(final GameObjectId id, final ObjectLocationMode locationMode) {
        final GameObjectId id1 = id;
        final GameObject res = new GameObject(id1);
        res.setStoringPlace(new StoringPlace(id, locationMode));
        return res;
    }
}
