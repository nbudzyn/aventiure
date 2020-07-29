package de.nb.aventiure2.data.world.syscomp.spatialconnection.impl;

import androidx.annotation.NonNull;

import com.google.common.collect.ImmutableList;

import java.util.List;

import javax.annotation.ParametersAreNonnullByDefault;

import de.nb.aventiure2.data.database.AvDatabase;
import de.nb.aventiure2.data.world.base.GameObjectId;
import de.nb.aventiure2.data.world.base.Known;
import de.nb.aventiure2.data.world.base.Lichtverhaeltnisse;
import de.nb.aventiure2.data.world.base.SpatialConnection;
import de.nb.aventiure2.data.world.gameobject.World;
import de.nb.aventiure2.data.world.syscomp.spatialconnection.AbstractSpatialConnectionComp;
import de.nb.aventiure2.german.base.AbstractDescription;

import static de.nb.aventiure2.data.world.base.SpatialConnection.con;
import static de.nb.aventiure2.data.world.gameobject.World.DRAUSSEN_VOR_DEM_SCHLOSS;
import static de.nb.aventiure2.data.world.gameobject.World.IM_WALD_NAHE_DEM_SCHLOSS;
import static de.nb.aventiure2.data.world.gameobject.World.VOR_DEM_ALTEN_TURM;
import static de.nb.aventiure2.data.world.time.AvTimeSpan.mins;
import static de.nb.aventiure2.data.world.time.AvTimeSpan.secs;
import static de.nb.aventiure2.german.base.DuDescription.du;
import static de.nb.aventiure2.german.base.StructuralElement.PARAGRAPH;
import static de.nb.aventiure2.german.base.StructuralElement.SENTENCE;

/**
 * An implementation of {@link AbstractSpatialConnectionComp}
 * for the {@link World#DRAUSSEN_VOR_DEM_SCHLOSS}
 * room.
 */
@ParametersAreNonnullByDefault
public class VorDemTurmConnectionComp extends AbstractSpatialConnectionComp {
    private static final String COUNTER_ALTER_TURM_UMRUNDET =
            "VorDemTurmConnectionComp_AlterTurm_Umrundet";
    public static final String COUNTER_SC_HOERT_RAPUNZELS_GESANG =
            "VorDemTurmConnectionComp_SCHoertRapunzelsGesang";

    public VorDemTurmConnectionComp(
            final AvDatabase db,
            final World world) {
        super(DRAUSSEN_VOR_DEM_SCHLOSS, db, world);
    }

    @Override
    public boolean isAlternativeMovementDescriptionAllowed(final GameObjectId to,
                                                           final Known newLocationKnown,
                                                           final Lichtverhaeltnisse lichtverhaeltnisseInNewLocation) {
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
                        "auf der anderen Seite des Turms",
                        "Um den Turm herumgehen",
                        secs(90),
                        this::getDescTo_VorDemTurm
                ),
                con(IM_WALD_NAHE_DEM_SCHLOSS,
                        "auf dem Pfad",
                        "Den Pfad zurückgehen",
                        mins(18),
                        du(SENTENCE, "gehst",
                                "den langen Pfad wieder zurück, den Hügel hinab, bis "
                                        + "zum Waldweg", mins(20))
                                .beendet(PARAGRAPH),
                        du(SENTENCE, "gehst", "den Hügel auf dem gewundenen Pfad wieder hinab, "
                                + "bis du unten am Waldweg ankommst", mins(18))
                                .komma()
                                .undWartest()
                ));
    }

    private AbstractDescription<?> getDescTo_VorDemTurm(
            final Known newLocationKnown, final Lichtverhaeltnisse lichtverhaeltnisse) {
        final int count = db.counterDao().incAndGet("counter_alterTurm_umrundet");
        switch (count) {
            case 1:
                if (db.counterDao().get(COUNTER_SC_HOERT_RAPUNZELS_GESANG) > 0) {
                    return du("möchtest", "zu der süßen Stimme hinaufsteigen, "
                            + "und suchst rundherum nach einer Türe des Turms, aber es ist keine "
                            + "zu finden", mins(2))
                            .dann();
                }

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
