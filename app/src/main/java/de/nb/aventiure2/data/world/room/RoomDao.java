package de.nb.aventiure2.data.world.room;

import androidx.annotation.Nullable;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

/**
 * DAO for {@link AvRoom}s.
 */
@Dao
public abstract class RoomDao {
    public void setKnown(final AvRoom room) {
        insert(new KnownRoom(room));
    }

    public boolean isKnownSync(final AvRoom room) {
        return getKnownRoom(room) != null;
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract void insert(KnownRoom room);

    @Query("SELECT room from KnownRoom where :room = room")
    @Nullable
    abstract AvRoom getKnownRoom(AvRoom room);
}
