package de.nb.aventiure2.data.world.syscomp.wetter.blitzunddonner;

import androidx.annotation.Nullable;
import androidx.room.TypeConverter;

public class BlitzUndDonnerConverters {
    @TypeConverter
    @Nullable
    public static BlitzUndDonner stringToEnum(@Nullable final String string) {
        if (string == null) {
            return null;
        }
        return BlitzUndDonner.valueOf(string);
    }

    @TypeConverter
    @Nullable
    public static String enumToString(@Nullable final BlitzUndDonner eenum) {
        if (eenum == null) {
            return null;
        }

        return eenum.name();
    }
}
