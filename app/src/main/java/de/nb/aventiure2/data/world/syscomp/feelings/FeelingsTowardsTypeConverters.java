package de.nb.aventiure2.data.world.syscomp.feelings;

import androidx.annotation.Nullable;
import androidx.room.TypeConverter;

public class FeelingsTowardsTypeConverters {
    @TypeConverter
    @Nullable
    public static FeelingTowardsType stringToEnum(@Nullable final String string) {
        if (string == null) {
            return null;
        }
        return FeelingTowardsType.valueOf(string);
    }

    @TypeConverter
    @Nullable
    public static String enumToString(@Nullable final FeelingTowardsType eenum) {
        if (eenum == null) {
            return null;
        }

        return eenum.name();
    }
}
