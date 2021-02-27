package de.nb.aventiure2.data.world.gameobject;

import androidx.annotation.NonNull;

import java.util.function.Supplier;

import javax.annotation.Nullable;

import de.nb.aventiure2.data.database.AvDatabase;
import de.nb.aventiure2.data.time.TimeTaker;
import de.nb.aventiure2.data.world.base.GameObject;
import de.nb.aventiure2.data.world.base.GameObjectId;
import de.nb.aventiure2.data.world.base.Lichtverhaeltnisse;
import de.nb.aventiure2.data.world.syscomp.description.impl.SimpleDescriptionComp;
import de.nb.aventiure2.data.world.syscomp.location.LocationComp;
import de.nb.aventiure2.data.world.syscomp.storingplace.StoringPlaceComp;
import de.nb.aventiure2.data.world.syscomp.storingplace.StoringPlaceType;
import de.nb.aventiure2.german.base.Nominalphrase;

/**
 * A factory for special {@link GameObject}s: Tangible objects, that might be found somewhere.
 */
class GeneralObjectFactory {
    private final AvDatabase db;
    private final TimeTaker timeTaker;
    private final World world;

    GeneralObjectFactory(final AvDatabase db, final TimeTaker timeTaker,
                         final World world) {
        this.db = db;
        this.timeTaker = timeTaker;
        this.world = world;
    }

    @NonNull
    public static GameObject create(final GameObjectId id) {
        return new GameObject(id);
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
                      final boolean niedrig,
                      final StoringPlaceType locationMode) {
        return create(id,
                descriptionAtFirstSightAndWhenKnown,
                initialLocationId, initialLastLocationId,
                movable, locationMode, niedrig, null);
    }

    public GameObject create(final GameObjectId id,
                             final Nominalphrase descriptionAtFirstSightAndWhenKnown,
                             @Nullable final GameObjectId initialLocationId,
                             @Nullable final GameObjectId initialLastLocationId,
                             final boolean movable,
                             final StoringPlaceType locationMode,
                             final boolean niedrig,
                             @Nullable
                             final Supplier<Lichtverhaeltnisse> lichtverhaeltnisseSupplier) {
        return create(id,
                descriptionAtFirstSightAndWhenKnown,
                descriptionAtFirstSightAndWhenKnown,
                descriptionAtFirstSightAndWhenKnown,
                initialLocationId, initialLastLocationId,
                movable, locationMode, niedrig, lichtverhaeltnisseSupplier);
    }

    GameObject create(final GameObjectId id,
                      final Nominalphrase descriptionAtFirstSight,
                      final Nominalphrase normalDescriptionWhenKnown,
                      final Nominalphrase shortDescriptionWhenKnown,
                      @Nullable final GameObjectId initialLocationId,
                      @Nullable final GameObjectId initialLastLocationId,
                      final boolean movable,
                      final StoringPlaceType locationMode,
                      final boolean niedrig) {
        return create(id, descriptionAtFirstSight, normalDescriptionWhenKnown,
                shortDescriptionWhenKnown,
                initialLocationId, initialLastLocationId, movable, locationMode,
                niedrig, null);
    }

    private GameObject create(final GameObjectId id,
                              final Nominalphrase descriptionAtFirstSight,
                              final Nominalphrase normalDescriptionWhenKnown,
                              final Nominalphrase shortDescriptionWhenKnown,
                              @Nullable final GameObjectId initialLocationId,
                              @Nullable final GameObjectId initialLastLocationId,
                              final boolean movable,
                              final StoringPlaceType locationMode,
                              final boolean niedrig,
                              @Nullable
                              final Supplier<Lichtverhaeltnisse> lichtverhaeltnisseSupplier) {
        final LocationComp locationComp =
                new LocationComp(id, db, world, initialLocationId, initialLastLocationId,
                        movable);
        return new StoringPlaceObject(id,
                new SimpleDescriptionComp(id, descriptionAtFirstSight,
                        normalDescriptionWhenKnown,
                        shortDescriptionWhenKnown),
                locationComp,
                new StoringPlaceComp(id, timeTaker, locationComp, locationMode,
                        niedrig, lichtverhaeltnisseSupplier));
    }
}
