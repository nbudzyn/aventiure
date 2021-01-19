package de.nb.aventiure2.data.world.syscomp.spatialconnection.impl;

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
import de.nb.aventiure2.data.world.syscomp.state.impl.FroschprinzState;
import de.nb.aventiure2.data.world.syscomp.state.impl.SchlossfestState;
import de.nb.aventiure2.german.description.TimedDescription;

import static de.nb.aventiure2.data.time.AvTimeSpan.mins;
import static de.nb.aventiure2.data.time.AvTimeSpan.secs;
import static de.nb.aventiure2.data.world.base.Known.KNOWN_FROM_DARKNESS;
import static de.nb.aventiure2.data.world.base.Known.UNKNOWN;
import static de.nb.aventiure2.data.world.base.Lichtverhaeltnisse.HELL;
import static de.nb.aventiure2.data.world.gameobject.World.*;
import static de.nb.aventiure2.data.world.syscomp.feelings.Mood.AUFGEDREHT;
import static de.nb.aventiure2.data.world.syscomp.state.impl.FroschprinzState.ZURUECKVERWANDELT_IN_VORHALLE;
import static de.nb.aventiure2.data.world.syscomp.state.impl.FroschprinzState.ZURUECKVERWANDELT_SCHLOSS_VORHALLE_VERLASSEN;
import static de.nb.aventiure2.data.world.syscomp.state.impl.SchlossfestState.BEGONNEN;
import static de.nb.aventiure2.german.base.Wortfolge.w;
import static de.nb.aventiure2.german.description.DescriptionBuilder.du;

/**
 * An implementation of {@link AbstractSpatialConnectionComp}
 * for the {@link World#SCHLOSS_VORHALLE}
 * room.
 */
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
        res.add(SpatialConnection.con(DRAUSSEN_VOR_DEM_SCHLOSS,
                "auf der Treppe",
                this::getActionNameTo_DraussenVorDemSchloss,
                secs(90),
                this::getDescTo_DraussenVorDemSchloss));

        return res.build();
    }

    private String getActionNameTo_DraussenVorDemSchloss() {
        if (world.loadSC().memoryComp().isKnown(DRAUSSEN_VOR_DEM_SCHLOSS)) {
            return "Das Schloss verlassen und in den Schlossgarten gehen";
        }
        return "Das Schloss verlassen";
    }

    @CheckReturnValue
    private TimedDescription<?> getDescTo_DraussenVorDemSchloss(
            final Known newLocationKnown, final Lichtverhaeltnisse lichtverhaeltnisse) {
        switch (((IHasStateGO<SchlossfestState>) world.load(SCHLOSSFEST)).stateComp().getState()) {
            case BEGONNEN:
                return getDescTo_DraussenVorDemSchloss_FestBegonnen();

            default:
                return getDescTo_DraussenVorDemSchlosss_KeinFest(newLocationKnown,
                        lichtverhaeltnisse);
        }
    }

    private static TimedDescription<?> getDescTo_DraussenVorDemSchlosss_KeinFest(
            final Known known, final Lichtverhaeltnisse lichtverhaeltnisse) {
        if (known == UNKNOWN) {
            return getDescTo_DraussenVorDemSchlosss_KeinFest_Unknown(
                    lichtverhaeltnisse);
        }

        if (known == KNOWN_FROM_DARKNESS && lichtverhaeltnisse == HELL) {
            // TODO Vielleicht ist es nur tagsüber / mittags heiß und morgens
            //  noch nicht?
            return du("verlässt", w("das Schloss. Draußen scheint dir die " +
                    "Sonne ins Gesicht; "
                    // TODO Vielleicht ist es nur tagsüber / mittags heiß und morgens
                    //  noch nicht?
                    + "der Tag ist recht heiß")).timed(mins(1));
        }

        // TODO: Wenn man aus dem hellen (Schloss) ins Dunkle kommt:
        //  "Draußen ist es dunkel" o.Ä.

        return du("verlässt", w("das Schloss")).timed(mins(1))
                .undWartest()
                .dann();
    }

    @NonNull
    private static TimedDescription<?>
    getDescTo_DraussenVorDemSchlosss_KeinFest_Unknown(
            final Lichtverhaeltnisse lichtverhaeltnisse) {
        if (lichtverhaeltnisse == HELL) {
            // TODO Vielleicht ist es nur tagsüber / mittags heiß und morgens
            //  noch nicht?
            return du("gehst", "über eine Marmortreppe hinaus in die Gärten vor dem Schloss.\n\n" +
                    "Draußen scheint dir die " +
                    "Sonne ins Gesicht; "
                    // TODO Vielleicht ist es nur tagsüber / mittags heiß und morgens
                    //  noch nicht?
                    + "der Tag ist recht heiß. " +
                    "Nahebei liegt ein großer, dunkler Wald", "über eine Marmortreppe")
                    .timed(mins(1));
        }

        return du("gehst", "über eine Marmortreppe hinaus den Garten vor dem Schloss.\n\n" +
                        "Draußen ist es dunkel. " +
                        "In der Nähe liegt ein großer Wald, der sehr bedrohlich wirkt",
                "über eine Marmortreppe")
                .timed(mins(1))
                .komma();
    }

    @NonNull
    @CheckReturnValue
    private TimedDescription<?>
    getDescTo_DraussenVorDemSchloss_FestBegonnen() {
        if (((IHasStateGO<FroschprinzState>) world.load(FROSCHPRINZ)).stateComp()
                .hasState(ZURUECKVERWANDELT_IN_VORHALLE,
                        ZURUECKVERWANDELT_SCHLOSS_VORHALLE_VERLASSEN)) {
            return du("drängst", w("dich durch das Eingangstor")).timed(mins(2))
                    .undWartest();
        }

        world.loadSC().feelingsComp().requestMoodMin(AUFGEDREHT);

        // TODO Nachts ist weniger Trubel?
        return du("gehst", "über die Marmortreppe hinaus in den Trubel "
                + "im Schlossgarten", "über die Marmortreppe")
                .timed(mins(3))
                .dann();
    }
}
