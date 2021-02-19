package de.nb.aventiure2.german.base;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

import static com.google.common.base.Preconditions.checkArgument;
import static de.nb.aventiure2.german.base.Konstituentenfolge.joinToNullKonstituentenfolge;
import static java.util.Arrays.asList;
import static java.util.Objects.requireNonNull;

/**
 * Static helper methods for the german language.
 */
@SuppressWarnings("BooleanMethodIsAlwaysInverted")
public class GermanUtil {
    // Not to be called
    private GermanUtil() {
    }

    /**
     * Fügt diese Teile zu einem String zusammen, wobei ein nichtleerer
     * * String das Ergebnis sein muss. . Diese Methode darf nur verwendet werden,
     * wenn nach dem letzten der Teile definitiv kein Komma aussteht - oder das
     * ausstehende Kommma auf andere Weise behandelt wird.
     */
    public static String joinToString(final Object... parts) {
        return checkJoiningResultNotNull(joinToNullString(parts), parts);
    }

    private static String checkJoiningResultNotNull(
            @Nullable final String joiningResult,
            final Object... parts) {
        return checkJoiningResultNotNull(joiningResult, asList(parts));
    }

    private static String checkJoiningResultNotNull(
            @Nullable final String joiningResult,
            final Iterable<?> parts) {
        if (joiningResult == null) {
            throw new IllegalStateException("Joining result was null. parts: " + parts);
        }

        return joiningResult;
    }

    /**
     * Fügt diese Teile zu einem String zusammen. Diese Methode darf nur verwendet werden,
     * wenn nach dem letzten der Teile definitiv kein Komma aussteht - oder das
     * ausstehende Kommma auf andere Weise behandelt wird.
     */
    @Nullable
    private static String joinToNullString(final Object... parts) {
        return joinToNullString(asList(parts));
    }

    /**
     * Fügt diese Teile zu einem String zusammen. Diese Methode darf nur verwendet werden,
     * wenn nach dem letzten der Teile definitiv kein Komma aussteht - oder das
     * ausstehende Kommma auf andere Weise behandelt wird.
     */
    @Nullable
    private static String joinToNullString(final Iterable<?> parts) {
        @Nullable final Konstituentenfolge res = joinToNullKonstituentenfolge(parts);
        if (res == null) {
            return null;
        }

        return res.joinToSingleKonstituente().toStringFixWoertlicheRedeNochOffenUndEndsThis();
    }

    /**
     * Gibt eine Aufzählung zurück wie "der hässliche Frosch",
     * "die goldene Kugel und der hässliche Frosch" oder "das schöne Glas, die goldene Kugel und
     * der hässliche Frosch".
     */
    @NonNull
    public static String buildAufzaehlung(final List<String> elemente) {
        checkArgument(!elemente.isEmpty(), "Elemente war leer");

        final StringBuilder res = new StringBuilder(elemente.size() * 30);
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

    static String getWhatsNeededToEndChapter(@Nullable final CharSequence base,
                                             @Nullable final CharSequence addition) {
        if (base == null || base.length() == 0) {
            return "";
        }

        final CharSequence lastCharBase = base.subSequence(base.length() - 1, base.length());
        if ("\n".contentEquals(lastCharBase)) {
            if (base.length() >= 2) {
                final CharSequence secondLastCharBase =
                        base.subSequence(base.length() - 2, base.length() - 1);
                if (" ,;.:!?…\n„".contains(secondLastCharBase)) {
                    // Sind vor \n nicht erlaubt, kein weiteres \n einfügen
                    return "";
                }
            }

            if (addition == null || addition.length() == 0) {
                return "\n";
            }

            final CharSequence firstCharAddition = addition.subSequence(0, 1);
            if (" ,;.:!?“\n".contains(firstCharAddition)) {
                // Sind nach \n nicht erlaubt, kein weiteres \n einfügen!
                return "";
            }

            return "\n";
        }

        // lastCharBase hört nicht mit "\n" auf

        if (addition == null || addition.length() == 0) {
            return "\n\n";
        }

        final CharSequence firstCharAddition = addition.subSequence(0, 1);
        if ("\n".contentEquals(firstCharAddition)) {
            if (addition.length() >= 2 && "\n\n".contentEquals(addition.subSequence(0, 2))) {
                return "";
            }

            if (" ,;„".contains(lastCharBase)) {
                // Sind vor \n nicht erlaubt, kein weiteres \n einfügen
                return "";
            }

            return "\n";
        }

        // lastCharBase hört nicht mit "\n" auf und firstCharAddition fängt auch nicht mit "\n" an

        if (" ,;„".contains(lastCharBase)) {
            // Sind vor \n nicht erlaubt, kein \n\n einfügen
            return "";
        }

        if (" ,;.:!?“".contains(firstCharAddition)) {
            // Sind nach \n nicht erlaubt, kein \n\n einfügen!
            return "";
        }

        // Regelfall
        return "\n\n";
    }

    static boolean newLineNeededToStartNewParagraph(@Nullable final CharSequence base,
                                                    @Nullable final CharSequence addition) {
        if (base == null || base.length() == 0) {
            return false;
        }

        final CharSequence lastCharBase = base.subSequence(base.length() - 1, base.length());
        if (" ,;„\n".contains(lastCharBase)) {
            return false;
        }

        if (addition == null || addition.length() == 0) {
            return true;
        }

        final CharSequence firstCharAddition = addition.subSequence(0, 1);
        return !" ,;.:!?“\n".contains(firstCharAddition);
    }

    static boolean fullStopNeededToEndSentence(@Nullable final CharSequence base,
                                               @Nullable final CharSequence addition) {
        if (base == null || base.length() == 0) {
            return false;
        }

        final CharSequence lastCharBase = base.subSequence(base.length() - 1, base.length());
        if (base.length() >= 2) {
            if ("“".contentEquals(lastCharBase)) {
                final CharSequence secondLastCharBase =
                        base.subSequence(base.length() - 2, base.length() - 1);
                return !" ,;.:!?…\n„".contains(secondLastCharBase);
            }
        }

        if (addition != null
                && addition.length() >= 2
                && "“.".contentEquals(addition.subSequence(0, 2))) {
            return false;
        }

        if (" ,;.:!?…\n„".contains(lastCharBase)) {
            return false;
        }

        if (addition == null || addition.length() == 0) {
            return true;
        }


        final CharSequence firstCharAddition = addition.subSequence(0, 1);
        return !".:!?…".contains(firstCharAddition);
    }


    public static boolean spaceNeeded(@Nullable final CharSequence base,
                                      @Nullable final CharSequence addition) {
        if (base == null || base.length() == 0 ||
                addition == null || addition.length() == 0) {
            return false;
        }

        final CharSequence lastCharBase = base.subSequence(base.length() - 1, base.length());
        if (" „\n".contains(lastCharBase)) {
            return false;
        }

        final CharSequence firstCharAddition = addition.subSequence(0, 1);
        return !" ,;.:!?“…\n".contains(firstCharAddition);
    }

    static boolean beginnDecktKommaAb(final CharSequence charSequence) {
        requireNonNull(charSequence, "charSequence");
        checkArgument(charSequence.length() > 0, "charSequence was empty");

        final CharSequence firstChar = charSequence.subSequence(0, 1);

        checkArgument(!"\n".contentEquals(firstChar), "charSequence beginnt mit "
                + "Zeilenwechsel. Hier wäre keine Möglichkeit, syntaktisch korrekt noch ein "
                + "Komma unterzubringen.");

        checkArgument(!"“".contains(firstChar), "charSequence beginnt "
                + "mit Abführungszeichen. Hier müsste man eigentlich erst das "
                + "Abführungszeichen "
                + "schreiben und dann das Komma (oder Punkt o.Ä.). Diese Logik ist noch nicht "
                + "implementiert");

        return ",;.:!?".contains(firstChar);
    }

    static boolean beginnDecktDoppelpunktAb(final CharSequence charSequence) {
        requireNonNull(charSequence, "charSequence");
        checkArgument(charSequence.length() > 0, "charSequence was empty");

        final CharSequence firstChar = charSequence.subSequence(0, 1);

        checkArgument(!"\n".contentEquals(firstChar), "charSequence beginnt mit "
                + "Zeilenwechsel. Hier wäre keine Möglichkeit, syntaktisch korrekt noch ein "
                + "Komma unterzubringen.");

        checkArgument(!"“".contains(firstChar), "charSequence beginnt "
                + "mit Abführungszeichen. Hier müsste man eigentlich erst das "
                + "Abführungszeichen "
                + "schreiben und dann das Komma (oder Punkt o.Ä.). Diese Logik ist noch nicht "
                + "implementiert");

        return ",;.:!?".contains(firstChar);
    }


    /**
     * Schneidet diesen Teil-Text (einmalig) aus diesem Text. Die Suche nach
     * dem Teil-Text beginnt am Anfang des Textes.
     */
    static @Nullable
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
}