package de.nb.aventiure2.data.world.syscomp.memory;

import androidx.room.TypeConverter;

public class InteractionTypeConverters {
    @TypeConverter
    public static Action.Type stringToEnum(final String string) {
        if (string == null) {
            return null;
        }
        return Action.Type.valueOf(string);
    }

    @TypeConverter
    public static String enumToString(final Action.Type type) {
        if (type == null) {
            return null;
        }

        return type.name();
    }
}
