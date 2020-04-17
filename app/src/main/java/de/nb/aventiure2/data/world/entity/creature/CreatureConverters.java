package de.nb.aventiure2.data.world.entity.creature;

import androidx.room.TypeConverter;

import de.nb.aventiure2.data.world.base.GameObjectId;

public class CreatureConverters {
    @TypeConverter
    public static Creature longToCreature(final Long along) {
        if (along == null) {
            return null;
        }
        return Creature.get(new GameObjectId(along));
    }

    @TypeConverter
    public static Long creatureToLong(final Creature creature) {
        if (creature == null) {
            return null;
        }

        return creature.getId().toLong();
    }
}
