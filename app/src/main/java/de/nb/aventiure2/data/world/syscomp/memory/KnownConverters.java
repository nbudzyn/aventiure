package de.nb.aventiure2.data.world.syscomp.memory;

import androidx.annotation.Nullable;
import androidx.room.TypeConverter;

import de.nb.aventiure2.data.world.base.Known;

public class KnownConverters {
    @TypeConverter
    @Nullable
    public static Known stringToEnum(@Nullable final String string) {
        if (string == null) {
            return null;
        }
        return Known.valueOf(string);
    }

    @TypeConverter
    @Nullable
    public static String enumToString(@Nullable final Known known) {
        if (known == null) {
            return null;
        }

        return known.name();
    }
}
