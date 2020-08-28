package de.nb.aventiure2.data.world.base;

import androidx.annotation.Nullable;
import androidx.room.TypeConverter;

public class GameObjectIdConverters {
    @TypeConverter
    @Nullable
    public static GameObjectId longToObject(final @Nullable Long aLong) {
        if (aLong == null) {
            return null;
        }
        return new GameObjectId(aLong);
    }

    @TypeConverter
    @Nullable
    public static Long objectToLong(@Nullable final GameObjectId object) {
        if (object == null) {
            return null;
        }

        return object.toLong();
    }
}
