package de.nb.aventiure2.data.storystate;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

import de.nb.aventiure2.data.world.time.AvTimeSpan;
import de.nb.aventiure2.german.base.AbstractDescription;
import de.nb.aventiure2.german.base.DuDescription;
import de.nb.aventiure2.german.base.StructuralElement;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static de.nb.aventiure2.data.storystate.StoryStateBuilder.t;
import static de.nb.aventiure2.german.base.StructuralElement.SENTENCE;
import static de.nb.aventiure2.german.base.StructuralElement.WORD;
import static de.nb.aventiure2.german.base.StructuralElement.max;
import static java.util.Arrays.asList;

/**
 * Android Room DAO for {@link StoryState}s.
 */
@Dao
public abstract class StoryStateDao {
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

    public AvTimeSpan addAlt(final AbstractDescription<?>... alternatives) {
        final StoryState initialStoryState = getStoryState();
        return addAlt(
                asList(alternatives), initialStoryState);
    }

    public AvTimeSpan addAlt(
            final ImmutableCollection.Builder<AbstractDescription<?>> alternatives) {
        return addAlt(alternatives.build());
    }

    public AvTimeSpan addAlt(final Collection<AbstractDescription<?>> alternatives) {
        final StoryState initialStoryState = getStoryState();
        return addAlt(
                alternatives, initialStoryState);
    }

    private AvTimeSpan addAlt(final StoryState initialStoryState,
                              final AbstractDescription<?>... alternatives) {
        return addAlt(asList(alternatives), initialStoryState);
    }

    private AvTimeSpan addAlt(final Collection<AbstractDescription<?>> alternatives,
                              final StoryState initialStoryState) {
        checkArgument(alternatives.size() > 0,
                "No alternatives");

        AbstractDescription bestDesc = null;
        float bestScore = Float.NEGATIVE_INFINITY;
        StoryStateBuilder bestStoryStateBuilder = null;
        for (final AbstractDescription<?> descAlternative : alternatives) {
            final List<StoryStateBuilder> storyStateBuildersForAlternative =
                    toStoryStateBuilders(descAlternative,
                            initialStoryState);
            final IndexAndScore indexAndScore = chooseNextIndexAndScoreFrom(
                    initialStoryState,
                    storyStateBuildersForAlternative);
            if (indexAndScore.getScore() > bestScore) {
                bestScore = indexAndScore.getScore();
                bestDesc = descAlternative;
                bestStoryStateBuilder =
                        storyStateBuildersForAlternative.get(indexAndScore.getIndex());
            }
        }

        add(bestStoryStateBuilder);

        return bestDesc.getTimeElapsed();
    }

    public AvTimeSpan add(final AbstractDescription<?> desc) {
        final StoryState initialStoryState = getStoryState();
        return add(desc, initialStoryState);
    }

    private AvTimeSpan add(final AbstractDescription<?> desc,
                           final StoryState initialStoryState) {
        add(chooseNextFrom(initialStoryState,
                toStoryStateBuilders(desc, initialStoryState)));

        return desc.getTimeElapsed();
    }

    private static List<StoryStateBuilder> toStoryStateBuilders(
            final AbstractDescription<?> desc,
            final StoryState initialStoryState) {
        if (initialStoryState
                .allowsAdditionalDuSatzreihengliedOhneSubjekt() &&
                desc.getStartsNew() == WORD &&
                desc instanceof DuDescription) {
            final DuDescription duDesc = (DuDescription) desc;
            return ImmutableList.of(t(desc.getStartsNew(),
                    "und " +
                            duDesc.getDescriptionSatzanschlussOhneSubjekt())
                    .komma(duDesc.isKommaStehtAus())
                    .dann(duDesc.isDann())
                    .beendet(desc.getEndsThis()));
        } else if (initialStoryState.dann()) {
            final String satzEvtlMitDann =
                    desc.getDescriptionHauptsatzMitKonjunktionaladverbWennNoetig("dann");
            return ImmutableList.of(t(
                    startsNewAtLeastSentenceForDuDescription(desc),
                    satzEvtlMitDann)
                    .komma(desc.isKommaStehtAus())
                    .undWartest(desc.isAllowsAdditionalDuSatzreihengliedOhneSubjekt())
                    .dann(!satzEvtlMitDann.startsWith("Dann"))
                    .beendet(desc.getEndsThis()));
        } else {
            final ImmutableList.Builder<StoryStateBuilder> alternatives =
                    ImmutableList.builder();

            final StructuralElement startsNew = startsNewAtLeastSentenceForDuDescription(desc);

            alternatives.add(toHauptsatzStoryStateBuilder(startsNew, desc));

            if (desc instanceof DuDescription) {
                alternatives.add(toHauptsatzMitSpeziellemVorfeldStoryStateBuilder(
                        startsNewAtLeastSentenceForDuDescription(desc),
                        (DuDescription) desc));
            }

            return alternatives.build();
        }
    }

    private static StructuralElement startsNewAtLeastSentenceForDuDescription(
            final AbstractDescription<?> desc) {
        return (desc instanceof DuDescription) ?
                // Bei einer DuDescription ist der Hauptsatz ein echter
                // Hauptsatz. Daher muss ein neuer Satz begonnen werden.
                max(desc.getStartsNew(), SENTENCE) :
                // Ansonsten könnte der "Hauptsatz" auch einfach ein paar Wörter sein,
                // die Vorgabe WORD soll dann erhalten bleiben
                desc.getStartsNew();
    }

    private static StoryStateBuilder toHauptsatzStoryStateBuilder(
            final StructuralElement startsNew,
            @NonNull final AbstractDescription desc) {
        return t(startsNew,
                desc.getDescriptionHauptsatz())
                .komma(desc.isKommaStehtAus())
                .undWartest(desc.isAllowsAdditionalDuSatzreihengliedOhneSubjekt())
                .dann(desc.isDann())
                .beendet(desc.getEndsThis());
    }

    private static StoryStateBuilder toHauptsatzMitSpeziellemVorfeldStoryStateBuilder(
            final StructuralElement startsNew,
            @NonNull final DuDescription desc) {
        return t(startsNew,
                desc.getDescriptionHauptsatzMitSpeziellemVorfeld())
                .komma(desc.isKommaStehtAus())
                .undWartest(desc.isAllowsAdditionalDuSatzreihengliedOhneSubjekt())
                .dann(desc.isDann())
                .beendet(desc.getEndsThis());
    }

    public void add(final StoryStateBuilder text) {
        add(text.build());
    }

    public void add(final StoryState text) {
        checkNotNull(text, "text is null");

        @Nullable final StoryState currentStoryState = getStoryState();

        if (currentStoryState == null) {
            insert(text);
            return;
        }

        delete(currentStoryState);

        final StoryState res = currentStoryState.prependTo(text);
        insert(res);
    }

    @Delete
    abstract void delete(StoryState storyState);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract void insert(StoryState playerLocation);

    /**
     * Wählt einen {@link StoryStateBuilder} aus den Alternativen -
     * versucht dabei vor allem, Wiederholgungen mit dem unmittelbar zuvor geschriebenen
     * Story-Text zu vermeiden.
     */
    private static StoryStateBuilder
    chooseNextFrom(final StoryState initialStoryState,
                   final Collection<StoryStateBuilder> alternatives) {
        return chooseNextFrom(initialStoryState, alternatives.toArray(new StoryStateBuilder[0]));
    }

    /**
     * Wählt einen {@link StoryStateBuilder} aus den Alternativen -
     * versucht dabei vor allem, Wiederholgungen mit dem unmittelbar zuvor geschriebenen
     * Story-Text zu vermeiden.
     */
    private static StoryStateBuilder
    chooseNextFrom(final StoryState initialStoryState, final StoryStateBuilder... alternatives) {
        return alternatives[chooseNextIndexFrom(initialStoryState, alternatives)];
    }

    /**
     * Wählt einen {@link StoryStateBuilder} aus den Alternativen -
     * versucht dabei vor allem, Wiederholgungen mit dem unmittelbar zuvor geschriebenen
     * Story-Text zu vermeiden.
     */
    public StoryStateBuilder
    chooseNextFrom(final StoryStateBuilder... alternatives) {
        return alternatives[chooseNextIndexFrom(alternatives)];
    }

    /**
     * Wählt einen {@link StoryStateBuilder} aus den Alternativen und gibt den Indes zurück -
     * versucht dabei vor allem, Wiederholgungen mit dem unmittelbar zuvor geschriebenen
     * Story-Text zu vermeiden.
     */
    private int chooseNextIndexFrom(final StoryStateBuilder... alternatives) {
        if (alternatives.length == 1) {
            return 0;
        }

        return chooseNextIndexFrom(getStoryState(), alternatives);
    }

    /**
     * Wählt einen {@link StoryStateBuilder} aus den Alternativen und gibt den Indes zurück -
     * versucht dabei vor allem, Wiederholgungen mit dem unmittelbar zuvor geschriebenen
     * Story-Text zu vermeiden.
     */
    private static int chooseNextIndexFrom(final StoryState inititalStoryState,
                                           final StoryStateBuilder... alternatives) {
        if (alternatives.length == 1) {
            return 0;
        }

        return chooseNextIndexAndScoreFrom(inititalStoryState, alternatives).getIndex();
    }

    /**
     * Wählt einen {@link StoryStateBuilder} aus den Alternativen und gibt den Indes zurück -
     * versucht dabei vor allem, Wiederholgungen mit dem unmittelbar zuvor geschriebenen
     * Story-Text zu vermeiden.
     */
    @NonNull
    private IndexAndScore chooseNextIndexAndScoreFrom(
            final StoryStateBuilder... alternatives) {
        return chooseNextIndexAndScoreFrom(getStoryState(), alternatives);
    }

    /**
     * Wählt einen {@link StoryStateBuilder} aus den Alternativen und gibt den Indes zurück -
     * versucht dabei vor allem, Wiederholgungen mit dem unmittelbar zuvor geschriebenen
     * Story-Text zu vermeiden.
     */
    @NonNull
    private static IndexAndScore chooseNextIndexAndScoreFrom(
            final StoryState initialStoryState,
            final Collection<StoryStateBuilder> alternatives) {
        return chooseNextIndexAndScoreFrom(initialStoryState,
                alternatives.toArray(new StoryStateBuilder[0]));
    }

    /**
     * Wählt einen {@link StoryStateBuilder} aus den Alternativen und gibt den Indes zurück -
     * versucht dabei vor allem, Wiederholgungen mit dem unmittelbar zuvor geschriebenen
     * Story-Text zu vermeiden.
     */
    @NonNull
    private static IndexAndScore chooseNextIndexAndScoreFrom(
            final StoryState initialStoryState,
            final StoryStateBuilder... alternatives) {
        checkArgument(alternatives.length > 0,
                "No alternatives");

        final String currentText = initialStoryState.getText();

        int bestIndex = -1;
        float bestScore = Float.NEGATIVE_INFINITY;

        for (int i = 0; i < alternatives.length; i++) {
            final StoryStateBuilder alternative = alternatives[i];
            final float score =
                    TextAdditionEvaluator
                            .evaluateAddition(currentText, alternative.build().getText());
            if (score > bestScore) {
                bestScore = score;
                bestIndex = i;
            }
        }

        return new IndexAndScore(bestIndex, bestScore);
    }


    @Query("SELECT * from StoryState")
    public abstract StoryState getStoryState();
}
