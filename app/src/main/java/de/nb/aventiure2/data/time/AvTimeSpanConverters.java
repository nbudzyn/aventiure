package de.nb.aventiure2.data.time;

import androidx.annotation.Nullable;
import androidx.room.TypeConverter;

public class AvTimeSpanConverters {
    @TypeConverter
    @Nullable
    public static AvTimeSpan longToAvTimeSpan(@Nullable final Long aLong) {
        if (aLong == null) {
            return null;
        }
        return AvTimeSpan.secs(aLong);
    }

    @TypeConverter
    @Nullable
    public static Long avTimeSpanToLong(@Nullable final AvTimeSpan timeSpan) {
        if (timeSpan == null) {
            return null;
        }

        return timeSpan.getSecs();
    }
}
