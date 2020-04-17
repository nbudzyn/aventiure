package de.nb.aventiure2.data.world.base;

import androidx.annotation.Nullable;
import androidx.room.TypeConverter;

public class GameObjectIdConverters {
    @TypeConverter
    public static GameObjectId longToObject(final @Nullable Long aLong) {
        if (aLong == null) {
            return null;
        }
        return new GameObjectId(aLong);
    }

    @TypeConverter
    public static @Nullable
    Long objectToLong(final GameObjectId object) {
        if (object == null) {
            return null;
        }

        return object.toLong();
    }
}
