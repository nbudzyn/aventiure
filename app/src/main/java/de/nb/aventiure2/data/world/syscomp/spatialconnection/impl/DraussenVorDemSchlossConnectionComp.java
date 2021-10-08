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
import static de.nb.aventiure2.data.world.syscomp.state.impl.SchlossfestState.NACH_VERWUESTUNG_WIEDER_GERICHTET_MARKTSTAENDE_GESCHLOSSEN;
import static de.nb.aventiure2.data.world.syscomp.state.impl.SchlossfestState.NACH_VERWUESTUNG_WIEDER_GERICHTET_MARKTSTAENDE_OFFEN;
import static de.nb.aventiure2.german.base.PraepositionMitKasus.AUF_AKK;
import static de.nb.aventiure2.german.base.PraepositionMitKasus.IN_DAT;
import static de.nb.aventiure2.german.base.StructuralElement.PARAGRAPH;
import static de.nb.aventiure2.german.base.StructuralElement.SENTENCE;
import static de.nb.aventiure2.german.description.DescriptionBuilder.du;
import static de.nb.aventiure2.german.description.DescriptionBuilder.neuerSatz;
import static de.nb.aventiure2.german.praedikat.VerbSubj.GEHEN;
import static de.nb.aventiure2.util.StreamUtil.*;

import androidx.annotation.NonNull;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;

import java.util.List;

import javax.annotation.CheckReturnValue;
import javax.annotation.ParametersAreNonnullByDefault;

import de.nb.aventiure2.data.database.AvDatabase;
import de.nb.aventiure2.data.narration.Narrator;
import de.nb.aventiure2.data.time.AvTimeSpan;
import de.nb.aventiure2.data.time.TimeTaker;
import de.nb.aventiure2.data.world.base.GameObjectId;
import de.nb.aventiure2.data.world.base.Known;
import de.nb.aventiure2.data.world.base.Lichtverhaeltnisse;
import de.nb.aventiure2.data.world.base.SpatialConnection;
import de.nb.aventiure2.data.world.gameobject.*;
import de.nb.aventiure2.data.world.syscomp.spatialconnection.AbstractSpatialConnectionComp;
import de.nb.aventiure2.data.world.syscomp.state.IHasStateGO;
import de.nb.aventiure2.data.world.syscomp.state.impl.SchlossfestState;
import de.nb.aventiure2.german.base.NomenFlexionsspalte;
import de.nb.aventiure2.german.description.AbstractDescription;
import de.nb.aventiure2.german.description.AltDescriptionsBuilder;
import de.nb.aventiure2.german.description.TimedDescription;
import de.nb.aventiure2.german.praedikat.AdvAngabeSkopusVerbAllg;
import de.nb.aventiure2.german.praedikat.AdvAngabeSkopusVerbWohinWoher;

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

        if (loadSchlossfest().stateComp()
                .hasState(NACH_VERWUESTUNG_WIEDER_GERICHTET_MARKTSTAENDE_OFFEN,
                        NACH_VERWUESTUNG_WIEDER_GERICHTET_MARKTSTAENDE_GESCHLOSSEN)) {
            res.add(SpatialConnection.conAltDescTimed(BAUERNMARKT,
                    "kurz vor dem Markt",
                    NORTH,
                    this::getActionNameTo_Bauernmarkt,
                    mins(3),
                    this::altDescTo_Bauernmarkt));
        }

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

    private String getActionNameTo_Bauernmarkt() {
        if (loadSchlossfest().stateComp().hasState(
                NACH_VERWUESTUNG_WIEDER_GERICHTET_MARKTSTAENDE_OFFEN)) {
            return "Auf den kleinen Markt gehen";
        }
        return "Zu den Markständen gehen";
    }

    private ImmutableList<TimedDescription<?>> altDescTo_Bauernmarkt(
            final Known known,
            final Lichtverhaeltnisse lichtverhaeltnisse) {
        final ImmutableList.Builder<TimedDescription<?>> res = ImmutableList.builder();

        switch (loadSchlossfest().stateComp().getState()) {
            case NACH_VERWUESTUNG_WIEDER_GERICHTET_MARKTSTAENDE_OFFEN:
                res.addAll(TimedDescription.toTimed(altDescTo_Bauernmarkt_Offen(), mins(5)));
                break;
            case NACH_VERWUESTUNG_WIEDER_GERICHTET_MARKTSTAENDE_GESCHLOSSEN:
                res.addAll(altDescTo_Bauernmarkt_Geschlossen());
                break;
            default:
                throw new IllegalStateException("Unerwarteter Zustand: "
                        + loadSchlossfest().stateComp().getState()
                        + ", erwartet: Markt");
        }

        loadSC().mentalModelComp().setAssumedStateToActual(SCHLOSSFEST);

        return res.build();
    }

    private ImmutableList<AbstractDescription<?>> altDescTo_Bauernmarkt_Offen() {
        if (standbesitzerKnown()) {
            return ImmutableList.of(
                    du("siehst",
                            "dich noch ein wenig auf dem Bauernmarkt um")
                            .dann(),
                    du("schaust",
                            "noch einmal kurz auf dem kleinen Markt vorbei")
                            .dann(),
                    du(GEHEN
                            .mitAdvAngabe(new AdvAngabeSkopusVerbAllg("wieder"))
                            .mitAdvAngabe(new AdvAngabeSkopusVerbWohinWoher(
                                    AUF_AKK.mit(NomenFlexionsspalte.BAUERNMARKT))))
                            .undWartest()
            );
        }

        return ImmutableList.of(
                neuerSatz(SENTENCE, "Auf dem kleinen Markt sitzen ein paar einfache",
                        "Leute und halten ihre Waren feil")
                        .schonLaenger());
    }

    private ImmutableCollection<TimedDescription<?>> altDescTo_Bauernmarkt_Geschlossen() {
        final AvTimeSpan wegZeit = mins(5);

        if (standbesitzerKnown()) {
            return ImmutableList.of(
                    du(PARAGRAPH, "betrittst", "kurz den kleinen Markt; er liegt wie",
                            "ausgestorben da")
                            .schonLaenger()
                            .timed(wegZeit),
                    du("gehst", "kurz einsam zwischen den verlassenen Markständen",
                            "umher")
                            .mitVorfeldSatzglied("kurz")
                            .timed(wegZeit)
            );
        }

        return AltDescriptionsBuilder.altNeueSaetze(
                PARAGRAPH, "Die Marktstände liegen verlassen",
                // "im Mondlicht"
                mapToList(
                        loadWetter().wetterComp().altLichtInDemEtwasLiegt(
                                timeTaker.now().plus(wegZeit),
                                true
                        ),
                        IN_DAT::mit)
        ).timed(wegZeit).build();
    }

    private boolean standbesitzerKnown() {
        return loadSC().memoryComp().isKnown(MUS_VERKAEUFERIN)
                || loadSC().memoryComp().isKnown(TOPF_VERKAEUFERIN)
                || loadSC().memoryComp().isKnown(KORBFLECHTERIN);
    }

    @NonNull
    private IHasStateGO<SchlossfestState> loadSchlossfest() {
        return loadRequired(SCHLOSSFEST);
    }
}
