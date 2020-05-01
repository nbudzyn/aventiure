package de.nb.aventiure2.data.world.syscomp.spatialconnection.builder;

import java.util.List;

import de.nb.aventiure2.data.database.AvDatabase;
import de.nb.aventiure2.data.world.gameobjects.GameObjects;
import de.nb.aventiure2.data.world.syscomp.spatialconnection.ISpatiallyConnectedGO;

public class SpatialConnections {
    public static List<SpatialConnection> getFrom(final AvDatabase db,
                                                  final ISpatiallyConnectedGO from) {
        if (from.is(GameObjects.SCHLOSS_VORHALLE)) {
            return new SchlossVorhalleConnectionBuilder(db).getConnections();
        }

        final SingleSpatialConnectionBuilder singleSpatialConnectionBuilder =
                new SingleSpatialConnectionBuilder(db, from);

        return singleSpatialConnectionBuilder.getConnections();
    }
}
