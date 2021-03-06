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
import de.nb.aventiure2.data.world.syscomp.storingplace.StoringPlaceComp;
import de.nb.aventiure2.german.base.NomenFlexionsspalte;
import de.nb.aventiure2.german.description.TimedDescription;

import static de.nb.aventiure2.data.time.AvTimeSpan.secs;
import static de.nb.aventiure2.data.world.base.SpatialConnectionData.conData;
import static de.nb.aventiure2.data.world.gameobject.BettgestellFactory.Counter.*;
import static de.nb.aventiure2.data.world.gameobject.World.*;
import static de.nb.aventiure2.data.world.syscomp.storingplace.Geschlossenheit.NACH_OBEN_WEITGEHEND_OFFEN_UND_UNGESCHUETZT;
import static de.nb.aventiure2.data.world.syscomp.storingplace.StoringPlaceComp.LEUCHTET_NIE;
import static de.nb.aventiure2.data.world.syscomp.storingplace.StoringPlaceType.ECKE_IM_BETTGESTELL;
import static de.nb.aventiure2.german.base.Artikel.Typ.INDEF;
import static de.nb.aventiure2.german.base.NomenFlexionsspalte.BETTGESTELL;
import static de.nb.aventiure2.german.base.Nominalphrase.np;
import static de.nb.aventiure2.german.base.StructuralElement.PARAGRAPH;
import static de.nb.aventiure2.german.base.StructuralElement.SENTENCE;
import static de.nb.aventiure2.german.description.DescriptionBuilder.du;

class BettgestellFactory extends AbstractGameObjectFactory {
    @SuppressWarnings({"unused", "RedundantSuppression"})
    enum Counter {
        BETTGESTELL__DESC_IN
    }

    BettgestellFactory(final AvDatabase db,
                       final TimeTaker timeTaker,
                       final World world) {
        super(db, timeTaker, world);
    }

    GameObject createInDerHuetteImWald() {
        return create(BETTGESTELL_IN_DER_HUETTE_IM_WALD, HUETTE_IM_WALD);
    }

    @NonNull
    private GameObject create(final GameObjectId id, final GameObjectId locationId) {
        final SimpleDescriptionComp descriptionComp =
                new SimpleDescriptionComp(id,
                        np(INDEF, NomenFlexionsspalte.BETTGESTELL, id),
                        np(BETTGESTELL, id),
                        np(BETTGESTELL, id));

        final LocationComp locationComp = new LocationComp(
                id, db, world, locationId,
                null, false);

        final StoringPlaceComp storingPlaceComp = new StoringPlaceComp(id, timeTaker, world,
                locationComp,
                ECKE_IM_BETTGESTELL,
                false,
                NACH_OBEN_WEITGEHEND_OFFEN_UND_UNGESCHUETZT,
                LEUCHTET_NIE,
                conData("auf der Bettkante",
                        "In das Bett legen",
                        secs(15),
                        this::getDescIn),
                conData("auf der Bettkante",
                        "Aufstehen",
                        secs(10),
                        du(SENTENCE, "reckst", "dich noch einmal und stehst "
                                + "wieder auf")
                                .dann()));

        return new StoringPlaceObject(id,
                descriptionComp,
                locationComp,
                storingPlaceComp);
    }


    @CheckReturnValue
    private TimedDescription<?> getDescIn(
            final Known newLocationKnown, final Lichtverhaeltnisse lichtverhaeltnisse) {
        if (db.counterDao().get(BETTGESTELL__DESC_IN) == 0) {
            return du(PARAGRAPH, "legst", "dich in das hölzere Bettgestell. "
                    + "Gemütlich ist etwas anderes, aber nach den "
                    + "vielen Schritten tut es sehr gut, sich "
                    + "einmal auszustrecken")
                    .timed(secs(15))
                    .withCounterIdIncrementedIfTextIsNarrated(BETTGESTELL__DESC_IN);
        }

        return du("legst", "dich noch einmal in das Holzbett")
                .mitVorfeldSatzglied("noch einmal")
                .timed(secs(15))
                .undWartest()
                .dann();
    }
}
