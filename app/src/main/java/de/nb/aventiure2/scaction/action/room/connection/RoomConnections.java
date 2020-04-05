package de.nb.aventiure2.scaction.action.room.connection;

import java.util.List;

import de.nb.aventiure2.data.database.AvDatabase;
import de.nb.aventiure2.data.world.room.AvRoom;

public class RoomConnections {
    public static List<RoomConnection> getFrom(final AvDatabase db,
                                               final AvRoom from) {
        final RoomConnectionBuilder roomConnectionBuilder =
                new RoomConnectionBuilder(db, from);

        return roomConnectionBuilder.getConnections();
    }
}
