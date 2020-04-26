package de.nb.aventiure2.data.world.syscomp.state;

import androidx.room.TypeConverter;

public class GameObjectStateConverters {
    @TypeConverter
    public static GameObjectState stringToState(final String string) {
        if (string == null) {
            return null;
        }
        return GameObjectState.valueOf(string);
    }

    @TypeConverter
    public static String stateToString(final GameObjectState gameObjectState) {
        if (gameObjectState == null) {
            return null;
        }

        return gameObjectState.name();
    }
}
