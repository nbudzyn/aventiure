package de.nb.aventiure2.data.world.syscomp.spatialconnection.impl;

import static com.google.common.collect.ImmutableSet.toImmutableSet;
import static de.nb.aventiure2.data.time.AvTimeSpan.mins;
import static de.nb.aventiure2.data.time.Tageszeit.NACHTS;
import static de.nb.aventiure2.data.world.base.Known.KNOWN_FROM_DARKNESS;
import static de.nb.aventiure2.data.world.base.Known.UNKNOWN;
import static de.nb.aventiure2.data.world.base.Lichtverhaeltnisse.DUNKEL;
import static de.nb.aventiure2.data.world.base.Lichtverhaeltnisse.HELL;
import static de.nb.aventiure2.data.world.base.SpatialConnection.con;
import static de.nb.aventiure2.data.world.base.SpatialConnection.conAltDescTimed;
import static de.nb.aventiure2.data.world.gameobject.World.*;
import static de.nb.aventiure2.data.world.syscomp.feelings.Mood.BETRUEBT;
import static de.nb.aventiure2.data.world.syscomp.feelings.Mood.ETWAS_GEKNICKT;
import static de.nb.aventiure2.data.world.syscomp.feelings.Mood.ZUFRIEDEN;
import static de.nb.aventiure2.data.world.syscomp.spatialconnection.CardinalDirection.EAST;
import static de.nb.aventiure2.data.world.syscomp.spatialconnection.CardinalDirection.NORTH;
import static de.nb.aventiure2.data.world.syscomp.spatialconnection.CardinalDirection.WEST;
import static de.nb.aventiure2.data.world.syscomp.spatialconnection.impl.ImWaldNaheDemSchlossConnectionComp.Counter.NACH_DRAUSSEN_KEIN_FEST;
import static de.nb.aventiure2.data.world.syscomp.state.impl.SchlossfestState.BEGONNEN;
import static de.nb.aventiure2.data.world.syscomp.state.impl.SchlossfestState.NACH_VERWUESTUNG_WIEDER_GERICHTET_MARKTSTAENDE_GESCHLOSSEN;
import static de.nb.aventiure2.data.world.syscomp.state.impl.SchlossfestState.NACH_VERWUESTUNG_WIEDER_GERICHTET_MARKTSTAENDE_OFFEN;
import static de.nb.aventiure2.data.world.syscomp.state.impl.SchlossfestState.NOCH_NICHT_BEGONNEN;
import static de.nb.aventiure2.data.world.syscomp.state.impl.SchlossfestState.VERWUESTET;
import static de.nb.aventiure2.german.base.NomenFlexionsspalte.SCHLOSSGARTEN;
import static de.nb.aventiure2.german.base.PraepositionMitKasus.IN_AKK;
import static de.nb.aventiure2.german.base.PraepositionMitKasus.IN_DAT;
import static de.nb.aventiure2.german.base.StructuralElement.PARAGRAPH;
import static de.nb.aventiure2.german.base.StructuralElement.SENTENCE;
import static de.nb.aventiure2.german.description.AltDescriptionsBuilder.altNeueSaetze;
import static de.nb.aventiure2.german.description.AltTimedDescriptionsBuilder.altTimed;
import static de.nb.aventiure2.german.description.DescriptionBuilder.du;
import static de.nb.aventiure2.german.description.DescriptionBuilder.neuerSatz;
import static de.nb.aventiure2.german.praedikat.VerbSubj.KOMMEN;
import static de.nb.aventiure2.german.praedikat.VerbSubj.STEHEN;
import static de.nb.aventiure2.util.StreamUtil.*;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;

import java.util.List;

import javax.annotation.ParametersAreNonnullByDefault;

import de.nb.aventiure2.data.database.AvDatabase;
import de.nb.aventiure2.data.narration.Narrator;
import de.nb.aventiure2.data.time.AvDateTime;
import de.nb.aventiure2.data.time.AvTimeSpan;
import de.nb.aventiure2.data.time.TimeTaker;
import de.nb.aventiure2.data.world.base.GameObjectId;
import de.nb.aventiure2.data.world.base.Known;
import de.nb.aventiure2.data.world.base.Lichtverhaeltnisse;
import de.nb.aventiure2.data.world.base.SpatialConnection;
import de.nb.aventiure2.data.world.gameobject.*;
import de.nb.aventiure2.data.world.syscomp.location.ILocatableGO;
import de.nb.aventiure2.data.world.syscomp.spatialconnection.AbstractSpatialConnectionComp;
import de.nb.aventiure2.data.world.syscomp.state.IHasStateGO;
import de.nb.aventiure2.data.world.syscomp.state.impl.FroschprinzState;
import de.nb.aventiure2.data.world.syscomp.state.impl.SchlossfestState;
import de.nb.aventiure2.german.base.PraepositionMitKasus;
import de.nb.aventiure2.german.base.Praepositionalphrase;
import de.nb.aventiure2.german.description.AltTimedDescriptionsBuilder;
import de.nb.aventiure2.german.description.TimedDescription;
import de.nb.aventiure2.german.praedikat.AdvAngabeSkopusSatz;
import de.nb.aventiure2.german.praedikat.AdvAngabeSkopusVerbAllg;
import de.nb.aventiure2.german.praedikat.AdvAngabeSkopusVerbWohinWoher;
import de.nb.aventiure2.german.satz.Konditionalsatz;
import de.nb.aventiure2.german.satz.Satz;

/**
 * An implementation of {@link AbstractSpatialConnectionComp}
 * for the {@link World#IM_WALD_NAHE_DEM_SCHLOSS}
 * room.
 */
@ParametersAreNonnullByDefault
public class ImWaldNaheDemSchlossConnectionComp extends AbstractSpatialConnectionComp {
    @SuppressWarnings({"unused", "RedundantSuppression"})
    public
    enum Counter {
        NACH_DRAUSSEN_KEIN_FEST
    }

    public ImWaldNaheDemSchlossConnectionComp(
            final AvDatabase db,
            final TimeTaker timeTaker,
            final Narrator n, final World world) {
        super(IM_WALD_NAHE_DEM_SCHLOSS, db, timeTaker, n, world);
    }

    @Override
    public boolean isAlternativeMovementDescriptionAllowed(final GameObjectId to,
                                                           final Known newLocationKnown,
                                                           final Lichtverhaeltnisse lichtverhaeltnisseInNewLocation) {
        return !to.equals(DRAUSSEN_VOR_DEM_SCHLOSS)
                || getAssumedSchlossfestState() == loadSchlossfest().stateComp().getState();
    }

    @NonNull
    private IHasStateGO<SchlossfestState> loadSchlossfest() {
        return loadRequired(SCHLOSSFEST);
    }

    @NonNull
    @Override
    public List<SpatialConnection> getConnections() {
        return ImmutableList.of(
                conAltDescTimed(DRAUSSEN_VOR_DEM_SCHLOSS,
                        "auf dem Weg aus dem Wald",
                        WEST, "Den Wald verlassen und in den Schlossgarten gehen",
                        mins(10),
                        this::altDescTo_DraussenVorDemSchloss),
                SpatialConnection.conAltDescTimed(VOR_DEM_ALTEN_TURM,
                        "auf dem schmalen Pfad den Hügel hinauf",
                        NORTH, this::getActionNameTo_VorDemAltenTurm,
                        mins(25),
                        this::getDescTo_VorDemAltenTurm),
                con(ABZWEIG_IM_WALD,
                        "auf dem Weg in den Wald hinein",
                        EAST,
                        "Tiefer in den Wald hineingehen",
                        mins(5),
                        du(PARAGRAPH, "gehst",
                                "den Weg weiter in den Wald hinein. "
                                        + "Nicht lang, und es geht zur Linken zwischen "
                                        + "den Bäumen ein alter, düsterer Weg ab, über "
                                        + "den Farn wuchert")
                                .komma(),
                        du("kommst", "an den farnüberwachsenen Abzweig")
                                .undWartest()
                ));
    }

    private ImmutableCollection<TimedDescription<?>> altDescTo_DraussenVorDemSchloss(
            final Known newLocationKnown, final Lichtverhaeltnisse lichtverhaeltnisse) {
        final ImmutableCollection<TimedDescription<?>> res;

        switch (loadSchlossfest().stateComp().getState()) {
            case NOCH_NICHT_BEGONNEN:
                res = altDescTo_DraussenVorDemSchloss_KeinFest();
                break;
            case BEGONNEN:
                res = ImmutableList.of(getDescTo_DraussenVorDemSchloss_FestBegonnen());
                break;
            case VERWUESTET:
                res = ImmutableList.of(getDescTo_DraussenVorDemSchloss_FestVerwuestet());
                break;
            case NACH_VERWUESTUNG_WIEDER_GERICHTET_MARKTSTAENDE_OFFEN:
                res = altDescTo_DraussenVorDemSchloss_GerichtetMarktstaendeOffen();
                break;
            case NACH_VERWUESTUNG_WIEDER_GERICHTET_MARKTSTAENDE_GESCHLOSSEN:
                res = altDescTo_DraussenVorDemSchloss_GerichtetMarktstaendeGeschlossen();
                break;
            default:
                throw new IllegalStateException("Unexpected state: "
                        + loadSchlossfest().stateComp().getState());
        }

        loadSC().mentalModelComp().setAssumedStateToActual(SCHLOSSFEST);

        return res;
    }

    @NonNull
    private ImmutableCollection<TimedDescription<?>> altDescTo_DraussenVorDemSchloss_KeinFest() {
        final AvDateTime now = timeTaker.now();
        if (now.getTageszeit() == NACHTS && db.counterDao().get(NACH_DRAUSSEN_KEIN_FEST) % 3 == 0) {
            final AvTimeSpan wegzeit = mins(15);
            // Wetterhinweis muss ausgegeben werden!
            return world.loadWetter().wetterComp().altWetterhinweiseWoDraussen(
                    now.plus(wegzeit), DRAUSSEN_VOR_DEM_SCHLOSS).stream()
                    .map(p ->
                            du(SENTENCE, "gehst",
                                    "noch eine Weile vorsichtig durch",
                                    "den dunklen",
                                    "Wald, dann öffnet sich der Weg wieder und du stehst",
                                    "im Schlossgarten",
                                    p.getDescription(duSc()) // "unter dem Sternenhimmel"
                            )
                                    .mitVorfeldSatzglied("noch eine Weile")
                                    .schonLaenger()
                                    .timed(wegzeit)
                                    .withCounterIdIncrementedIfTextIsNarrated(
                                            NACH_DRAUSSEN_KEIN_FEST))
                    .collect(toImmutableSet());
        }

        final AltTimedDescriptionsBuilder alt = altTimed();

        final AvTimeSpan wegzeit = mins(10);
        alt.addAll(world.loadWetter().wetterComp().altLichtInDemEtwasLiegt(
                now.plus(wegzeit), true).stream()
                .map(licht ->
                        du("erreichst", "bald",
                                licht.nomK(), // "das helle Tageslicht"
                                ", in",
                                licht.relPron().datK(), // "dem"
                                "der Schlossgarten liegt").schonLaenger()
                                .mitVorfeldSatzglied("bald")
                                .timed(wegzeit)
                                .withCounterIdIncrementedIfTextIsNarrated(
                                        NACH_DRAUSSEN_KEIN_FEST)
                                .undWartest()
                                .komma()));

        alt.add(du("erreichst", "bald den Schlossgarten")
                .schonLaenger()
                .mitVorfeldSatzglied("bald")
                .timed(wegzeit)
                .withCounterIdIncrementedIfTextIsNarrated(NACH_DRAUSSEN_KEIN_FEST)
                .undWartest());

        return alt.build();
    }

    @NonNull
    private TimedDescription<?> getDescTo_DraussenVorDemSchloss_FestBegonnen() {
        if (!loadSC().mentalModelComp().hasAssumedState(SCHLOSSFEST, BEGONNEN)) {
            return du("bist", "von dem Lärm überrascht, der dir",
                    "schon von weitem",
                    "entgegenschallt. Als du aus dem Wald heraustrittst,",
                    "ist der Anblick überwältigend:",
                    "Überall im Schlossgarten stehen kleine Pagoden",
                    "in lustigen Farben. Kinder werden auf Kähnen durch Kanäle",
                    "gestakt und aus dem Schloss duftet es verführerisch nach",
                    "Gebratenem", PARAGRAPH).schonLaenger().timed(mins(10));
        }

        return neuerSatz("Das Schlossfest ist immer noch in vollem Gange")
                .schonLaenger()
                .timed(mins(10));
    }

    private TimedDescription<?> getDescTo_DraussenVorDemSchloss_FestVerwuestet() {
        @Nullable final SchlossfestState assumedSchlossfestState = getAssumedSchlossfestState();

        if (assumedSchlossfestState == null || assumedSchlossfestState == NOCH_NICHT_BEGONNEN) {
            loadSC().feelingsComp().requestMoodMax(BETRUEBT);

            return du("bist",
                    "betroffen, als du aus dem Wald heraustrittst.",
                    "Das Schlossfest hat begonnen, aber der Sturm hat heftig gewütet:",
                    "Viele der kleinen farbigen Pagoden überall im Schlossgarten sind "
                            + "umgeworfen",
                    "oder ihr Dach ist abgerissen. Einige Marktstände sind ausgeräumt oder",
                    "stehen aufwendig verzurrt an windgeschützten Plätzen. –",
                    "Aus dem Schloss allerdings hört man die Menschenmenge und es duftet",
                    "verführerisch nach Gebratenem")
                    .schonLaenger().timed(mins(10));
        }
        if (assumedSchlossfestState == BEGONNEN) {
            loadSC().feelingsComp().requestMoodMax(BETRUEBT);

            return neuerSatz(
                    "Als du aus dem Wald heraustrittst, bietet sich dir ein trauriges Bild.",
                    "Der Sturm hat im Schlossgarten heftig gewütet, viele der Pagoden sind",
                    "umgeworfen oder ihre Dächer abgerissen. Einzelne Marktstände sind",
                    "ausgeräumt oder stehen aufwendig verzurrt an windgeschützten Plätzen. –",
                    "Aus dem Schloss aber hört man immer noch die Menschenmenge")
                    .schonLaenger().timed(mins(10));
        }

        loadSC().feelingsComp().requestMoodMax(ETWAS_GEKNICKT);

        return du("erreichst", "bald den vom Sturm verwüsteten Schlossgarten")
                .schonLaenger()
                .mitVorfeldSatzglied("bald")
                .timed(mins(10))
                .undWartest();
    }

    private ImmutableCollection<TimedDescription<?>>
    altDescTo_DraussenVorDemSchloss_GerichtetMarktstaendeOffen() {
        final AvTimeSpan wegZeit = mins(10);

        @Nullable final SchlossfestState assumedSchlossfestState = getAssumedSchlossfestState();

        if (assumedSchlossfestState == null || assumedSchlossfestState == NOCH_NICHT_BEGONNEN
                || assumedSchlossfestState == BEGONNEN) {
            return ImmutableList.of(neuerSatz(
                    PARAGRAPH,
                    "im Schlossgarten herrscht reges Treiben.",
                    "In einer Ecke hat sich",
                    "ein kleiner Bauernmarkt aufgebaut")
                    .schonLaenger().timed(wegZeit));
        }
        if (assumedSchlossfestState == VERWUESTET) {
            loadSC().feelingsComp().requestMoodMin(ZUFRIEDEN);

            return ImmutableList.of(neuerSatz(
                    PARAGRAPH,
                    "im Schlossgarten herrscht wieder reges Treiben. Die Diener haben",
                    "die vom Sturm zerstörten Pagoden abgeräumt; in einer Ecke hat sich ein",
                    "kleiner Bauernmarkt aufgebaut")
                    .schonLaenger()
                    .timed(wegZeit));
        }
        if (assumedSchlossfestState
                == NACH_VERWUESTUNG_WIEDER_GERICHTET_MARKTSTAENDE_GESCHLOSSEN) {
            return altNeueSaetze(altWiederImSchlossgartenWetterHinweisSaetze(wegZeit),
                    // "als du wieder im Schlossgarten stehst, ist es hellichter Tag[.]"
                    SENTENCE, "der kleine Bauernmarkt ist jetzt geöffnet")
                    .schonLaenger()
                    .timed(wegZeit)
                    .build();
        }

        return ImmutableList.of(neuerSatz("Nachdem du einige Schritte gelaufen bist,",
                "stehst du wieder im Schlossgarten")
                .schonLaenger()
                .timed(wegZeit));
    }

    private ImmutableCollection<TimedDescription<?>>
    altDescTo_DraussenVorDemSchloss_GerichtetMarktstaendeGeschlossen() {
        final AvTimeSpan wegZeit = mins(10);

        @Nullable final SchlossfestState assumedSchlossfestState = getAssumedSchlossfestState();

        if (assumedSchlossfestState == null || assumedSchlossfestState == NOCH_NICHT_BEGONNEN
                || assumedSchlossfestState == BEGONNEN) {
            return altNeueSaetze("der Schlossgarten liegt",
                    mapToList(world.loadWetter().wetterComp().altLichtInDemEtwasLiegt(
                            timeTaker.now().plus(wegZeit), true),
                            PraepositionMitKasus.IN_DAT::mit),
                    SENTENCE,
                    "In einer Ecke stehen einige verlassene Marktstände")
                    .schonLaenger().timed(wegZeit)
                    .build();
        }
        if (assumedSchlossfestState == VERWUESTET) {
            loadSC().feelingsComp().requestMoodMin(ZUFRIEDEN);

            return altNeueSaetze(
                    "der Schlossgarten liegt",
                    mapToList(world.loadWetter().wetterComp().altLichtInDemEtwasLiegt(
                            timeTaker.now(), true),
                            PraepositionMitKasus.IN_DAT::mit),
                    SENTENCE,
                    "die Diener haben",
                    "die vom Sturm zerstörten Pagoden abgeräumt",
                    SENTENCE,
                    "in einer Ecke stehen einige verlassene Marktstände")
                    .schonLaenger()
                    .timed(wegZeit)
                    .build();
        }
        if (assumedSchlossfestState
                == NACH_VERWUESTUNG_WIEDER_GERICHTET_MARKTSTAENDE_OFFEN) {
            return altNeueSaetze(altWiederImSchlossgartenWetterHinweisSaetze(wegZeit),
                    // "als du wieder im Schlossgarten stehst, ist es dunkel[.]"
                    SENTENCE, "der kleine Markt liegt verlassen da")
                    .schonLaenger()
                    .timed(wegZeit)
                    .build();
        }

        return ImmutableList.of(neuerSatz("Nachdem du einige Schritte gelaufen bist,",
                "stehst du wieder im Schlossgarten")
                .schonLaenger()
                .timed(wegZeit));
    }

    /**
     * Gibt alternative Sätze zurück wie "Als du wieder im Schlossgarten stehst, ist es dunkel" oder
     * "Du kommst wieder in den Schlossgarten". Der Aufrufer muss dafür sorgen, dass einer
     * dieser Sätze ausgegeben wird, denn ggf. vermerkt diese Methode, dass der Spieler über das
     * aktuelle Wetter informiert wurde.
     */
    @NonNull
    private ImmutableCollection<Satz> altWiederImSchlossgartenWetterHinweisSaetze(
            final AvTimeSpan wegZeit) {
        final ImmutableCollection<Satz> altSpWetterhinweisSaetze =
                world.loadWetter().wetterComp().altSpWetterhinweisSaetze(
                        timeTaker.now().plus(wegZeit), DRAUSSEN_VOR_DEM_SCHLOSS,
                        // Damit vermeidet man "temporalsemantische Doppelbesetzungen" wie
                        // "Als du in den Schlossgarten kommst, ist heute schönes Wetter".
                        true);

        if (altSpWetterhinweisSaetze.isEmpty()) {
            // "du kommst wieder in den Schlossgarten"
            return ImmutableList.of(
                    // "du kommst wieder in den Schlossgarten"
                    KOMMEN.mitAdvAngabe(new AdvAngabeSkopusVerbAllg("wieder"))
                            .mitAdvAngabe(new AdvAngabeSkopusVerbWohinWoher(
                                    IN_AKK.mit(SCHLOSSGARTEN)))
                            .alsSatzMitSubjekt(duSc())
            );
        }

        // "als du wieder im Schlossgarten stehst, ist es dunkel"
        return mapToList(altSpWetterhinweisSaetze,
                // "als du wieder im Schlossgarten stehst, ist es dunkel"
                s -> s.mitAngabensatz(
                        new Konditionalsatz("als",
                                STEHEN
                                        .mitAdvAngabe(new AdvAngabeSkopusSatz("wieder"))
                                        .mitAdvAngabe(new AdvAngabeSkopusVerbAllg(
                                                IN_DAT.mit(SCHLOSSGARTEN)))
                                        .alsSatzMitSubjekt(duSc())),
                        true));
    }

    @Nullable
    private SchlossfestState getAssumedSchlossfestState() {
        return (SchlossfestState) loadSC().mentalModelComp().getAssumedState(SCHLOSSFEST);
    }

    private String getActionNameTo_VorDemAltenTurm() {
        if (loadSC().memoryComp().isKnown(VOR_DEM_ALTEN_TURM)) {
            return "Den langen schmalen Pfad zum Turm aufwärtsgehen";
        }

        return "Den schmalen Pfad aufwärtsgehen";
    }

    private ImmutableCollection<TimedDescription<?>> getDescTo_VorDemAltenTurm(
            final Known newLocationKnown, final Lichtverhaeltnisse lichtverhaeltnisse) {
        if (newLocationKnown == UNKNOWN && lichtverhaeltnisse == HELL) {
            return ImmutableList.of(du("nimmst",
                    "den schmalen Pfad, der sich lange durch",
                    "den Wald aufwärts windet. Ein Hase kreuzt den Weg",
                    (alleinAufDemPfadZumTurm() ? ", aber keine Menschenseele begegnet dir" : null),
                    ". Ganz am Ende – auf der Hügelkuppe – kommst du an einen alten Turm",
                    PARAGRAPH)
                    .timed(mins(25)));
        }
        if (newLocationKnown == UNKNOWN && lichtverhaeltnisse == DUNKEL) {
            return ImmutableList.of(neuerSatz("Trotz der Dunkelheit nimmst du den schmalen Pfad, "
                    + "der sich lange durch "
                    + "den nächtlichen Wald aufwärts windet. "
                    + "Du erschrickst, als eine Nachteule laut „uhu“ schreit, "
                    + "auch, als es laut neben dir im Unterholz raschelt. "
                    + "Endlich endet der Pfad an einen alten Turm", PARAGRAPH)
                    .timed(mins(40)));
        }
        if (newLocationKnown == KNOWN_FROM_DARKNESS
                && lichtverhaeltnisse == HELL) {
            final AvTimeSpan timeElapsed = mins(25);

            return altNeueSaetze("Der schmale Pfad den Hügel hinauf ist",
                    world.loadWetter().wetterComp()
                            .altBeiLichtImLicht(
                                    timeTaker.now().plus(timeElapsed),
                                    true).stream()
                            .map(Praepositionalphrase::getDescription),
                    "auch nicht kürzer, aber endlich stehst du wieder vor dem alten Turm")
                    .timed(timeElapsed).build();
        }
        return ImmutableList.of(du("gehst",
                "wieder den langen, schmalen Pfad den Hügel hinauf bis zum Turm")
                .timed(mins(25)));
    }

    private <FROSCHPRINZ extends ILocatableGO & IHasStateGO<FroschprinzState>>
    boolean alleinAufDemPfadZumTurm() {
        if (((ILocatableGO) loadRequired(RAPUNZELS_ZAUBERIN)).locationComp()
                .hasLocation(IM_WALD_NAHE_DEM_SCHLOSS, VOR_DEM_ALTEN_TURM)) {
            return false;
        }

        if (((ILocatableGO) loadRequired(RAPUNZEL)).locationComp()
                .hasLocation(IM_WALD_NAHE_DEM_SCHLOSS, VOR_DEM_ALTEN_TURM)) {
            return false;
        }

        final FROSCHPRINZ froschprinz = loadRequired(FROSCHPRINZ);
        return !froschprinz.stateComp().getState().hasGestalt(FroschprinzState.Gestalt.MENSCH) ||
                !froschprinz.locationComp()
                        .hasLocation(IM_WALD_NAHE_DEM_SCHLOSS, VOR_DEM_ALTEN_TURM);
    }
}
