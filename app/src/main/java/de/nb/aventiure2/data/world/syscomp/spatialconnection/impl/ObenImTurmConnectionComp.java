package de.nb.aventiure2.data.world.syscomp.spatialconnection.impl;

import static de.nb.aventiure2.data.time.AvTimeSpan.mins;
import static de.nb.aventiure2.data.time.AvTimeSpan.secs;
import static de.nb.aventiure2.data.world.base.SpatialConnection.con;
import static de.nb.aventiure2.data.world.gameobject.World.*;
import static de.nb.aventiure2.data.world.syscomp.spatialconnection.impl.ObenImTurmConnectionComp.Counter.HERABGESTIEGEN;
import static de.nb.aventiure2.german.base.NumerusGenus.PL_MFN;
import static de.nb.aventiure2.german.base.StructuralElement.WORD;
import static de.nb.aventiure2.german.description.DescriptionBuilder.du;
import static de.nb.aventiure2.german.praedikat.VerbSubj.HINABKLETTERN;
import static de.nb.aventiure2.german.praedikat.VerbSubj.HINABSTEIGEN;

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
import de.nb.aventiure2.data.world.syscomp.state.impl.RapunzelState;
import de.nb.aventiure2.german.description.TimedDescription;
import de.nb.aventiure2.german.praedikat.AdvAngabeSkopusVerbAllg;

/**
 * An implementation of {@link AbstractSpatialConnectionComp}
 * for the {@link World#OBEN_IM_ALTEN_TURM}
 * room.
 */
@ParametersAreNonnullByDefault
public class ObenImTurmConnectionComp extends AbstractSpatialConnectionComp {
    @SuppressWarnings({"unused", "RedundantSuppression"})
    enum Counter {
        HERABGESTIEGEN
    }

    public ObenImTurmConnectionComp(
            final AvDatabase db, final TimeTaker timeTaker,
            final Narrator n,
            final World world) {
        super(OBEN_IM_ALTEN_TURM, db, timeTaker, n, world);
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

        if (world.<IHasStateGO<RapunzelState>>loadRequired(RAPUNZEL).stateComp()
                .hasState(RapunzelState.HAARE_VOM_TURM_HERUNTERGELASSEN)) {
            res.add(con(VOR_DEM_ALTEN_TURM,
                    "an den Zöpfen",
                    "An den Haaren hinabsteigen",
                    secs(90),
                    this::getDescTo_VorDemTurm));
        }

        return res.build();
    }

    @CheckReturnValue
    private TimedDescription<?> getDescTo_VorDemTurm(
            final Known newLocationKnown, final Lichtverhaeltnisse lichtverhaeltnisse) {
        if (db.counterDao().get(HERABGESTIEGEN) % 2 == 1) {
            // 2.Mal, 4. Mal, ...
            return du(WORD, "bist", "schnell wieder hinab")
                    .mitVorfeldSatzglied("schnell")
                    .schonLaenger()
                    .timed(secs(30))
                    .withCounterIdIncrementedIfTextIsNarrated(HERABGESTIEGEN)
                    .undWartest()
                    .dann();
        }

        if (n.isAnaphorischerBezugMoeglich(RAPUNZELS_HAARE)) {
            return du(WORD, HINABSTEIGEN
                    .mitAdvAngabe(new AdvAngabeSkopusVerbAllg("daran")))
                    .phorikKandidat(PL_MFN, RAPUNZELS_HAARE)
                    .timed(mins(1))
                    .withCounterIdIncrementedIfTextIsNarrated(HERABGESTIEGEN)
                    .undWartest()
                    .dann();
        }

        return du(WORD, HINABKLETTERN
                .mitAdvAngabe(new AdvAngabeSkopusVerbAllg("an den Haaren")))
                .phorikKandidat(PL_MFN, RAPUNZELS_HAARE)
                .timed(mins(1))
                .withCounterIdIncrementedIfTextIsNarrated(HERABGESTIEGEN)
                .undWartest()
                .dann();
    }
}