package de.nb.aventiure2.data.world.syscomp.spatialconnection.impl;

import androidx.annotation.NonNull;

import com.google.common.collect.ImmutableList;

import javax.annotation.ParametersAreNonnullByDefault;

import de.nb.aventiure2.data.database.AvDatabase;
import de.nb.aventiure2.data.world.base.GameObjectId;
import de.nb.aventiure2.data.world.gameobject.World;
import de.nb.aventiure2.data.world.syscomp.memory.Known;
import de.nb.aventiure2.data.world.syscomp.spatialconnection.AbstractSpatialConnectionComp;
import de.nb.aventiure2.data.world.syscomp.storingplace.Lichtverhaeltnisse;

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
            final AvDatabase db,
            final World world,
            final SpatialConnection... connections) {
        this(gameObjectId, db, world, ImmutableList.copyOf(connections));
    }

    public SimpleConnectionComp(
            final GameObjectId gameObjectId,
            final AvDatabase db,
            final World world,
            final Iterable<SpatialConnection> connections) {
        super(gameObjectId, db, world);
        this.connections = ImmutableList.copyOf(connections);
    }

    @Override
    public boolean isAlternativeMovementDescriptionAllowed(final GameObjectId to,
                                                           final Known newRoomKnown,
                                                           final Lichtverhaeltnisse lichtverhaeltnisseInNewRoom) {
        return true;
    }

    @Override
    @NonNull
    public ImmutableList<SpatialConnection> getConnections() {
        return connections;
    }
}
