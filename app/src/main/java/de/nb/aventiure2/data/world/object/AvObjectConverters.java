package de.nb.aventiure2.data.world.object;

import androidx.room.TypeConverter;

public class AvObjectConverters {
    @TypeConverter
    public static AvObject stringToObject(final String string) {
        if (string == null) {
            return null;
        }
        return AvObject.get(AvObject.Key.valueOf(string));
    }

    @TypeConverter
    public static String objectToString(final AvObject object) {
        if (object == null) {
            return null;
        }

        return object.getKey().name();
    }
}
