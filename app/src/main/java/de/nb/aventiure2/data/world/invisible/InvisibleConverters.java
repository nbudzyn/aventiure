package de.nb.aventiure2.data.world.invisible;

import androidx.room.TypeConverter;

public class InvisibleConverters {
    @TypeConverter
    public static Invisible stringToInvisible(final String string) {
        if (string == null) {
            return null;
        }
        return Invisible.get(Invisible.Key.valueOf(string));
    }

    @TypeConverter
    public static String invisibleToString(final Invisible invisble) {
        if (invisble == null) {
            return null;
        }

        return invisble.getKey().name();
    }
}
