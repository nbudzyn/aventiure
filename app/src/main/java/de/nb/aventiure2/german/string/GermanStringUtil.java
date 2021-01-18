package de.nb.aventiure2.german.string;

import com.google.common.collect.ImmutableList;

import java.util.Collection;
import java.util.Locale;

public class GermanStringUtil {
    public static String uncapitalize(final String str) {
        if (str.isEmpty()) {
            return "";
        }

        return str.substring(0, 1).toLowerCase(Locale.GERMAN) + str.substring(1);
    }

    public static ImmutableList<String> capitalize(final Collection<String> strings) {
        return strings.stream()
                .map(GermanStringUtil::capitalize)
                .collect(ImmutableList.toImmutableList());
    }

    public static String capitalize(final String str) {
        if (str.isEmpty()) {
            return "";
        }

        return str.substring(0, 1).toUpperCase(Locale.GERMAN) + str.substring(1);
    }
}
