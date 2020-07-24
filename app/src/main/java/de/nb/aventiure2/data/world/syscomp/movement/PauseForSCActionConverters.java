package de.nb.aventiure2.data.world.syscomp.movement;

import androidx.room.TypeConverter;

public class PauseForSCActionConverters {
    @TypeConverter
    public static MovementPCD.PauseForSCAction stringToEnum(final String string) {
        if (string == null) {
            return null;
        }
        return MovementPCD.PauseForSCAction.valueOf(string);
    }

    @TypeConverter
    public static String enumToString(final MovementPCD.PauseForSCAction pauseForSCAction) {
        if (pauseForSCAction == null) {
            return null;
        }

        return pauseForSCAction.name();
    }
}
