package de.nb.aventiure2.data.world.syscomp.spatialconnection;

import java.util.List;

import de.nb.aventiure2.data.database.AvDatabase;
import de.nb.aventiure2.data.world.base.GameObjectId;
import de.nb.aventiure2.data.world.lichtverhaeltnisse.Lichtverhaeltnisse;
import de.nb.aventiure2.data.world.time.Tageszeit;

import static de.nb.aventiure2.data.world.gameobjects.GameObjects.load;

/**
 * Builds all {@link SpatialConnection}s starting from
 * one {@link ISpatiallyConnectedGO}.
 */
abstract class AbstractSpatialConnectionBuilder {
    protected final AvDatabase db;
    private final GameObjectId gameObjectId;

    AbstractSpatialConnectionBuilder(
            final AvDatabase db, final GameObjectId gameObjectId) {
        this.db = db;
        this.gameObjectId = gameObjectId;
    }

    abstract List<SpatialConnection> getConnections();

    protected ISpatiallyConnectedGO getFrom() {
        return (ISpatiallyConnectedGO) load(db, gameObjectId);
    }

    protected Lichtverhaeltnisse getLichtverhaeltnisseFrom() {
        final Tageszeit tageszeit = db.dateTimeDao().now().getTageszeit();
        return Lichtverhaeltnisse.getLichtverhaeltnisse(tageszeit, getFrom().getId());
    }
}
