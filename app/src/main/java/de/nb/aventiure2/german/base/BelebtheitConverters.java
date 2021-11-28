package de.nb.aventiure2.german.base;

import androidx.annotation.Nullable;
import androidx.room.TypeConverter;

public class BelebtheitConverters {
    @TypeConverter
    @Nullable
    public static Belebtheit stringToEnum(@Nullable final String string) {
        if (string == null) {
            return null;
        }
        return Belebtheit.valueOf(string);
    }

    @TypeConverter
    @Nullable
    public static String enumToString(@Nullable final Belebtheit eenum) {
        if (eenum == null) {
            return null;
        }

        return eenum.name();
    }
}
