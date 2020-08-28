package de.nb.aventiure2.data.world.syscomp.story;

import androidx.annotation.Nullable;
import androidx.room.TypeConverter;

public class StoryStateConverters {
    @TypeConverter
    @Nullable
    public static StoryData.State stringToEnum(@Nullable final String string) {
        if (string == null) {
            return null;
        }
        return StoryData.State.valueOf(string);
    }

    @TypeConverter
    @Nullable
    public static String enumToString(@Nullable final StoryData.State eenum) {
        if (eenum == null) {
            return null;
        }

        return eenum.name();
    }
}
