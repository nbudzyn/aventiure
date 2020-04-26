package de.nb.aventiure2.data.world.feelings;

import androidx.room.TypeConverter;

public class HungerConverters {
    @TypeConverter
    public static Hunger stringToEnum(final String string) {
        if (string == null) {
            return null;
        }
        return Hunger.valueOf(string);
    }

    @TypeConverter
    public static String enumToString(final Hunger eenum) {
        if (eenum == null) {
            return null;
        }

        return eenum.name();
    }
}
