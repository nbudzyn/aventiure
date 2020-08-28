package de.nb.aventiure2.german.base;

import androidx.annotation.Nullable;
import androidx.room.TypeConverter;

public class NumerusGenusConverters {
    @TypeConverter
    @Nullable
    public static NumerusGenus stringToEnum(@Nullable final String string) {
        if (string == null) {
            return null;
        }
        return NumerusGenus.valueOf(string);
    }

    @TypeConverter
    @Nullable
    public static String enumToString(@Nullable final NumerusGenus type) {
        if (type == null) {
            return null;
        }

        return type.name();
    }
}
