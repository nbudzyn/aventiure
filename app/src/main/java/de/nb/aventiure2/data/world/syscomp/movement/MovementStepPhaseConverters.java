package de.nb.aventiure2.data.world.syscomp.movement;

import androidx.room.TypeConverter;

import de.nb.aventiure2.data.world.syscomp.movement.MovementStep.Phase;

public class MovementStepPhaseConverters {
    @TypeConverter
    public static Phase stringToEnum(final String string) {
        if (string == null) {
            return null;
        }
        return Phase.valueOf(string);
    }

    @TypeConverter
    public static String enumToString(final Phase phase) {
        if (phase == null) {
            return null;
        }

        return phase.name();
    }
}
