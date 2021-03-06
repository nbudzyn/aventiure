package de.nb.aventiure2.data.world.syscomp.feelings;

import androidx.annotation.Nullable;
import androidx.room.TypeConverter;

public class HungerConverters {
    @TypeConverter
    @Nullable
    public static Hunger stringToEnum(@Nullable final String string) {
        if (string == null) {
            return null;
        }
        return Hunger.valueOf(string);
    }

    @TypeConverter
    @Nullable
    public static String enumToString(@Nullable final Hunger eenum) {
        if (eenum == null) {
            return null;
        }

        return eenum.name();
    }
}
