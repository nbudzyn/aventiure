package de.nb.aventiure2.data.narration;

import androidx.annotation.Nullable;
import androidx.room.TypeConverter;

import de.nb.aventiure2.german.base.StructuralElement;

public class StructuralElementConverters {
    @TypeConverter
    @Nullable
    public static StructuralElement stringToStartsNew(@Nullable final String string) {
        if (string == null) {
            return null;
        }
        return StructuralElement.valueOf(string);
    }

    @TypeConverter
    @Nullable
    public static String startsNewToString(@Nullable final StructuralElement startsNew) {
        if (startsNew == null) {
            return null;
        }

        return startsNew.name();
    }
}
