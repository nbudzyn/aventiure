package de.nb.aventiure2.data.world.syscomp.spatialconnection.impl;

import androidx.annotation.NonNull;

import com.google.common.collect.ImmutableList;

import java.util.List;

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

import static de.nb.aventiure2.data.time.AvTimeSpan.secs;
import static de.nb.aventiure2.data.world.base.SpatialConnection.conNichtSC;
import static de.nb.aventiure2.data.world.gameobject.World.*;

/**
 * An implementation of {@link AbstractSpatialConnectionComp}
 * for the {@link World#ZWISCHEN_DEN_HECKEN_VOR_DEM_SCHLOSS_EXTERN}
 * room.
 */
@ParametersAreNonnullByDefault
public class ZwischenDenHeckenVorDemSchlossExternConnectionComp
        extends AbstractSpatialConnectionComp {
    public ZwischenDenHeckenVorDemSchlossExternConnectionComp(
            final AvDatabase db, final TimeTaker timeTaker, final Narrator n, final World world) {
        super(ZWISCHEN_DEN_HECKEN_VOR_DEM_SCHLOSS_EXTERN, db, timeTaker, n, world);
    }

    @Override
    public boolean isAlternativeMovementDescriptionAllowed(final GameObjectId to,
                                                           final Known newLocationKnown,
                                                           final Lichtverhaeltnisse lichtverhaeltnisseInNewLocation) {
        return true;
    }

    @Override
    @NonNull
    public List<SpatialConnection> getConnections() {
        final ImmutableList.Builder<SpatialConnection> res = ImmutableList.builder();
        res.add(conNichtSC(DRAUSSEN_VOR_DEM_SCHLOSS,
                "zwischen Hecken und Beeten",
                secs(90)));

        return res.build();
    }
}
