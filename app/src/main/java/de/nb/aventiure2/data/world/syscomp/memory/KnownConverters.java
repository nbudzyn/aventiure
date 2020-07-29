package de.nb.aventiure2.data.world.syscomp.memory;

import androidx.room.TypeConverter;

import de.nb.aventiure2.data.world.base.Known;

public class KnownConverters {
    @TypeConverter
    public static Known stringToEnum(final String string) {
        if (string == null) {
            return null;
        }
        return Known.valueOf(string);
    }

    @TypeConverter
    public static String enumToString(final Known known) {
        if (known == null) {
            return null;
        }

        return known.name();
    }
}
