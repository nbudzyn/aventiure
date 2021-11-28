package de.nb.aventiure2.data.narration;

import androidx.annotation.Nullable;
import androidx.room.TypeConverter;

import de.nb.aventiure2.data.world.base.GameObjectId;
import de.nb.aventiure2.german.base.IBezugsobjekt;

public class GameObjectIdIBezugsobjektConverters {
    @TypeConverter
    @Nullable
    public static IBezugsobjekt longToBezugsobjekt(@Nullable final Long llong) {
        if (llong == null) {
            return null;
        }

        return new GameObjectId(llong);
    }

    @TypeConverter
    @Nullable
    public static Long bezugsobjektToLong(@Nullable final IBezugsobjekt bezugsobjekt) {
        if (bezugsobjekt == null) {
            return null;
        }

        if (!(bezugsobjekt instanceof GameObjectId)) {
            throw new IllegalArgumentException(
                    "This converter only takes GameObjectIds, no other IBezugsobjekt instances");
        }

        return ((GameObjectId) bezugsobjekt).toLong();
    }
}
