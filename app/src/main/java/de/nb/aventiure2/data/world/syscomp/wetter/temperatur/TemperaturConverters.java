package de.nb.aventiure2.data.world.syscomp.wetter.temperatur;

import androidx.annotation.Nullable;
import androidx.room.TypeConverter;

import de.nb.aventiure2.data.world.base.Temperatur;

public class TemperaturConverters {
    @TypeConverter
    @Nullable
    public static Temperatur stringToEnum(@Nullable final String string) {
        if (string == null) {
            return null;
        }
        return Temperatur.valueOf(string);
    }

    @TypeConverter
    @Nullable
    public static String enumToString(@Nullable final Temperatur eenum) {
        if (eenum == null) {
            return null;
        }

        return eenum.name();
    }
}
