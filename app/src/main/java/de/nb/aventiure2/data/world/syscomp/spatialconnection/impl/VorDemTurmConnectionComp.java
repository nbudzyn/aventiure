package de.nb.aventiure2.data.world.syscomp.spatialconnection.impl;

import androidx.annotation.NonNull;

import com.google.common.collect.ImmutableList;

import java.util.List;

import javax.annotation.CheckReturnValue;
import javax.annotation.ParametersAreNonnullByDefault;

import de.nb.aventiure2.data.database.AvDatabase;
import de.nb.aventiure2.data.narration.Narrator;
import de.nb.aventiure2.data.world.base.GameObjectId;
import de.nb.aventiure2.data.world.base.Known;
import de.nb.aventiure2.data.world.base.Lichtverhaeltnisse;
import de.nb.aventiure2.data.world.base.SpatialConnection;
import de.nb.aventiure2.data.world.gameobject.*;
import de.nb.aventiure2.data.world.syscomp.spatialconnection.AbstractSpatialConnectionComp;
import de.nb.aventiure2.data.world.syscomp.state.IHasStateGO;
import de.nb.aventiure2.data.world.syscomp.state.impl.RapunzelState;
import de.nb.aventiure2.german.description.TimedDescription;

import static de.nb.aventiure2.data.world.base.SpatialConnection.con;
import static de.nb.aventiure2.data.world.gameobject.World.*;
import static de.nb.aventiure2.data.world.time.AvTimeSpan.*;
import static de.nb.aventiure2.german.base.StructuralElement.PARAGRAPH;
import static de.nb.aventiure2.german.base.StructuralElement.SENTENCE;
import static de.nb.aventiure2.german.description.DescriptionBuilder.du;
import static de.nb.aventiure2.german.description.DescriptionBuilder.neuerSatz;

/**
 * An implementation of {@link AbstractSpatialConnectionComp}
 * for the {@link World#VOR_DEM_ALTEN_TURM}
 * room.
 */
@ParametersAreNonnullByDefault
public class VorDemTurmConnectionComp extends AbstractSpatialConnectionComp {
    public static final String COUNTER_ALTER_TURM_UMRUNDET =
            "VorDemTurmConnectionComp_AlterTurm_Umrundet";

    public VorDemTurmConnectionComp(
            final AvDatabase db,
            final Narrator n, final World world) {
        super(VOR_DEM_ALTEN_TURM, db, n, world);
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
        final ImmutableList.Builder<SpatialConnection> res = ImmutableList.builder();

        res.add(con(VOR_DEM_ALTEN_TURM,
                "auf der anderen Seite des Turms",
                "Um den Turm herumgehen",
                secs(90),
                this::getDescTo_VorDemTurm
        ));
        res.add(con(IM_WALD_NAHE_DEM_SCHLOSS,
                "auf dem Pfad",
                "Den Pfad zurückgehen",
                mins(18),
                du(PARAGRAPH, "gehst",
                        "den langen Pfad wieder zurück, den Hügel hinab, bis "
                                + "zum Waldweg", mins(20))
                        .beendet(PARAGRAPH),
                du(PARAGRAPH, "gehst", "den Hügel auf dem gewundenen Pfad wieder hinab, "
                        + "bis du unten am Waldweg ankommst", mins(18))
                        .komma()
                        .undWartest()
        ));

        if (((IHasStateGO<RapunzelState>) world.load(RAPUNZEL)).stateComp()
                .hasState(RapunzelState.HAARE_VOM_TURM_HERUNTERGELASSEN)) {
            res.add(con(OBEN_IM_ALTEN_TURM,
                    "an den Zöpfen",
                    "An den Haaren hinaufsteigen",
                    mins(1),
                    du("steigst",
                            "hinauf.\n"
                                    + "Durch das Fensterchen kletterst du in eine kleine Kammer: "
                                    + "Tisch, Stuhl, ein Bett",
                            mins(1)),
                    du(SENTENCE, "steigst",
                            "vorsichtig hinauf und klettest durch das Fensterchen "
                                    + "in eine kleine Kammer. Alles ist dunkel, ein Bett kannst "
                                    + "du wohl erkennen",
                            "vorsichtig",
                            mins(2)),
                    du("steigst",
                            "wieder hinauf. Im Hellen siehst du, dass die Kammer nur "
                                    + "sehr einfach eingerichtet ist: Ein Tisch, ein Stuhl, "
                                    + "ein Bett",
                            mins(1)),
                    neuerSatz("In einem Augenblick bist du oben",
                            secs(45))));
        }

        return res.build();
    }

    @CheckReturnValue
    private TimedDescription<?> getDescTo_VorDemTurm(
            final Known newLocationKnown, final Lichtverhaeltnisse lichtverhaeltnisse) {
        final int count = db.counterDao().get(
                VorDemTurmConnectionComp.COUNTER_ALTER_TURM_UMRUNDET);
        switch (count) {
            case 0:
                if (world.loadSC().memoryComp().isKnown(RAPUNZELS_GESANG) &&
                        !world.loadSC().memoryComp().isKnown(RAPUNZEL)) {
                    return du("möchtest", "zu der süßen Stimme hinaufsteigen, "
                            + "und suchst rundherum nach einer Türe des Turms, aber es ist keine "
                            + "zu finden", mins(2), COUNTER_ALTER_TURM_UMRUNDET)
                            .dann();
                }

                if (!world.loadSC().memoryComp().isKnown(RAPUNZELS_HAARE)) {
                    return du("gehst", "einmal um den Turm herum. Es ist keine "
                                    + "Türe zu sehen, nur ganz oben ein kleines Fensterchen",
                            mins(2), COUNTER_ALTER_TURM_UMRUNDET);
                }

                return du("gehst", "einmal um den Turm herum, findest "
                        + "aber nicht die kleinste Tür", secs(90), COUNTER_ALTER_TURM_UMRUNDET)
                        .dann();
            case 1:
                return du("schaust", "noch einmal um den Turm, ob dir vielleicht "
                        + "eine Tür entgangen wäre – nichts", mins(2), COUNTER_ALTER_TURM_UMRUNDET)
                        .dann();
            default:
                return du("gehst", "noch einmal um den Turm herum",
                        mins(1), COUNTER_ALTER_TURM_UMRUNDET)
                        .dann();
        }
    }
}