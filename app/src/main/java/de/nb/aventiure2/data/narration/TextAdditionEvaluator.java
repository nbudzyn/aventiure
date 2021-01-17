package de.nb.aventiure2.data.narration;

import androidx.annotation.NonNull;

import com.google.common.collect.ImmutableList;

import org.jetbrains.annotations.Contract;

import java.util.LinkedList;
import java.util.List;

import de.nb.aventiure2.german.base.GermanUtil;

import static java.util.Arrays.asList;

class TextAdditionEvaluator {
    private static final float ERFAHRUNGSWERT = 0.0001f;

    private static final List<String> REPITION_ACCEPTABLE =
            asList("ein", "eines", "einem", "einen", "eine", "einer", "der", "des", "dem", "den",
                    "die", "das", "derer", "denen");

    private TextAdditionEvaluator() {
    }

    /**
     * Bewertet diese Hinzufügung (<code>addition</code>) an diesen
     * <code>base</code>-Text. Wiederholgungen gegenüber dem
     * Ende des <code>base</code>-Textes gelten als schlecht. Außerdem wird geprüft, ob
     * derselbe Satz an Alternativen schon einmal geprüft wurde - Alternativen, die bei einem
     * früheren Mal gewählt wurden, werden dann vermieden.
     *
     * @param base              Der Text, dem etwas hinzugefügt werdnen soll
     * @param isConsumed        Wurde dieser Kandidat bereits bei einem der letzten Male gewählt, als aus
     *                          <i>denselben Alternativen</i> ausgewählt wurde - gilt er also
     *                          aus "verbraucht"?
     * @param additionCandidate Der Text, der als Kandidat (Alternative) zum Hinzugefügt-werden geprüft
     *                          werden soll
     * @return Bewertungsergebnis - je größer die Zahl, desto besser
     */
    static float evaluateAddition(final String base,
                                  final String additionCandidate,
                                  final boolean isConsumed) {
        float res = 0;

        // Wir wollen Wiederholungen vermeiden.
        // Dazu gibt es zwei Stratgien:
        // Strategie "verbrauchte Kandidaten vermeiden": Wenn bei den letzten Malen, als aus denselben
        // Alternativen ausgewählt werden sollte, dieser Kandidat ausgewählt wurde, wollen wir
        // ihn vermeiden.
        if (isConsumed) {
            res += -11.04;
            // Das ist ein Wertsein im Bereich einer richtig bösen Endwiederholung.
            // So, dass es immer noch besser ist, dieselbe, schon verbrauchte Alternative zu
            // verwenden, als diese bösen Endwiederholungen einzugehen.
            // In der Praxis führen allerdings alle Werte ab -0.9 dazu, dass alle
            // Alternativen ausgeschöpft werden - und das ist der Erfahrung nach auch
            // gut so.
        }

        // Strategie "Endwiederholungen vermeiden": Wenn der additionCandidate Wörter enthält,
        // die das Ende von base wiederholen, ist das schlecht.
        // Wir machen es dazu so: Wir teilen addition in Wörter auf,
        // dabei verwerfen wir Interpunktion.
        res += evaluateAdditionEndwiederholungen(base, additionCandidate);

        return res;
    }

    /**
     * Bewertet diese Hinzufügung (<code>addition</code>) an diesen
     * <code>base</code>-Text auf Basis von Wiederholgungen von Wortstämmen
     * am Ende des <code>base</code>-Textes.
     */
    private static float evaluateAdditionEndwiederholungen(final String base,
                                                           final String additionCandidate) {
        float res = 0;

        final ImmutableList<String> additionWords = stem(additionCandidate);

        // Dasselbe machen wir mit dem Ende von base.
        final ImmutableList<String> baseWords = stemEnd(base, 100);

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
        // Das addieren wir alles - je höher die Zahl, desto besser!
        return res;
    }

    /**
     * Teilt den Text grob in einzelne Wortstämme auf
     */
    private static ImmutableList<String> stem(final String text) {
        return stemEnd(text, Integer.MAX_VALUE);
    }

    /**
     * Teilt das Ende des Textes grob in einzelne Wortstämme auf
     */
    private static ImmutableList<String> stemEnd(@NonNull final String text,
                                                 final int maxWords) {
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

            res.addFirst(stemWord(text.substring(from + 1, to + 1)));

            if (res.size() >= maxWords) {
                return ImmutableList.copyOf(res);
            }

            to = from;
        }
    }

    /**
     * Ermittelt zu diesem Wort grob den Stamm
     */
    @NonNull
    private static String stemWord(final String word) {
        final String uncapitalized = GermanUtil.uncapitalize(word);
        if (GermanStopwords.isStopword(uncapitalized)) {
            return uncapitalized;
        }

        return GermanStemmer.toDiscriminator(word);
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
    private static float evaluateAdditionWordSequence(
            @NonNull final ImmutableList<String> baseWords,
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

                final int distanceBase = baseWords.size() - num - baseWordsIndex;
                final int distance = distanceBase + additionWordsIndex + 1;
                final float penaltyIfUnacceptableRepitition =
                        (num * num * num) / (float)
                                (distance * distance);

                // Längere additionSequences haben logischerweise mehr Wiederholungen
                // als kürzere. Deshalb haben kürzere additionSequences einen
                // Vorteil gegenüber längeren, wenn wir penalties nur ABZIEHEN.
                // Deshalb ADDIEREN wir hier eine DURCHSCHNITTLICHE
                // penalty. Damit pendelt res immer um 0 statt mit
                // längeren additionSequences immer kleiner und kleiner zu werden.
                res += penaltyIfUnacceptableRepitition * ERFAHRUNGSWERT;

                if (baseSequence.equals(additionSequence) &&
                        !repetitionAcceptable(baseSequence)) {
                    // Schlecht! - Und je näher am Ende von baseWords und je näher am
                    //  Anfang von additionWords, desto schlechter (negative Zahlen).

                    res -= penaltyIfUnacceptableRepitition;
                }
            }
        }
        return res;
    }

    private static boolean repetitionAcceptable(@NonNull final List<String> sequence) {
        if (sequence.isEmpty()) {
            return true;
        }

        if (sequence.size() > 1) {
            return false;
        }
        return repetitionAcceptable(sequence.iterator().next());
    }

    @Contract(pure = true)
    private static boolean repetitionAcceptable(final String word) {
        return REPITION_ACCEPTABLE.contains(word);
    }
}
