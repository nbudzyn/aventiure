package de.nb.aventiure2.data.world.syscomp.feelings;

import androidx.room.TypeConverter;

public class MoodConverters {
    @TypeConverter
    public static Mood stringToEnum(final String string) {
        if (string == null) {
            return null;
        }
        return Mood.valueOf(string);
    }

    @TypeConverter
    public static String enumToString(final Mood mood) {
        if (mood == null) {
            return null;
        }

        return mood.name();
    }
}
