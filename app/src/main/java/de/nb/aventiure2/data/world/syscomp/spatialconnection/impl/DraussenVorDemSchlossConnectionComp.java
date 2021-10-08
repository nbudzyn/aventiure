package de.nb.aventiure2.data.world.syscomp.spatialconnection.impl;

import static de.nb.aventiure2.data.time.AvTimeSpan.mins;
import static de.nb.aventiure2.data.time.AvTimeSpan.secs;
import static de.nb.aventiure2.data.world.base.SpatialConnection.con;
import static de.nb.aventiure2.data.world.base.SpatialConnection.conNichtSC;
import static de.nb.aventiure2.data.world.gameobject.World.*;
import static de.nb.aventiure2.data.world.syscomp.spatialconnection.CardinalDirection.EAST;
import static de.nb.aventiure2.data.world.syscomp.spatialconnection.CardinalDirection.NORTH;
import static de.nb.aventiure2.data.world.syscomp.spatialconnection.CardinalDirection.WEST;
import static de.nb.aventiure2.data.world.syscomp.spatialconnection.impl.DraussenVorDemSchlossConnectionComp.Counter.SCHLOSS_VORHALLE_FEST_ZUMINDEST_BEGONNEN;
import static de.nb.aventiure2.german.base.StructuralElement.PARAGRAPH;
import static de.nb.aventiure2.german.description.DescriptionBuilder.du;
import static de.nb.aventiure2.german.description.DescriptionBuilder.neuerSatz;

import androidx.annotation.NonNull;

import com.google.common.collect.ImmutableList;

import java.util.List;

import javax.annotation.CheckReturnValue;
import javax.annotation.ParametersAreNonnullByDefault;

import de.nb.aventiure2.data.database.AvDatabase;
import de.nb.aventiure2.data.narration.Narrator;
import de.nb.aventiure2.data.time.TimeTaker;
import de.nb.aventiure2.data.world.base.GameObjectId;
import de.nb.aventiure2.data.world.base.Known;
import de.nb.aventiure2.data.world.base.Lichtverhaeltnisse;
import de.nb.aventiure2.data.world.base.SpatialConnection;
import de.nb.aventiure2.data.world.gameobject.*;
import de.nb.aventiure2.data.world.syscomp.spatialconnection.AbstractSpatialConnectionComp;
import de.nb.aventiure2.data.world.syscomp.state.IHasStateGO;
import de.nb.aventiure2.data.world.syscomp.state.impl.SchlossfestState;
import de.nb.aventiure2.german.description.TimedDescription;

/**
 * An implementation of {@link AbstractSpatialConnectionComp}
 * for the {@link World#DRAUSSEN_VOR_DEM_SCHLOSS}
 * room.
 */
@ParametersAreNonnullByDefault
public class DraussenVorDemSchlossConnectionComp extends AbstractSpatialConnectionComp {
    @SuppressWarnings({"unused", "RedundantSuppression"})
    enum Counter {
        SCHLOSS_VORHALLE_FEST_ZUMINDEST_BEGONNEN
    }

    public DraussenVorDemSchlossConnectionComp(
            final AvDatabase db, final TimeTaker timeTaker,
            final Narrator n, final World world) {
        super(DRAUSSEN_VOR_DEM_SCHLOSS, db, timeTaker, n, world);
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
        final ImmutableList.Builder<SpatialConnection> res = ImmutableList.builder();

        res.add(
                con(SCHLOSS_VORHALLE,
                        "auf der Treppe",
                        WEST, "Das Schloss betreten",
                        secs(90),
                        this::getDescTo_SchlossVorhalle),
                conNichtSC(
                        ZWISCHEN_DEN_HECKEN_VOR_DEM_SCHLOSS_EXTERN,
                        "zwischen den Buchsbaumhecken",
                        NORTH,
                        secs(90)));

        // FIXME Je nach Status: Markt!

        //FIXME einer (alten? armen?) Frau?
        // - "Auf den kleinen Markt gehen" / "Bauernmarkt" / "zu den Marktständen"
        // - "Auf dem kleinen Markt  / Dort sitzen ein paar einfache Leute und halten ihre
        // Waren feil:
        // Eine dicke Bäuerin verkauft Mus, eine schöne junge Frau hat Töpfe und irdenes
        // Geschirr vor
        // sich stehen und eine alte Frau flicht Körbe.

        // FIXME "Du wendest dich der alten Frau zu. Sie arbeitet an einem Korb. Du siehst ihr
        //  genau dabei zu: Sie verdreht immer drei Stränge von Binsen zu einem
        //  Seil, das legt sie dann immer wieder im Kreis, bis daraus ein großes Korb entsteht.


        // FIXME -> "Dich der alten Frau / alten Korbflechterin zuwenden"
        //  Gespräch
        //  - Man könnte sie fragen - sie hat kein Seil mehr, das lang genug ist?!
        //  - man sieht ihr zu (beobachtet sie bei ihrer Tätigkeit) und lernt es dabei?!
        //  ("du schaust ihr genau dabei zu. So schwer sieht
        //   es eigentlich gar nicht aus. Man dreht drei Binsenhalme zusammen... und dann
        //   nimmt man
        //   wieder...  Mh-hm,  gut zu wissen!")

        // FIXME "Die Korbflechterin hat ihren Stand gut verschnürt und abgedeckt"

        // FIXME "Du siehst der alten Frau weiter bei der Arbeit zu."
        //  "Es ist ein Freude, der fleißigen Alten zuzusehen."
        //  "Eine langwierige Arbeit!"

        // FIXME "Du schaust, was es sonst noch auf dem Markt zu sehen gibt: "Mus feil", ruft
        //  die
        //  dicke Bauersfrau. Die schöne junge Frau sortiert ihre irdenen Näpfe und Töpfe."

        // FIXME "Der Geruch von dem süßen Mus kitzelt dir die Nase"
        //  "Wieder steigt dir der Geruch von dem süßen Mus in die Nase" (Hunger?!)

        // FIXME "Dann wendest du dich wieder der alten Korbflechterin zu"....

        // FIXME "Die schöne junge Frau hat wohl gerade ein Schüsselchen / Tellerchen / irdenes
        //  Schälchen
        //  verkauft"

        // FIXME "Auch die dicke Bäuerin ist nicht mehr da. Die Menschen haben sich
        //  verlaufen, und
        //  du stehst allein zwischen den leeren Bänken und Ständen"


        res.add(
                con(IM_WALD_NAHE_DEM_SCHLOSS,
                        "auf dem Weg",
                        EAST,
                        "In den Wald gehen",
                        mins(10),
                        du("folgst",
                                "einem Weg in den Wald.",
                                "Nach ein paar Schritten führt linker Hand ein schmaler Pfad",
                                "einen Hügel hinauf"),
                        neuerSatz("Jeder kennt die Geschichten, die man "
                                + "sich über den Wald erzählt: Räuber sind noch "
                                + "die kleinste Gefahr. Aber das schreckt dich ganz "
                                + "offenbar nicht und du folgst dem erstbesten "
                                + "Weg hinein in den dunklen Wald. Schon nach ein paar "
                                + "Schritten "
                                + "führt linker Hand ein schmaler, dunkler Pfad einen Hügel "
                                + "hinauf")
                                .timed(mins(12)),
                        du("läufst",
                                "wieder in den dunklen Wald")
                                .mitVorfeldSatzglied("wieder")
                                .dann(),
                        du("läufst",
                                "wieder in den dunklen Wald")
                                .mitVorfeldSatzglied("wieder")
                                .dann()));

        return res.build();
    }

    private TimedDescription<?> getDescTo_SchlossVorhalle(
            final Known newLocationKnown, final Lichtverhaeltnisse lichtverhaeltnisse) {
        final IHasStateGO<SchlossfestState> schlossfest = loadRequired(SCHLOSSFEST);
        switch (schlossfest.stateComp().getState()) {
            case NOCH_NICHT_BEGONNEN:
                return getDescTo_SchlossVorhalle_KeinFest();
            case BEGONNEN:
                return getDescTo_SchlossVorhalle_FestBegonnen();
            case VERWUESTET:
                // fall-through
            case NACH_VERWUESTUNG_WIEDER_GERICHTET_MARKTSTAENDE_OFFEN:
                // fall-through
            case NACH_VERWUESTUNG_WIEDER_GERICHTET_MARKTSTAENDE_GESCHLOSSEN:
                return getDescTo_SchlossVorhalle_FestZumindestVerwuestet();
            default:
                throw new IllegalStateException("Unexpected Schlossfest state: "
                        + schlossfest.stateComp().getState());
        }
    }

    @NonNull
    private static TimedDescription<?>
    getDescTo_SchlossVorhalle_KeinFest() {
        return du("gehst", "wieder hinein in das Schloss").timed(mins(1))
                .undWartest()
                .dann();
    }

    private TimedDescription<?> getDescTo_SchlossVorhalle_FestBegonnen() {
        if (db.counterDao().get(SCHLOSS_VORHALLE_FEST_ZUMINDEST_BEGONNEN) == 0) {
            return neuerSatz(PARAGRAPH,
                    "Vor dem Schloss gibt es ein großes Gedränge und es dauert",
                    "eine Weile, bis",
                    "die Menge dich hineinschiebt. Die prächtige Vorhalle steht voller",
                    "Tische, auf denen in großen Schüsseln Eintöpfe dampfen")
                    .timed(mins(7))
                    .withCounterIdIncrementedIfTextIsNarrated(
                            SCHLOSS_VORHALLE_FEST_ZUMINDEST_BEGONNEN)
                    .komma();
        }

        return getDescTo_SchlossVorhalle_FestZumindestBegonnen_Wiederholung();
    }

    private TimedDescription<?> getDescTo_SchlossVorhalle_FestZumindestVerwuestet() {
        if (db.counterDao().get(SCHLOSS_VORHALLE_FEST_ZUMINDEST_BEGONNEN) == 0) {
            return du("gehst", "die Treppe zum Schloss hinauf;",
                    "drinnen empfängt dich großes Gedränge. Die prächtige Vorhalle steht voller",
                    "Tische, auf denen in großen Schüsseln Eintöpfe dampfen")
                    .timed(mins(3))
                    .withCounterIdIncrementedIfTextIsNarrated(
                            SCHLOSS_VORHALLE_FEST_ZUMINDEST_BEGONNEN)
                    .komma();
        }

        return getDescTo_SchlossVorhalle_FestZumindestBegonnen_Wiederholung();
    }

    private static TimedDescription<?> getDescTo_SchlossVorhalle_FestZumindestBegonnen_Wiederholung() {
        return du("betrittst", "wieder das Schloss")
                .mitVorfeldSatzglied("wieder")
                .timed(mins(2))
                .undWartest()
                .dann();
    }

    @NonNull
    private IHasStateGO<SchlossfestState> loadSchlossfest() {
        return loadRequired(SCHLOSSFEST);
    }
}
