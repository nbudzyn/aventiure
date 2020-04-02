package de.nb.aventiure2.data.world.room;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * Changeable data for a room in the world.
 */
@Entity
public class RoomData {
    @NonNull
    @PrimaryKey
    private final AvRoom room;

    @NonNull
    private final RoomKnown known;

    RoomData(@NonNull final AvRoom room, @NonNull final RoomKnown known) {
        this.room = room;
        this.known = known;
    }

    @NonNull
    public AvRoom getRoom() {
        return room;
    }

    @NonNull
    public RoomKnown getKnown() {
        return known;
    }
}
