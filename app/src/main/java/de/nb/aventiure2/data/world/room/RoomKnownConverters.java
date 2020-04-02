package de.nb.aventiure2.data.world.room;

import androidx.room.TypeConverter;

public class RoomKnownConverters {
    @TypeConverter
    public static RoomKnown stringToRoomKnown(final String string) {
        if (string == null) {
            return null;
        }
        return RoomKnown.valueOf(string);
    }

    @TypeConverter
    public static String roomKnownToString(final RoomKnown roomKnown) {
        if (roomKnown == null) {
            return null;
        }

        return roomKnown.name();
    }
}
