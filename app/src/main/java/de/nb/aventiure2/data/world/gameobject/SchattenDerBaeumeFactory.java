package de.nb.aventiure2.data.world.gameobject;

import de.nb.aventiure2.data.database.AvDatabase;
import de.nb.aventiure2.data.world.base.GameObject;
import de.nb.aventiure2.data.world.base.Known;
import de.nb.aventiure2.data.world.base.Lichtverhaeltnisse;
import de.nb.aventiure2.data.world.syscomp.description.impl.SimpleDescriptionComp;
import de.nb.aventiure2.data.world.syscomp.location.LocationComp;
import de.nb.aventiure2.data.world.syscomp.storingplace.StoringPlaceComp;
import de.nb.aventiure2.german.base.AbstractDescription;

import static de.nb.aventiure2.data.world.base.Lichtverhaeltnisse.DUNKEL;
import static de.nb.aventiure2.data.world.base.SpatialConnection.con;
import static de.nb.aventiure2.data.world.gameobject.World.VOR_DEM_ALTEN_TURM;
import static de.nb.aventiure2.data.world.gameobject.World.VOR_DEM_ALTEN_TURM_SCHATTEN_DER_BAEUME;
import static de.nb.aventiure2.data.world.syscomp.storingplace.StoringPlaceType.STAMM_EINES_BAUMS;
import static de.nb.aventiure2.data.world.time.AvTimeSpan.mins;
import static de.nb.aventiure2.data.world.time.AvTimeSpan.secs;
import static de.nb.aventiure2.german.base.DuDescription.du;
import static de.nb.aventiure2.german.base.Nominalphrase.np;
import static de.nb.aventiure2.german.base.NumerusGenus.M;
import static de.nb.aventiure2.german.base.StructuralElement.SENTENCE;

/**
 * A factory for special {@link GameObject}s: Tangible objects, that might be found somewhere.
 */
public class SchattenDerBaeumeFactory {
    private final AvDatabase db;
    private final World world;

    SchattenDerBaeumeFactory(final AvDatabase db,
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
                VOR_DEM_ALTEN_TURM_SCHATTEN_DER_BAEUME, db, STAMM_EINES_BAUMS,
                false,
                con(VOR_DEM_ALTEN_TURM_SCHATTEN_DER_BAEUME,
                        "vor den Bäumen",
                        "In den Schatten der Bäume setzen",
                        secs(10),
                        this::getDescTo_VorDemAltenTurmSchattenDerBaeume)
                // STORY Man kann aus VOR_DEM_ALTEN_TURM_BÄUME auch wieder aufstehen.
        );

        return new StoringPlaceObject(VOR_DEM_ALTEN_TURM_SCHATTEN_DER_BAEUME,
                descriptionComp,
                locationComp,
                storingPlaceComp);
    }

    private AbstractDescription<?> getDescTo_VorDemAltenTurmSchattenDerBaeume(
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

        if (db.counterDao().incAndGet(
                "DescTo_VorDemAltenTurmSchattenDerBaeume__SCSetztSichTagsueberInDenSchattenDerBaeume")
                == 1) {
            return du("lässt", "dich im Schatten der umstehenden Bäume nieder. Es tut gut, "
                            + "eine Weile zu rasten",
                    "im Schatten der umstehenden Bäume",
                    mins(5))
                    .komma()
                    .beendet(SENTENCE)
                    .dann();
        }

        return du("setzt", "dich wieder in den Schatten der Bäume", secs(30))
                .undWartest()
                .dann();
    }
}
