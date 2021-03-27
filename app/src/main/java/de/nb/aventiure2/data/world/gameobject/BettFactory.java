package de.nb.aventiure2.data.world.gameobject;

import androidx.annotation.NonNull;

import javax.annotation.CheckReturnValue;

import de.nb.aventiure2.data.database.AvDatabase;
import de.nb.aventiure2.data.time.TimeTaker;
import de.nb.aventiure2.data.world.base.GameObject;
import de.nb.aventiure2.data.world.base.GameObjectId;
import de.nb.aventiure2.data.world.base.Known;
import de.nb.aventiure2.data.world.base.Lichtverhaeltnisse;
import de.nb.aventiure2.data.world.syscomp.description.impl.SimpleDescriptionComp;
import de.nb.aventiure2.data.world.syscomp.location.LocationComp;
import de.nb.aventiure2.data.world.syscomp.state.IHasStateGO;
import de.nb.aventiure2.data.world.syscomp.state.impl.RapunzelState;
import de.nb.aventiure2.data.world.syscomp.storingplace.StoringPlaceComp;
import de.nb.aventiure2.german.description.TimedDescription;

import static de.nb.aventiure2.data.time.AvTimeSpan.secs;
import static de.nb.aventiure2.data.world.base.SpatialConnectionData.conData;
import static de.nb.aventiure2.data.world.gameobject.BettFactory.Counter.*;
import static de.nb.aventiure2.data.world.gameobject.World.*;
import static de.nb.aventiure2.data.world.syscomp.state.impl.RapunzelState.PAUSED_BEFORE_HAARE_VOM_TURM_HERUNTERGELASSEN;
import static de.nb.aventiure2.data.world.syscomp.storingplace.StoringPlaceType.UNTER_DEM_BETT;
import static de.nb.aventiure2.german.base.NomenFlexionsspalte.BETT;
import static de.nb.aventiure2.german.base.NomenFlexionsspalte.EIN_BETT;
import static de.nb.aventiure2.german.base.Nominalphrase.np;
import static de.nb.aventiure2.german.base.StructuralElement.SENTENCE;
import static de.nb.aventiure2.german.description.DescriptionBuilder.du;

public class BettFactory {
    @SuppressWarnings({"unused", "RedundantSuppression"})
    public
    enum Counter {
        BETT_DRUNTERKRIECHEN_VERSTECKEN,
        BETT_DRUNTERKRIECHEN_OHNE_SINN
    }

    private final AvDatabase db;
    private final TimeTaker timeTaker;
    private final World world;

    BettFactory(final AvDatabase db,
                final TimeTaker timeTaker,
                final World world) {
        this.db = db;
        this.timeTaker = timeTaker;
        this.world = world;
    }

    GameObject createObenImAltenTurm() {
        return create(BETT_OBEN_IM_ALTEN_TURM, OBEN_IM_ALTEN_TURM);
    }

    @NonNull
    private GameObject create(final GameObjectId id, final GameObjectId locationId) {
        final SimpleDescriptionComp descriptionComp =
                new SimpleDescriptionComp(id,
                        np(EIN_BETT, id),
                        np(BETT, id),
                        np(BETT, id));

        final LocationComp locationComp = new LocationComp(
                id, db, world, locationId,
                null, false);

        final StoringPlaceComp storingPlaceComp = new StoringPlaceComp(id, timeTaker, locationComp,
                UNTER_DEM_BETT,
                true, Lichtverhaeltnisse.DAUERHAFT_DUNKEL,
                conData("auf dem Holzboden",
                        "Unter das Bett kriechen",
                        secs(5),
                        this::getDescDrunter),
                conData("im Staub unter dem Bett",
                        "Unter dem Bett hervorkriechen",
                        secs(10),
                        du("kriechst", "etwas mühevoll wieder unter dem",
                                "Bett hervor")
                                .mitVorfeldSatzglied("etwas mühevoll")
                                .undWartest()
                                .dann()));

        return new StoringPlaceObject(id,
                descriptionComp,
                locationComp,
                storingPlaceComp);
    }


    @SuppressWarnings("unchecked")
    @CheckReturnValue
    private TimedDescription<?> getDescDrunter(
            final Known newLocationKnown, final Lichtverhaeltnisse lichtverhaeltnisse) {
        if (((IHasStateGO<RapunzelState>) world.load(RAPUNZEL)).stateComp().hasState(
                PAUSED_BEFORE_HAARE_VOM_TURM_HERUNTERGELASSEN)) {
            if (db.counterDao().get(BETT_DRUNTERKRIECHEN_VERSTECKEN) == 0) {
                return du(SENTENCE, "schaust",
                        "um dich und dein Blick fällt auf das",
                        "Bett", SENTENCE, "schnell kriechst du darunter")
                        .timed(secs(5))
                        .withCounterIdIncrementedIfTextIsNarrated(BETT_DRUNTERKRIECHEN_VERSTECKEN);
            }

            return du("versteckst", "dich eilig",
                    "unters Bett")
                    .mitVorfeldSatzglied("eilig")
                    .undWartest()
                    .dann()
                    .timed(secs(5))
                    .withCounterIdIncrementedIfTextIsNarrated(BETT_DRUNTERKRIECHEN_VERSTECKEN);
        }

        if (db.counterDao().get(BETT_DRUNTERKRIECHEN_OHNE_SINN) == 0) {
            return du("kriechst",
                    "unter das Bett. Hier ist es eng und staubig")
                    .undWartest()
                    .dann()
                    .timed(secs(10))
                    .withCounterIdIncrementedIfTextIsNarrated(BETT_DRUNTERKRIECHEN_OHNE_SINN);
        }

        return du("kriechst",
                "noch einmal unter das Bett")
                .mitVorfeldSatzglied("noch einmal")
                .undWartest()
                .dann()
                .timed(secs(10))
                .withCounterIdIncrementedIfTextIsNarrated(BETT_DRUNTERKRIECHEN_OHNE_SINN);
    }
}
