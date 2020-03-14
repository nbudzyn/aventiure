package de.nb.aventiure2.data.world.room;

import androidx.room.TypeConverter;

public class AvRoomConverters {
    @TypeConverter
    public static AvRoom stringToRoom(final String string) {
        if (string == null) {
            return null;
        }
        return AvRoom.valueOf(string);
    }

    @TypeConverter
    public static String roomToString(final AvRoom room) {
        if (room == null) {
            return null;
        }

        return room.name();
    }
}
