package de.nb.aventiure2.data.world.syscomp.spatialconnection.impl;

import androidx.annotation.NonNull;

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
import de.nb.aventiure2.german.base.Praepositionalphrase;
import de.nb.aventiure2.german.description.AltTimedDescriptionsBuilder;
import de.nb.aventiure2.german.description.TimedDescription;

import static com.google.common.collect.ImmutableSet.toImmutableSet;
import static de.nb.aventiure2.data.time.AvTimeSpan.mins;
import static de.nb.aventiure2.data.time.Tageszeit.NACHTS;
import static de.nb.aventiure2.data.world.base.Known.KNOWN_FROM_DARKNESS;
import static de.nb.aventiure2.data.world.base.Known.UNKNOWN;
import static de.nb.aventiure2.data.world.base.Lichtverhaeltnisse.DUNKEL;
import static de.nb.aventiure2.data.world.base.Lichtverhaeltnisse.HELL;
import static de.nb.aventiure2.data.world.base.SpatialConnection.con;
import static de.nb.aventiure2.data.world.base.SpatialConnection.conAltDesc;
import static de.nb.aventiure2.data.world.gameobject.World.*;
import static de.nb.aventiure2.data.world.syscomp.spatialconnection.CardinalDirection.EAST;
import static de.nb.aventiure2.data.world.syscomp.spatialconnection.CardinalDirection.NORTH;
import static de.nb.aventiure2.data.world.syscomp.spatialconnection.CardinalDirection.WEST;
import static de.nb.aventiure2.data.world.syscomp.spatialconnection.impl.ImWaldNaheDemSchlossConnectionComp.Counter.NACH_DRAUSSEN_KEIN_FEST;
import static de.nb.aventiure2.data.world.syscomp.state.impl.SchlossfestState.BEGONNEN;
import static de.nb.aventiure2.german.base.StructuralElement.PARAGRAPH;
import static de.nb.aventiure2.german.base.StructuralElement.SENTENCE;
import static de.nb.aventiure2.german.description.AltDescriptionsBuilder.altNeueSaetze;
import static de.nb.aventiure2.german.description.AltTimedDescriptionsBuilder.altTimed;
import static de.nb.aventiure2.german.description.DescriptionBuilder.du;
import static de.nb.aventiure2.german.description.DescriptionBuilder.neuerSatz;

/**
 * An implementation of {@link AbstractSpatialConnectionComp}
 * for the {@link World#IM_WALD_NAHE_DEM_SCHLOSS}
 * room.
 */
@SuppressWarnings("unchecked")
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

    @SuppressWarnings("unchecked")
    @Override
    public boolean isAlternativeMovementDescriptionAllowed(final GameObjectId to,
                                                           final Known newLocationKnown,
                                                           final Lichtverhaeltnisse lichtverhaeltnisseInNewLocation) {
        return !to.equals(DRAUSSEN_VOR_DEM_SCHLOSS)
                || !((IHasStateGO<SchlossfestState>) world.load(SCHLOSSFEST)).stateComp()
                .hasState(BEGONNEN)
                || world.loadSC().memoryComp().isKnown(SCHLOSSFEST);
    }

    @NonNull
    @Override
    public List<SpatialConnection> getConnections() {
        return ImmutableList.of(
                conAltDesc(DRAUSSEN_VOR_DEM_SCHLOSS,
                        "auf dem Weg aus dem Wald",
                        WEST, "Den Wald verlassen und in den Schlossgarten gehen",
                        mins(10),
                        this::altDescTo_DraussenVorDemSchloss),
                conAltDesc(VOR_DEM_ALTEN_TURM,
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
        if (((IHasStateGO<SchlossfestState>) world.load(SCHLOSSFEST)).stateComp().getState()
                == BEGONNEN) {
            return ImmutableList.of(getDescTo_DraussenVorDemSchloss_FestBegonnen(mins(10)));
        }

        return altDescTo_DraussenVorDemSchloss_KeinFest();
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
                .undWartest()
                .komma());

        return alt.build();
    }

    @NonNull
    private TimedDescription<?>
    getDescTo_DraussenVorDemSchloss_FestBegonnen(final AvTimeSpan timeSpan) {
        if (!world.loadSC().memoryComp().isKnown(SCHLOSSFEST)) {
            world.loadSC().memoryComp().narrateAndUpgradeKnown(SCHLOSSFEST);
            // FIXME Schlossfest sollte (Hier und an anderen Stellen - auf den Sturm reagieren.
            //  Schwierigkeiten dabei:
            //  - Es müsste zwei initial-Texte geben, je nachdem, ob der SC das Schlossfest
            //   zuerst bei normalem Wetter oder zuerst bei Sturm sieht
            //   "Du bist betroffen, als du aus dem Wald heraustrittst. Das
            //   Schlossfest hat begonnen, doch die kleinen farbigen Pagoden überall im
            //   Schlossgarten
            //   machen einen traurigen Eindruck --- viele hat der Sturm umgeworfen
            //   oder ihr Dach abgerissen. Einige geschlossene Marktstände sind ausgeräumt oder
            //   stehen
            //   aufwendig verzurrt an windgeschützten Plätzen. ---
            //   Aus dem Schloss allerdings klingt Gelächter und es duftet verführerisch nach
            //   Gebratenem"
            //  - Die App müsste berücksichtigen, welchen Stand der SC kennt - der SC müsste
            //   ein Mental Model des Schlossfest-States haben (ähnlich wie den AssumedLocations -
            //   allerdings kennt der SC im Allgemeinen gar nicht den State z.B. der Zauberin oder
            //   von Rapunzel - oder er hat ein anderes internes Modell als die Dinge selbst.
            //   Vielleicht könnte man für jede GameObjectId einen State als Enum oder
            //   String speichern - ungetypt - und der Aufrufer - z.B. die Comp - hätte die
            //   Verantwortung, den State selbst zu pflegen?).
            //  - Die App müsste prüfen, ob es eine Veränderung gegenüber dem Mental Model des SC
            //  gab:
            //   -- SC kennt normales Schlossfest, aber jetzt Sturm.
            //   "Als du aus dem Wald heraustrittst bietet sich dir ein trauriges Bild. Der Sturm
            //   hat im Schlossgarten heftig gewütet, viele der Pagoden sind umgeworfen oder ihre
            //   Dächer sind abgerissen."
            //   -- SC kennt Schlossfest im Sturm, aber jetzt Sturm beendet.
            //   "Im Schlossgarten sind die meisten Verwüstungen durch den Sturm schon wieder
            //   gerichtet und es herscht wieder reges Treiben"
            //   -- SC kennt Schlossfest im Sturm, weiterhin Sturm.
            //   "Du erreichst bald den von Sturm verwüsteten Schlossgarten"
            //   -- SC kennt normales Schlossfest, weiterhin kein Sturm.
            //  - Die App müsste speichern, welchen Stand der SC kennt.
            //  - Dasselbe für den Fall, dass der SC aus dem Schloss tritt
            //  (SchlossVorhalleConnectionComp)
            //  - Außerdem leichte Anpassungen im der DraussenVorDemSchlossConnectionComp
            //
            //  if (world.loadWetter().wetterComp().getLokaleWindstaerke(DRAUSSEN_VOR_DEM_SCHLOSS)
            // .compareTo(Windstaerke.STURM)) {
            // "Du bist überrascht und betroffen, als du aus dem Wald heraustrittst. Ganz
            // offenbar hat das Schlossfest begonnen.",
            // "Überall im Schlossgarten sind kleine Pagoden aufgebaut, "
            // "...machen nur noch einen traurigen Eindruck"
            // "Dächer sind abgerissen"
            // "umgeworfen"
            // "aus dem Schloss hörst du Lachen und Tumult"

            return du("bist", "von dem Lärm überrascht, der dir "
                    + "schon von weitem "
                    + "entgegenschallt. Als du aus dem Wald heraustrittst, "
                    + "ist der Anblick überwältigend: "
                    + "Überall im Schlossgarten stehen kleine Pagoden "
                    + "in lustigen Farben. Kinder werden auf Kähnen durch Kanäle "
                    + "gestakt und aus dem Schloss duftet es verführerisch nach "
                    + "Gebratenem").timed(timeSpan);

        }

        return neuerSatz("Das Schlossfest ist immer noch in vollem Gange")
                .timed(timeSpan);
    }

    private String getActionNameTo_VorDemAltenTurm() {
        if (world.loadSC().memoryComp().isKnown(VOR_DEM_ALTEN_TURM)) {
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

    @SuppressWarnings("unchecked")
    private <FROSCHPRINZ extends ILocatableGO & IHasStateGO<FroschprinzState>>
    boolean alleinAufDemPfadZumTurm() {
        if (((ILocatableGO) world.load(RAPUNZELS_ZAUBERIN)).locationComp()
                .hasLocation(IM_WALD_NAHE_DEM_SCHLOSS, VOR_DEM_ALTEN_TURM)) {
            return false;
        }

        if (((ILocatableGO) world.load(RAPUNZEL)).locationComp()
                .hasLocation(IM_WALD_NAHE_DEM_SCHLOSS, VOR_DEM_ALTEN_TURM)) {
            return false;
        }

        final FROSCHPRINZ froschprinz = (FROSCHPRINZ) world.load(FROSCHPRINZ);
        return !froschprinz.stateComp().getState().hasGestalt(FroschprinzState.Gestalt.MENSCH) ||
                !froschprinz.locationComp()
                        .hasLocation(IM_WALD_NAHE_DEM_SCHLOSS, VOR_DEM_ALTEN_TURM);
    }
}
