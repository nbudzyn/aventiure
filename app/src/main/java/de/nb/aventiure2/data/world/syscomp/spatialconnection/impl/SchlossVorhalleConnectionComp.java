package de.nb.aventiure2.data.world.syscomp.spatialconnection.impl;

import androidx.annotation.NonNull;

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
import de.nb.aventiure2.german.praedikat.AdvAngabeSkopusVerbAllg;

import static de.nb.aventiure2.data.time.AvTimeSpan.mins;
import static de.nb.aventiure2.data.time.AvTimeSpan.secs;
import static de.nb.aventiure2.data.world.base.Known.KNOWN_FROM_DARKNESS;
import static de.nb.aventiure2.data.world.base.Known.UNKNOWN;
import static de.nb.aventiure2.data.world.base.Lichtverhaeltnisse.DUNKEL;
import static de.nb.aventiure2.data.world.base.Lichtverhaeltnisse.HELL;
import static de.nb.aventiure2.data.world.gameobject.World.*;
import static de.nb.aventiure2.data.world.syscomp.feelings.Mood.AUFGEDREHT;
import static de.nb.aventiure2.data.world.syscomp.spatialconnection.CardinalDirection.EAST;
import static de.nb.aventiure2.data.world.syscomp.state.impl.FroschprinzState.ZURUECKVERWANDELT_IN_VORHALLE;
import static de.nb.aventiure2.data.world.syscomp.state.impl.FroschprinzState.ZURUECKVERWANDELT_SCHLOSS_VORHALLE_VERLASSEN;
import static de.nb.aventiure2.data.world.syscomp.state.impl.SchlossfestState.BEGONNEN;
import static de.nb.aventiure2.german.base.StructuralElement.CHAPTER;
import static de.nb.aventiure2.german.base.StructuralElement.SENTENCE;
import static de.nb.aventiure2.german.description.AltDescriptionsBuilder.alt;
import static de.nb.aventiure2.german.description.DescriptionBuilder.du;
import static de.nb.aventiure2.german.praedikat.VerbSubj.GEHEN;
import static de.nb.aventiure2.util.StreamUtil.*;

/**
 * An implementation of {@link AbstractSpatialConnectionComp}
 * for the {@link World#SCHLOSS_VORHALLE}
 * room.
 */
@SuppressWarnings("unchecked")
@ParametersAreNonnullByDefault
public class SchlossVorhalleConnectionComp extends AbstractSpatialConnectionComp {
    public SchlossVorhalleConnectionComp(
            final AvDatabase db, final TimeTaker timeTaker, final Narrator n, final World world) {
        super(SCHLOSS_VORHALLE, db, timeTaker, n, world);
    }

    @Override
    public boolean isAlternativeMovementDescriptionAllowed(final GameObjectId to,
                                                           final Known newLocationKnown,
                                                           final Lichtverhaeltnisse lichtverhaeltnisseInNewLocation) {
        if (to.equals(SCHLOSS_VORHALLE_AM_TISCH_BEIM_FEST) &&
                ((IHasStateGO<SchlossfestState>) world.load(SCHLOSSFEST)).stateComp()
                        .hasState(BEGONNEN) &&
                ((IHasStateGO<FroschprinzState>) world.load(FROSCHPRINZ)).stateComp()
                        .hasState(ZURUECKVERWANDELT_IN_VORHALLE,
                                ZURUECKVERWANDELT_SCHLOSS_VORHALLE_VERLASSEN)) {
            return false;
        }

        return true;
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
        if (world.loadSC().memoryComp().isKnown(DRAUSSEN_VOR_DEM_SCHLOSS)) {
            return "Das Schloss verlassen und in den Schlossgarten gehen";
        }
        return "Das Schloss verlassen";
    }

    @CheckReturnValue
    private ImmutableCollection<TimedDescription<?>>
    altDescTo_DraussenVorDemSchloss(
            final Known newLocationKnown, final Lichtverhaeltnisse lichtverhaeltnisse) {
        switch (((IHasStateGO<SchlossfestState>) world.load(SCHLOSSFEST)).stateComp().getState()) {
            case BEGONNEN:
                return ImmutableList.of(getDescTo_DraussenVorDemSchloss_FestBegonnen());
            default:
                return altDescTo_DraussenVorDemSchlosss_KeinFest(
                        newLocationKnown, lichtverhaeltnisse);
        }
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
            final ImmutableSet<AbstractDescription<?>> altWetterDraussen =
                    world.loadWetter().wetterComp().altKommtNachDraussen(
                            timeTaker.now().plus(wegZeit), true);
            if (!altWetterDraussen.isEmpty()) {
                return mapToSet(
                        altWetterDraussen,
                        wetterDesc ->
                                du("verlässt", "das Schloss",
                                        SENTENCE,
                                        wetterDesc.toSingleKonstituente()).timed(wegZeit));
            }
        }

        final AltDescriptionsBuilder alt = alt();
        alt.add(du("verlässt", "das Schloss").undWartest().dann());
        // "Du gehst die Marmortreppe hinunten in den Sonnenschein hinaus"
        alt.addAll(world.loadWetter().wetterComp().altWohinHinaus(
                timeTaker.now().plus(wegZeit), true).stream()
                .map(a -> GEHEN
                        .mitAdvAngabe(new AdvAngabeSkopusVerbAllg("die Marmortreppe hinunter"))
                        .mitAdvAngabe(a)
                        .alsSatzMitSubjekt(duSc())));

        return alt.timed(wegZeit).build();
    }

    @NonNull
    private ImmutableCollection<TimedDescription<?>>
    altDescTo_DraussenVorDemSchlosss_KeinFest_Unknown(
            final Lichtverhaeltnisse lichtverhaeltnisseDraussen) {
        final AvTimeSpan wegZeit = mins(1);
        final ImmutableSet<AbstractDescription<?>> altWetterDraussen =
                world.loadWetter().wetterComp().altKommtNachDraussen(
                        timeTaker.now().plus(wegZeit), true);
        if (!altWetterDraussen.isEmpty()) {
            return mapToSet(altWetterDraussen, wetterDesc ->
                    du("gehst",
                            "über eine Marmortreppe hinaus in die Gärten vor dem",
                            "Schloss", CHAPTER,
                            wetterDesc.toSingleKonstituente(),
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
    private TimedDescription<?>
    getDescTo_DraussenVorDemSchloss_FestBegonnen() {
        if (((IHasStateGO<FroschprinzState>) world.load(FROSCHPRINZ)).stateComp()
                .hasState(ZURUECKVERWANDELT_IN_VORHALLE,
                        ZURUECKVERWANDELT_SCHLOSS_VORHALLE_VERLASSEN)) {
            return du("drängst", "dich durch das Eingangstor").timed(mins(2))
                    .undWartest();
        }

        world.loadSC().feelingsComp().requestMoodMin(AUFGEDREHT);

        // TODO Nachts ist weniger Trubel?
        return du("gehst", "über die Marmortreppe hinaus in den Trubel "
                + "im Schlossgarten").mitVorfeldSatzglied("über die Marmortreppe")
                .timed(mins(3))
                .dann();
    }
}
