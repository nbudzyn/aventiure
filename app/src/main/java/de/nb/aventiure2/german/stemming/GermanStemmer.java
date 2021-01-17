package de.nb.aventiure2.german.stemming;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import static de.nb.aventiure2.german.string.GermanStringUtil.uncapitalize;

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

    /**
     * Ermittelt zu diesem Wort grob den Stamm
     */
    @NonNull
    static String stemWord(final String word) {
        final String uncapitalized = uncapitalize(word);
        if (GermanStopwords.isStopword(uncapitalized)) {
            return uncapitalized;
        }

        return toDiscriminator(word);
    }

    /**
     * Erzeugt aus der Wortform den Discriminator. Verschiedene Wortformen desselben
     * Lexems ergeben - im Großen und Ganzen - denselben Discriminator.
     */
    @NonNull
    static String toDiscriminator(final String word) {
        final boolean wasLowercase = startsWithLowerCase(word);

        final StringBuilder res = new StringBuilder(word.length());

        if (wasLowercase) {
            res.append(word);
        } else {
            res.append(uncapitalize(word));
        }

        doSubstitutions1(res);

        while (res.length() > 3) {
            final boolean changed = stripSomeSuffixesIfAny(res, wasLowercase);
            if (!changed) {
                break;
            }
        }

        doSubstitutions2(res);

        return res.toString();
    }

    /**
     * Ersetzt gewisse Zeichen(folgen) - für den Beginn des Algorithmus
     */
    private static void doSubstitutions1(final StringBuilder stringBuilder) {
        replaceAll(stringBuilder, "ß", "ss");
        if (stringBuilder.length() > 6
                && "ge".contentEquals(stringBuilder.subSequence(0, 2))) {
            stringBuilder.delete(0, 2);
        }
        replaceAll(stringBuilder, "sch", "$");

        // Schauspielerin (nicht bei Cistem)
        replaceAll(stringBuilder, "erinn", "erin");

        replaceSecondOfDoubleCharsWithAsterisk(stringBuilder);

        replaceAll(stringBuilder, "ü", "u");
        replaceAll(stringBuilder, "ö", "o");
        replaceAll(stringBuilder, "ä", "a");
        replaceAll(stringBuilder, "ei", "%");
        replaceAll(stringBuilder, "ie", "&");

        // "Irregular verbs (sah / sieht) and foreign words (Drama / Dramen) can only be solved
        //  by using a dictionary"
    }

    /**
     * Folgen zwei gleiche Buchstaben aufeinander, so wird der zweite
     * durch "*" ersetzt.
     */
    private static void replaceSecondOfDoubleCharsWithAsterisk(final StringBuilder stringBuilder) {
        @Nullable
        String prevChar = null;
        for (int i = 0; i < stringBuilder.length(); i++) {
            final String currChar = stringBuilder.substring(i, i + 1);
            if (currChar.equals(prevChar)) {
                stringBuilder.replace(i, i + 1, "*");
                prevChar = null; // "nnn" -> "n*n"
            } else {
                prevChar = currChar;
            }
        }
    }

    /**
     * Folgt auf einen Buchstaben ein "*", wird er durch den vorigen Buchstaben ersetzt.
     */
    private static void replaceAsteriskWithPreviousChar(final StringBuilder stringBuilder) {
        @Nullable
        String prevChar = null;
        for (int i = 0; i < stringBuilder.length(); i++) {
            final String currChar = stringBuilder.substring(i, i + 1);
            if (prevChar != null && currChar.equals("*")) {
                stringBuilder.replace(i, i + 1, prevChar);
            }
            prevChar = currChar;
        }
    }


    /**
     * Entfernt Endungen von diesem intermediateWord;
     */
    private static boolean stripSomeSuffixesIfAny(final StringBuilder stringBuilder,
                                                  final boolean wasLowerCase) {
        // "In German there are seven declensional suffixes for
        // nouns: -s, -es, -e, -en, -n, -er, and -ern, 16 for
        // adjectives: -e, -er, -en, -em, -ere, -erer, -eren, -erem,
        // -ste, -ster, -sten, and -stem, and 48 for verbs: -e, -est,
        // -st, -et, -t, -en, -ete, -te, etest, -test, eten, -ten, -etet,
        // tet, -end-, and -nd- (-end- and -nd- turn verbs into adverbs
        // and can be followed by any of the adjective suffixes)."

        boolean changed = false;

        if (stringBuilder.length() >= 5) {
            changed = stripSuffixesIfExist(stringBuilder, SUFFIXES_MIN_LENGTH_5) || changed;
        }

        if (wasLowerCase) {
            // Hier entsteht eine Ungenauigkeit: Ist eine Verbform am Satzanfang groß
            // geschrieben (z.B. in "Kommt Peter auch?"), so wird das "-t" nicht entfernt.
            // Anscheinend ist das immer noch besser, als das -t auch von Substantiven
            // zu entfernen, da Fragen eher selten sind.
            changed = stripSuffixesIfExist(stringBuilder, SUFFIXES_LOWER_CASE) || changed;
        }

        changed = stripSuffixesIfExist(stringBuilder, SUFFIXES_ALL_CASES) || changed;

        return changed;
    }

    /**
     * Ersetzt gewisse Zeichen(folgen) - für das Ende des Algorithmus
     */
    private static void doSubstitutions2(final StringBuilder stringBuilder) {
        replaceAsteriskWithPreviousChar(stringBuilder);

        replaceAll(stringBuilder, "$", "sch");
        replaceAll(stringBuilder, "%", "ei");
        replaceAll(stringBuilder, "&", "ie");
    }

    private static boolean startsWithLowerCase(final String string) {
        return !string.isEmpty()
                && Character.isLowerCase(string.codePointAt(0));
    }

    /**
     * Entfernt das erste dieser Suffixe - sofern eines vorkommt.
     *
     * @return true if something has changed
     */
    private static boolean stripSuffixesIfExist(final StringBuilder stringBuilder,
                                                final String[] suffixes) {
        boolean changed = false;
        for (final String suffix : suffixes) {
            changed = stripSuffixIfExists(stringBuilder, suffix) || changed;
        }

        return changed;
    }

    private static boolean stripSuffixIfExists(final StringBuilder stringBuilder,
                                               final String suffix) {
        if (suffix.contentEquals(
                stringBuilder.subSequence(
                        stringBuilder.length() - suffix.length(), stringBuilder.length()))) {
            stringBuilder.delete(stringBuilder.length() - suffix.length(), stringBuilder.length());
            return true;
        }

        return false;
    }

    private static void replaceAll(final StringBuilder stringBuilder, final String from,
                                   final String to) {
        int index = stringBuilder.indexOf(from);
        while (index != -1) {
            stringBuilder.replace(index, index + from.length(), to);
            index += to.length(); // Move to the end of the replacement
            index = stringBuilder.indexOf(from, index);
        }
    }
}
