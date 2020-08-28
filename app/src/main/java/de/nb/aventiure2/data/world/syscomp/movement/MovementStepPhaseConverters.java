package de.nb.aventiure2.data.world.syscomp.movement;

import androidx.annotation.Nullable;
import androidx.room.TypeConverter;

import de.nb.aventiure2.data.world.syscomp.movement.MovementStep.Phase;

public class MovementStepPhaseConverters {
    @TypeConverter
    @Nullable
    public static Phase stringToEnum(@Nullable final String string) {
        if (string == null) {
            return null;
        }
        return Phase.valueOf(string);
    }

    @TypeConverter
    @Nullable
    public static String enumToString(@Nullable final Phase phase) {
        if (phase == null) {
            return null;
        }

        return phase.name();
    }
}
