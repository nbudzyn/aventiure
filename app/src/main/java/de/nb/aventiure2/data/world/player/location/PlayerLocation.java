package de.nb.aventiure2.data.world.player.location;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import de.nb.aventiure2.data.world.base.GameObject;
import de.nb.aventiure2.data.world.base.GameObjectId;
import de.nb.aventiure2.data.world.room.Rooms;

/**
 * The player character's location in the world.
 */
@Entity
public class PlayerLocation {
    @PrimaryKey
    @NonNull
    private final GameObject room;

    PlayerLocation(@NonNull final GameObjectId room) {
        this(Rooms.get(room));
    }

    PlayerLocation(@NonNull final GameObject room) {
        this.room = room;
    }

    public GameObject getRoom() {
        return room;
    }
}
