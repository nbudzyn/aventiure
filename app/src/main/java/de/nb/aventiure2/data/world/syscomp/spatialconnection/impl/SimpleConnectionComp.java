package de.nb.aventiure2.data.world.syscomp.spatialconnection.impl;

import androidx.annotation.NonNull;

import com.google.common.collect.ImmutableList;

import javax.annotation.ParametersAreNonnullByDefault;

import de.nb.aventiure2.data.database.AvDatabase;
import de.nb.aventiure2.data.world.base.GameObjectId;
import de.nb.aventiure2.data.world.gameobjects.GameObjects;
import de.nb.aventiure2.data.world.lichtverhaeltnisse.Lichtverhaeltnisse;
import de.nb.aventiure2.data.world.syscomp.memory.Known;
import de.nb.aventiure2.data.world.syscomp.spatialconnection.AbstractSpatialConnectionComp;

/**
 * An implementation of {@link AbstractSpatialConnectionComp}
 * for the {@link GameObjects#SCHLOSS_VORHALLE_TISCH_BEIM_FEST}
 * room.
 */
@ParametersAreNonnullByDefault
class SimpleConnectionComp extends AbstractSpatialConnectionComp {
    private final ImmutableList<SpatialConnection> connections;

    SimpleConnectionComp(
            final GameObjectId gameObjectId,
            final AvDatabase db,
            final SpatialConnection... connections) {
        this(gameObjectId, db, ImmutableList.copyOf(connections));
    }

    public SimpleConnectionComp(
            final GameObjectId gameObjectId,
            final AvDatabase db,
            final Iterable<SpatialConnection> connections) {
        super(gameObjectId, db);
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
