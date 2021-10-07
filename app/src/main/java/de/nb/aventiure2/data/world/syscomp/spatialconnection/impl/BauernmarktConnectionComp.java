package de.nb.aventiure2.data.world.syscomp.spatialconnection.impl;

import static de.nb.aventiure2.data.world.gameobject.World.*;

import androidx.annotation.NonNull;

import com.google.common.collect.ImmutableList;

import java.util.List;

import javax.annotation.CheckReturnValue;

import de.nb.aventiure2.data.database.AvDatabase;
import de.nb.aventiure2.data.narration.Narrator;
import de.nb.aventiure2.data.time.TimeTaker;
import de.nb.aventiure2.data.world.base.GameObjectId;
import de.nb.aventiure2.data.world.base.Known;
import de.nb.aventiure2.data.world.base.Lichtverhaeltnisse;
import de.nb.aventiure2.data.world.base.SpatialConnection;
import de.nb.aventiure2.data.world.gameobject.*;
import de.nb.aventiure2.data.world.syscomp.spatialconnection.AbstractSpatialConnectionComp;

/**
 * An implementation of {@link AbstractSpatialConnectionComp}
 * for the {@link World#BAUERNMARKT}
 * room.
 */
public class BauernmarktConnectionComp extends AbstractSpatialConnectionComp {

    public BauernmarktConnectionComp(
            final AvDatabase db, final TimeTaker timeTaker,
            final Narrator n, final World world) {
        super(BAUERNMARKT, db, timeTaker, n, world);
    }

    @Override
    public boolean isAlternativeMovementDescriptionAllowed(final GameObjectId to,
                                                           final Known newLocationKnown,
                                                           final Lichtverhaeltnisse lichtverhaeltnisseInNewLocation) {
        return true;
    }

    @Override
    @NonNull
    @CheckReturnValue
    public List<SpatialConnection> getConnections() {
        return ImmutableList.of(
                // FIXME "Den Bauernmarkt verlassen", "Die Marktstände verlassen"
                //  "Die Marktweiber verlassen", "Den kleinen Markt verlassen" ....
        );
    }


    //FIXME einer (alten? armen?) Frau?
    // - "Auf den kleinen Markt gehen" / "Bauernmarkt" / "zu den Marktständen"
    // - "Auf dem kleinen Markt  / Dort sitzen ein paar einfache Leute und halten ihre Waren feil:
    // Eine dicke Bäuerin verkauft Mus, eine schöne junge Frau hat Töpfe und irdenes Geschirr vor
    // sich stehen und eine alte Frau flicht Körbe.

    // FIXME "Du wendest dich der alten Frau zu. Sie arbeitet an einem Korb. Du siehst ihr
    //  genau dabei zu: Sie verdreht immer drei Stränge von Binsen zu einem
    //  Seil, das legt sie dann immer wieder im Kreis, bis daraus ein großes Korb entsteht.


    // FIXME -> "Dich der alten Frau / alten Korbflechterin zuwenden"
    //  Gespräch
    //  - Man könnte sie fragen - sie hat kein Seil mehr, das lang genug ist?!
    //  - man sieht ihr zu (beobachtet sie bei ihrer Tätigkeit) und lernt es dabei?!
    //  ("du schaust ihr genau dabei zu. So schwer sieht
    //   es eigentlich gar nicht aus. Man dreht drei Binsenhalme zusammen... und dann nimmt man
    //   wieder...  Mh-hm,  gut zu wissen!")

    // FIXME "Die Korbflechterin hat ihren Stand gut verschnürt und abgedeckt"

    // FIXME "Du siehst der alten Frau weiter bei der Arbeit zu."
    //  "Es ist ein Freude, der fleißigen Alten zuzusehen."
    //  "Eine langwierige Arbeit!"

    // FIXME "Du schaust, was es sonst noch auf dem Markt zu sehen gibt: "Mus feil", ruft die
    //  dicke Bauersfrau. Die schöne junge Frau sortiert ihre irdenen Näpfe und Töpfe."

    // FIXME "Der Geruch von dem süßen Mus kitzelt dir die Nase"
    //  "Wieder steigt dir der Geruch von dem süßen Mus in die Nase" (Hunger?!)

    // FIXME "Dann wendest du dich wieder der alten Korbflechterin zu"....

    // FIXME "Die schöne junge Frau hat wohl gerade ein Schüsselchen / Tellerchen / irdenes
    //  Schälchen
    //  verkauft"

    // FIXME "Auch die dicke Bäuerin ist nicht mehr da. Die Menschen haben sich verlaufen, und
    //  du stehst allein zwischen den leeren Bänken und Ständen"

    // FIXME "Du verlässt den Markt."
    //  (Zeit / Wetterbeschreibung?!)
}



