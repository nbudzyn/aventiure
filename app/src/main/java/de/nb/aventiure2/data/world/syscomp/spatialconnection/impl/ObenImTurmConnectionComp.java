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
import de.nb.aventiure2.data.world.gameobject.*;
import de.nb.aventiure2.data.world.syscomp.spatialconnection.AbstractSpatialConnectionComp;
import de.nb.aventiure2.data.world.syscomp.state.IHasStateGO;
import de.nb.aventiure2.data.world.syscomp.state.impl.RapunzelState;
import de.nb.aventiure2.german.description.AbstractDescription;

import static de.nb.aventiure2.data.world.base.SpatialConnection.con;
import static de.nb.aventiure2.data.world.gameobject.World.*;
import static de.nb.aventiure2.data.world.time.AvTimeSpan.*;
import static de.nb.aventiure2.german.description.DuDescriptionBuilder.du;

/**
 * An implementation of {@link AbstractSpatialConnectionComp}
 * for the {@link World#OBEN_IM_ALTEN_TURM}
 * room.
 */
@ParametersAreNonnullByDefault
public class ObenImTurmConnectionComp extends AbstractSpatialConnectionComp {
    private static final String COUNTER_HERABGESTIEGEN =
            "ObenImTurmConnectionComp_Herabgestiegen";

    public ObenImTurmConnectionComp(
            final AvDatabase db,
            final World world) {
        super(OBEN_IM_ALTEN_TURM, db, world);
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

        if (((IHasStateGO<RapunzelState>) world.load(RAPUNZEL)).stateComp()
                .hasState(RapunzelState.HAARE_VOM_TURM_HERUNTERGELASSEN)) {
            res.add(con(VOR_DEM_ALTEN_TURM,
                    "an den Zöpfen",
                    "An den Haaren hinabsteigen",
                    secs(90),
                    this::getDescTo_VorDemTurm));
        }

        return res.build();
    }

    private AbstractDescription<?> getDescTo_VorDemTurm(
            final Known newLocationKnown, final Lichtverhaeltnisse lichtverhaeltnisse) {
        if (db.counterDao().incAndGet(COUNTER_HERABGESTIEGEN) % 2 == 0) {
            // 2.Mal, 4. Mal, ...
            return du("bist",
                    "schnell wieder hinab",
                    "schnell",
                    secs(30))
                    .undWartest()
                    .dann();
        }

        if (db.narrationDao().requireNarration().isAnaphorischerBezugMoeglich(RAPUNZELS_HAARE)) {
            return du("steigst",
                    "daran hinab",
                    mins(1))
                    .undWartest()
                    .dann();
        }

        return du("steigst",
                "wieder hinab",
                mins(1))
                .undWartest()
                .dann();
    }
}