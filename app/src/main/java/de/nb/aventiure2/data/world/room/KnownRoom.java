package de.nb.aventiure2.data.world.room;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * A Room the player already knows (and recognizes).
 */
@Entity
public class KnownRoom {
    @NonNull
    @PrimaryKey
    private final AvRoom room;

    KnownRoom(@NonNull final AvRoom room) {
        this.room = room;
    }

    @NonNull
    public AvRoom getRoom() {
        return room;
    }
}
