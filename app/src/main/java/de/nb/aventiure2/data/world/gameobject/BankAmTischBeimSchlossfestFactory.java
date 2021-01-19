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
import de.nb.aventiure2.data.world.syscomp.location.ILocatableGO;
import de.nb.aventiure2.data.world.syscomp.location.LocationComp;
import de.nb.aventiure2.data.world.syscomp.storingplace.StoringPlaceComp;
import de.nb.aventiure2.german.description.StructuredDescription;
import de.nb.aventiure2.german.description.TimedDescription;
import de.nb.aventiure2.german.praedikat.VerbSubjObj;

import static de.nb.aventiure2.data.time.AvTimeSpan.mins;
import static de.nb.aventiure2.data.world.base.SpatialConnectionData.conData;
import static de.nb.aventiure2.data.world.gameobject.World.*;
import static de.nb.aventiure2.data.world.syscomp.storingplace.StoringPlaceType.NEBEN_SC_AUF_EINER_BANK;
import static de.nb.aventiure2.german.base.Artikel.Typ.INDEF;
import static de.nb.aventiure2.german.base.Nominalphrase.np;
import static de.nb.aventiure2.german.base.NumerusGenus.M;
import static de.nb.aventiure2.german.base.Wortfolge.w;
import static de.nb.aventiure2.german.description.DescriptionBuilder.du;

class BankAmTischBeimSchlossfestFactory {
    private static final String BANK_AM_TISCH_BEIM_SCHLOSSFEST_FACTORY_IN =
            "BankAmTischBeimSchlossfestFactory_In";
    private final AvDatabase db;
    private final TimeTaker timeTaker;
    private final World world;

    BankAmTischBeimSchlossfestFactory(final AvDatabase db,
                                      final TimeTaker timeTaker,
                                      final World world) {
        this.db = db;
        this.timeTaker = timeTaker;
        this.world = world;
    }

    GameObject create() {
        return create(SCHLOSS_VORHALLE_AM_TISCH_BEIM_FEST);
    }

    @NonNull
    private GameObject create(final GameObjectId id) {
        final SimpleDescriptionComp descriptionComp =
                new SimpleDescriptionComp(id,
                        np(M, INDEF, "Platz auf einer "
                                + "Bank an einem Tisch", id),
                        np(M, INDEF, "Platz auf einer "
                                + "Bank an einem Tisch", id),
                        np(M, INDEF, "Platz an einem Tisch", id));

        final LocationComp locationComp = new LocationComp(
                id, db, world,
                // Zunächst sind Bank und Tisch nicht da. Sie werden erst beim
                // Aufbau fürs Schlossfest "aufgebaut".
                null, null, false);

        final StoringPlaceComp storingPlaceComp = new StoringPlaceComp(id, timeTaker, locationComp,
                NEBEN_SC_AUF_EINER_BANK,
                null,
                conData("neben einer Bank an einem Tisch",
                        "An einen Tisch setzen",
                        mins(3),
                        this::getDescIn),
                conData("neben einer Bank an einem Tisch",
                        "Vom Tisch aufstehen",
                        mins(3),
                        this::getDescOut));

        return new StoringPlaceObject(id,
                descriptionComp,
                locationComp,
                storingPlaceComp);
    }

    @CheckReturnValue
    private TimedDescription<?> getDescIn(
            final Known newLocationKnown, final Lichtverhaeltnisse lichtverhaeltnisse) {
        // Der Tisch wird erst jetzt hinzugefügt, damit nicht vorher
        // "Die Kugel auf den Tisch legen" o.Ä. angeboten wird (wenn noch gar kein
        // konkreter Tisch ausgewählt ist).
        // Das Problem gibt es nur, weil es ja eigentlich ganz viele
        // Tische beim Schlossfest gibt.
        ((ILocatableGO) world.load(SCHLOSS_VORHALLE_LANGER_TISCH_BEIM_FEST))
                .locationComp().setLocation(SCHLOSS_VORHALLE_AM_TISCH_BEIM_FEST);

        if (db.counterDao().get(BANK_AM_TISCH_BEIM_SCHLOSSFEST_FACTORY_IN) == 0) {
            return du("ergatterst", w("einen Platz auf einer Bank an einem langen,"
                    + " aus Brettern gezimmerten Tisch.\n"
                    + "Unter einem Baldachin sitzen – soweit du durch das Gedänge "
                    + "erkennen kannst – "
                    + "einige Hofleute an einer Tafel mit "
                    + "goldenen Tellern vor Fasan und anderem Wildbret. "
                    + "Immerhin stellt "
                    + "dir ein eifriger Diener einen leeren Holzteller und einen "
                    + "Löffel bereit")).timed(mins(3))
                    .withCounterIdIncrementedIfTextIsNarrated(
                            BANK_AM_TISCH_BEIM_SCHLOSSFEST_FACTORY_IN);
        }

        return du("suchst", "dir erneut im Gedränge einen Platz an einem Tisch", "erneut")
                .timed(mins(3));
    }

    private StructuredDescription getDescOut(
            final Known newLocationKnown, final Lichtverhaeltnisse lichtverhaeltnisse) {
        // Der Tisch wird wieder entfernt, s.o.
        ((ILocatableGO) world.load(SCHLOSS_VORHALLE_LANGER_TISCH_BEIM_FEST))
                .locationComp().unsetLocation();

        // "du stehst vom Tisch auf"
        return du(VerbSubjObj.AUFSTEHEN_VON
                .mit(world.getDescription(
                        SCHLOSS_VORHALLE_LANGER_TISCH_BEIM_FEST, true)))
                .undWartest()
                .dann();
    }
}
