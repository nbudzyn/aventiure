package de.nb.aventiure2.data.world.player.stats;

import androidx.room.TypeConverter;

public class ScStateOfMindConverters {
    @TypeConverter
    public static ScStateOfMind stringToPlayerStateOfMind(final String string) {
        if (string == null) {
            return null;
        }
        return ScStateOfMind.valueOf(string);
    }

    @TypeConverter
    public static String playerStateOfMindToString(final ScStateOfMind scStateOfMind) {
        if (scStateOfMind == null) {
            return null;
        }

        return scStateOfMind.name();
    }
}
