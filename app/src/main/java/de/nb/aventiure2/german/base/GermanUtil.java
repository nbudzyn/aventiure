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

    /**
     * Fügt diese Teile zu einem String zusammen. Diese Methode darf nur verwendet werden,
     * wenn nach dem letzten der Teile definitiv kein Komma aussteht - oder das
     * ausstehende Kommma auf andere Weise behandelt wird.
     */
    @Nullable
    public static String joinToNullString(final Object... parts) {
        return joinToNullString(asList(parts));
    }

    /**
     * Fügt diese Teile zu einem String zusammen. Diese Methode darf nur verwendet werden,
     * wenn nach dem letzten der Teile definitiv kein Komma aussteht - oder das
     * ausstehende Kommma auf andere Weise behandelt wird.
     */
    @Nullable
    public static String joinToNullString(final Iterable<?> parts) {
        @Nullable final Wortfolge res = Wortfolge.joinToNullWortfolge(parts);
        if (res == null) {
            return null;
        }

        return res.getString();
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

    public static boolean spaceNeeded(@Nullable final CharSequence base,
                                      @Nullable final CharSequence addition) {
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

    static boolean beginnDecktKommaAb(final CharSequence charSequence) {
        requireNonNull(charSequence, "charSequence");
        checkArgument(charSequence.length() > 0, "charSequence was empty");

        final CharSequence firstChar = charSequence.subSequence(0, 1);

        checkArgument(!"\n" .contentEquals(firstChar), "charSequence beginnt mit "
                + "Zeilenwechsel. Hier wäre keine Möglichkeit, syntaktisch korrekt noch ein "
                + "Komma unterzubringen.");

        checkArgument(!"“" .contains(firstChar), "charSequence beginnt "
                + "mit Abführungszeichen. Hier müsste man eigentlich erst das Abführungszeichen "
                + "schreiben und dann das Komma (oder Punkt o.Ä.). Diese Logik ist noch nicht "
                + "implementiert");

        return ",;.:!?" .contains(firstChar);
    }

    /**
     * Schneidet diesen Teil-Text (einmalig) aus diesem Text. Die Suche nach
     * dem Teil-Text beginnt am Anfang des Textes.
     */
    public static @Nullable
    String cutFirst(@Nullable final String text, @Nullable final String part) {
        if (text == null) {
            if (part != null) {
                throw new IllegalArgumentException(
                        "Text null, but Satzglied was \"" + part + "\".");
            }

            return null;
        }

        if (part == null) {
            return text;
        }

        final int startIndex = text.indexOf(part);
        if (startIndex < 0) {
            throw new IllegalArgumentException("Satzglied \"" + part + "\" not contained "
                    + "in \"" + text + "\"");
        }

        return cut(text, startIndex, part.length());
    }

    @Nullable
    private static String cut(@NonNull final String text, final int startIndex,
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
        return joinToNullString(
                capitalize(vorfeld),
                verb,
                mittelfeldEtc);
    }
}