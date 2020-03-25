package de.nb.aventiure2.data.storystate;

import com.google.common.collect.ImmutableList;

import java.util.LinkedList;
import java.util.List;

import static java.util.Arrays.asList;

class TextAdditionEvaluator {
    private static final List<String> REPITION_ACCEPTABLE =
            asList(new String[]{
                    "ein", "eines", "einem", "einen", "eine", "einer", "der", "des", "dem", "den",
                    "die", "das", "derer", "denen"});

    private TextAdditionEvaluator() {
    }

    /**
     * Bewertet diese Hinzufügung (<code>addition</code>) an diesen
     * <code>base</code>-Text. Wiederholgungen gegenüber dem
     * Ende des <code>base</code>-Textes gelten als schlecht.
     *
     * @return Bewertungsergebnis - je größer die Zahl, desto besser
     */
    public static float evaluateAddition(final String base, final String addition) {
        // Wir wollen wiederholungen vermeiden.
        // Wenn also addition Wörter enthält, die das Ende von base wiederholen, ist
        // das schlecht.

        float res = 0;
        // Wir machen es so: Wir teilen addition in Wörter auf,
        // dabei verwerfen wir Interpunktion.
        final ImmutableList<String> additionWords = splitInWords(addition);

        // Dasselbe machen wir mit dem Ende von base.
        final ImmutableList<String> baseWords = splitEndInWords(base, 100);

        // Dann suchen wir: Gibt es am Ende von base genau 10 Wörter aus addition in Folge?
        res += evaluateAdditionWordSequence(baseWords, additionWords, 10);
        // Das wäre schlecht (negative Zahlen).
        // Dann suchen wir: Gibt es am Ende von base genau 7 Wörter aus addition in Folge?
        res += evaluateAdditionWordSequence(baseWords, additionWords, 7);
        // 5?
        res += evaluateAdditionWordSequence(baseWords, additionWords, 5);
        // 3?
        res += evaluateAdditionWordSequence(baseWords, additionWords, 3);
        // 2?
        res += evaluateAdditionWordSequence(baseWords, additionWords, 2);
        // 1?
        res += evaluateAdditionWordSequence(baseWords, additionWords, 1);
        // Das addieren wir alles - je größer die Zahl, desto besser!

        return res;
    }

    private static ImmutableList<String> splitInWords(final String text) {
        return splitEndInWords(text, Integer.MAX_VALUE);
    }

    private static ImmutableList<String> splitEndInWords(final String text, final int maxWords) {
        final LinkedList<String> res = new LinkedList<>();
        int to = text.length() - 1;
        while (true) {
            while (to >= 0 && !Character.isLetter(text.charAt(to))) {
                to--;
            }
            if (to < 0) {
                return ImmutableList.copyOf(res); // ==>
            }

            int from = to;

            while (from >= 0 && Character.isLetter(text.charAt(from))) {
                from--;
            }

            res.addFirst(text.substring(from + 1, to + 1).toLowerCase());

            if (res.size() >= maxWords) {
                return ImmutableList.copyOf(res);
            }

            to = from;
        }
    }

    /**
     * Bewertet diese Hinzufügung (<code>additionWords</code>) an diesen
     * <code>baseWords</code>-Text - wobei nur Wortfolgen der Länge
     * <code>num</code> untersucht werden. <code>additionWords</code>
     * und <code>baseWords</code> müssen in gleicher Groß-/Kleinschreibung
     * vorliegen.
     *
     * @return Bewertungsergebnis - je größer die Zahl, desto besser
     */
    private static float evaluateAdditionWordSequence(final ImmutableList<String> baseWords,
                                                      final ImmutableList<String> additionWords,
                                                      final int num) {
        float res = 0;
        // Wir suchen: Gibt es am Ende von baseWords genau num Wörter aus dem Anfang von
        // additionWords in Folge?
        for (int baseWordsIndex = baseWords.size() - num; baseWordsIndex > 0;
             baseWordsIndex--) {
            final List<String> baseSequence =
                    baseWords.subList(baseWordsIndex, baseWordsIndex + num);

            for (int additionWordsIndex = 0; additionWordsIndex + num <= additionWords.size();
                 additionWordsIndex++) {
                final List<String> additionSequence =
                        additionWords.subList(additionWordsIndex, additionWordsIndex + num);

                if (baseSequence.equals(additionSequence) &&
                        !repetitionAcceptable(baseSequence)) {
                    // Schlecht! - Und je näher am Ende von baseWords und je näher am
                    //  Anfang von additionWords, desto schlechter (negative Zahlen).
                    final int distanceBase = baseWords.size() - num - baseWordsIndex;
                    final int distanceAddition = additionWordsIndex;
                    final int distance = distanceBase + distanceAddition;

                    final float penalty =
                            (num * num * num) / (float)
                                    (distance * distance);
                    res -= penalty;
                }
            }
        }
        return res;
    }

    private static boolean repetitionAcceptable(final List<String> sequence) {
        if (sequence.isEmpty()) {
            return true;
        }

        if (sequence.size() > 1) {
            return false;
        }
        return repetitionAcceptable(sequence.iterator().next());
    }

    private static boolean repetitionAcceptable(final String word) {
        return REPITION_ACCEPTABLE.contains(word);
    }
}
