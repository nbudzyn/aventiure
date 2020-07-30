package de.nb.aventiure2.data.world.gameobject;

import javax.annotation.Nullable;

import de.nb.aventiure2.data.database.AvDatabase;
import de.nb.aventiure2.data.world.base.GameObject;
import de.nb.aventiure2.data.world.base.GameObjectId;
import de.nb.aventiure2.data.world.syscomp.description.impl.SimpleDescriptionComp;
import de.nb.aventiure2.data.world.syscomp.location.LocationComp;
import de.nb.aventiure2.data.world.syscomp.storingplace.StoringPlaceComp;
import de.nb.aventiure2.data.world.syscomp.storingplace.StoringPlaceType;
import de.nb.aventiure2.german.base.Nominalphrase;

/**
 * A factory for special {@link GameObject}s: Tangible objects, that might be found somewhere.
 */
public class GeneralObjectFactory {
    private final AvDatabase db;
    private final World world;

    GeneralObjectFactory(final AvDatabase db,
                         final World world) {
        this.db = db;
        this.world = world;
    }

    GameObject create(final GameObjectId id,
                      final Nominalphrase descriptionAtFirstSight,
                      final Nominalphrase normalDescriptionWhenKnown,
                      final Nominalphrase shortDescriptionWhenKnown,
                      @Nullable final GameObjectId initialLocationId,
                      @Nullable final GameObjectId initialLastLocationId,
                      final boolean movable) {
        return new SimpleObject(id,
                new SimpleDescriptionComp(id, descriptionAtFirstSight,
                        normalDescriptionWhenKnown,
                        shortDescriptionWhenKnown),
                new LocationComp(id, db, world, initialLocationId, initialLastLocationId,
                        movable));
    }

    GameObject create(final GameObjectId id,
                      final Nominalphrase descriptionAtFirstSightAndWhenKnown,
                      @Nullable final GameObjectId initialLocationId,
                      @Nullable final GameObjectId initialLastLocationId,
                      final boolean movable,
                      final StoringPlaceType locationMode,
                      final boolean dauerhaftBeleuchtet) {
        return create(id,
                descriptionAtFirstSightAndWhenKnown,
                descriptionAtFirstSightAndWhenKnown,
                descriptionAtFirstSightAndWhenKnown,
                initialLocationId, initialLastLocationId,
                movable, locationMode, dauerhaftBeleuchtet);
    }

    GameObject create(final GameObjectId id,
                      final Nominalphrase descriptionAtFirstSight,
                      final Nominalphrase normalDescriptionWhenKnown,
                      final Nominalphrase shortDescriptionWhenKnown,
                      @Nullable final GameObjectId initialLocationId,
                      @Nullable final GameObjectId initialLastLocationId,
                      final boolean movable,
                      final StoringPlaceType locationMode,
                      final boolean dauerhaftBeleuchtet) {
        return new StoringPlaceObject(id,
                new SimpleDescriptionComp(id, descriptionAtFirstSight,
                        normalDescriptionWhenKnown,
                        shortDescriptionWhenKnown),
                new LocationComp(id, db, world, initialLocationId, initialLastLocationId,
                        movable),
                new StoringPlaceComp(id, db, locationMode, dauerhaftBeleuchtet));
    }
}
