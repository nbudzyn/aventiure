package de.nb.aventiure2.data.storystate;

import androidx.room.TypeConverter;

public class AvStoryStateConverters {
    @TypeConverter
    public static StoryState.StartsNew stringToStartsNew(final String string) {
        if (string == null) {
            return null;
        }
        return StoryState.StartsNew.valueOf(string);
    }

    @TypeConverter
    public static String startsNewToString(final StoryState.StartsNew startsNew) {
        if (startsNew == null) {
            return null;
        }

        return startsNew.name();
    }
}
