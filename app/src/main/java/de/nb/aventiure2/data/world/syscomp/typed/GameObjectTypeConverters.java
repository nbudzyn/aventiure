package de.nb.aventiure2.data.world.syscomp.typed;

import androidx.annotation.Nullable;
import androidx.room.TypeConverter;

public class GameObjectTypeConverters {
    @TypeConverter
    @Nullable
    public static GameObjectType stringToEnum(@Nullable final String string) {
        if (string == null) {
            return null;
        }
        return GameObjectType.valueOf(string);
    }

    @TypeConverter
    @Nullable
    public static String enumToString(@Nullable final GameObjectType eenum) {
        if (eenum == null) {
            return null;
        }

        return eenum.name();
    }
}
