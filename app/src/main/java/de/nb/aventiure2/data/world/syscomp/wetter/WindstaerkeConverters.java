package de.nb.aventiure2.data.world.syscomp.wetter;

import androidx.annotation.Nullable;
import androidx.room.TypeConverter;

public class WindstaerkeConverters {
    @TypeConverter
    @Nullable
    public static Windstaerke stringToEnum(@Nullable final String string) {
        if (string == null) {
            return null;
        }
        return Windstaerke.valueOf(string);
    }

    @TypeConverter
    @Nullable
    public static String enumToString(@Nullable final Windstaerke eenum) {
        if (eenum == null) {
            return null;
        }

        return eenum.name();
    }
}
