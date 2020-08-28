package de.nb.aventiure2.data.world.syscomp.memory;

import androidx.annotation.Nullable;
import androidx.room.TypeConverter;

public class ActionTypeConverters {
    @TypeConverter
    @Nullable
    public static Action.Type stringToEnum(@Nullable final String string) {
        if (string == null) {
            return null;
        }
        return Action.Type.valueOf(string);
    }

    @TypeConverter
    @Nullable
    public static String enumToString(@Nullable final Action.Type type) {
        if (type == null) {
            return null;
        }

        return type.name();
    }
}
