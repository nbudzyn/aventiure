package de.nb.aventiure2.playeraction.action.room.connection;

import java.util.Map;

import de.nb.aventiure2.data.world.room.AvRoom;

public class RoomConnections {
    public static Map<AvRoom, RoomConnection> getFrom(final AvRoom from) {
        final RoomConnectionBuilder roomConnectionBuilder =
                new RoomConnectionBuilder(from);

        return roomConnectionBuilder.getPossibleConnections();
    }
}
