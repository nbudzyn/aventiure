package de.nb.aventiure2.german.string;

import java.util.Locale;

public class GermanStringUtil {
    public static String uncapitalize(final String str) {
        if (str.isEmpty()) {
            return "";
        }

        return str.substring(0, 1).toLowerCase(Locale.GERMAN) + str.substring(1);
    }

    public static String capitalize(final String str) {
        if (str.isEmpty()) {
            return "";
        }

        return str.substring(0, 1).toUpperCase(Locale.GERMAN) + str.substring(1);
    }
}
