package de.nb.aventiure2.data.world.entity.creature;

import androidx.room.TypeConverter;

public class CreatureStateConverters {
    @TypeConverter
    public static CreatureState stringToCreatureState(final String string) {
        if (string == null) {
            return null;
        }
        return CreatureState.valueOf(string);
    }

    @TypeConverter
    public static String creatureStateToString(final CreatureState creatureState) {
        if (creatureState == null) {
            return null;
        }

        return creatureState.name();
    }
}
