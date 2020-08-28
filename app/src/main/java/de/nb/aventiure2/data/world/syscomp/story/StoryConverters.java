package de.nb.aventiure2.data.world.syscomp.story;

import androidx.annotation.Nullable;
import androidx.room.TypeConverter;

public class StoryConverters {
    @TypeConverter
    @Nullable
    public static Story stringToEnum(@Nullable final String string) {
        if (string == null) {
            return null;
        }
        return Story.valueOf(string);
    }

    @TypeConverter
    @Nullable
    public static String enumToString(@Nullable final Story story) {
        if (story == null) {
            return null;
        }

        return story.name();
    }
}
