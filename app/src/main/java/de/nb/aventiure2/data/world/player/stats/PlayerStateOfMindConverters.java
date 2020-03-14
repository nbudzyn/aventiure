package de.nb.aventiure2.data.world.player.stats;

import androidx.room.TypeConverter;

public class PlayerStateOfMindConverters {
    @TypeConverter
    public static PlayerStateOfMind stringToPlayerStateOfMind(final String string) {
        if (string == null) {
            return null;
        }
        return PlayerStateOfMind.valueOf(string);
    }

    @TypeConverter
    public static String playerStateOfMindToString(final PlayerStateOfMind playerStateOfMind) {
        if (playerStateOfMind == null) {
            return null;
        }

        return playerStateOfMind.name();
    }
}
