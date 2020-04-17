package de.nb.aventiure2.data.world.entity.object;

import androidx.room.TypeConverter;

import de.nb.aventiure2.data.world.base.GameObjectId;

public class AvObjectConverters {
    @TypeConverter
    public static AvObject longToObject(final Long aLong) {
        if (aLong == null) {
            return null;
        }
        return AvObject.get(new GameObjectId(aLong));
    }

    @TypeConverter
    public static Long objectToLong(final AvObject object) {
        if (object == null) {
            return null;
        }

        return object.getId().toLong();
    }
}
