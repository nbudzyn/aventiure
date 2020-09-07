package de.nb.aventiure2.german.base;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

import static com.google.common.base.Preconditions.checkArgument;
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
        final StringBuilder res = new StringBuilder();
        for (final Object part : parts) {
            if (part == null) {
                continue;
            }

            final String partString = part.toString();
            if (spaceNeeded(res, partString)) {
                res.append(" ");
            }

            res.append(partString);
        }

        if (res.length() == 0) {
            return null;
        }

        return res.toString();
    }

    /**
     * Gibt eine Aufzählung zurück wie "der hässliche Frosch",
     * "die goldene Kugel und der hässliche Frosch" oder "das schöne Glas, die goldene Kugel und
     * der hässliche Frosch".
     */
    @NonNull
    public static String buildAufzaehlung(final List<String> elemente) {
        checkArgument(!elemente.isEmpty(), "Elemente war leer");

        final StringBuilder res = new StringBuilder();
        for (int i = 0; i < elemente.size(); i++) {
            res.append(elemente.get(i));
            if (i == elemente.size() - 2) {
                // one before the last
                res.append(" und ");
            } else if (i < elemente.size() - 2) {
                // more than one after this
                res.append(", ");
            }
        }

        return res.toString();
    }

    public static boolean spaceNeeded(final CharSequence base, final CharSequence addition) {
        if (base == null || base.length() == 0 ||
                addition == null || addition.length() == 0) {
            return false;
        }

        final CharSequence lastCharBase = base.subSequence(0, base.length() - 1);
        if (" „\n".contains(lastCharBase)) {
            return false;
        }

        final CharSequence firstCharAdditional = addition.subSequence(0, 1);
        return !" ,;.:!?“\n".contains(firstCharAdditional);
    }
}
