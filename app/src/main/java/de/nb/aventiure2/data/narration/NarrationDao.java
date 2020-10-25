package de.nb.aventiure2.data.narration;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

import de.nb.aventiure2.data.world.time.*;
import de.nb.aventiure2.german.description.AbstractDescription;
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

        final List<NarrationAddition> narrationAdditionAlternatives =
                NarrationAdditionBuilder.toNarrationAdditions(
                        alternatives, initialNarration);

        narrateAltNarrationAdditions(narrationSource, narrationAdditionAlternatives,
                initialNarration);
    }

    private void narrateAltNarrationAdditions(final Narration.NarrationSource narrationSource,
                                              final List<NarrationAddition> alternatives,
                                              final Narration initialNarration) {
        checkArgument(alternatives.size() > 0, "No alternatives");

        final NarrationAddition bestNarrationAddition =
                getBestNarrationAddition(alternatives, initialNarration);

        narrate(narrationSource, bestNarrationAddition);
    }

    @Nullable
    private static NarrationAddition getBestNarrationAddition(
            final List<NarrationAddition> alternatives,
            final Narration initialNarration) {
        float bestScore = Float.NEGATIVE_INFINITY;
        NarrationAddition bestNarrationAddition = null;

        for (final NarrationAddition narrationAdditionAlternative : alternatives) {
            final float score = TextAdditionEvaluator.evaluateAddition(
                    initialNarration.getText(),
                    narrationAdditionAlternative.getText());

            if (score > bestScore) {
                bestScore = score;
                bestNarrationAddition = narrationAdditionAlternative;
            }
        }
        return bestNarrationAddition;
    }

    NarrationAdditionWithScoreAndElapsedTime chooseBest(
            final Collection<TimedDescription> alternatives) {
        checkArgument(alternatives.size() > 0, "No alternatives");

        final Narration initialNarration = requireNarration();

        NarrationAddition bestNarrationAddition = null;
        float bestScore = Float.NEGATIVE_INFINITY;
        AvTimeSpan bestTimeElapsed = null;

        // TODO Hier könnte es textuelle Duplikate geben - sowohl zwischen den
        //  NarrationAdditions einer AbstractDescriptions also auch zwischen den NarrationAdditions
        //  verschiedener AbstractDescriptions. Die Duplikate kosten vermutlich viel Zeit -
        //  also sollte man sie herausfiltern. Da nach den ganzen NarrationAddition-Prüfungen
        //  am Ende wieder die bestDesc relevant ist, ist das nicht trivial.

        for (final TimedDescription descAlternative : alternatives) {
            final List<NarrationAddition> narrationAdditions =
                    NarrationAdditionBuilder.toNarrationAdditions(
                            descAlternative.getDescription(),
                            initialNarration);
            final IndexAndScore indexAndScore = calcBest(
                    initialNarration,
                    narrationAdditions);
            if (indexAndScore.score > bestScore) {
                bestScore = indexAndScore.score;
                bestNarrationAddition = narrationAdditions.get(indexAndScore.index);
                bestTimeElapsed = descAlternative.getTimeElapsed();
            }
        }

        return new NarrationAdditionWithScoreAndElapsedTime(
                bestNarrationAddition, bestScore, bestTimeElapsed
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
            @NonNull final NarrationAddition narrationAddition) {
        checkNotNull(narrationAddition, "narrationAddition is null");

        @Nullable final Narration currentNarration = requireNarration();

        delete(currentNarration);

        final Narration res = currentNarration.add(narrationSource,
                narrationAddition);
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
     * Wählt einen {@link NarrationAddition} aus den Alternativen und gibt den Score zurück -
     * versucht dabei vor allem, Wiederholgungen mit der unmittelbar zuvor geschriebenen
     * Narration zu vermeiden.
     */
    private static IndexAndScore calcBest(
            final Narration initialNarration,
            final Collection<NarrationAddition> alternatives) {
        return calcBest(initialNarration,
                alternatives.toArray(new NarrationAddition[0]));
    }

    /**
     * Wählt einen {@link NarrationAddition} aus den Alternativen und gibt den Score zurück.
     */
    private static IndexAndScore calcBest(
            final Narration initialNarration,
            final NarrationAddition... alternatives) {
        checkArgument(alternatives.length > 0,
                "No alternatives");

        final String currentText = initialNarration.getText();

        int bestIndex = -1;
        float bestScore = Float.NEGATIVE_INFINITY;

        for (int i = 0; i < alternatives.length; i++) {
            final NarrationAddition alternative = alternatives[i];
            final float score =
                    TextAdditionEvaluator
                            .evaluateAddition(currentText, alternative.getText());
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

        private int getIndex() {
            return index;
        }

        private float getScore() {
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
