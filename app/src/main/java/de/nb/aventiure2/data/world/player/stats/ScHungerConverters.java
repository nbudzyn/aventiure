package de.nb.aventiure2.data.world.player.stats;

import androidx.room.TypeConverter;

public class ScHungerConverters {
    @TypeConverter
    public static ScHunger stringToEnum(final String string) {
        if (string == null) {
            return null;
        }
        return ScHunger.valueOf(string);
    }

    @TypeConverter
    public static String enumToString(final ScHunger eenum) {
        if (eenum == null) {
            return null;
        }

        return eenum.name();
    }
}
