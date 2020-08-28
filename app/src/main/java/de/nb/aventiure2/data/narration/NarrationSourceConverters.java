package de.nb.aventiure2.data.narration;

import androidx.annotation.Nullable;
import androidx.room.TypeConverter;

import de.nb.aventiure2.data.narration.Narration.NarrationSource;

public class NarrationSourceConverters {
    @TypeConverter
    @Nullable
    public static NarrationSource stringToEnum(@Nullable final String string) {
        if (string == null) {
            return null;
        }
        return NarrationSource.valueOf(string);
    }

    @TypeConverter
    @Nullable
    public static String enumToString(@Nullable final NarrationSource eenum) {
        if (eenum == null) {
            return null;
        }

        return eenum.name();
    }
}
