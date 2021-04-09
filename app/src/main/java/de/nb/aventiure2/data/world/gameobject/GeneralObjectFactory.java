package de.nb.aventiure2.data.world.gameobject;

import androidx.annotation.NonNull;

import java.util.function.Supplier;

import javax.annotation.Nullable;

import de.nb.aventiure2.data.database.AvDatabase;
import de.nb.aventiure2.data.time.TimeTaker;
import de.nb.aventiure2.data.world.base.GameObject;
import de.nb.aventiure2.data.world.base.GameObjectId;
import de.nb.aventiure2.data.world.syscomp.description.impl.SimpleDescriptionComp;
import de.nb.aventiure2.data.world.syscomp.location.LocationComp;
import de.nb.aventiure2.data.world.syscomp.storingplace.Geschlossenheit;
import de.nb.aventiure2.data.world.syscomp.storingplace.StoringPlaceComp;
import de.nb.aventiure2.data.world.syscomp.storingplace.StoringPlaceType;
import de.nb.aventiure2.german.base.EinzelneSubstantivischePhrase;

import static de.nb.aventiure2.data.world.syscomp.storingplace.StoringPlaceComp.LEUCHTET_NIE;

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

    public GameObject create(final GameObjectId id,
                             final EinzelneSubstantivischePhrase descriptionAtFirstSight,
                             final EinzelneSubstantivischePhrase normalDescriptionWhenKnown,
                             final EinzelneSubstantivischePhrase shortDescriptionWhenKnown,
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
                      final EinzelneSubstantivischePhrase descriptionAtFirstSightAndWhenKnown,
                      @Nullable final GameObjectId initialLocationId,
                      @Nullable final GameObjectId initialLastLocationId,
                      final boolean movable,
                      final Geschlossenheit geschlossenheit,
                      final boolean niedrig,
                      final StoringPlaceType locationMode) {
        return create(id,
                descriptionAtFirstSightAndWhenKnown,
                initialLocationId, initialLastLocationId,
                movable, locationMode, niedrig, geschlossenheit,
                LEUCHTET_NIE);
    }

    public GameObject create(final GameObjectId id,
                             final EinzelneSubstantivischePhrase descriptionAtFirstSightAndWhenKnown,
                             @Nullable final GameObjectId initialLocationId,
                             @Nullable final GameObjectId initialLastLocationId,
                             final boolean movable,
                             final StoringPlaceType locationMode,
                             final boolean niedrig,
                             final Geschlossenheit geschlossenheit,
                             final Supplier<Boolean> leuchgetErmittler) {
        return create(id,
                descriptionAtFirstSightAndWhenKnown,
                descriptionAtFirstSightAndWhenKnown,
                descriptionAtFirstSightAndWhenKnown,
                initialLocationId, initialLastLocationId,
                movable, locationMode, niedrig, geschlossenheit,
                leuchgetErmittler);
    }

    GameObject create(final GameObjectId id,
                      final EinzelneSubstantivischePhrase descriptionAtFirstSight,
                      final EinzelneSubstantivischePhrase normalDescriptionWhenKnown,
                      final EinzelneSubstantivischePhrase shortDescriptionWhenKnown,
                      @Nullable final GameObjectId initialLocationId,
                      @Nullable final GameObjectId initialLastLocationId,
                      final boolean movable,
                      final StoringPlaceType locationMode,
                      final Geschlossenheit geschlossenheit,
                      final boolean niedrig) {
        return create(id, descriptionAtFirstSight, normalDescriptionWhenKnown,
                shortDescriptionWhenKnown,
                initialLocationId, initialLastLocationId, movable, locationMode,
                niedrig, geschlossenheit, LEUCHTET_NIE);
    }

    private GameObject create(final GameObjectId id,
                              final EinzelneSubstantivischePhrase descriptionAtFirstSight,
                              final EinzelneSubstantivischePhrase normalDescriptionWhenKnown,
                              final EinzelneSubstantivischePhrase shortDescriptionWhenKnown,
                              @Nullable final GameObjectId initialLocationId,
                              @Nullable final GameObjectId initialLastLocationId,
                              final boolean movable,
                              final StoringPlaceType locationMode,
                              final boolean niedrig,
                              final Geschlossenheit geschlossenheit,
                              final Supplier<Boolean> leuchtetErmittler) {
        final LocationComp locationComp =
                new LocationComp(id, db, world, initialLocationId, initialLastLocationId,
                        movable);
        return new StoringPlaceObject(id,
                new SimpleDescriptionComp(id, descriptionAtFirstSight,
                        normalDescriptionWhenKnown,
                        shortDescriptionWhenKnown),
                locationComp,
                new StoringPlaceComp(id, timeTaker, world, locationComp, locationMode,
                        niedrig, geschlossenheit,
                        leuchtetErmittler));
    }
}
