package de.nb.aventiure2.data.world.invisible;

import androidx.room.TypeConverter;

import de.nb.aventiure2.data.world.base.GameObjectId;

public class InvisibleConverters {
    @TypeConverter
    public static Invisible longToInvisible(final Long aLong) {
        if (aLong == null) {
            return null;
        }
        return Invisibles.get(new GameObjectId(aLong));
    }

    @TypeConverter
    public static Long invisibleToLong(final Invisible invisble) {
        if (invisble == null) {
            return null;
        }

        return invisble.getId().toLong();
    }
}
