package de.nb.aventiure2.data.world.syscomp.spatialconnection.impl;

import androidx.annotation.NonNull;

import com.google.common.collect.ImmutableList;

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

/**
 * An implementation of {@link AbstractSpatialConnectionComp}
 * for the {@link World#SCHLOSS_VORHALLE_AM_TISCH_BEIM_FEST}
 * room.
 */
@ParametersAreNonnullByDefault
class SimpleConnectionComp extends AbstractSpatialConnectionComp {
    private final ImmutableList<SpatialConnection> connections;

    SimpleConnectionComp(
            final GameObjectId gameObjectId,
            final AvDatabase db, final TimeTaker timeTaker,
            final Narrator n, final World world,
            final SpatialConnection... connections) {
        this(gameObjectId, db, timeTaker, n, world, ImmutableList.copyOf(connections));
    }

    private SimpleConnectionComp(
            final GameObjectId gameObjectId,
            final AvDatabase db, final TimeTaker timeTaker,
            final Narrator n, final World world,
            final Iterable<SpatialConnection> connections) {
        super(gameObjectId, db, timeTaker, n, world);
        this.connections = ImmutableList.copyOf(connections);
    }

    @Override
    public boolean isAlternativeMovementDescriptionAllowed(final GameObjectId to,
                                                           final Known newLocationKnown,
                                                           final Lichtverhaeltnisse lichtverhaeltnisseInNewLocation) {
        return true;
    }

    @Override
    @NonNull
    public ImmutableList<SpatialConnection> getConnections() {
        return connections;
    }
}
