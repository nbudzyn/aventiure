package de.nb.aventiure2.data.world.player.stats;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * The player character's stats.
 */
@Entity
public class PlayerStats {
    @PrimaryKey // Something has to be the primary key
    @NonNull
    private final PlayerStateOfMind stateOfMind;

    public PlayerStats(@NonNull final PlayerStateOfMind stateOfMind) {
        this.stateOfMind = stateOfMind;
    }

    @NonNull
    public PlayerStateOfMind getStateOfMind() {
        return stateOfMind;
    }
}
