package de.nb.aventiure2.data.world.gameobject;

import androidx.annotation.NonNull;

import de.nb.aventiure2.data.database.AvDatabase;
import de.nb.aventiure2.data.world.base.GameObject;
import de.nb.aventiure2.data.world.base.GameObjectId;
import de.nb.aventiure2.data.world.base.Known;
import de.nb.aventiure2.data.world.base.Lichtverhaeltnisse;
import de.nb.aventiure2.data.world.syscomp.description.impl.SimpleDescriptionComp;
import de.nb.aventiure2.data.world.syscomp.location.LocationComp;
import de.nb.aventiure2.data.world.syscomp.storingplace.StoringPlaceComp;
import de.nb.aventiure2.german.base.AbstractDescription;

import static de.nb.aventiure2.data.world.base.Lichtverhaeltnisse.DUNKEL;
import static de.nb.aventiure2.data.world.base.SpatialConnectionData.conData;
import static de.nb.aventiure2.data.world.gameobject.World.VOR_DEM_ALTEN_TURM;
import static de.nb.aventiure2.data.world.gameobject.World.VOR_DEM_ALTEN_TURM_SCHATTEN_DER_BAEUME;
import static de.nb.aventiure2.data.world.syscomp.storingplace.StoringPlaceType.STAMM_EINES_BAUMS;
import static de.nb.aventiure2.data.world.time.AvTimeSpan.mins;
import static de.nb.aventiure2.data.world.time.AvTimeSpan.secs;
import static de.nb.aventiure2.german.base.DuDescription.du;
import static de.nb.aventiure2.german.base.Nominalphrase.np;
import static de.nb.aventiure2.german.base.NumerusGenus.M;
import static de.nb.aventiure2.german.base.StructuralElement.SENTENCE;

public class SchattenDerBaeumeFactory {
    private final AvDatabase db;
    private final World world;

    SchattenDerBaeumeFactory(final AvDatabase db,
                             final World world) {
        this.db = db;
        this.world = world;
    }

    GameObject createVorDemAltenTurm() {
        return create(VOR_DEM_ALTEN_TURM_SCHATTEN_DER_BAEUME, VOR_DEM_ALTEN_TURM);
    }

    @NonNull
    private GameObject create(final GameObjectId id, final GameObjectId locationId) {
        final SimpleDescriptionComp descriptionComp =
                new SimpleDescriptionComp(id,
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
                id, db, world, locationId,
                null, false);

        final StoringPlaceComp storingPlaceComp = new StoringPlaceComp(
                id, db, STAMM_EINES_BAUMS,
                false,
                conData("vor den Bäumen",
                        "In den Schatten der Bäume setzen",
                        secs(10),
                        this::getDescIn),
                conData("vor den Bäumen",
                        "Aus dem Schatten der Bäume treten",
                        secs(10),
                        SchattenDerBaeumeFactory::getDescOut)
        );

        return new StoringPlaceObject(id,
                descriptionComp,
                locationComp,
                storingPlaceComp);
    }

    private AbstractDescription<?> getDescIn(
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
                "DescTo_SchattenDerBaeume__SCSetztSichTagsueberInDenSchattenDerBaeume")
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

    private static AbstractDescription<?> getDescOut(
            final Known newLocationKnown, final Lichtverhaeltnisse lichtverhaeltnisse) {
        if (lichtverhaeltnisse == DUNKEL) {
            return du("stehst", "wieder auf", secs(10))
                    .undWartest()
                    .dann();
        }

        return du("erhebst",
                "dich und trittst aus dem Schatten der Bäume hervor",
                secs(10))
                .beendet(SENTENCE)
                .dann();
    }
}
