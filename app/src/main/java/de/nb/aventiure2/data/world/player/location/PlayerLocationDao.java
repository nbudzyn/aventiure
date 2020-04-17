package de.nb.aventiure2.data.world.player.location;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import de.nb.aventiure2.data.world.room.AvRoom;

/**
 * Room DAO for the {@link de.nb.aventiure2.data.world.player.location.PlayerLocation}.
 */
@Dao
public abstract class PlayerLocationDao {
    public void setRoom(final AvRoom room) {
        setRoom(room.getKey());
    }

    public void setRoom(final AvRoom.Key room) {
        deleteAll();

        insert(new PlayerLocation(room));
    }

    @Query("DELETE FROM PlayerLocation")
    abstract void deleteAll();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract void insert(PlayerLocation playerLocation);

    @Query("SELECT * from PlayerLocation")
    public abstract PlayerLocation getPlayerLocation();
}
