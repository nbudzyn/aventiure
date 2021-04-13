package de.nb.aventiure2.data.world.syscomp.wetter.bewoelkung;

import androidx.annotation.Nullable;
import androidx.room.TypeConverter;

public class BewoelkungConverters {
    @TypeConverter
    @Nullable
    public static Bewoelkung stringToEnum(@Nullable final String string) {
        if (string == null) {
            return null;
        }
        return Bewoelkung.valueOf(string);
    }

    @TypeConverter
    @Nullable
    public static String enumToString(@Nullable final Bewoelkung eenum) {
        if (eenum == null) {
            return null;
        }

        return eenum.name();
    }
}
