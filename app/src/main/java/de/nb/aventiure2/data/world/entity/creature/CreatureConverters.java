package de.nb.aventiure2.data.world.entity.creature;

import androidx.room.TypeConverter;

public class CreatureConverters {
    @TypeConverter
    public static Creature stringToCreature(final String string) {
        if (string == null) {
            return null;
        }
        return Creature.get(Creature.Key.valueOf(string));
    }

    @TypeConverter
    public static String creatureToString(final Creature creature) {
        if (creature == null) {
            return null;
        }

        return creature.getKey().name();
    }
}
