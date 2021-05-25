package de.nb.aventiure2.german.string;

import androidx.annotation.NonNull;

import java.util.Locale;

import de.nb.aventiure2.german.base.GermanUtil;
import de.nb.aventiure2.german.base.StructuralElement;

import static de.nb.aventiure2.german.base.GermanUtil.getWhatsNeededToStartNewChapter;
import static de.nb.aventiure2.german.base.GermanUtil.newLineNeededToStartNewParagraph;
import static de.nb.aventiure2.german.base.GermanUtil.spaceNeeded;
import static de.nb.aventiure2.german.base.StructuralElement.CHAPTER;
import static de.nb.aventiure2.german.base.StructuralElement.PARAGRAPH;
import static de.nb.aventiure2.german.base.StructuralElement.WORD;

public class GermanStringUtil {
    /**
     * Versetzt den ersten <i>Buchstaben</i> in Großschreibung.
     */
    public static String capitalizeFirstLetter(final String string)
            throws NoLetterException {
        int i = 0;
        while (i < string.length()) {
            final String currentZeichen = string.substring(i, i + 1);
            if ("– „\".:!?".contains(currentZeichen)) {
                // Diese Zeichen einfach überspringen - DANACH soll
                // großgeschrieben werden
                i++;
                continue;
            }

            // Normaler Buchstabe gefunden! Es soll großgeschrieben werden
            return capitalize(string, i);
        }

        // Kein normaler Buchstabe gefunden.
        throw new NoLetterException("No letter found in \"" + string + "\"");
    }

    private static String capitalize(final String string, final int index) {
        return string.substring(0, index)
                + capitalize(string.substring(index, index + 1))
                + string.substring(index + 1);
    }

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

    public static boolean newlineNeededToStartNewParagraph(
            final String base, final String addition) {
        if (base.endsWith("\n")) {
            return false;
        }

        return !addition.startsWith("\n");
    }

    /**
     * Gibt den String zurück, mit dem die noch offene wörtliche Rede abgeschlossen wird.
     * Dies können ein Leerstring, "“" oder ".“" sein.
     *
     * @param satzende Ob der Satz damit beendet werden soll
     */
    public static String schliesseWoertlicheRede(
            final String base, final String addition, final boolean satzende) {

        if (satzende) {
            return schliesseWoertlicheRedeSatzende(base, addition);
        }

        return schliesseWoertlicheRedeNichtSatzende(base, addition);
    }

    public static boolean beginnStehtCapitalizeNichtImWeg(final String addition) {
        return !addition.startsWith(",")
                && !addition.startsWith(";")
                && !addition.startsWith("…")
                && !addition.startsWith("„…");
    }

    @NonNull
    private static String schliesseWoertlicheRedeSatzende(final String base,
                                                          final String addition) {
        final String baseTrimmed = base.trim();
        final String additionTrimmed = addition.trim();

        final String lastRelevantCharBase = baseTrimmed.substring(baseTrimmed.length() - 1);
        if ("….!?:\"“".contains(lastRelevantCharBase)) {
            if (baseTrimmed.endsWith("…“") || baseTrimmed.endsWith(".“")
                    || baseTrimmed.endsWith("!“") || baseTrimmed.endsWith("?“")
                    || baseTrimmed.endsWith("…\"") || baseTrimmed.endsWith(".\"")
                    || baseTrimmed.endsWith("!\"") || baseTrimmed.endsWith("?\"")) {
                return "";
            }

            if (additionTrimmed.startsWith("“")) {
                return "";
            }

            return "“";
        }

        if (additionTrimmed.startsWith(".“")) {
            return "";
        }

        if (additionTrimmed.startsWith("“")) {
            return ".";
        }

        return ".“";
    }

    @NonNull
    private static String schliesseWoertlicheRedeNichtSatzende(final String base,
                                                               final String addition) {
        final String baseTrimmed = base.trim();
        final String additionTrimmed = addition.trim();

        if (baseTrimmed.endsWith("“")) {
            return "";
        }

        if (additionTrimmed.startsWith("“")) {
            return "";
        }

        return "“";

        // Das Komma sollte ohnehin durch kommaStehtAus gefordert sein
    }

    public static void appendBreak(final StringBuilder stringBuilder,
                                   final StructuralElement brreak,
                                   final String addition) {
        if (brreak != WORD) {
            if (GermanUtil.periodNeededToStartNewSentence(stringBuilder, addition)) {
                stringBuilder.append(".");
            }
        }

        if (brreak == CHAPTER) {
            stringBuilder.append(getWhatsNeededToStartNewChapter(stringBuilder, addition));
        } else if (brreak == PARAGRAPH) {
            if (newLineNeededToStartNewParagraph(stringBuilder, addition)) {
                stringBuilder.append("\n");
            }
        } else if (spaceNeeded(stringBuilder, addition)) {
            stringBuilder.append(" ");
        }
    }
}
