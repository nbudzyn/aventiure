package de.nb.aventiure2.data.world.gameobject;

import androidx.annotation.NonNull;

import javax.annotation.CheckReturnValue;

import de.nb.aventiure2.data.database.AvDatabase;
import de.nb.aventiure2.data.world.base.GameObject;
import de.nb.aventiure2.data.world.base.GameObjectId;
import de.nb.aventiure2.data.world.base.Known;
import de.nb.aventiure2.data.world.base.Lichtverhaeltnisse;
import de.nb.aventiure2.data.world.syscomp.description.impl.SimpleDescriptionComp;
import de.nb.aventiure2.data.world.syscomp.location.LocationComp;
import de.nb.aventiure2.data.world.syscomp.storingplace.StoringPlaceComp;
import de.nb.aventiure2.german.description.TimedDescription;
import de.nb.aventiure2.german.praedikat.AdverbialeAngabeSkopusVerbAllg;
import de.nb.aventiure2.german.praedikat.VerbSubj;

import static de.nb.aventiure2.data.world.base.Lichtverhaeltnisse.DUNKEL;
import static de.nb.aventiure2.data.world.base.SpatialConnectionData.conData;
import static de.nb.aventiure2.data.world.gameobject.World.*;
import static de.nb.aventiure2.data.world.syscomp.storingplace.StoringPlaceType.STAMM_EINES_BAUMS;
import static de.nb.aventiure2.data.world.time.AvTimeSpan.*;
import static de.nb.aventiure2.german.base.Artikel.Typ.DEF;
import static de.nb.aventiure2.german.base.Nominalphrase.np;
import static de.nb.aventiure2.german.base.NumerusGenus.M;
import static de.nb.aventiure2.german.base.StructuralElement.PARAGRAPH;
import static de.nb.aventiure2.german.base.StructuralElement.SENTENCE;
import static de.nb.aventiure2.german.description.DescriptionBuilder.du;

class SchattenDerBaeumeFactory {
    private static final String
            DESC_TO_SCHATTEN_DER_BAEUME__SC_SETZT_SICH_TAGSUEBER_IN_DEN_SCHATTEN_DER_BAEUME =
            "DescTo_SchattenDerBaeume__SCSetztSichTagsueberInDenSchattenDerBaeume";
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
                        np(M, DEF, "Schatten der Bäume"),
                        np(M, DEF, "Schatten der Bäume"),
                        np(M, DEF, "Schatten"));

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

        // STORY Aus dem Schatten der Bäume: Auf einen Baum klettern?!
        return new StoringPlaceObject(id,
                descriptionComp,
                locationComp,
                storingPlaceComp);
    }

    @CheckReturnValue
    private TimedDescription<?> getDescIn(
            final Known newLocationKnown, final Lichtverhaeltnisse lichtverhaeltnisse) {
        if (lichtverhaeltnisse == DUNKEL) {
            return du("setzt", "dich unter die Bäume", secs(20))
                    .dann();
        }

        // FIXME Klarer machen, dass man unter den Bäumen versteckt ist. (Ggf. als Tipp.)

        if (db.counterDao().get(
                DESC_TO_SCHATTEN_DER_BAEUME__SC_SETZT_SICH_TAGSUEBER_IN_DEN_SCHATTEN_DER_BAEUME)
                == 0) {
            return du(
                    PARAGRAPH,
                    "lässt", "dich im Schatten der umstehenden Bäume nieder",
                    "im Schatten der umstehenden Bäume",
                    mins(5),
                    DESC_TO_SCHATTEN_DER_BAEUME__SC_SETZT_SICH_TAGSUEBER_IN_DEN_SCHATTEN_DER_BAEUME)
                    .beendet(SENTENCE)
                    .dann();
        }

        return du("setzt", "dich wieder in den Schatten der Bäume", secs(30))
                .undWartest()
                .dann();
    }

    private static TimedDescription<?> getDescOut(
            final Known newLocationKnown, final Lichtverhaeltnisse lichtverhaeltnisse) {
        if (lichtverhaeltnisse == DUNKEL) {

            // "du stehst wieder auf"
            return du(VerbSubj.AUFSTEHEN
                            .mitAdverbialerAngabe(
                                    new AdverbialeAngabeSkopusVerbAllg("wieder")),
                    secs(10))
                    .undWartest()
                    .dann();
        }

        return du(PARAGRAPH, "erhebst",
                "dich wieder und trittst aus dem Schatten der Bäume hervor",
                secs(10))
                .beendet(SENTENCE)
                .dann();
    }
}
