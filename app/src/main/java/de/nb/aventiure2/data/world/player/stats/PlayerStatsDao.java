package de.nb.aventiure2.data.world.player.stats;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

/**
 * Room DAO for the {@link PlayerStats}.
 */
@Dao
public abstract class PlayerStatsDao {
    @Query("UPDATE PlayerStats SET stateOfMind = :stateOfMind")
    public abstract void setStateOfMind(final PlayerStateOfMind stateOfMind);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void insert(PlayerStats stats);

    @Query("SELECT * from PlayerStats")
    public abstract PlayerStats getPlayerStats();
}
