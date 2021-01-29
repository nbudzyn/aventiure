package de.nb.aventiure2.data.narration;

import androidx.annotation.NonNull;

import com.google.common.collect.ImmutableList;

import org.jetbrains.annotations.Contract;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import de.nb.aventiure2.german.stemming.StemmedWords;

import static java.util.Arrays.asList;

class TextAdditionEvaluator {
    private interface IPenaltyChecker {
        boolean deservesPenalty(@NonNull StemmedWords baseStems,
                                StemmedWords additionStems, int num, int baseWordsIndex,
                                int additionWordsIndex);
    }

    private static final float ERFAHRUNGSWERT = 0.0001f;

    private static final StemmedWords MAX_NUMBER_ADDED_WORDS_STEMMED_WORDS;

    static {
        final String[] dummy = new String[1000];
        Arrays.fill(dummy, "x");

        MAX_NUMBER_ADDED_WORDS_STEMMED_WORDS =
                new StemmedWords(ImmutableList.copyOf(asList(dummy)));
    }

    private static final List<String> REPITION_ACCEPTABLE =
            asList("ein", "eines", "einem", "einen", "eine", "einer", "der", "des", "dem", "den",
                    "die", "das", "derer", "denen");

    private TextAdditionEvaluator() {
    }

    /**
     * Bewertet diese Hinzufügung (<code>addition</code>) an diesen
     * Basistext. Wiederholungen gegenüber dem
     * Ende des <code>base</code>-Textes gelten als schlecht. Außerdem wird geprüft, ob
     * derselbe Satz an Alternativen schon einmal geprüft wurde - Alternativen, die bei einem
     * früheren Mal gewählt wurden, werden dann vermieden.
     *
     * @param baseStems         Der Text, dem etwas hinzugefügt werden soll (nur das Ende, und für
     *                          das wurde bereits ein Stemming durchgeführt)
     * @param additionCandidate Der Text, der als Kandidat (Alternative) zum Hinzugefügt-werden
     *                          geprüft
     *                          werden soll
     * @param isConsumed        Wurde dieser Kandidat bereits bei einem der letzten Male gewählt,
     *                          als aus
     *                          <i>denselben Alternativen</i> ausgewählt wurde - gilt er also
     *                          aus "verbraucht"?
     * @param bestScoreSoFar    Das bisher beste Ergebnis. Wenn klar ist, das der hier berechnete
     *                          score schlechter ist, kann das Rechnen abgebrochen werden.
     * @return Bewertungsergebnis - je größer die Zahl, desto besser
     */
    static float evaluateAddition(final StemmedWords baseStems,
                                  final String additionCandidate,
                                  final boolean isConsumed, final float bestScoreSoFar) {
        float res = 0;
        float tweekedBestScoreSoFar = bestScoreSoFar;

        // Wir wollen Wiederholungen vermeiden.
        // Dazu gibt es zwei Stratgien:
        // Strategie "verbrauchte Kandidaten vermeiden": Wenn bei den letzten Malen, als aus
        // denselben
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
            tweekedBestScoreSoFar += 11.04;
        }

        // Strategie "Endwiederholungen vermeiden": Wenn der additionCandidate Wörter enthält,
        // die das Ende von base wiederholen, ist das schlecht.
        // Wir machen es dazu so: Wir teilen addition in Wortstämme auf.

        res += evaluateAdditionEndwiederholungen(baseStems, additionCandidate,
                tweekedBestScoreSoFar);

        return res;
    }

    /**
     * Bewertet diese Hinzufügung (<code>addition</code>) an diesen Basistext auf Basis von
     * Wiederholgungen von Wortstämmen
     * am Ende des <code>base</code>-Textes.
     *
     * @param bestScoreSoFar Das bisher beste Ergebnis. Wenn klar ist, das der hier
     *                       berechnete score schlechter ist, kann das Rechnen abgebrochen werden.
     */
    private static float evaluateAdditionEndwiederholungen(final StemmedWords baseStems,
                                                           final String additionCandidate,
                                                           final float bestScoreSoFar) {
        final LinkedList<Integer> nums = new LinkedList<>(asList(10, 7, 5, 3, 2, 1));

        if (bestScoreSoFar > Float.NEGATIVE_INFINITY &&
                bestAdditionWordSequenceScoresStillPossible(baseStems,
                        // Mehr als 1000 Wörter werden sicher nicht angefügt werden.
                        MAX_NUMBER_ADDED_WORDS_STEMMED_WORDS, nums) <
                        bestScoreSoFar) {
            return Float.NEGATIVE_INFINITY;
        }

        final StemmedWords additionStems = StemmedWords.stem(additionCandidate);

        return evaluateAdditionEndwiederholungen(baseStems, additionStems, bestScoreSoFar,
                nums);
    }

    /**
     * Bewertet diese Hinzufügung (<code>addition</code>) an diesen Basistext auf Basis von
     * Wiederholgungen von Wortstämmen
     * am Ende des <code>base</code>-Textes.
     *
     * @param bestScoreSoFar Das bisher beste Ergebnis. Wenn klar ist, das der hier
     *                       berechnete score schlechter ist, kann das Rechnen abgebrochen werden.
     */
    private static float evaluateAdditionEndwiederholungen(final StemmedWords baseStems,
                                                           final StemmedWords additionStems,
                                                           final float bestScoreSoFar,
                                                           final List<Integer> nums) {
        if (bestScoreSoFar > Float.NEGATIVE_INFINITY &&
                bestAdditionWordSequenceScoresStillPossible(baseStems, additionStems, nums) <
                        bestScoreSoFar) {
            return Float.NEGATIVE_INFINITY;
        }
        float res = 0;

        // Dann suchen wir: Gibt es am Ende von base genau 10 (z.B.) Wortstämme aus addition in
        // Folge?

        // Das wäre schlecht (negative Zahlen).
        res += evaluateAdditionWordSequence(baseStems, additionStems,
                TextAdditionEvaluator::calcDeservesPenalty, nums.iterator().next());

        // Dann suchen wir: Gibt es am Ende von base genau 7 (z.B.) Wortstämme aus addition in
        // Folge?
        // 5? 3? 2? 1?
        final List<Integer> newNums = nums.subList(1, nums.size());
        if (!newNums.isEmpty()) {
            res += evaluateAdditionEndwiederholungen(baseStems, additionStems,
                    bestScoreSoFar, newNums);
        }
        // Das addieren wir alles - je höher die Zahl, desto besser!

        return res;
    }

    private static float bestAdditionWordSequenceScoresStillPossible(
            @NonNull final StemmedWords baseStems,
            final StemmedWords additionStems,
            final List<Integer> nums) {
        float res = 0;

        for (final int num : nums) {
            res += evaluateAdditionWordSequence(baseStems, additionStems,
                    TextAdditionEvaluator::deservesNoPenalty, num);
        }
        return res;
    }

    /**
     * Bewertet diese Hinzufügung (<code>additionStems</code>) an diesen
     * <code>baseStems</code>-Text - wobei nur Wortfolgen der Länge
     * <code>num</code> untersucht werden. <code>additionStems</code>
     * und <code>baseStems</code> müssen in gleicher Groß-/Kleinschreibung
     * vorliegen.
     *
     * @return Bewertungsergebnis - je größer die Zahl, desto besser
     */
    private static float evaluateAdditionWordSequence(
            @NonNull final StemmedWords baseStems,
            final StemmedWords additionStems,
            final IPenaltyChecker penaltyChecker,
            final int num) {
        // Dieser Code wird sehr oft durchlaufen, es ist gut, Zeit zu sparen!

        float res = 0;
        // Wir suchen: Gibt es am Ende von baseStems genau num Wörter aus dem Anfang von
        // additionStems in Folge?
        final int baseStemsSize = baseStems.size();
        final int additionStemsSize = additionStems.size();

        for (int baseWordsIndex = baseStemsSize - num; baseWordsIndex > 0; baseWordsIndex--) {
            for (int additionWordsIndex = 0; additionWordsIndex + num <= additionStemsSize;
                 additionWordsIndex++) {
                final int distanceBase = baseStemsSize - num - baseWordsIndex;
                final int distance = distanceBase + additionWordsIndex + 1;
                final float penaltyIfUnacceptableRepitition =
                        calcPenaltyIfUnacceptableRepitition(num, distance);

                // Längere additionSequences haben logischerweise mehr Wiederholungen
                // als kürzere. Deshalb haben kürzere additionSequences einen
                // Vorteil gegenüber längeren, wenn wir penalties nur ABZIEHEN.
                // Deshalb ADDIEREN wir hier eine DURCHSCHNITTLICHE
                // penalty. Damit pendelt res immer um 0 statt mit
                // längeren additionSequences immer kleiner und kleiner zu werden.
                res += penaltyIfUnacceptableRepitition * ERFAHRUNGSWERT;

                if (penaltyChecker.deservesPenalty(baseStems, additionStems, num, baseWordsIndex,
                        additionWordsIndex)) {
                    // Schlecht! - Und je näher am Ende von baseStems und je näher am
                    //  Anfang von additionStems, desto schlechter (negative Zahlen).

                    res -= penaltyIfUnacceptableRepitition;
                }
            }
        }
        return res;
    }

    private static boolean calcDeservesPenalty(@NonNull final StemmedWords baseStems,
                                               final StemmedWords additionStems, final int num,
                                               final int baseWordsIndex,
                                               final int additionWordsIndex) {
        return StemmedWords.subListsEqual(baseStems, baseWordsIndex,
                additionStems, additionWordsIndex, num) &&
                !repetitionAcceptable(baseStems, baseWordsIndex, num);
    }

    private static boolean deservesNoPenalty(@NonNull final StemmedWords baseStems,
                                             final StemmedWords additionStems,
                                             final int num,
                                             final int baseWordsIndex,
                                             final int additionWordsIndex) {
        return false;
    }

    private static float calcPenaltyIfUnacceptableRepitition(final int num, final int distance) {
        return (num * num * num) / (float)
                (distance * distance);
    }

    private static boolean repetitionAcceptable(
            final StemmedWords sequence, final int index, final int length) {
        if (length == 0) {
            return true;
        }

        if (length > 1) {
            return false;
        }
        return repetitionAcceptable(sequence.get(index));
    }

    @Contract(pure = true)
    private static boolean repetitionAcceptable(final String word) {
        return REPITION_ACCEPTABLE.contains(word);
    }
}
