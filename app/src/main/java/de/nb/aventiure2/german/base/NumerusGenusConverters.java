package de.nb.aventiure2.german.base;

import androidx.room.TypeConverter;

public class NumerusGenusConverters {
    @TypeConverter
    public static NumerusGenus stringToEnum(final String string) {
        if (string == null) {
            return null;
        }
        return NumerusGenus.valueOf(string);
    }

    @TypeConverter
    public static String enumToString(final NumerusGenus type) {
        if (type == null) {
            return null;
        }

        return type.name();
    }
}
