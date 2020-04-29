package de.nb.aventiure2.german.base;

import androidx.annotation.Nullable;

import com.google.common.base.Joiner;
import com.google.common.base.Strings;

import static java.util.Arrays.asList;

/**
 * Static helper methods for the german language.
 */
public class GermanUtil {
    // Not to be called
    private GermanUtil() {
    }

    public static String capitalize(final String str) {
        if (str.isEmpty()) {
            return "";
        }

        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    public static String uncapitalize(final String str) {
        if (str.isEmpty()) {
            return "";
        }

        return str.substring(0, 1).toLowerCase() + str.substring(1);
    }

    @Nullable
    public static String joinToNull(final Object... parts) {
        return joinToNull(asList(parts));
    }

    @Nullable
    public static String joinToNull(final Iterable<?> parts) {
        return Strings.emptyToNull(
                Joiner.on(" ").skipNulls().join(parts)
        );
    }
}
