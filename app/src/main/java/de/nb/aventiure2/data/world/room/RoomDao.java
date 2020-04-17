package de.nb.aventiure2.data.world.room;

import androidx.annotation.Nullable;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import javax.annotation.Nonnull;

/**
 * DAO for {@link AvRoom}s.
 */
@Dao
public abstract class RoomDao {
    public void setKnown(final AvRoom.Key room, final RoomKnown known) {
        setKnown(AvRoom.get(room), known);
    }

    public void setKnown(final AvRoom room, final RoomKnown known) {
        insert(new RoomData(room, known));
    }

    public @Nonnull
    RoomKnown getKnown(final AvRoom.Key room) {
        return getKnown(AvRoom.get(room));
    }

    public @Nonnull
    RoomKnown getKnown(final AvRoom room) {
        @Nullable final RoomData roomData = getRoomData(room);
        if (roomData == null) {
            return RoomKnown.UNKNOWN;
        }

        return roomData.getKnown();
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract void insert(RoomData roomData);

    @Query("SELECT * from RoomData where :room = room")
    @Nullable
    abstract RoomData getRoomData(AvRoom room);
}
