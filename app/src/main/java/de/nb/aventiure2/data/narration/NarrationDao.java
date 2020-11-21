package de.nb.aventiure2.data.narration;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

import de.nb.aventiure2.german.description.AbstractDescription;
import de.nb.aventiure2.german.description.AllgDescription;
import de.nb.aventiure2.german.description.TimedDescription;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Android Room DAO for {@link Narration}s.
 */
@Dao
public abstract class NarrationDao {
    @Nullable
    private Narration narrationCached;

    public NarrationDao() {
    }

    void narrateAltDescriptions(final Narration.NarrationSource narrationSource,
                                final Collection<AbstractDescription<?>> alternatives) {
        checkArgument(alternatives.size() > 0, "No alternatives");

        final Narration initialNarration = requireNarration();

        final List<AllgDescription> allgDescriptionAlternatives =
                AllgDescriptionBuilder.toAllgDescriptions(
                        alternatives, initialNarration);

        narrateAltAllgDescriptions(narrationSource, allgDescriptionAlternatives,
                initialNarration);
    }

    private void narrateAltAllgDescriptions(final Narration.NarrationSource narrationSource,
                                            final List<AllgDescription> alternatives,
                                            final Narration initialNarration) {
        checkArgument(!alternatives.isEmpty(), "No alternatives");

        final AllgDescription bestAllgDescription =
                getBestAllgDescription(alternatives, initialNarration);

        narrate(narrationSource, bestAllgDescription);
    }

    @Nullable
    public
    AllgTimedDescriptionWithScore chooseBestCombination(
            final Collection<AbstractDescription<?>> firstAlternatives,
            final Collection<? extends TimedDescription<?>> secondAlternatives) {
        checkArgument(!firstAlternatives.isEmpty(), "No first alternatives");
        checkArgument(!secondAlternatives.isEmpty(), "No second alternatives");

        final Narration initialNarration = requireNarration();

        final List<TimedDescription<AllgDescription>> combinations = Lists.newArrayList();

        for (final AbstractDescription<?> first : firstAlternatives) {
            for (final TimedDescription<?> second : secondAlternatives) {
                combinations.addAll(
                        DescriptionCombiner.combine(
                                first,
                                second.getDescription(),
                                initialNarration).stream()
                                .map(na -> new TimedDescription<>
                                        (na, second.getTimeElapsed(),
                                                second.getCounterIdIncrementedIfTextIsNarrated()))
                                .collect(ImmutableList.toImmutableList())
                );
            }
        }

        if (combinations.isEmpty()) {
            return null;
        }

        return chooseBest(combinations);
    }

    @Nullable
    private static AllgDescription getBestAllgDescription(
            final List<AllgDescription> alternatives,
            final Narration initialNarration) {
        float bestScore = Float.NEGATIVE_INFINITY;
        AllgDescription bestAllgDescription = null;

        for (final AllgDescription allgDescriptionAlternative : alternatives) {
            final float score = TextAdditionEvaluator.evaluateAddition(
                    initialNarration.getText(),
                    allgDescriptionAlternative.getDescriptionHauptsatz());

            if (score > bestScore) {
                bestScore = score;
                bestAllgDescription = allgDescriptionAlternative;
            }
        }
        return bestAllgDescription;
    }

    AllgTimedDescriptionWithScore chooseBest(
            final  Collection<? extends TimedDescription<?>> alternatives) {
        checkArgument(!alternatives.isEmpty(), "No alternatives");

        final Narration initialNarration = requireNarration();

        TimedDescription<AllgDescription> bestAllgTimedDescription = null;
        float bestScore = Float.NEGATIVE_INFINITY;

        // TODO Hier könnte es textuelle Duplikate geben - sowohl zwischen den
        //  AllgDescriptions einer AbstractDescriptions also auch zwischen den AllgDescriptions
        //  verschiedener AbstractDescriptions. Die Duplikate kosten vermutlich viel Zeit -
        //  also sollte man sie herausfiltern. Da nach den ganzen AllgDescription-Prüfungen
        //  am Ende wieder die bestDesc relevant ist, ist das nicht trivial.

        for (final TimedDescription<?> descAlternative : alternatives) {
            final List<AllgDescription> allgDescriptions =
                    AllgDescriptionBuilder.toAllgDescriptions(
                            descAlternative.getDescription(),
                            initialNarration);
            final IndexAndScore indexAndScore = calcBest(
                    initialNarration,
                    allgDescriptions);
            if (indexAndScore.score > bestScore) {
                bestScore = indexAndScore.score;
                bestAllgTimedDescription =
                        new TimedDescription<>(
                                allgDescriptions.get(indexAndScore.index),
                                descAlternative.getTimeElapsed());
            }
        }

        return new AllgTimedDescriptionWithScore(
                bestAllgTimedDescription, bestScore
        );
    }

    // TODO Bei narrate() eine eingebettete Sprache erlauben:
    //  - {RAPUNZEL.std.nom) immer die Langform?
    //  - {RAPUNZEL.short.nom) immer die Langform?
    //  - {RAPUNZEL.persPron.nom) Personalprononem (kontextabhängig von dem was zuvor stand!)
    //  - {persPron.nom} (Kurzform)
    //  - {RAPUNZEL.ana.nom) Nimmt möglichst eine Anapher
    //  - {RAPUNZEL.nom): Wählt automatisch richtig (kontextabhängig!)
    //  - .phorik(..) automatisch oder heuristisch setzen?!

    void narrate(
            final Narration.NarrationSource narrationSource,
            @NonNull final AllgDescription allgDescription) {
        checkNotNull(allgDescription, "allgDescription is null");

        @Nullable final Narration currentNarration = requireNarration();

        delete(currentNarration);

        final Narration res = currentNarration.add(narrationSource,
                allgDescription);
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
     * Wählt einen {@link AllgDescription} aus den Alternativen und gibt den Score zurück -
     * versucht dabei vor allem, Wiederholgungen mit der unmittelbar zuvor geschriebenen
     * Narration zu vermeiden.
     */
    private static IndexAndScore calcBest(
            final Narration initialNarration,
            final Collection<AllgDescription> alternatives) {
        return calcBest(initialNarration,
                alternatives.toArray(new AllgDescription[0]));
    }

    /**
     * Wählt einen {@link AllgDescription} aus den Alternativen und gibt den Score zurück.
     */
    private static IndexAndScore calcBest(
            final Narration initialNarration,
            final AllgDescription... alternatives) {
        checkArgument(alternatives.length > 0,
                "No alternatives");

        final String currentText = initialNarration.getText();

        int bestIndex = -1;
        float bestScore = Float.NEGATIVE_INFINITY;

        for (int i = 0; i < alternatives.length; i++) {
            final AllgDescription alternative = alternatives[i];
            final float score =
                    TextAdditionEvaluator
                            .evaluateAddition(currentText, alternative.getDescriptionHauptsatz());
            if (score > bestScore) {
                bestScore = score;
                bestIndex = i;
            }
        }

        return new IndexAndScore(bestIndex, bestScore);
    }

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

    private static class IndexAndScore {
        private final int index;
        private final float score;

        private IndexAndScore(final int index, final float score) {
            this.index = index;
            this.score = score;
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
