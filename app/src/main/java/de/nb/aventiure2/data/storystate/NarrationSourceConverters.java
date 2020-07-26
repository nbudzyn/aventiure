package de.nb.aventiure2.data.storystate;

import androidx.room.TypeConverter;

import de.nb.aventiure2.data.storystate.StoryState.NarrationSource;

public class NarrationSourceConverters {
    @TypeConverter
    public static NarrationSource stringToEnum(final String string) {
        if (string == null) {
            return null;
        }
        return NarrationSource.valueOf(string);
    }

    @TypeConverter
    public static String enumToString(final NarrationSource eenum) {
        if (eenum == null) {
            return null;
        }

        return eenum.name();
    }
}
