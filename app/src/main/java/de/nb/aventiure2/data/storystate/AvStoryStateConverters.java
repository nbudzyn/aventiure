package de.nb.aventiure2.data.storystate;

import androidx.room.TypeConverter;

public class AvStoryStateConverters {
    @TypeConverter
    public static StoryState.StructuralElement stringToStartsNew(final String string) {
        if (string == null) {
            return null;
        }
        return StoryState.StructuralElement.valueOf(string);
    }

    @TypeConverter
    public static String startsNewToString(final StoryState.StructuralElement startsNew) {
        if (startsNew == null) {
            return null;
        }

        return startsNew.name();
    }
}
