package de.nb.aventiure2.data.world.invisible;

import androidx.room.TypeConverter;

public class InvisibleStateConverters {
    @TypeConverter
    public static InvisibleState stringToInvisibleState(final String string) {
        if (string == null) {
            return null;
        }
        return InvisibleState.valueOf(string);
    }

    @TypeConverter
    public static String invisibleStateToString(final InvisibleState invisibleState) {
        if (invisibleState == null) {
            return null;
        }

        return invisibleState.name();
    }
}
