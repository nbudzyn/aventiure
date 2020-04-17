package de.nb.aventiure2.data.world.room;

import androidx.room.TypeConverter;

import de.nb.aventiure2.data.world.base.GameObjectId;

public class AvRoomConverters {
    @TypeConverter
    public static AvRoom longToRoom(final Long aLong) {
        if (aLong == null) {
            return null;
        }
        return Rooms.get(new GameObjectId(aLong));
    }

    @TypeConverter
    public static Long roomToString(final AvRoom room) {
        if (room == null) {
            return null;
        }

        return room.getId().toLong();
    }
}
