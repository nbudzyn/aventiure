package de.nb.aventiure2.data.world.gameobject;

import static de.nb.aventiure2.data.time.AvTimeSpan.mins;
import static de.nb.aventiure2.data.time.AvTimeSpan.secs;
import static de.nb.aventiure2.data.world.base.Lichtverhaeltnisse.DUNKEL;
import static de.nb.aventiure2.data.world.base.SpatialConnectionData.conData;
import static de.nb.aventiure2.data.world.gameobject.SchattenDerBaeumeFactory.Counter.*;
import static de.nb.aventiure2.data.world.gameobject.World.*;
import static de.nb.aventiure2.data.world.syscomp.storingplace.Geschlossenheit.MAN_KANN_HINEINSEHEN_UND_LICHT_SCHEINT_HINEIN_UND_HINAUS;
import static de.nb.aventiure2.data.world.syscomp.storingplace.StoringPlaceComp.LEUCHTET_NIE;
import static de.nb.aventiure2.data.world.syscomp.storingplace.StoringPlaceType.STAMM_EINES_BAUMS;
import static de.nb.aventiure2.german.base.Artikel.Typ.DEF;
import static de.nb.aventiure2.german.base.NomenFlexionsspalte.SCHATTEN;
import static de.nb.aventiure2.german.base.Nominalphrase.np;
import static de.nb.aventiure2.german.base.NumerusGenus.M;
import static de.nb.aventiure2.german.base.StructuralElement.PARAGRAPH;
import static de.nb.aventiure2.german.description.DescriptionBuilder.du;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import javax.annotation.CheckReturnValue;

import de.nb.aventiure2.data.database.AvDatabase;
import de.nb.aventiure2.data.time.TimeTaker;
import de.nb.aventiure2.data.world.base.EnumRange;
import de.nb.aventiure2.data.world.base.GameObject;
import de.nb.aventiure2.data.world.base.GameObjectId;
import de.nb.aventiure2.data.world.base.Known;
import de.nb.aventiure2.data.world.base.Lichtverhaeltnisse;
import de.nb.aventiure2.data.world.base.Temperatur;
import de.nb.aventiure2.data.world.syscomp.description.impl.SimpleDescriptionComp;
import de.nb.aventiure2.data.world.syscomp.location.ILocatableGO;
import de.nb.aventiure2.data.world.syscomp.location.LocationComp;
import de.nb.aventiure2.data.world.syscomp.storingplace.StoringPlaceComp;
import de.nb.aventiure2.german.description.AbstractDescription;
import de.nb.aventiure2.german.description.TimedDescription;
import de.nb.aventiure2.german.praedikat.AdvAngabeSkopusVerbAllg;
import de.nb.aventiure2.german.praedikat.VerbSubj;

class SchattenDerBaeumeFactory extends AbstractGameObjectFactory {
    @SuppressWarnings({"unused", "RedundantSuppression"})
    enum Counter {
        DESC_TO_SCHATTEN_DER_BAEUME__SC_SETZT_SICH_TAGSUEBER_IN_DEN_SCHATTEN_DER_BAEUME
    }

    SchattenDerBaeumeFactory(final AvDatabase db,
                             final TimeTaker timeTaker, final World world) {
        super(db, timeTaker, world);
    }

    GameObject createVorDemAltenTurm() {
        return create(VOR_DEM_ALTEN_TURM_SCHATTEN_DER_BAEUME, VOR_DEM_ALTEN_TURM);
    }

    @SuppressWarnings("SameParameterValue")
    @NonNull
    private GameObject create(final GameObjectId id, final GameObjectId locationId) {
        final SimpleDescriptionComp descriptionComp =
                new SimpleDescriptionComp(id,
                        np(M, DEF, "Schatten der Bäume", id),
                        np(M, DEF, "Schatten der Bäume", id),
                        np(SCHATTEN, id));

        final LocationComp locationComp = new LocationComp(
                id, db, world, locationId,
                null, false);

        final StoringPlaceComp storingPlaceComp = new StoringPlaceComp(id, timeTaker, world,
                locationComp,
                STAMM_EINES_BAUMS,
                false, MAN_KANN_HINEINSEHEN_UND_LICHT_SCHEINT_HINEIN_UND_HINAUS, LEUCHTET_NIE,
                EnumRange.of(Temperatur.KLIRREND_KALT, Temperatur.RECHT_HEISS),
                conData("vor den Bäumen",
                        "In den Schatten der Bäume setzen",
                        secs(10),
                        this::getDescIn),
                conData("vor den Bäumen",
                        "Aus dem Schatten der Bäume treten",
                        secs(10),
                        SchattenDerBaeumeFactory::getDescOut));

        return new StoringPlaceObject(id,
                descriptionComp,
                locationComp,
                storingPlaceComp);
    }

    @CheckReturnValue
    private TimedDescription<?> getDescIn(
            final Known newLocationKnown, final Lichtverhaeltnisse lichtverhaeltnisse) {
        final boolean unauffaelligHinweisSinnvoll = isDescInUnauffaelligHinweisSinnvoll();
        if (lichtverhaeltnisse == DUNKEL) {
            return du("setzt",
                    "dich unter die Bäume",
                    unauffaelligHinweisSinnvoll ? ". Stockdunkel ist es hier" : null)
                    .timed(secs(20))
                    .dann();
        }
        if (db.counterDao().get(
                DESC_TO_SCHATTEN_DER_BAEUME__SC_SETZT_SICH_TAGSUEBER_IN_DEN_SCHATTEN_DER_BAEUME)
                == 0) {
            return du(PARAGRAPH, "lässt",
                    "dich im Schatten der umstehenden Bäume nieder",
                    unauffaelligHinweisSinnvoll ? ". Ein sehr unauffälliger Platz" : null)
                    .mitVorfeldSatzglied("im Schatten der umstehenden Bäume")
                    .timed(mins(5))
                    .withCounterIdIncrementedIfTextIsNarrated(
                            DESC_TO_SCHATTEN_DER_BAEUME__SC_SETZT_SICH_TAGSUEBER_IN_DEN_SCHATTEN_DER_BAEUME)
                    .dann();
        }

        return du("setzt", "dich wieder in den Schatten der Bäume",
                unauffaelligHinweisSinnvoll ? unauffaelligTagsueberString() : null)
                .timed(secs(30))
                .withCounterIdIncrementedIfTextIsNarrated(
                        DESC_TO_SCHATTEN_DER_BAEUME__SC_SETZT_SICH_TAGSUEBER_IN_DEN_SCHATTEN_DER_BAEUME)
                .undWartest(!unauffaelligHinweisSinnvoll)
                .dann();
    }

    private boolean isDescInUnauffaelligHinweisSinnvoll() {
        return (
                loadSC().memoryComp().isKnown(RAPUNZELS_ZAUBERIN)
                        || loadSC().memoryComp().isKnown(RAPUNZELS_GESANG)
        )
                && !loadSC().memoryComp().isKnown(RAPUNZELRUF)
                && !((ILocatableGO) loadRequired(RAPUNZELS_ZAUBERIN)).locationComp()
                .hasRecursiveLocation(VOR_DEM_ALTEN_TURM);
    }

    @Nullable
    private String unauffaelligTagsueberString() {
        switch (db.counterDao().get(
                DESC_TO_SCHATTEN_DER_BAEUME__SC_SETZT_SICH_TAGSUEBER_IN_DEN_SCHATTEN_DER_BAEUME)
                % 4) {
            case 0:
                return ". Der Platz vor dem Turm lässt sich von hier aus ganz insgeheim übersehen";
            case 2:
                return ". Keiner wird dich hier vermuten";
            case 3:
                return ". Hier sitzt es sich ganz versteckt";
            default:
                return null;
        }
    }

    private static AbstractDescription<? extends AbstractDescription<?>> getDescOut(
            final Known newLocationKnown, final Lichtverhaeltnisse lichtverhaeltnisse) {
        if (lichtverhaeltnisse == DUNKEL) {
            // "du stehst wieder auf"
            return du(VerbSubj.AUFSTEHEN
                    .mitAdvAngabe(new AdvAngabeSkopusVerbAllg("wieder")))
                    .undWartest()
                    .dann();
        }

        return du(PARAGRAPH, "erhebst",
                "dich wieder und trittst aus dem Schatten der Bäume hervor")
                .dann();
    }
}
