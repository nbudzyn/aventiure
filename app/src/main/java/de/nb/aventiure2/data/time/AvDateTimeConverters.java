package de.nb.aventiure2.data.time;

import androidx.annotation.Nullable;
import androidx.room.TypeConverter;

public class AvDateTimeConverters {
    @TypeConverter
    @Nullable
    public static AvDateTime longToAvDateTime(@Nullable final Long aLong) {
        if (aLong == null) {
            return null;
        }
        return new AvDateTime(aLong);
    }

    @TypeConverter
    @Nullable
    public static Long avDateTimeToLong(@Nullable final AvDateTime dateTime) {
        if (dateTime == null) {
            return null;
        }

        return dateTime.getSecsSinceBeginning();
    }
}
