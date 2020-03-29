package de.nb.aventiure2.data.world.player.inventory;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import de.nb.aventiure2.data.world.entity.object.AvObject;

/**
 * An item the player character's carries with them.
 */
@Entity
public class PlayerInventoryItem {
    @PrimaryKey
    @NonNull
    private final AvObject object;

    PlayerInventoryItem(@NonNull final AvObject object) {
        this.object = object;
    }

    public AvObject getObject() {
        return object;
    }
}
