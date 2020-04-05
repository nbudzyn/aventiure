package de.nb.aventiure2.data.world.player.stats;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import de.nb.aventiure2.data.world.time.AvDateTime;

/**
 * Room DAO for the {@link ScStats}.
 */
@Dao
public abstract class ScStatsDao {
    @Query("UPDATE ScStats SET stateOfMind = :stateOfMind")
    public abstract void setStateOfMind(final ScStateOfMind stateOfMind);

    @Query("UPDATE ScStats SET hunger = :hunger")
    public abstract void setHunger(final ScHunger hunger);

    @Query("UPDATE ScStats SET zuletztGegessen = :zuletztGegessen")
    public abstract void setZuletztGegessen(final AvDateTime zuletztGegessen);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void insert(ScStats stats);

    @Query("SELECT * from ScStats")
    public abstract ScStats getPlayerStats();
}
