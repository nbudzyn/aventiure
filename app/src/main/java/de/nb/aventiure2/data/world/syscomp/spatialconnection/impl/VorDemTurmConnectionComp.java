package de.nb.aventiure2.data.world.syscomp.spatialconnection.impl;

import androidx.annotation.NonNull;

import com.google.common.collect.ImmutableList;

import java.util.List;

import javax.annotation.ParametersAreNonnullByDefault;

import de.nb.aventiure2.data.database.AvDatabase;
import de.nb.aventiure2.data.world.base.GameObjectId;
import de.nb.aventiure2.data.world.gameobjects.GameObjectService;
import de.nb.aventiure2.data.world.syscomp.memory.Known;
import de.nb.aventiure2.data.world.syscomp.spatialconnection.AbstractSpatialConnectionComp;
import de.nb.aventiure2.data.world.syscomp.storingplace.Lichtverhaeltnisse;
import de.nb.aventiure2.german.base.AbstractDescription;

import static de.nb.aventiure2.data.world.gameobjects.GameObjectService.DRAUSSEN_VOR_DEM_SCHLOSS;
import static de.nb.aventiure2.data.world.gameobjects.GameObjectService.IM_WALD_NAHE_DEM_SCHLOSS;
import static de.nb.aventiure2.data.world.gameobjects.GameObjectService.VOR_DEM_ALTEN_TURM;
import static de.nb.aventiure2.data.world.syscomp.spatialconnection.impl.SpatialConnection.con;
import static de.nb.aventiure2.data.world.time.AvTimeSpan.mins;
import static de.nb.aventiure2.german.base.DuDescription.du;
import static de.nb.aventiure2.german.base.StructuralElement.PARAGRAPH;
import static de.nb.aventiure2.german.base.StructuralElement.SENTENCE;

/**
 * An implementation of {@link AbstractSpatialConnectionComp}
 * for the {@link GameObjectService#DRAUSSEN_VOR_DEM_SCHLOSS}
 * room.
 */
@ParametersAreNonnullByDefault
public class VorDemTurmConnectionComp extends AbstractSpatialConnectionComp {
    private static final String COUNTER_ALTER_TURM_UMRUNDET =
            "VorDemTurmConnectionComp_AlterTurm_Umrundet";

    public VorDemTurmConnectionComp(
            final AvDatabase db,
            final GameObjectService gos) {
        super(DRAUSSEN_VOR_DEM_SCHLOSS, db, gos);
    }

    @Override
    public boolean isAlternativeMovementDescriptionAllowed(final GameObjectId to,
                                                           final Known newRoomKnown,
                                                           final Lichtverhaeltnisse lichtverhaeltnisseInNewRoom) {
        if (to.equals(VOR_DEM_ALTEN_TURM) &&
                db.counterDao().get(COUNTER_ALTER_TURM_UMRUNDET) == 0) {
            return false;
        }

        return true;
    }

    @Override
    @NonNull
    public List<SpatialConnection> getConnections() {
        return ImmutableList.of(
                con(VOR_DEM_ALTEN_TURM,
                        "Um den Turm herumgehen",
                        this::getDescTo_VorDemTurm
                ),
                con(IM_WALD_NAHE_DEM_SCHLOSS,
                        "Den Weg zurückgehen",
                        du(SENTENCE, "gehst",
                                "den langen Pfad wieder zurück, den Hügel hinab, bis "
                                        + "zum Waldweg", mins(20))
                                .beendet(PARAGRAPH),
                        du(SENTENCE, "gehst", "den gewundenen Pfad den Hügel hinab, "
                                + "bis du wieder unten am Waldweg ankommst", mins(18))
                                .komma()
                                .undWartest()
                ));
    }

    private AbstractDescription getDescTo_VorDemTurm(
            final Known newRoomKnown, final Lichtverhaeltnisse lichtverhaeltnisse) {
        final int count = db.counterDao().incAndGet("COUNTER_ALTER_TURM_UMRUNDET");
        switch (count) {
            case 1:
                return du("gehst", "einmal um den Turm herum. Es ist keine "
                        + "Türe zu sehen, nur ganz oben ein kleines Fensterchen", mins(2))
                        .dann();
            case 2:
                return du("schaust", "noch einmal um den Turm, ob dir vielleicht "
                        + "eine Tür entgangen wäre – nichts", mins(2))
                        .dann();
            default:
                return du("gehst", "noch einmal um den Turm herum", mins(1))
                        .dann();
        }
    }
}