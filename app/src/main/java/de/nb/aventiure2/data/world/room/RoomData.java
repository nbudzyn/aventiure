package de.nb.aventiure2.data.world.room;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import de.nb.aventiure2.data.world.base.GameObject;

/**
 * Changeable data for a room in the world.
 */
@Entity
public class RoomData {
    @NonNull
    @PrimaryKey
    private final GameObject room;

    @NonNull
    private final RoomKnown known;

    RoomData(@NonNull final GameObject room, @NonNull final RoomKnown known) {
        this.room = room;
        this.known = known;
    }

    @NonNull
    public GameObject getRoom() {
        return room;
    }

    @NonNull
    public RoomKnown getKnown() {
        return known;
    }
}
