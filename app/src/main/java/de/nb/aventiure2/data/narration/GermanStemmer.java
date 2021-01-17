package de.nb.aventiure2.data.narration;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import static de.nb.aventiure2.german.base.GermanUtil.toLowerCase;

/**
 * Ein einfacher und schneller Stemming-Algorithmus für das Deutsche, implementiert auf der
 * Basis von http://www.inf.fu-berlin.de/lehre/WS98/digBib/projekt/_stemming.html
 * und https://www.cis.uni-muenchen.de/~weissweiler/cistem/ .
 * Es werden keine wirklichen Stämme erzeugt, sondern Diskriminatoren.
 */
class GermanStemmer {
    private static final String[] SUFFIXES_MIN_LENGTH_5 = {"em", "er", "nd"};

    private static final String[] SUFFIXES_LOWER_CASE = {"t"};

    private static final String[] SUFFIXES_ALL_CASES = {"e", "s", "n"};

    private GermanStemmer() {
    }

    // FIXME Stopwords dürfen nicht übergeben werden ("determiners and other
    //  function words")

    /**
     * Erzeugt aus der Wortform den Discriminator. Verschiedene Wortformen desselben
     * Lexems ergeben - im Großen und Ganzen - denselben Discriminator.
     */
    @NonNull
    static String toDiscriminator(final String word) {
        final boolean wasLowercase = startsWithLowerCase(word);

        String res = toLowerCase(word);
        res = doSubstitutions1(res);

        while (res.length() > 3) {
            final String tmp = stripSuffixes(res, wasLowercase);
            if (tmp.equals(res)) {
                break;
            }
            res = tmp;
        }

        return doSubstitutions2(res);
    }

    /**
     * Ersetzt gewisse Zeichen(folgen) - für den Beginn des Algorithmus
     */
    @NonNull
    @SuppressWarnings("DynamicRegexReplaceableByCompiledPattern")
    private static String doSubstitutions1(final String string) {
        String res = string.replace("ß", "ss");
        if (res.length() > 6) {
            res = stripPrefixIfAny(res, "ge");
        }
        res = res.replace("sch", "$");

        // Schauspielerin (nicht bei Cistem)
        res = res.replace("erinn", "erin");

        res = replaceSecondOfDoubleCharsWithAsterisk(res);

        res = res.replace("ü", "u");
        res = res.replace("ö", "o");
        res = res.replace("ä", "a");
        res = res.replace("ei", "%");
        return res.replace("ie", "&");

        // "Irregular verbs (sah / sieht) and foreign words (Drama / Dramen) can only be solved
        //  by using a dictionary"
    }

    /**
     * Folgen zwei gleiche Buchstaben aufeinander, so wird der zweite
     * durch "*" ersetzt.
     */
    @NonNull
    private static String replaceSecondOfDoubleCharsWithAsterisk(final String string) {
        final StringBuilder res = new StringBuilder(string.length());

        @Nullable
        String prevChar = null;
        for (int i = 0; i < string.length(); i++) {
            final String currChar = string.substring(i, i + 1);
            if (currChar.equals(prevChar)) {
                res.append("*");
                prevChar = null; // "nnn" -> "n*n"
            } else {
                res.append(currChar);
                prevChar = currChar;
            }
        }

        return res.toString();
    }

    /**
     * Folgt auf einen Buchstaben ein "*", wird er durch den vorigen Buchstaben ersetzt.
     */
    @NonNull
    private static String replaceAsteriskWithPreviousChar(final String string) {
        final StringBuilder res = new StringBuilder(string.length());

        @Nullable
        String prevChar = null;
        for (int i = 0; i < string.length(); i++) {
            final String currChar = string.substring(i, i + 1);
            if (prevChar != null && currChar.equals("*")) {
                res.append(prevChar);
            } else {
                res.append(currChar);
            }
            prevChar = currChar;
        }

        return res.toString();
    }


    /**
     * Entfernt Endungen von diesem intermediateWord;
     */
    private static String stripSuffixes(final String string, final boolean wasLowerCase) {
        // "In German there are seven declensional suffixes for
        // nouns: -s, -es, -e, -en, -n, -er, and -ern, 16 for
        // adjectives: -e, -er, -en, -em, -ere, -erer, -eren, -erem,
        // -ste, -ster, -sten, and -stem, and 48 for verbs: -e, -est,
        // -st, -et, -t, -en, -ete, -te, etest, -test, eten, -ten, -etet,
        // tet, -end-, and -nd- (-end- and -nd- turn verbs into adverbs
        // and can be followed by any of the adjective suffixes)."

        final String tmp =
                stripFirstSuffixIfAnyIfLengthIsAtLeast(5, string, SUFFIXES_MIN_LENGTH_5);
        if (!tmp.equals(string)) {
            return tmp;
        }

        if (wasLowerCase) {
            // Hier entsteht eine Ungenauigkeit: Ist eine Verbform am Satzanfang groß
            // geschrieben (z.B. in "Kommt Peter auch?"), so wird das "-t" nicht entfernt.
            // Anscheinend ist das immer noch besser, als das -t auch von Substantiven
            // zu entfernen, da Fragen eher selten sind.
            final String tmp2 = stripFirstSuffixIfAny(string, SUFFIXES_LOWER_CASE);

            if (!tmp2.equals(string)) {
                return tmp2;
            }
        }

        final String tmp3 = stripFirstSuffixIfAny(string, SUFFIXES_ALL_CASES);
        if (!tmp3.equals(string)) {
            return tmp3;
        }

        return string;
    }

    /**
     * Ersetzt gewisse Zeichen(folgen) - für das Ende des Algorithmus
     */
    @NonNull
    @SuppressWarnings("DynamicRegexReplaceableByCompiledPattern")
    private static String doSubstitutions2(final String string) {
        String res = replaceAsteriskWithPreviousChar(string);

        res = res.replace("$", "sch");
        res = res.replace("%", "ei");
        return res.replace("&", "ie");
    }


    private static boolean startsWithLowerCase(final String string) {
        return !string.isEmpty()
                && Character.isLowerCase(string.codePointAt(0));
    }

    private static String stripFirstSuffixIfAnyIfLengthIsAtLeast(
            final int minLength, final String string, final String[] suffixes) {
        if (string.length() < minLength) {
            return string;
        }

        return stripFirstSuffixIfAny(string, suffixes);
    }

    @NonNull
    private static String stripPrefixIfAny(final String string, final String prefix) {
        if (!string.startsWith(prefix) || string.length() <= prefix.length()) {
            return string;
        }

        return stripPrefix(string, prefix);
    }

    @NonNull
    private static String stripPrefix(final String string, final String prefix) {
        return string.substring(prefix.length());
    }

    /**
     * Entfernt das erste dieser Suffixe - sofern eines vorkommt.
     */
    private static String stripFirstSuffixIfAny(final String string, final String[] suffixes) {
        for (final String suffix : suffixes) {
            if (string.endsWith(suffix) && string.length() > suffix.length()) {
                return stripSuffix(string, suffix);
            }
        }

        return string;
    }

    @NonNull
    private static String stripSuffix(final String string, final String suffix) {
        return string.substring(0, string.length() - suffix.length());
    }
}
