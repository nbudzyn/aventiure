package de.nb.aventiure2.data.world.syscomp.spatialconnection.impl;

import androidx.annotation.NonNull;

import com.google.common.collect.ImmutableList;

import java.util.List;

import javax.annotation.CheckReturnValue;
import javax.annotation.ParametersAreNonnullByDefault;

import de.nb.aventiure2.data.database.AvDatabase;
import de.nb.aventiure2.data.world.base.GameObjectId;
import de.nb.aventiure2.data.world.base.Known;
import de.nb.aventiure2.data.world.base.Lichtverhaeltnisse;
import de.nb.aventiure2.data.world.base.SpatialConnection;
import de.nb.aventiure2.data.world.gameobject.World;
import de.nb.aventiure2.data.world.syscomp.spatialconnection.AbstractSpatialConnectionComp;
import de.nb.aventiure2.data.world.syscomp.state.IHasStateGO;
import de.nb.aventiure2.data.world.syscomp.state.impl.SchlossfestState;
import de.nb.aventiure2.german.description.AbstractDescription;

import static de.nb.aventiure2.data.world.gameobject.World.DRAUSSEN_VOR_DEM_SCHLOSS;
import static de.nb.aventiure2.data.world.gameobject.World.IM_WALD_NAHE_DEM_SCHLOSS;
import static de.nb.aventiure2.data.world.gameobject.World.SCHLOSSFEST;
import static de.nb.aventiure2.data.world.gameobject.World.SCHLOSS_VORHALLE;
import static de.nb.aventiure2.data.world.syscomp.state.impl.SchlossfestState.BEGONNEN;
import static de.nb.aventiure2.data.world.time.AvTimeSpan.mins;
import static de.nb.aventiure2.data.world.time.AvTimeSpan.secs;
import static de.nb.aventiure2.german.description.AllgDescription.neuerSatz;
import static de.nb.aventiure2.german.description.DuDescription.du;

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
            final AvDatabase db,
            final World world) {
        super(DRAUSSEN_VOR_DEM_SCHLOSS, db, world);
    }

    @Override
    public boolean isAlternativeMovementDescriptionAllowed(final GameObjectId to,
                                                           final Known newLocationKnown,
                                                           final Lichtverhaeltnisse lichtverhaeltnisseInNewLocation) {
        if (to.equals(SCHLOSS_VORHALLE) &&
                ((IHasStateGO<SchlossfestState>) world.load(SCHLOSSFEST)).stateComp()
                        .hasState(BEGONNEN) &&
                db.counterDao().get(COUNTER_SCHLOSS_VORHALLE_FEST_BEGONNEN) == 0) {
            return false;
        }

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
                                        + "einen Hügel hinauf",
                                // TODO Es wäre schön, wennn man die Standard-Duration hier
                                //  nicht wiederholen müsste.
                                mins(10)),
                        neuerSatz("Jeder kennt die Geschichten, die man "
                                + "sich über den Wald erzählt: Räuber sind noch "
                                + "die kleinste Gefahr. Aber das schreckt dich ganz "
                                + "offenbar nicht und du folgst dem erstbesten "
                                + "Weg hinein in den dunklen Wald. Schon nach ein paar Schritten "
                                + "führt rechter Hand ein schmaler, dunkler Pfad einen Hügel "
                                + "hinauf", mins(12)),
                        du("läufst", "wieder in den dunklen Wald", "wieder", mins(10))
                                .dann(),
                        du("läufst", "wieder in den dunklen Wald", "wieder", mins(10))
                                .dann()));
    }

    private AbstractDescription<?> getDescTo_SchlossVorhalle(
            final Known newLocationKnown, final Lichtverhaeltnisse lichtverhaeltnisse) {
        switch (((IHasStateGO<SchlossfestState>) world.load(SCHLOSSFEST)).stateComp().getState()) {
            case BEGONNEN:
                return getDescTo_SchlossVorhalle_FestBegonnen();

            default:
                return getDescTo_SchlossVorhalle_KeinFest();
        }
    }

    @NonNull
    private static AbstractDescription<?>
    getDescTo_SchlossVorhalle_KeinFest() {
        return du("gehst", "wieder hinein in das Schloss", mins(1))
                .undWartest()
                .dann();
    }

    private AbstractDescription<?>
    getDescTo_SchlossVorhalle_FestBegonnen() {
        if (db.counterDao().incAndGet(COUNTER_SCHLOSS_VORHALLE_FEST_BEGONNEN) == 1) {
            return neuerSatz("Vor dem Schloss gibt es ein großes Gedränge und es dauert "
                    + "eine Weile, bis "
                    + "die Menge dich hineinschiebt. Die prächtige Vorhalle steht voller "
                    + "Tische, auf denen in großen Schüsseln Eintöpfe dampfen", mins(7))
                    .komma();
        }

        return du("betrittst", "wieder das Schloss", "wieder", mins(2))
                .undWartest()
                .dann();
    }
}
