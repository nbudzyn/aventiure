package de.nb.aventiure2.data.world.syscomp.location;

import com.google.common.collect.ImmutableList;

import java.util.Collection;
import java.util.List;

import de.nb.aventiure2.data.database.AvDatabase;
import de.nb.aventiure2.data.world.base.GameObjectId;
import de.nb.aventiure2.data.world.syscomp.description.IDescribableGO;

import static com.google.common.collect.ImmutableList.toImmutableList;

/**
 * Functionality concerned with Location that might span several game objects:
 * Game Object Queries etc.
 */
public class LocationSystem {
    private final LocationDao dao;

    public LocationSystem(final AvDatabase db) {
        dao = db.locationDao();
    }

    public List<GameObjectId> findByLocation(final GameObjectId locationId) {
        return dao.findByLocation(locationId);
    }

    public static <DESC_OBJ extends ILocatableGO & IDescribableGO>
    ImmutableList<DESC_OBJ> filterMovable(final Collection<DESC_OBJ> gameObjects) {
        return filterMovable(gameObjects, true);
    }

    public static <DESC_OBJ extends ILocatableGO & IDescribableGO> ImmutableList<DESC_OBJ>
    filterNotMovable(final Collection<DESC_OBJ> gameObjects) {
        return filterMovable(gameObjects, false);
    }

    public static <DESC_OBJ extends ILocatableGO & IDescribableGO> ImmutableList<DESC_OBJ>
    filterMovable(final Collection<DESC_OBJ> gameObjects, final boolean movable) {
        return gameObjects.stream()
                .filter(go -> go.locationComp().isMovable() == movable)
                .collect(toImmutableList());
    }
}
