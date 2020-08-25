package de.nb.aventiure2.data.narration;

import androidx.room.TypeConverter;

import de.nb.aventiure2.german.base.StructuralElement;

public class StructuralElementConverters {
    @TypeConverter
    public static StructuralElement stringToStartsNew(final String string) {
        if (string == null) {
            return null;
        }
        return StructuralElement.valueOf(string);
    }

    @TypeConverter
    public static String startsNewToString(final StructuralElement startsNew) {
        if (startsNew == null) {
            return null;
        }

        return startsNew.name();
    }
}
