package de.nb.aventiure2.data.world.time;

import androidx.room.TypeConverter;

public class AvDateTimeConverters {
    @TypeConverter
    public static AvDateTime longToAvDateTime(final Long aLong) {
        if (aLong == null) {
            return null;
        }
        return new AvDateTime(aLong);
    }

    @TypeConverter
    public static Long avDateTimeToLong(final AvDateTime dateTime) {
        if (dateTime == null) {
            return null;
        }

        return dateTime.getSecsSinceBeginning();
    }
}
