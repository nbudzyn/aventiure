package de.nb.aventiure2.data.world.syscomp.spatialconnection.impl;

import androidx.annotation.NonNull;

import com.google.common.collect.ImmutableList;

import java.util.List;

import javax.annotation.ParametersAreNonnullByDefault;

import de.nb.aventiure2.data.database.AvDatabase;
import de.nb.aventiure2.data.world.base.GameObjectId;
import de.nb.aventiure2.data.world.gameobjects.GameObjects;
import de.nb.aventiure2.data.world.lichtverhaeltnisse.Lichtverhaeltnisse;
import de.nb.aventiure2.data.world.syscomp.memory.Known;
import de.nb.aventiure2.data.world.syscomp.spatialconnection.AbstractSpatialConnectionComp;
import de.nb.aventiure2.data.world.syscomp.state.IHasStateGO;
import de.nb.aventiure2.german.base.AbstractDescription;

import static de.nb.aventiure2.data.world.gameobjects.GameObjects.DRAUSSEN_VOR_DEM_SCHLOSS;
import static de.nb.aventiure2.data.world.gameobjects.GameObjects.IM_WALD_NAHE_DEM_SCHLOSS;
import static de.nb.aventiure2.data.world.gameobjects.GameObjects.SCHLOSSFEST;
import static de.nb.aventiure2.data.world.gameobjects.GameObjects.SCHLOSS_VORHALLE;
import static de.nb.aventiure2.data.world.syscomp.state.GameObjectState.BEGONNEN;
import static de.nb.aventiure2.data.world.time.AvTimeSpan.mins;
import static de.nb.aventiure2.german.base.AllgDescription.neuerSatz;
import static de.nb.aventiure2.german.base.DuDescription.du;

/**
 * An implementation of {@link AbstractSpatialConnectionComp}
 * for the {@link GameObjects#DRAUSSEN_VOR_DEM_SCHLOSS}
 * room.
 */
@ParametersAreNonnullByDefault
public class DraussenVorDemSchlossConnectionComp extends AbstractSpatialConnectionComp {
    private static final String COUNTER_SCHLOSS_VORHALLE_FEST_BEGONNEN =
            "RoomConnectionBuilder_DraussenVorDemSchloss_SchlossVorhalle_FestBegonnen";

    public DraussenVorDemSchlossConnectionComp(
            final AvDatabase db) {
        super(DRAUSSEN_VOR_DEM_SCHLOSS, db);
    }

    @Override
    public boolean isAlternativeMovementDescriptionAllowed(final GameObjectId to,
                                                           final Known newRoomKnown,
                                                           final Lichtverhaeltnisse lichtverhaeltnisseInNewRoom) {
        if (to.equals(SCHLOSS_VORHALLE) &&
                ((IHasStateGO) GameObjects.load(db, SCHLOSSFEST)).stateComp().hasState(BEGONNEN) &&
                db.counterDao().get(COUNTER_SCHLOSS_VORHALLE_FEST_BEGONNEN) == 0) {
            return false;
        }

        return true;
    }

    @Override
    @NonNull
    public List<SpatialConnection> getConnections() {
        return ImmutableList.of(
                SpatialConnection.con(SCHLOSS_VORHALLE,
                        "Das Schloss betreten",
                        this::getDescTo_SchlossVorhalle),

                SpatialConnection.con(IM_WALD_NAHE_DEM_SCHLOSS,
                        "In den Wald gehen",
                        du("folgst", "einem Pfad in den Wald", mins(10))
                                .undWartest()
                                .dann(),

                        neuerSatz("Jeder kennt die Geschichten, die man "
                                + "sich über den Wald erzählt: Räuber sind noch "
                                + "die kleinste Gefahr. Aber das schreckt dich ganz "
                                + "offenbar nicht und du folgst dem erstbesten "
                                + "Pfad hinein in den dunklen Wald", mins(12)),

                        du("läufst", "wieder in den dunklen Wald", "wieder", mins(10))
                                .undWartest()
                                .dann(),

                        du("läufst", "wieder in den dunklen Wald", "wieder", mins(10))
                                .undWartest()
                                .dann()));
    }

    private AbstractDescription getDescTo_SchlossVorhalle(
            final Known newRoomKnown, final Lichtverhaeltnisse lichtverhaeltnisse) {
        switch (((IHasStateGO) GameObjects.load(db, SCHLOSSFEST)).stateComp().getState()) {
            case BEGONNEN:
                return getDescTo_SchlossVorhalle_FestBegonnen();

            default:
                return getDescTo_SchlossVorhalle_KeinFest();
        }
    }

    @NonNull
    private static AbstractDescription
    getDescTo_SchlossVorhalle_KeinFest() {
        return du("gehst", "wieder hinein in das Schloss", mins(1))
                .undWartest()
                .dann();
    }

    private AbstractDescription
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
