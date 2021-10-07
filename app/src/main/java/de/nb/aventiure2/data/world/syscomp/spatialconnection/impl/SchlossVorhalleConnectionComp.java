package de.nb.aventiure2.data.world.syscomp.spatialconnection.impl;

import static de.nb.aventiure2.data.time.AvTimeSpan.mins;
import static de.nb.aventiure2.data.time.AvTimeSpan.secs;
import static de.nb.aventiure2.data.world.base.Known.KNOWN_FROM_DARKNESS;
import static de.nb.aventiure2.data.world.base.Known.UNKNOWN;
import static de.nb.aventiure2.data.world.base.Lichtverhaeltnisse.DUNKEL;
import static de.nb.aventiure2.data.world.base.Lichtverhaeltnisse.HELL;
import static de.nb.aventiure2.data.world.gameobject.World.*;
import static de.nb.aventiure2.data.world.syscomp.feelings.Mood.AUFGEDREHT;
import static de.nb.aventiure2.data.world.syscomp.feelings.Mood.BETRUEBT;
import static de.nb.aventiure2.data.world.syscomp.feelings.Mood.BEWEGT;
import static de.nb.aventiure2.data.world.syscomp.spatialconnection.CardinalDirection.EAST;
import static de.nb.aventiure2.data.world.syscomp.spatialconnection.impl.SchlossVorhalleConnectionComp.Counter.VORHALLE_NACHDRAUSSEN_VERLASSEN_KEIN_FEST_REGELFALL;
import static de.nb.aventiure2.data.world.syscomp.state.impl.FroschprinzState.ZURUECKVERWANDELT_IN_VORHALLE;
import static de.nb.aventiure2.data.world.syscomp.state.impl.FroschprinzState.ZURUECKVERWANDELT_SCHLOSS_VORHALLE_VERLASSEN;
import static de.nb.aventiure2.data.world.syscomp.state.impl.SchlossfestState.NACH_VERWUESTUNG_WIEDER_GERICHTET_MARKTSTAENDE_GESCHLOSSEN;
import static de.nb.aventiure2.data.world.syscomp.state.impl.SchlossfestState.NACH_VERWUESTUNG_WIEDER_GERICHTET_MARKTSTAENDE_OFFEN;
import static de.nb.aventiure2.data.world.syscomp.state.impl.SchlossfestState.VERWUESTET;
import static de.nb.aventiure2.german.base.GermanUtil.joinToString;
import static de.nb.aventiure2.german.base.PraepositionMitKasus.IN_AKK;
import static de.nb.aventiure2.german.base.StructuralElement.CHAPTER;
import static de.nb.aventiure2.german.base.StructuralElement.PARAGRAPH;
import static de.nb.aventiure2.german.base.StructuralElement.SENTENCE;
import static de.nb.aventiure2.german.description.AltDescriptionsBuilder.alt;
import static de.nb.aventiure2.german.description.AltDescriptionsBuilder.altNeueSaetze;
import static de.nb.aventiure2.german.description.DescriptionBuilder.du;
import static de.nb.aventiure2.german.description.DescriptionBuilder.neuerSatz;
import static de.nb.aventiure2.german.praedikat.ReflVerbSubj.SICH_DRAENGEN;
import static de.nb.aventiure2.german.praedikat.VerbSubj.GEHEN;
import static de.nb.aventiure2.util.StreamUtil.*;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

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
import de.nb.aventiure2.data.world.syscomp.state.impl.FroschprinzState;
import de.nb.aventiure2.data.world.syscomp.state.impl.SchlossfestState;
import de.nb.aventiure2.german.description.AbstractDescription;
import de.nb.aventiure2.german.description.AltDescriptionsBuilder;
import de.nb.aventiure2.german.description.TimedDescription;
import de.nb.aventiure2.german.praedikat.AdvAngabeSkopusVerbWohinWoher;

/**
 * An implementation of {@link AbstractSpatialConnectionComp}
 * for the {@link World#SCHLOSS_VORHALLE}
 * room.
 */
@ParametersAreNonnullByDefault
public class SchlossVorhalleConnectionComp extends AbstractSpatialConnectionComp {
    @SuppressWarnings({"unused", "RedundantSuppression"})
    public
    enum Counter {
        VORHALLE_NACHDRAUSSEN_VERLASSEN_KEIN_FEST_REGELFALL
    }

    public SchlossVorhalleConnectionComp(
            final AvDatabase db, final TimeTaker timeTaker, final Narrator n, final World world) {
        super(SCHLOSS_VORHALLE, db, timeTaker, n, world);
    }

    @Override
    public boolean isAlternativeMovementDescriptionAllowed(
            final GameObjectId to,
            final Known newLocationKnown,
            final Lichtverhaeltnisse lichtverhaeltnisseInNewLocation) {
        return !to.equals(SCHLOSS_VORHALLE_AM_TISCH_BEIM_FEST)
                && getAssumedSchlossfestState() == loadSchlossfest().stateComp().getState()
                && !loadFroschprinz().stateComp()
                .hasState(ZURUECKVERWANDELT_IN_VORHALLE,
                        ZURUECKVERWANDELT_SCHLOSS_VORHALLE_VERLASSEN);
    }

    @NonNull
    private IHasStateGO<FroschprinzState> loadFroschprinz() {
        return world.loadRequired(FROSCHPRINZ);
    }

    @NonNull
    private IHasStateGO<SchlossfestState> loadSchlossfest() {
        return world.loadRequired(SCHLOSSFEST);
    }

    @Override
    @NonNull
    public List<SpatialConnection> getConnections() {
        final ImmutableList.Builder<SpatialConnection> res = ImmutableList.builder();
        res.add(SpatialConnection.conAltDesc(DRAUSSEN_VOR_DEM_SCHLOSS,
                "auf der Treppe",
                EAST, this::getActionNameTo_DraussenVorDemSchloss,
                secs(90),
                this::altDescTo_DraussenVorDemSchloss));

        return res.build();
    }

    private String getActionNameTo_DraussenVorDemSchloss() {
        if (loadSC().memoryComp().isKnown(DRAUSSEN_VOR_DEM_SCHLOSS)) {
            return "Das Schloss verlassen und in den Schlossgarten gehen";
        }
        return "Das Schloss verlassen";
    }

    @CheckReturnValue
    private ImmutableCollection<TimedDescription<?>>
    altDescTo_DraussenVorDemSchloss(
            final Known newLocationKnown, final Lichtverhaeltnisse lichtverhaeltnisse) {
        final ImmutableCollection<TimedDescription<?>> res;

        switch (loadSchlossfest().stateComp().getState()) {
            case NOCH_NICHT_BEGONNEN:
                res = altDescTo_DraussenVorDemSchlosss_KeinFest(
                        newLocationKnown, lichtverhaeltnisse);
                break;
            case BEGONNEN:
                res = ImmutableList.of(getDescTo_DraussenVorDemSchloss_FestBegonnen());
                break;
            case VERWUESTET:
                res = altDescTo_DraussenVorDemSchloss_FestVerwuestet();
                break;
            case NACH_VERWUESTUNG_WIEDER_GERICHTET_MARKTSTAENDE_OFFEN:
                res = altDescTo_DraussenVorDemSchloss_MarktOffen();
                break;
            case NACH_VERWUESTUNG_WIEDER_GERICHTET_MARKTSTAENDE_GESCHLOSSEN:
                res = altDescTo_DraussenVorDemSchloss_MarktGeschlossen();
                break;
            default:
                throw new IllegalStateException("Unexpected state: "
                        + loadSchlossfest().stateComp().getState());
        }

        loadSC().mentalModelComp().setAssumedStateToActual(SCHLOSSFEST);

        return res;
    }

    private ImmutableCollection<TimedDescription<?>>
    altDescTo_DraussenVorDemSchlosss_KeinFest(
            final Known known, final Lichtverhaeltnisse lichtverhaeltnisseDraussen) {
        if (known == UNKNOWN) {
            return altDescTo_DraussenVorDemSchlosss_KeinFest_Unknown(
                    lichtverhaeltnisseDraussen);
        }

        final AvTimeSpan wegZeit = mins(1);
        if ((known == KNOWN_FROM_DARKNESS && lichtverhaeltnisseDraussen == HELL)
                || lichtverhaeltnisseDraussen == DUNKEL) {
            final ImmutableSet<AbstractDescription<?>> altSpWetterhinweiseDraussen =
                    world.loadWetter().wetterComp().altSpWetterhinweiseKommtNachDraussen(
                            timeTaker.now().plus(wegZeit), DRAUSSEN_VOR_DEM_SCHLOSS);
            if (!altSpWetterhinweiseDraussen.isEmpty()) {
                return mapToSet(
                        altSpWetterhinweiseDraussen,
                        wetterDesc ->
                                du("verlässt", "das Schloss",
                                        SENTENCE,
                                        wetterDesc)
                                        .timed(wegZeit));
            }
        }

        if (db.counterDao().get(VORHALLE_NACHDRAUSSEN_VERLASSEN_KEIN_FEST_REGELFALL) % 2 == 0) {
            final AltDescriptionsBuilder alt = alt();
            // "Du gehst die Marmortreppe hinunten und in den Sonnenschein hinaus"
            // Wetterhinweise auf jeden Fall ausgeben!
            alt.addAll(
                    world.loadWetter().wetterComp().altWetterhinweiseWohinHinaus(
                            timeTaker.now().plus(wegZeit), DRAUSSEN_VOR_DEM_SCHLOSS).stream()
                            .map(a -> a.getDescription(duSc()))
                            .map(advAngkonstituente -> GEHEN
                                    .mitAdvAngabe(
                                            new AdvAngabeSkopusVerbWohinWoher(
                                                    joinToString(
                                                            "die Marmortreppe hinunter",
                                                            advAngkonstituente.guessNumWords() > 5 ?
                                                                    "und" : null,
                                                            advAngkonstituente,
                                                            "hinaus")))
                                    .alsSatzMitSubjekt(duSc())));
            return alt.timed(wegZeit)
                    .withCounterIdIncrementedIfTextIsNarrated(
                            VORHALLE_NACHDRAUSSEN_VERLASSEN_KEIN_FEST_REGELFALL).build();
        }

        final AltDescriptionsBuilder alt = alt();
        alt.add(du("verlässt", "das Schloss").undWartest().dann());

        return alt.timed(wegZeit)
                .withCounterIdIncrementedIfTextIsNarrated(
                        VORHALLE_NACHDRAUSSEN_VERLASSEN_KEIN_FEST_REGELFALL).build();
    }

    @NonNull
    private ImmutableCollection<TimedDescription<?>>
    altDescTo_DraussenVorDemSchlosss_KeinFest_Unknown(
            final Lichtverhaeltnisse lichtverhaeltnisseDraussen) {
        final AvTimeSpan wegZeit = mins(1);
        final ImmutableSet<AbstractDescription<?>> altSpWetterhinweiseDraussen =
                world.loadWetter().wetterComp().altSpWetterhinweiseKommtNachDraussen(
                        timeTaker.now().plus(wegZeit), DRAUSSEN_VOR_DEM_SCHLOSS);
        if (!altSpWetterhinweiseDraussen.isEmpty()) {
            return mapToSet(altSpWetterhinweiseDraussen, wetterDesc ->
                    du("gehst",
                            "über eine Marmortreppe hinaus in die Gärten vor dem",
                            "Schloss", CHAPTER,
                            wetterDesc,
                            SENTENCE,
                            descWald(lichtverhaeltnisseDraussen))
                            .mitVorfeldSatzglied("über eine Marmortreppe")
                            .timed(wegZeit));
        }
        return ImmutableSet.of(du("gehst",
                "über eine Marmortreppe hinaus in die Gärten vor dem",
                "Schloss", CHAPTER,
                descWald(lichtverhaeltnisseDraussen))
                .mitVorfeldSatzglied("über eine Marmortreppe")
                .timed(wegZeit));
    }

    @NonNull
    private static String descWald(final Lichtverhaeltnisse lichtverhaeltnisseDraussen) {
        return lichtverhaeltnisseDraussen == HELL ?
                "Nahebei liegt ein großer, dunkler Wald" :
                "In der Nähe liegt ein großer Wald, der sehr bedrohlich wirkt";
    }

    @NonNull
    @CheckReturnValue
    private TimedDescription<?> getDescTo_DraussenVorDemSchloss_FestBegonnen() {
        if (loadFroschprinz().stateComp()
                .hasState(ZURUECKVERWANDELT_IN_VORHALLE,
                        ZURUECKVERWANDELT_SCHLOSS_VORHALLE_VERLASSEN)) {
            return du("drängst", "dich durch das Eingangstor").timed(mins(2))
                    .undWartest();
        }

        loadSC().feelingsComp().requestMoodMin(AUFGEDREHT);

        return du("gehst", "über die Marmortreppe hinaus in den Trubel "
                + "im Schlossgarten").mitVorfeldSatzglied("über die Marmortreppe")
                .timed(mins(3))
                .dann();
    }

    @NonNull
    @CheckReturnValue
    private ImmutableList<TimedDescription<? extends AbstractDescription<?>>>
    altDescTo_DraussenVorDemSchloss_FestVerwuestet() {
        if (getAssumedSchlossfestState() != VERWUESTET) {
            loadSC().feelingsComp().requestMoodMax(BETRUEBT);

            return ImmutableList.of(neuerSatz(PARAGRAPH,
                    "Als du aus dem Schloss heraustrittst, bietet sich dir ein trauriges Bild.",
                    "Der Sturm hat im Schlossgarten heftig gewütet, viele der Pagoden sind",
                    "umgeworfen oder ihre Dächer abgerissen. Einzelne Marktstände sind",
                    "ausgeräumt oder stehen aufwendig verzurrt an windgeschützten Plätzen",
                    !loadFroschprinz().stateComp()
                            .hasState(ZURUECKVERWANDELT_IN_VORHALLE,
                                    ZURUECKVERWANDELT_SCHLOSS_VORHALLE_VERLASSEN) ?
                            "Menschen sind nur noch wenige zu sehen" : null)
                    .schonLaenger()
                    .timed(mins(2)));
        }

        if (loadFroschprinz().stateComp()
                .hasState(ZURUECKVERWANDELT_IN_VORHALLE,
                        ZURUECKVERWANDELT_SCHLOSS_VORHALLE_VERLASSEN)) {
            return altDescTo_DraussenVorDemSchloss_InWetterHinaus();
        }

        return altDescTo_DraussenVorDemSchloss_UeberDieMarmortreppeHinaus();
    }

    @NonNull
    @CheckReturnValue
    private ImmutableList<TimedDescription<? extends AbstractDescription<?>>>
    altDescTo_DraussenVorDemSchloss_MarktOffen() {
        final AvTimeSpan wegZeit = mins(10);

        if (getAssumedSchlossfestState() == VERWUESTET) {
            loadSC().feelingsComp().requestMoodMin(BEWEGT);

            return ImmutableList.of(neuerSatz(PARAGRAPH,
                    "Als du aus dem Schloss heraustrittst, haben die Diener",
                    "die vom Sturm zerstörten Pagoden",
                    "abgeräumt; in einer Ecke hat sich ein kleiner Bauernmarkt aufgebaut")
                    .schonLaenger()
                    .timed(wegZeit));
        }

        if (getAssumedSchlossfestState()
                == NACH_VERWUESTUNG_WIEDER_GERICHTET_MARKTSTAENDE_GESCHLOSSEN) {
            return alt().addAll(
                    mapToList(
                            world.loadWetter().wetterComp()
                                    .altLichtInDemEtwasLiegt(
                                            timeTaker.now().plus(wegZeit), true),
                            sonnenlicht ->
                                    du(PARAGRAPH,
                                            "trittst",
                                            "aus dem Schloss nach draußen",
                                            IN_AKK.mit(sonnenlicht),
                                            SENTENCE,
                                            "wie es scheint, hat der Markt begonnen")))
                    .undWartest()
                    .timed(wegZeit)
                    .build();
        }

        if (loadFroschprinz().stateComp()
                .hasState(ZURUECKVERWANDELT_IN_VORHALLE,
                        ZURUECKVERWANDELT_SCHLOSS_VORHALLE_VERLASSEN)) {
            return altDescTo_DraussenVorDemSchloss_InWetterHinaus();
        }

        return altDescTo_DraussenVorDemSchloss_UeberDieMarmortreppeHinaus();
    }

    @NonNull
    @CheckReturnValue
    private ImmutableList<TimedDescription<? extends AbstractDescription<?>>>
    altDescTo_DraussenVorDemSchloss_MarktGeschlossen() {
        final AvTimeSpan wegZeit = mins(10);

        if (getAssumedSchlossfestState() == VERWUESTET) {
            loadSC().feelingsComp().requestMoodMin(BEWEGT);

            return altNeueSaetze(PARAGRAPH,
                    "Als du aus dem Schloss heraustrittst, haben die Diener",
                    "die vom Sturm zerstörten Pagoden abgeräumt",
                    SENTENCE,
                    "in einer Ecke stehen verlassen einige Marktstände",
                    // "unter dem Sternenhimmel"
                    world.loadWetter().wetterComp().altWetterhinweiseWoDraussen(
                            timeTaker.now().plus(wegZeit), DRAUSSEN_VOR_DEM_SCHLOSS))
                    .schonLaenger()
                    .timed(wegZeit)
                    .build();
        }

        if (getAssumedSchlossfestState()
                == NACH_VERWUESTUNG_WIEDER_GERICHTET_MARKTSTAENDE_OFFEN) {
            loadSC().feelingsComp().requestMoodMin(BETRUEBT);

            return alt().addAll(
                    mapToList(
                            world.loadWetter().wetterComp()
                                    .altLichtInDemEtwasLiegt(
                                            timeTaker.now().plus(wegZeit), true),
                            sonnenlicht ->
                                    du(PARAGRAPH,
                                            "verlässt",
                                            "das Schloss",
                                            SENTENCE,
                                            "Marktzeit ist offenbar schon vorbei")))
                    .schonLaenger()
                    .timed(wegZeit)
                    .build();
        }

        if (loadFroschprinz().stateComp()
                .hasState(ZURUECKVERWANDELT_IN_VORHALLE,
                        ZURUECKVERWANDELT_SCHLOSS_VORHALLE_VERLASSEN)) {
            return altDescTo_DraussenVorDemSchloss_InWetterHinaus();
        }

        return altDescTo_DraussenVorDemSchloss_UeberDieMarmortreppeHinaus();
    }

    @NonNull
    private static ImmutableList<TimedDescription<? extends AbstractDescription<?>>>
    altDescTo_DraussenVorDemSchloss_UeberDieMarmortreppeHinaus() {
        return ImmutableList.of(du("gehst",
                "über die Marmortreppe hinaus in den Schlossgarten")
                .mitVorfeldSatzglied("über die Marmortreppe")
                .dann()
                .timed(mins(2)));
    }

    private ImmutableList<TimedDescription<? extends AbstractDescription<?>>> altDescTo_DraussenVorDemSchloss_InWetterHinaus() {
        final AvTimeSpan wegZeit = mins(2);

        return alt()
                .addAll(
                        world.loadWetter().wetterComp().altWetterhinweiseWohinHinaus(
                                timeTaker.now().plus(wegZeit), DRAUSSEN_VOR_DEM_SCHLOSS)
                                .stream()
                                .map(a -> a.getDescription(duSc()))
                                .map(advAngkonstituente -> SICH_DRAENGEN
                                        .mitAdvAngabe(
                                                new AdvAngabeSkopusVerbWohinWoher(
                                                        joinToString(
                                                                "durch das Eingangstor "
                                                                        + "hinaus",
                                                                advAngkonstituente))) // "in
                                        // den Sturm"
                                        .alsSatzMitSubjekt(duSc())))
                .dann()
                .timed(wegZeit).build();
    }

    @Nullable
    private SchlossfestState getAssumedSchlossfestState() {
        return (SchlossfestState) loadSC().mentalModelComp().getAssumedState(SCHLOSSFEST);
    }

}
