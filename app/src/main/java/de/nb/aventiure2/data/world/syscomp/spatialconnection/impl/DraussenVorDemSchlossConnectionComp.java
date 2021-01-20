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
import de.nb.aventiure2.data.world.syscomp.state.impl.SchlossfestState;
import de.nb.aventiure2.german.description.TimedDescription;

import static de.nb.aventiure2.data.time.AvTimeSpan.mins;
import static de.nb.aventiure2.data.time.AvTimeSpan.secs;
import static de.nb.aventiure2.data.world.gameobject.World.*;
import static de.nb.aventiure2.german.base.Wortfolge.w;
import static de.nb.aventiure2.german.description.DescriptionBuilder.du;
import static de.nb.aventiure2.german.description.DescriptionBuilder.neuerSatz;

/**
 * An implementation of {@link AbstractSpatialConnectionComp}
 * for the {@link World#DRAUSSEN_VOR_DEM_SCHLOSS}
 * room.
 */
@ParametersAreNonnullByDefault
public class DraussenVorDemSchlossConnectionComp extends AbstractSpatialConnectionComp {
    private static final String COUNTER_SCHLOSS_VORHALLE_FEST_BEGONNEN =
            "RoomConnectionBuilder_DraussenVorDemSchloss_SchlossVorhalle_FestBegonnen";

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
        return ImmutableList.of(
                SpatialConnection.con(SCHLOSS_VORHALLE,
                        "auf der Treppe",
                        "Das Schloss betreten",
                        secs(90),
                        this::getDescTo_SchlossVorhalle),

                SpatialConnection.con(IM_WALD_NAHE_DEM_SCHLOSS,
                        "auf dem Weg",
                        "In den Wald gehen",
                        mins(10),
                        du("folgst", "einem Weg in den Wald. "
                                + "Nach ein paar Schritten führt rechter Hand ein schmaler Pfad "
                                + "einen Hügel hinauf"),
                        neuerSatz("Jeder kennt die Geschichten, die man "
                                + "sich über den Wald erzählt: Räuber sind noch "
                                + "die kleinste Gefahr. Aber das schreckt dich ganz "
                                + "offenbar nicht und du folgst dem erstbesten "
                                + "Weg hinein in den dunklen Wald. Schon nach ein paar Schritten "
                                + "führt rechter Hand ein schmaler, dunkler Pfad einen Hügel "
                                + "hinauf")
                                .timed(mins(12)),
                        du("läufst", "wieder in den dunklen Wald").mitVorfeldSatzglied("wieder")
                                .dann(),
                        du("läufst", "wieder in den dunklen Wald").mitVorfeldSatzglied("wieder")
                                .dann()));
    }

    private TimedDescription<?> getDescTo_SchlossVorhalle(
            final Known newLocationKnown, final Lichtverhaeltnisse lichtverhaeltnisse) {
        switch (((IHasStateGO<SchlossfestState>) world.load(SCHLOSSFEST)).stateComp().getState()) {
            case BEGONNEN:
                return getDescTo_SchlossVorhalle_FestBegonnen();

            default:
                return getDescTo_SchlossVorhalle_KeinFest();
        }
    }

    @NonNull
    private static TimedDescription<?>
    getDescTo_SchlossVorhalle_KeinFest() {
        return du("gehst", w("wieder hinein in das Schloss")).timed(mins(1))
                .undWartest()
                .dann();
    }

    private TimedDescription<?>
    getDescTo_SchlossVorhalle_FestBegonnen() {
        if (db.counterDao().get(COUNTER_SCHLOSS_VORHALLE_FEST_BEGONNEN) == 0) {
            return neuerSatz("Vor dem Schloss gibt es ein großes Gedränge und es dauert "
                    + "eine Weile, bis "
                    + "die Menge dich hineinschiebt. Die prächtige Vorhalle steht voller "
                    + "Tische, auf denen in großen Schüsseln Eintöpfe dampfen")
                    .timed(mins(7))
                    .withCounterIdIncrementedIfTextIsNarrated(
                            COUNTER_SCHLOSS_VORHALLE_FEST_BEGONNEN)
                    .komma();
        }

        return du("betrittst", "wieder das Schloss")
                .mitVorfeldSatzglied("wieder")
                .timed(mins(2))
                .undWartest()
                .dann();
    }
}
