package de.nb.aventiure2.data.world.syscomp.spatialconnection.impl;

import androidx.annotation.NonNull;

import com.google.common.collect.ImmutableList;

import java.util.List;

import javax.annotation.ParametersAreNonnullByDefault;

import de.nb.aventiure2.data.database.AvDatabase;
import de.nb.aventiure2.data.world.base.GameObjectId;
import de.nb.aventiure2.data.world.gameobject.World;
import de.nb.aventiure2.data.world.syscomp.memory.Known;
import de.nb.aventiure2.data.world.syscomp.spatialconnection.AbstractSpatialConnectionComp;
import de.nb.aventiure2.data.world.syscomp.state.IHasStateGO;
import de.nb.aventiure2.data.world.syscomp.state.impl.FroschprinzState;
import de.nb.aventiure2.data.world.syscomp.state.impl.SchlossfestState;
import de.nb.aventiure2.data.world.syscomp.storingplace.Lichtverhaeltnisse;
import de.nb.aventiure2.german.base.AbstractDescription;

import static de.nb.aventiure2.data.world.gameobject.World.DRAUSSEN_VOR_DEM_SCHLOSS;
import static de.nb.aventiure2.data.world.gameobject.World.FROSCHPRINZ;
import static de.nb.aventiure2.data.world.gameobject.World.SCHLOSSFEST;
import static de.nb.aventiure2.data.world.gameobject.World.SCHLOSS_VORHALLE;
import static de.nb.aventiure2.data.world.gameobject.World.SCHLOSS_VORHALLE_AM_TISCH_BEIM_FEST;
import static de.nb.aventiure2.data.world.syscomp.feelings.Mood.AUFGEDREHT;
import static de.nb.aventiure2.data.world.syscomp.memory.Known.KNOWN_FROM_DARKNESS;
import static de.nb.aventiure2.data.world.syscomp.memory.Known.UNKNOWN;
import static de.nb.aventiure2.data.world.syscomp.state.impl.FroschprinzState.ZURUECKVERWANDELT_IN_VORHALLE;
import static de.nb.aventiure2.data.world.syscomp.state.impl.FroschprinzState.ZURUECKVERWANDELT_SCHLOSS_VORHALLE_VERLASSEN;
import static de.nb.aventiure2.data.world.syscomp.state.impl.SchlossfestState.BEGONNEN;
import static de.nb.aventiure2.data.world.syscomp.storingplace.Lichtverhaeltnisse.HELL;
import static de.nb.aventiure2.data.world.time.AvTimeSpan.mins;
import static de.nb.aventiure2.german.base.DuDescription.du;

/**
 * An implementation of {@link AbstractSpatialConnectionComp}
 * for the {@link World#SCHLOSS_VORHALLE}
 * room.
 */
@ParametersAreNonnullByDefault
public class SchlossVorhalleConnectionComp extends AbstractSpatialConnectionComp {
    private static final String COUNTER_TISCH_BEIM_FEST =
            "RoomConnectionBuilder_SchlossVorhalle_SchlossVorhalleTischBeimFest";

    public SchlossVorhalleConnectionComp(
            final AvDatabase db, final World world) {
        super(SCHLOSS_VORHALLE, db, world);
    }

    @Override
    public boolean isAlternativeMovementDescriptionAllowed(final GameObjectId to,
                                                           final Known newRoomKnown,
                                                           final Lichtverhaeltnisse lichtverhaeltnisseInNewRoom) {
        if (to.equals(SCHLOSS_VORHALLE_AM_TISCH_BEIM_FEST) &&
                ((IHasStateGO<SchlossfestState>) world.load(SCHLOSSFEST)).stateComp()
                        .hasState(BEGONNEN) &&
                db.counterDao().get(COUNTER_TISCH_BEIM_FEST) == 0) {
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
                "Das Schloss verlassen",
                this::getDescTo_DraussenVorDemSchloss));
        if (((IHasStateGO<SchlossfestState>) world.load(SCHLOSSFEST)).stateComp()
                .hasState(BEGONNEN)) {
            res.add(SpatialConnection.con(SCHLOSS_VORHALLE_AM_TISCH_BEIM_FEST,
                    "während der Suche nach einem Platz",
                    "An einen Tisch setzen",
                    this::getDescTo_SchlossVorhalleTischBeimFest));
        }
        return res.build();
    }

    private AbstractDescription getDescTo_DraussenVorDemSchloss(
            final Known newRoomKnown, final Lichtverhaeltnisse lichtverhaeltnisse) {
        switch (((IHasStateGO<SchlossfestState>) world.load(SCHLOSSFEST)).stateComp().getState()) {
            case BEGONNEN:
                return getDescTo_DraussenVorDemSchloss_FestBegonnen();

            default:
                return getDescTo_DraussenVorDemSchlosss_KeinFest(newRoomKnown,
                        lichtverhaeltnisse);
        }
    }

    private static AbstractDescription getDescTo_DraussenVorDemSchlosss_KeinFest(
            final Known known, final Lichtverhaeltnisse lichtverhaeltnisse) {
        if (known == UNKNOWN) {
            return getDescTo_DraussenVorDemSchlosss_KeinFest_Unknown(
                    lichtverhaeltnisse);
        }

        if (known == KNOWN_FROM_DARKNESS && lichtverhaeltnisse == HELL) {
            // STORY Vielleicht ist es nur tagsüber / mittags heiß und morgens
            //  noch nicht?
            return du("verlässt", "das Schloss. Draußen scheint dir die " +
                    "Sonne ins Gesicht; "
                    // STORY Vielleicht ist es nur tagsüber / mittags heiß und morgens
                    //  noch nicht?
                    + "der Tag ist recht heiß", mins(1));
        }

        // STORY: Wenn man aus dem hellen (Schloss) ins Dunkle kommt:
        //  "Draußen ist es dunkel" o.Ä.

        return du("verlässt", "das Schloss", mins(1))
                .undWartest()
                .dann();
    }

    @NonNull
    private static AbstractDescription
    getDescTo_DraussenVorDemSchlosss_KeinFest_Unknown(
            final Lichtverhaeltnisse lichtverhaeltnisse) {
        if (lichtverhaeltnisse == HELL) {
            // STORY Vielleicht ist es nur tagsüber / mittags heiß und morgens
            //  noch nicht?
            return du("gehst", "über eine Marmortreppe hinaus in die Gärten vor dem Schloss.\n\n" +
                            "Draußen scheint dir die " +
                            "Sonne ins Gesicht; "
                            // STORY Vielleicht ist es nur tagsüber / mittags heiß und morgens
                            //  noch nicht?
                            + "der Tag ist recht heiß. " +
                            "Nahebei liegt ein großer, dunkler Wald", "über eine Marmortreppe",
                    mins(1));
        }

        return du("gehst", "über eine Marmortreppe hinaus den Garten vor dem Schloss.\n\n" +
                        "Draußen ist es dunkel. " +
                        "In der Nähe liegt ein großer Wald, der sehr bedrohlich wirkt",
                "über eine Marmortreppe", mins(1))
                .komma();
    }

    @NonNull
    private AbstractDescription
    getDescTo_DraussenVorDemSchloss_FestBegonnen() {
        if (((IHasStateGO<FroschprinzState>) world.load(FROSCHPRINZ)).stateComp()
                .hasState(ZURUECKVERWANDELT_IN_VORHALLE,
                        ZURUECKVERWANDELT_SCHLOSS_VORHALLE_VERLASSEN)) {
            return du("drängst",
                    "dich durch das Eingangstor",
                    mins(2))
                    .undWartest()
                    .dann();
        }

        world.loadSC().feelingsComp().setMoodMin(AUFGEDREHT);

        // STORY: Nachts ist weniger Trubel?
        return du("gehst",
                "über die Marmortreppe hinaus in den Trubel "
                        + "im Schlossgarten",
                "über die Marmortreppe",
                mins(3))
                .dann();
    }

    private AbstractDescription getDescTo_SchlossVorhalleTischBeimFest(
            final Known newRoomKnown, final Lichtverhaeltnisse lichtverhaeltnisse) {
        if (db.counterDao().incAndGet(
                "RoomConnectionBuilder_SchlossVorhalle_SchlossVorhalleTischBeimFest")
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
}
