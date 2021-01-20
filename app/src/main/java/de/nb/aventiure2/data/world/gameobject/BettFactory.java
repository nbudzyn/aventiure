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
import de.nb.aventiure2.german.description.TimedDescription;

import static de.nb.aventiure2.data.time.AvTimeSpan.secs;
import static de.nb.aventiure2.data.world.base.SpatialConnectionData.conData;
import static de.nb.aventiure2.data.world.gameobject.World.*;
import static de.nb.aventiure2.data.world.syscomp.storingplace.StoringPlaceType.ECKE_IM_BETTGESTELL;
import static de.nb.aventiure2.german.base.Artikel.Typ.DEF;
import static de.nb.aventiure2.german.base.Artikel.Typ.INDEF;
import static de.nb.aventiure2.german.base.Nominalphrase.np;
import static de.nb.aventiure2.german.base.NumerusGenus.N;
import static de.nb.aventiure2.german.base.StructuralElement.PARAGRAPH;
import static de.nb.aventiure2.german.base.StructuralElement.SENTENCE;
import static de.nb.aventiure2.german.description.DescriptionBuilder.du;

class BettFactory {
    private static final String BETT__DESC_IN = "Bett__DescIn";
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

    GameObject createInDerHuetteImWald() {
        return create(BETT_IN_DER_HUETTE_IM_WALD, HUETTE_IM_WALD);
    }

    @NonNull
    private GameObject create(final GameObjectId id, final GameObjectId locationId) {
        final SimpleDescriptionComp descriptionComp =
                new SimpleDescriptionComp(id,
                        np(N, INDEF, "Bettgestell", id),
                        np(N, DEF, "Bettgestell", id),
                        np(N, DEF, "Bettgestell", id));

        final LocationComp locationComp = new LocationComp(
                id, db, world, locationId,
                null, false);

        final StoringPlaceComp storingPlaceComp = new StoringPlaceComp(id, timeTaker, locationComp,
                ECKE_IM_BETTGESTELL,
                null,
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
        if (db.counterDao().get(BETT__DESC_IN) == 0) {
            return du(PARAGRAPH, "legst", "dich in das hölzere Bettgestell. "
                    + "Gemütlich ist etwas anderes, aber nach den "
                    + "vielen Schritten tut es sehr gut, sich "
                    + "einmal auszustrecken")
                    .timed(secs(15))
                    .withCounterIdIncrementedIfTextIsNarrated(BETT__DESC_IN);
        }

        return du("legst", "dich noch einmal in das Holzbett").mitVorfeldSatzglied("noch einmal")
                .timed(secs(15))
                .undWartest()
                .dann();
    }
}
