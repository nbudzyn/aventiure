package de.nb.aventiure2.data.world.syscomp.feelings;

import androidx.annotation.Nullable;
import androidx.room.TypeConverter;

public class MoodConverters {
    @TypeConverter
    @Nullable
    public static Mood stringToEnum(@Nullable final String string) {
        if (string == null) {
            return null;
        }
        return Mood.valueOf(string);
    }

    @TypeConverter
    @Nullable
    public static String enumToString(@Nullable final Mood mood) {
        if (mood == null) {
            return null;
        }

        return mood.name();
    }
}
