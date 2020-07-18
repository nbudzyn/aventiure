package de.nb.aventiure2.data.world.time;

import androidx.room.TypeConverter;

public class AvTimeSpanConverters {
    @TypeConverter
    public static AvTimeSpan longToAvTimeSpan(final Long aLong) {
        if (aLong == null) {
            return null;
        }
        return AvTimeSpan.secs(aLong);
    }

    @TypeConverter
    public static Long avTimeSpanToLong(final AvTimeSpan timeSpan) {
        if (timeSpan == null) {
            return null;
        }

        return timeSpan.getSecs();
    }
}
