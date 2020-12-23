package de.nb.aventiure2.german.base;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Arrays.asList;
import static java.util.Objects.requireNonNull;

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

            final String partString;
            if (part.getClass().isArray()) {
                partString = joinToNull((Object[]) part);
            } else if (part instanceof Iterable<?>) {
                partString = joinToNull((Iterable<?>) part);
            } else {
                partString = part.toString();
            }
            if (spaceNeeded(res, partString)) {
                res.append(" ");
            }

            if (partString != null) {
                res.append(partString);
            }
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

        final CharSequence lastCharBase = base.subSequence(base.length() - 1, base.length());
        if (" „\n" .contains(lastCharBase)) {
            return false;
        }

        final CharSequence firstCharAddition = addition.subSequence(0, 1);
        return !" ,;.:!?“\n" .contains(firstCharAddition);
    }

    /**
     * Schneidet das Satzglied (einmalig) aus diesem Text. Die Suche nach
     * dem Satzglied beginnt von vorn.
     */
    public static @Nullable
    String cutSatzglied(@Nullable final String text, @Nullable final String satzglied) {
        if (text == null) {
            if (satzglied != null) {
                throw new IllegalArgumentException(
                        "Text null, but Satzglied was \"" + satzglied + "\".");
            }

            return null;
        }

        if (satzglied == null) {
            return text;
        }

        final int startIndex = text.indexOf(satzglied);
        if (startIndex < 0) {
            throw new IllegalArgumentException("Satzglied \"" + satzglied + "\" not contained "
                    + "in \"" + text + "\"");
        }

        return cutSatzglied(text, startIndex, satzglied.length());
    }


    /**
     * Schneidet das Satzglied (einmalig) aus diesem Text;  die Suche nach
     * dem Satzglied beginnt von hinten.
     */
    public static @Nullable
    String cutSatzgliedVonHinten(@Nullable final String text, @Nullable final String satzglied) {
        if (text == null) {
            if (satzglied != null) {
                throw new IllegalArgumentException(
                        "Text null, but Satzglied was \"" + satzglied + "\".");
            }

            return null;
        }

        if (satzglied == null) {
            return text;
        }

        final int startIndex = text.lastIndexOf(satzglied);
        if (startIndex < 0) {
            throw new IllegalArgumentException("Satzglied \"" + satzglied + "\" not contained "
                    + "in \"" + text + "\"");
        }

        return cutSatzglied(text, startIndex, satzglied.length());
    }

    @Nullable
    private static String cutSatzglied(@NonNull final String text, final int startIndex,
                                       final int satzgliedLength) {
        requireNonNull(text, "text");

        @Nullable final String charBefore = startIndex == 0 ?
                null :
                text.substring(startIndex - 1, startIndex);

        final int endIndex = startIndex + satzgliedLength;
        @Nullable final String charAfter = endIndex >= text.length() ?
                null :
                text.substring(endIndex, startIndex + satzgliedLength + 1);

        if (charBefore == null) {
            if (charAfter == null) {
                return null;
            }

            if (charAfter.equals(" ")) {
                return text.substring(endIndex + 1);
            }

            return text.substring(endIndex);
        }

        // charBefore != null
        if (charBefore.equals(" ")) {
            if (charAfter == null) {
                return text.substring(0, startIndex - 1);
            }

            if (charAfter.equals(" ")) {
                return text.substring(0, startIndex - 1) + text.substring(endIndex);
            }

            return text.substring(0, startIndex - 1) + " " + text.substring(endIndex);
        }

        // charBefore != null, !charBefore.equals(" ")
        if (charAfter == null) {
            return text.substring(0, startIndex);
        }

        if (charAfter.equals(" ")) {
            return text.substring(0, startIndex) + text.substring(endIndex + 1);
        }

        return text.substring(0, startIndex) + " " + text.substring(endIndex);
    }

    public static String buildHauptsatz(final String vorfeld, final String verb,
                                        @Nullable final String mittelfeldEtc) {
        return joinToNull(
                capitalize(vorfeld),
                verb,
                mittelfeldEtc);
    }
}
