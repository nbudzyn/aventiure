package de.nb.aventiure2.data.narration;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import org.jetbrains.annotations.Contract;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

import de.nb.aventiure2.german.description.AbstractDescription;
import de.nb.aventiure2.german.description.TextDescription;
import de.nb.aventiure2.german.description.TimedDescription;
import de.nb.aventiure2.german.stemming.StemmedWords;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.collect.ImmutableList.toImmutableList;
import static de.nb.aventiure2.data.narration.DescriptionCombiner.combine;
import static de.nb.aventiure2.data.narration.TextDescriptionBuilder.toTextDescriptions;
import static de.nb.aventiure2.german.description.TimedDescription.toUntimed;
import static de.nb.aventiure2.util.StreamUtil.*;
import static java.util.Objects.requireNonNull;

/**
 * Android Room DAO for {@link Narration}s.
 */
@Dao
public abstract class NarrationDao {
    @Nullable
    private Narration narrationCached;

    NarrationDao() {
    }

    void narrateAltDescriptions(final Narration.NarrationSource narrationSource,
                                final Collection<AbstractDescription<?>> alternatives) {
        checkArgument(alternatives.size() > 0, "No alternatives");

        final Narration initialNarration = requireNarration();

        final ImmutableList<TextDescription> textDescriptionAlternatives =
                toTextDescriptions(alternatives, initialNarration);

        narrateAltTextDescriptions(
                narrationSource, textDescriptionAlternatives, initialNarration);
    }

    private void narrateAltTextDescriptions(final Narration.NarrationSource narrationSource,
                                            final ImmutableList<TextDescription> alternatives,
                                            final Narration initialNarration) {
        checkArgument(!alternatives.isEmpty(), "No alternatives");

        final TextDescription bestTextDescription =
                calcBestTextDescription(alternatives, initialNarration);

        narrateAndConsume(alternatives, narrationSource, bestTextDescription);
    }

    @NonNull
    List<TimedDescription<TextDescription>> altCombinations(
            final Collection<AbstractDescription<?>> firstAlternatives,
            final Collection<? extends TimedDescription<?>> secondAlternatives) {
        checkArgument(!firstAlternatives.isEmpty(), "No first alternatives");
        checkArgument(!secondAlternatives.isEmpty(), "No second alternatives");

        final Narration initialNarration = requireNarration();

        final List<TimedDescription<TextDescription>> combinations = Lists.newArrayList();

        for (final AbstractDescription<?> first : firstAlternatives) {
            for (final TimedDescription<?> second : secondAlternatives) {
                combinations.addAll(
                        mapToList(combine(
                                first,
                                second.getDescription(),
                                initialNarration), na -> na
                                .timed(second.getTimeElapsed())
                                .withCounterIdIncrementedIfTextIsNarrated(
                                        second.getCounterIdIncrementedIfTextIsNarrated()))
                );
            }
        }
        return combinations;
    }

    private TextDescription calcBestTextDescription(
            final ImmutableList<TextDescription> alternatives,
            final Narration initialNarration) {
        // Optimierung
        if (alternatives.size() == 1) {
            return alternatives.get(0);
        }

        return alternatives.get(calcBestIndexAndScore(alternatives, initialNarration).index);
    }

    TimedTextDescriptionWithScore chooseBest(
            final Collection<? extends TimedDescription<?>> alternatives) {
        return chooseBest(requireNarration(), alternatives);
    }

    @NonNull
    private TimedTextDescriptionWithScore chooseBest(
            final Narration initialNarration,
            final Collection<? extends TimedDescription<?>> timedAlternatives) {
        checkArgument(!timedAlternatives.isEmpty(), "No timedAlternatives");

        // Es könnte Duplikate geben:
        //  - Duplikate innerhalb einer der timedAlternatives
        //  - Duplikate zwischen mehreren timedAlternatives
        // Um Zeit zu sparen, filtern wir die Duplikate heraus.

        final ImmutableList<TimedDescription<TextDescription>> allGeneratedDescriptionsTimed =
                timedAlternatives.stream()
                        .flatMap(t ->
                                toTextDescriptions(initialNarration, t.getDescription()).stream()
                                        .map(a -> a.timed(t.getTimeElapsed())
                                                .withCounterIdIncrementedIfTextIsNarrated(
                                                        t.getCounterIdIncrementedIfTextIsNarrated())))
                        .distinct()
                        .collect(toImmutableList());

        final IndexAndScore indexAndScore =
                calcBestTimed(initialNarration, allGeneratedDescriptionsTimed);
        return new TimedTextDescriptionWithScore(
                allGeneratedDescriptionsTimed.get(indexAndScore.index),
                indexAndScore.score
        );
    }

    // IDEA Bei narrate() eine eingebettete Sprache erlauben:
    //  - {RAPUNZEL.std.nom) immer die Langform?
    //  - {RAPUNZEL.short.nom) immer die Langform?
    //  - {RAPUNZEL.persPron.nom) Personalprononem (kontextabhängig von dem was zuvor stand!)
    //  - {persPron.nom} (Kurzform)
    //  - {RAPUNZEL.ana.nom) Nimmt möglichst eine Anapher
    //  - {RAPUNZEL.nom): Wählt automatisch richtig (kontextabhängig!)
    //  - .phorik(..) automatisch oder heuristisch setzen?!
    //  - Beachten: Meist weiß man "RAPUNZEL" gar nicht..., sondern es ist variabel!

    void narrateAndConsume(
            final ImmutableList<? extends TextDescription> alternativesChosenFrom,
            final Narration.NarrationSource narrationSource,
            @NonNull final TextDescription textDescription) {
        narrateInternal(narrationSource, textDescription);
        consume(alternativesChosenFrom, textDescription);
    }

    private void consume(final ImmutableList<? extends TextDescription> alternativesChosenFrom,
                         final TextDescription textDescription) {
        checkArgument(alternativesChosenFrom.contains(textDescription),
                "textDescription not contained in alternativesChosenFrom");

        if (alternativesChosenFrom.size() != 1) {
            // (Optimierung. Wenn es nur eine Alternative gibt, machen wir keine
            // Buchführung, ob sie verbraucht wird.)

            final ConsumedNarrationAlternativeInfo info =
                    new ConsumedNarrationAlternativeInfo(alternativesChosenFrom, textDescription);

            final boolean wasAlreadyConsumed = insert(info) == -1;
            if (wasAlreadyConsumed) {
                resetConsumed(alternativesChosenFrom);
            }
        }
    }

    private void narrateInternal(final Narration.NarrationSource narrationSource,
                                 @NonNull final TextDescription textDescription) {
        requireNonNull(textDescription, "textDescription is null");

        @Nullable final Narration currentNarration = requireNarration();

        delete(currentNarration);

        final Narration res = currentNarration.add(narrationSource, textDescription);

        insert(res);
    }

    private void delete(final Narration narration) {
        narrationCached = null;
        deleteInternal(narration);
    }

    @NonNull
    public Narration requireNarration() {
        @Nullable final Narration narration = getNarration();
        if (narration == null) {
            throw new IllegalStateException("No current narration to add to");
        }
        return narration;
    }

    @Nullable
    public Narration getNarration() {
        if (narrationCached != null) {
            return narrationCached;
        }

        return loadNarration();
    }

    /**
     * Wählt einen {@link TextDescription} aus den Alternativen und gibt den Score zurück -
     * versucht dabei vor allem, Wiederholgungen mit der unmittelbar zuvor geschriebenen
     * Narration zu vermeiden.
     */
    private IndexAndScore calcBestTimed(
            final Narration initialNarration,
            final Collection<TimedDescription<TextDescription>> alternatives) {
        return calcBestIndexAndScore(toUntimed(alternatives), initialNarration);
    }

    /**
     * Wählt eine {@link TextDescription} aus den Alternativen und gibt den Score zurück -
     * versucht dabei vor allem, Wiederholgungen mit der unmittelbar zuvor geschriebenen
     * Narration zu vermeiden.
     */
    private IndexAndScore calcBestIndexAndScore(
            final ImmutableList<TextDescription> alternatives,
            final Narration initialNarration) {
        checkArgument(!alternatives.isEmpty(), "No alternatives");

        return calcBestIndexAndScore(
                alternatives,
                StemmedWords.stemEnd(initialNarration.getText(), 100));

    }

    /**
     * Wählt eine {@link TextDescription} aus den Alternativen und gibt den Score zurück -
     * versucht dabei vor allem, Wiederholgungen mit der unmittelbar zuvor geschriebenen
     * Narration zu vermeiden.
     */
    IndexAndScore calcBestIndexAndScore(
            final ImmutableList<TextDescription> alternatives,
            final StemmedWords baseStems) {
        checkArgument(!alternatives.isEmpty(), "No alternatives");

        // LOGGER.d("Alternatives: " + alternatives.stream().map(d -> d.getText()).collect(
        //        Collectors.toList()));

        // Optimierung. Wenn es nur eine Alternative gibt, machen wir keine
        // Buchführung, ob sie verbraucht wird.
        final ConsumedAlternatives consumedAlternatives =
                alternatives.size() != 1 ?
                        loadConsumed(alternatives) :
                        ConsumedAlternatives.EMPTY;

        // LOGGER.d("# consumed: " + consumedAlternatives.size());

        float bestScore = Float.NEGATIVE_INFINITY;
        int bestIndex = -1;
        for (int i = 0; i < alternatives.size(); i++) {
            final TextDescription alternative = alternatives.get(i);

            final float score = TextAdditionEvaluator.evaluateAddition(
                    baseStems, alternative.getTextOhneKontext(),
                    consumedAlternatives.isConsumed(alternative),
                    bestScore);

            if (score > bestScore) {
                bestScore = score;
                bestIndex = i;
            }

            // LOGGER.d("Score " + score + " für \"" + alternative.getText() + "\"");
        }

        // LOGGER.d("-> Best score " + bestScore + " für \"" + alternatives.get(bestIndex).getText()
        //        + "\"");

        return new IndexAndScore(bestIndex, bestScore);
    }

    /**
     * Lädt zu diesem <i>Satz von Alternativen</i>, welche der Alternativen
     * bereits "verbraucht" sind. Eine Alternative wird "verbraucht", wenn
     * sie gewählt wird. (Die Alternative sollte also eher nicht erneut gewählt werden, um
     * Wiederholungen zu vermeiden.)
     *
     * @param alternatives Der Satz von Alternativen
     */
    private ConsumedAlternatives loadConsumed(
            final ImmutableCollection<? extends TextDescription> alternatives) {
        return loadConsumed(
                ConsumedNarrationAlternativeInfo.calcAlternativesStringHash(alternatives));
    }

    /**
     * Lädt zu diesem <i>Hash-Code eines Satzes von Alternativen</i>, welche der Alternativen
     * bereits "verbraucht" sind. Eine Alternative wird "verbraucht", wenn
     * sie gewählt wird. (Die Alternative sollte also eher nicht erneut gewählt werden, um
     * Wiederholungen zu vermeiden.)
     */
    @NonNull
    private ConsumedAlternatives loadConsumed(final int alternativesStringHash) {
        return toConsumedAlternatives(loadConsumedInternal(alternativesStringHash));
    }

    @NonNull
    @Contract("_ -> new")
    private static ConsumedAlternatives toConsumedAlternatives(
            final List<ConsumedNarrationAlternativeInfo> infos) {
        return new ConsumedAlternatives(mapToSet(infos,
                ConsumedNarrationAlternativeInfo::getConsumedAlternativeStringHash));
    }

    @Query("SELECT * from ConsumedNarrationAlternativeInfo where :alternativesStringHash = "
            + "alternativesStringHash")
    abstract List<ConsumedNarrationAlternativeInfo> loadConsumedInternal(
            final int alternativesStringHash);

    private void resetConsumed(
            final ImmutableList<? extends TextDescription> alternativesChosenFrom) {
        resetConsumed(ConsumedNarrationAlternativeInfo
                .calcAlternativesStringHash(alternativesChosenFrom));
    }

    /**
     * Speichert das ConsumedNarrationAlternativeInfo, sofern noch keines vorhanden ist.
     *
     * @return Gab es bereits eines mit gleichen Schlüsseln (also gleichen Daten), so
     * wird -1 zurückgegeben.
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract long insert(ConsumedNarrationAlternativeInfo info);

    @Query("DELETE FROM ConsumedNarrationAlternativeInfo WHERE :alternativesStringHash = "
            + "alternativesStringHash")
    abstract void resetConsumed(int alternativesStringHash);

    @Query("SELECT * from Narration")
    abstract Narration loadNarration();

    @Delete
    abstract void deleteInternal(Narration narration);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(final Narration narration) {
        narrationCached = narration;
        insertInternal(narration);
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract void insertInternal(Narration narration);

    static class IndexAndScore {
        private final int index;
        private final float score;

        private IndexAndScore(final int index, final float score) {
            this.index = index;
            this.score = score;
        }

        int getIndex() {
            return index;
        }

        float getScore() {
            return score;
        }

        @Override
        public boolean equals(final Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            final IndexAndScore that = (IndexAndScore) o;
            return index == that.index &&
                    Float.compare(that.score, score) == 0;
        }

        @Override
        public int hashCode() {
            return Objects.hash(index, score);
        }
    }
}
