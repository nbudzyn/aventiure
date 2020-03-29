package de.nb.aventiure2.playeraction.action.room.connection;

import java.util.List;

import de.nb.aventiure2.data.world.room.AvRoom;

public class RoomConnections {
    public static List<RoomConnection> getFrom(final AvRoom from) {
        final RoomConnectionBuilder roomConnectionBuilder =
                new RoomConnectionBuilder(from);

        return roomConnectionBuilder.getConnections();
    }
}
