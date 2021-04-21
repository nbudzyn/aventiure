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
        final AltTimedDescriptionsBuilder alt = altTimed();

        final AvDateTime now = timeTaker.now();
        if (now.getTageszeit() == NACHTS) {
            final AvTimeSpan wegzeit = mins(15);
            alt.addAll(
                    world.loadWetter().wetterComp().altWoDraussen(
                            now.plus(wegzeit), true).stream()
                            .map(p ->
                            {
                                return du(SENTENCE, "gehst",
                                        "noch eine Weile vorsichtig durch",
                                        "den dunklen",
                                        "Wald, dann öffnet sich der Weg wieder und du stehst",
                                        "im Schlossgarten",
                                        p.getDescription(duSc()) // "unter dem Sternenhimmel"
                                )
                                        .mitVorfeldSatzglied("noch eine Weile")
                                        .schonLaenger()
                                        .timed(wegzeit);
                            }));
        }

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
                                .undWartest()
                                .komma()));

        alt.add(du("erreichst", "bald den Schlossgarten")
                .schonLaenger()
                .mitVorfeldSatzglied("bald")
                .timed(wegzeit)
                .undWartest()
                .komma());

        return alt.build();
    }

    @NonNull
    private TimedDescription<?>
    getDescTo_DraussenVorDemSchloss_FestBegonnen(final AvTimeSpan timeSpan) {
        if (!world.loadSC().memoryComp().isKnown(SCHLOSSFEST)) {
            world.loadSC().memoryComp().narrateAndUpgradeKnown(SCHLOSSFEST);
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
