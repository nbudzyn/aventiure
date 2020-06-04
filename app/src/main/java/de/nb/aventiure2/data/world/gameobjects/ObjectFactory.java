package de.nb.aventiure2.data.world.gameobjects;

import androidx.annotation.NonNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import de.nb.aventiure2.data.database.AvDatabase;
import de.nb.aventiure2.data.world.base.GameObject;
import de.nb.aventiure2.data.world.base.GameObjectId;
import de.nb.aventiure2.data.world.syscomp.description.DescriptionComp;
import de.nb.aventiure2.data.world.syscomp.description.IDescribableGO;
import de.nb.aventiure2.data.world.syscomp.location.ILocatableGO;
import de.nb.aventiure2.data.world.syscomp.location.LocationComp;
import de.nb.aventiure2.data.world.syscomp.storingplace.ILocationGO;
import de.nb.aventiure2.data.world.syscomp.storingplace.StoringPlaceComp;
import de.nb.aventiure2.data.world.syscomp.storingplace.StoringPlaceType;
import de.nb.aventiure2.german.base.Nominalphrase;
import de.nb.aventiure2.german.base.NumerusGenus;

import static de.nb.aventiure2.german.base.Nominalphrase.np;

/**
 * A factory for special {@link GameObject}s: Tangible objects, that might be found somewhere.
 */
public class ObjectFactory {
    private final AvDatabase db;

    ObjectFactory(final AvDatabase db) {
        this.db = db;
    }

    public GameObject create(final GameObjectId id,
                             final NumerusGenus numerusGenus,
                             final String descriptionAtFirstSightNomDatAkk,
                             final String normalDescriptionWhenKnownNomDatAkk,
                             final String shortDescriptionWhenKnownNomDatAkk,
                             @Nullable final GameObjectId initialLocationId,
                             @Nullable final GameObjectId initialLastLocationId,
                             final boolean movable) {
        return create(id,
                np(numerusGenus, descriptionAtFirstSightNomDatAkk),
                np(numerusGenus, normalDescriptionWhenKnownNomDatAkk),
                np(numerusGenus, shortDescriptionWhenKnownNomDatAkk),
                initialLocationId,
                initialLastLocationId,
                movable);
    }

    GameObject create(final GameObjectId id,
                      final Nominalphrase descriptionAtFirstSight,
                      final Nominalphrase normalDescriptionWhenKnown,
                      final Nominalphrase shortDescriptionWhenKnown,
                      @Nullable final GameObjectId initialLocationId,
                      @Nullable final GameObjectId initialLastLocationId,
                      final boolean movable) {
        return new SimpleObject(id,
                new DescriptionComp(id, descriptionAtFirstSight, normalDescriptionWhenKnown,
                        shortDescriptionWhenKnown),
                new LocationComp(id, db, initialLocationId, initialLastLocationId, movable));
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
                new DescriptionComp(id, descriptionAtFirstSight, normalDescriptionWhenKnown,
                        shortDescriptionWhenKnown),
                new LocationComp(id, db, initialLocationId, initialLastLocationId, movable),
                new StoringPlaceComp(id, db, locationMode, dauerhaftBeleuchtet));
    }

    private static class SimpleObject extends GameObject
            implements IDescribableGO, ILocatableGO {
        private final DescriptionComp descriptionComp;
        private final LocationComp locationComp;

        SimpleObject(final GameObjectId id,
                     final DescriptionComp descriptionComp,
                     final LocationComp locationComp) {
            super(id);
            // Jede Komponente muss registiert werden!
            this.descriptionComp = addComponent(descriptionComp);
            this.locationComp = addComponent(locationComp);
        }

        @NonNull
        @Override
        public DescriptionComp descriptionComp() {
            return descriptionComp;
        }

        @Nonnull
        @Override
        public LocationComp locationComp() {
            return locationComp;
        }
    }

    private static class StoringPlaceObject extends SimpleObject
            implements ILocationGO {
        private final StoringPlaceComp storingPlaceComp;

        public StoringPlaceObject(final GameObjectId id,
                                  final DescriptionComp descriptionComp,
                                  final LocationComp locationComp,
                                  final StoringPlaceComp storingPlaceComp) {
            super(id, descriptionComp, locationComp);
            // Jede Komponente muss registiert werden!
            this.storingPlaceComp = addComponent(storingPlaceComp);
        }

        @Override
        public StoringPlaceComp storingPlaceComp() {
            return storingPlaceComp;
        }
    }
}
