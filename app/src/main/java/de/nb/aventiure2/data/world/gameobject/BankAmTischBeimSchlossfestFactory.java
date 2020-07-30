package de.nb.aventiure2.data.world.gameobject;

import androidx.annotation.NonNull;

import de.nb.aventiure2.data.database.AvDatabase;
import de.nb.aventiure2.data.world.base.GameObject;
import de.nb.aventiure2.data.world.base.GameObjectId;
import de.nb.aventiure2.data.world.base.Known;
import de.nb.aventiure2.data.world.base.Lichtverhaeltnisse;
import de.nb.aventiure2.data.world.syscomp.description.impl.SimpleDescriptionComp;
import de.nb.aventiure2.data.world.syscomp.location.ILocatableGO;
import de.nb.aventiure2.data.world.syscomp.location.LocationComp;
import de.nb.aventiure2.data.world.syscomp.storingplace.StoringPlaceComp;
import de.nb.aventiure2.german.base.AbstractDescription;

import static de.nb.aventiure2.data.world.base.SpatialConnectionData.conData;
import static de.nb.aventiure2.data.world.gameobject.World.SCHLOSS_VORHALLE_AM_TISCH_BEIM_FEST;
import static de.nb.aventiure2.data.world.gameobject.World.SCHLOSS_VORHALLE_LANGER_TISCH_BEIM_FEST;
import static de.nb.aventiure2.data.world.syscomp.storingplace.StoringPlaceType.NEBEN_SC_AUF_EINER_BANK;
import static de.nb.aventiure2.data.world.time.AvTimeSpan.mins;
import static de.nb.aventiure2.german.base.DuDescription.du;
import static de.nb.aventiure2.german.base.Nominalphrase.np;
import static de.nb.aventiure2.german.base.NumerusGenus.M;

public class BankAmTischBeimSchlossfestFactory {
    private final AvDatabase db;
    private final World world;

    BankAmTischBeimSchlossfestFactory(final AvDatabase db,
                                      final World world) {
        this.db = db;
        this.world = world;
    }

    GameObject create(final boolean dauerhaftBeleuchtet) {
        return create(SCHLOSS_VORHALLE_AM_TISCH_BEIM_FEST, dauerhaftBeleuchtet);
    }

    @NonNull
    private GameObject create(final GameObjectId id,
                              final boolean dauerhaftBeleuchtet) {


        final SimpleDescriptionComp descriptionComp =
                new SimpleDescriptionComp(id,
                        np(M, "ein Platz auf einer Bank an einem Tisch",
                                "einem Platz auf einer Bank an einem Tisch",
                                "einen Platz auf einer Bank an einem Tisch"),
                        np(M, "ein Platz auf einer Bank an einem Tisch",
                                "einem Platz auf einer Bank an einem Tisch",
                                "einen Platz auf einer Bank an einem Tisch"),
                        np(M, "ein Platz an einem Tisch",
                                "einem Platz an einem Tisch",
                                "einen Platz an einem Tisch"));

        final LocationComp locationComp = new LocationComp(
                id, db, world,
                // Zunächst sind Bank und Tisch nicht da. Sie werden erst beim
                // Aufbau fürs Schlossfest "aufgebaut".
                null, null, false);

        final StoringPlaceComp storingPlaceComp = new StoringPlaceComp(
                id, db, NEBEN_SC_AUF_EINER_BANK,
                dauerhaftBeleuchtet,
                conData("neben einer Bank an einem Tisch",
                        "An einen Tisch setzen",
                        mins(3),
                        this::getDescIn),
                conData("neben einer Bank an einem Tisch",
                        "Vom Tisch aufstehen",
                        mins(3),
                        this::getDescOut)
        );

        return new StoringPlaceObject(id,
                descriptionComp,
                locationComp,
                storingPlaceComp);
    }

    private AbstractDescription<?> getDescIn(
            final Known newLocationKnown, final Lichtverhaeltnisse lichtverhaeltnisse) {
        // Der Tisch wird erst jetzt hinzugefügt, damit nicht vorher
        // "Die Kugel auf den Tisch legen" o.Ä. angeboten wird (wenn noch gar kein
        // konkreter Tisch ausgewählt ist).
        // Das Problem gibt es nur, weil es ja eigentlich ganz viele
        // Tische beim Schlossfest gibt.
        ((ILocatableGO) world.load(SCHLOSS_VORHALLE_LANGER_TISCH_BEIM_FEST))
                .locationComp().setLocation(SCHLOSS_VORHALLE_AM_TISCH_BEIM_FEST);

        if (db.counterDao().incAndGet(
                "BankAmTischBeimSchlossfestFactory_In")
                == 1) {
            return du("ergatterst", "einen Platz auf einer Bank an einem langen,"
                    + " aus Brettern gezimmerten Tisch.\n"
                    + "Unter einem Baldachin sitzen – soweit du durch das Gedänge "
                    + "erkennen kannst – "
                    + "einige Hofleute an einer Tafel mit "
                    + "goldenen Tellern vor Fasan und anderem Wildbret. "
                    + "Immerhin stellt "
                    + "dir ein eifriger Diener einen leeren Holzteller und einen "
                    + "Löffel bereit", mins(3));
        }

        return du("suchst", "dir erneut im Gedränge einen Platz an einem Tisch", "erneut", mins(3));
    }

    private AbstractDescription<?> getDescOut(
            final Known newLocationKnown, final Lichtverhaeltnisse lichtverhaeltnisse) {
        // Der Tisch wird wieder entfernt, s.o.
        ((ILocatableGO) world.load(SCHLOSS_VORHALLE_LANGER_TISCH_BEIM_FEST))
                .locationComp().unsetLocation();

        return du("stehst", "vom Tisch auf", mins(3))
                .undWartest()
                .dann();
    }
}