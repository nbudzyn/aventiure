package de.nb.aventiure2.data.world.syscomp.movement;

import androidx.annotation.Nullable;
import androidx.room.TypeConverter;

public class PauseForSCActionConverters {
    @TypeConverter
    @Nullable
    public static MovementPCD.PauseForSCAction stringToEnum(@Nullable final String string) {
        if (string == null) {
            return null;
        }
        return MovementPCD.PauseForSCAction.valueOf(string);
    }

    @TypeConverter
    @Nullable
    public static String enumToString(
            @Nullable final MovementPCD.PauseForSCAction pauseForSCAction) {
        if (pauseForSCAction == null) {
            return null;
        }

        return pauseForSCAction.name();
    }
}
