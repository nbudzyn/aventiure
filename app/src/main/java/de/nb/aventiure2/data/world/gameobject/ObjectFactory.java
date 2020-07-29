package de.nb.aventiure2.data.world.gameobject;

import androidx.annotation.NonNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import de.nb.aventiure2.data.database.AvDatabase;
import de.nb.aventiure2.data.world.base.GameObject;
import de.nb.aventiure2.data.world.base.GameObjectId;
import de.nb.aventiure2.data.world.base.Known;
import de.nb.aventiure2.data.world.base.Lichtverhaeltnisse;
import de.nb.aventiure2.data.world.syscomp.description.AbstractDescriptionComp;
import de.nb.aventiure2.data.world.syscomp.description.IDescribableGO;
import de.nb.aventiure2.data.world.syscomp.description.impl.SimpleDescriptionComp;
import de.nb.aventiure2.data.world.syscomp.location.ILocatableGO;
import de.nb.aventiure2.data.world.syscomp.location.LocationComp;
import de.nb.aventiure2.data.world.syscomp.storingplace.ILocationGO;
import de.nb.aventiure2.data.world.syscomp.storingplace.StoringPlaceComp;
import de.nb.aventiure2.data.world.syscomp.storingplace.StoringPlaceType;
import de.nb.aventiure2.german.base.AbstractDescription;
import de.nb.aventiure2.german.base.Nominalphrase;
import de.nb.aventiure2.german.base.NumerusGenus;

import static de.nb.aventiure2.data.world.base.Lichtverhaeltnisse.DUNKEL;
import static de.nb.aventiure2.data.world.base.SpatialConnection.con;
import static de.nb.aventiure2.data.world.gameobject.World.VOR_DEM_ALTEN_TURM;
import static de.nb.aventiure2.data.world.gameobject.World.VOR_DEM_ALTEN_TURM_SCHATTEN_DER_BAEUME;
import static de.nb.aventiure2.data.world.syscomp.storingplace.StoringPlaceType.SCHATTEN_DER_BAEUME;
import static de.nb.aventiure2.data.world.time.AvTimeSpan.mins;
import static de.nb.aventiure2.data.world.time.AvTimeSpan.secs;
import static de.nb.aventiure2.german.base.DuDescription.du;
import static de.nb.aventiure2.german.base.Nominalphrase.np;
import static de.nb.aventiure2.german.base.NumerusGenus.M;
import static de.nb.aventiure2.german.base.StructuralElement.SENTENCE;

/**
 * A factory for special {@link GameObject}s: Tangible objects, that might be found somewhere.
 */
public class ObjectFactory {
    private final AvDatabase db;
    private final World world;

    ObjectFactory(final AvDatabase db,
                  final World world) {
        this.db = db;
        this.world = world;
    }

    GameObject createVorDemAltenTurmSchattenDerBaeume() {
        final SimpleDescriptionComp descriptionComp =
                new SimpleDescriptionComp(VOR_DEM_ALTEN_TURM_SCHATTEN_DER_BAEUME,
                        np(M, "der Schatten der Bäume",
                                "dem Schatten der Bäume",
                                "den Schatten der Bäume"),
                        np(M, "der Schatten der Bäume",
                                "dem Schatten der Bäume",
                                "den Schatten der Bäume"),
                        np(M, "der Schatten",
                                "dem Schatten",
                                "den Schatten"));

        final LocationComp locationComp = new LocationComp(
                VOR_DEM_ALTEN_TURM_SCHATTEN_DER_BAEUME, db, world, VOR_DEM_ALTEN_TURM,
                null, false);

        final StoringPlaceComp storingPlaceComp = new StoringPlaceComp(
                VOR_DEM_ALTEN_TURM_SCHATTEN_DER_BAEUME, db, SCHATTEN_DER_BAEUME,
                false,
                con(VOR_DEM_ALTEN_TURM_SCHATTEN_DER_BAEUME,
                        "vor den Bäumen",
                        "In den Schatten der Bäume setzen",
                        secs(10),
                        ObjectFactory::getDescTo_VorDemAltenTurmSchattenDerBaeume)
                // STORY Man kann aus VOR_DEM_ALTEN_TURM_BÄUME auch wieder aufstehen.
        );

        return new StoringPlaceObject(VOR_DEM_ALTEN_TURM_SCHATTEN_DER_BAEUME,
                descriptionComp,
                locationComp,
                storingPlaceComp);
    }

    private static AbstractDescription<?> getDescTo_VorDemAltenTurmSchattenDerBaeume(
            final Known newLocationKnown, final Lichtverhaeltnisse lichtverhaeltnisse) {
        if (lichtverhaeltnisse == DUNKEL) {
            return du("setzt", "dich unter die Bäume. Die Bäume rauschen in "
                    + "der Dunkelheit, die Eulen schnarren, und "
                    + "und es fängt an, dir angst zu werden", secs(30))
                    .beendet(SENTENCE);
            // STORY Alternativen:
            //  - "Du setzt dich unter die Bäume, wo es dunkel und ungemütlich ist. Krabbelt da etwas auf "
            //   + "deinem rechten Bein? Du schlägst mit der Hand zu, kannst aber nichts erkennen"
            //  - "Du setzt dich unter die Bäume. In den Ästen über dir knittert und rauscht es"
        }

        if (!newLocationKnown.isKnown()) {
            return du("lässt", "dich im Schatten der Bäume nieder. Es tut gut, "
                            + "eine Weile zu rasten",
                    "im Schatten der Bäume",
                    mins(5))
                    .komma()
                    .beendet(SENTENCE)
                    .dann();
        }

        return du("setzt", "dich wieder in den Schatten der Bäume", secs(30))
                .undWartest()
                .dann();
    }

    /**
     * Erzeugt ein Objekt, das immer gleich beschrieben wird ("eine Tasche", niemals "die Tasche").
     */
    GameObject create(final GameObjectId id,
                      final Nominalphrase descriptionAtFirstSightAndWhenKnown,
                      @Nullable final GameObjectId initialLocationId,
                      @Nullable final GameObjectId initialLastLocationId,
                      final boolean movable) {
        return create(id,
                descriptionAtFirstSightAndWhenKnown,
                descriptionAtFirstSightAndWhenKnown,
                descriptionAtFirstSightAndWhenKnown,
                initialLocationId,
                initialLastLocationId,
                movable);
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
                      final Nominalphrase descriptionAtFirstSightAndWhenKnown,
                      final Nominalphrase shortDescriptionWhenKnown,
                      @Nullable final GameObjectId initialLocationId,
                      @Nullable final GameObjectId initialLastLocationId,
                      final boolean movable,
                      final StoringPlaceType locationMode,
                      final boolean dauerhaftBeleuchtet) {
        return create(id,
                descriptionAtFirstSightAndWhenKnown,
                descriptionAtFirstSightAndWhenKnown,
                shortDescriptionWhenKnown,
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
                new StoringPlaceComp(id, db, locationMode, dauerhaftBeleuchtet, null));
    }

    private static class SimpleObject extends GameObject
            implements IDescribableGO, ILocatableGO {
        private final AbstractDescriptionComp descriptionComp;
        private final LocationComp locationComp;

        SimpleObject(final GameObjectId id,
                     final AbstractDescriptionComp descriptionComp,
                     final LocationComp locationComp) {
            super(id);
            // Jede Komponente muss registiert werden!
            this.descriptionComp = addComponent(descriptionComp);
            this.locationComp = addComponent(locationComp);
        }

        @NonNull
        @Override
        public AbstractDescriptionComp descriptionComp() {
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
                                  final AbstractDescriptionComp descriptionComp,
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
