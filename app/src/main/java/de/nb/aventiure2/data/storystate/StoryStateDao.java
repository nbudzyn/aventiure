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

import de.nb.aventiure2.data.storystate.StoryState.NarrationSource;
import de.nb.aventiure2.data.world.time.AvTimeSpan;
import de.nb.aventiure2.german.base.AbstractDescription;
import de.nb.aventiure2.german.base.DuDescription;
import de.nb.aventiure2.german.base.StructuralElement;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static de.nb.aventiure2.data.storystate.StoryAddition.t;
import static de.nb.aventiure2.german.base.StructuralElement.SENTENCE;
import static de.nb.aventiure2.german.base.StructuralElement.WORD;
import static de.nb.aventiure2.german.base.StructuralElement.max;
import static java.util.Arrays.asList;

/**
 * Android Room DAO for {@link StoryState}s.
 */
@Dao
public abstract class StoryStateDao {
    private NarrationSource narrationSourceJustInCase = NarrationSource.INITIALIZATION;

    @Nullable
    private StoryState storyStateCached;

    public void setNarrationSourceJustInCase(final NarrationSource narrationSourceJustInCase) {
        this.narrationSourceJustInCase = narrationSourceJustInCase;
    }

    public NarrationSource getNarrationSourceJustInCase() {
        return narrationSourceJustInCase;
    }

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
        final StoryState initialStoryState = requireStoryState();
        return addAlt(
                asList(alternatives), initialStoryState);
    }

    public AvTimeSpan addAlt(
            final ImmutableCollection.Builder<AbstractDescription<?>> alternatives) {
        return addAlt(alternatives.build());
    }

    public AvTimeSpan addAlt(final Collection<AbstractDescription<?>> alternatives) {
        final StoryState initialStoryState = requireStoryState();
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

        AbstractDescription<?> bestDesc = null;
        float bestScore = Float.NEGATIVE_INFINITY;
        StoryAddition bestStoryAddition = null;
        for (final AbstractDescription<?> descAlternative : alternatives) {
            final List<StoryAddition> storyStateBuildersForAlternative =
                    toStoryStateBuilders(descAlternative,
                            initialStoryState);
            final IndexAndScore indexAndScore = chooseNextIndexAndScoreFrom(
                    initialStoryState,
                    storyStateBuildersForAlternative);
            if (indexAndScore.getScore() > bestScore) {
                bestScore = indexAndScore.getScore();
                bestDesc = descAlternative;
                bestStoryAddition =
                        storyStateBuildersForAlternative.get(indexAndScore.getIndex());
            }
        }

        add(bestStoryAddition);

        return bestDesc.getTimeElapsed();
    }

    public AvTimeSpan add(final AbstractDescription<?> desc) {
        final StoryState initialStoryState = requireStoryState();
        return add(desc, initialStoryState);
    }

    private AvTimeSpan add(final AbstractDescription<?> desc,
                           final StoryState initialStoryState) {
        add(chooseNextFrom(initialStoryState,
                toStoryStateBuilders(desc, initialStoryState)));

        return desc.getTimeElapsed();
    }

    private static List<StoryAddition> toStoryStateBuilders(
            final AbstractDescription<?> desc,
            final StoryState initialStoryState) {
        // STORY Statt "und gehst nach Norden": ", bevor du nach Norden gehst"?
        //  (Allerdings sollte der Nebensatz dann eher eine Nebensache enthalten...)

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
                    .phorikKandidat(duDesc.getPhorikKandidat())
                    .beendet(desc.getEndsThis()));
        } else if (initialStoryState.dann()) {
            final String satzEvtlMitDann =
                    desc.getDescriptionHauptsatzMitKonjunktionaladverbWennNoetig("dann");
            return ImmutableList.of(t(
                    startsNewAtLeastSentenceForDuDescription(desc),
                    satzEvtlMitDann)
                    .komma(desc.isKommaStehtAus())
                    .undWartest(desc.isAllowsAdditionalDuSatzreihengliedOhneSubjekt())
                    .dann(desc.isDann()
                            && !satzEvtlMitDann.startsWith("Dann"))
                    .phorikKandidat(desc.getPhorikKandidat())
                    .beendet(desc.getEndsThis()));
        } else {
            final ImmutableList.Builder<StoryAddition> alternatives =
                    ImmutableList.builder();

            final StructuralElement startsNew = startsNewAtLeastSentenceForDuDescription(desc);

            final StoryAddition standard = toHauptsatzStoryStateBuilder(startsNew, desc);
            alternatives.add(standard);

            if (desc instanceof DuDescription) {
                final StoryAddition speziellesVorfeld =
                        toHauptsatzMitSpeziellemVorfeldStoryStateBuilder(
                                startsNewAtLeastSentenceForDuDescription(desc),
                                (DuDescription) desc);
                if (!speziellesVorfeld.getText().equals(
                        standard.getText())) {
                    alternatives.add(speziellesVorfeld);
                }
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

    private static StoryAddition toHauptsatzStoryStateBuilder(
            final StructuralElement startsNew,
            @NonNull final AbstractDescription desc) {
        return t(startsNew,
                desc.getDescriptionHauptsatz())
                .komma(desc.isKommaStehtAus())
                .undWartest(desc.isAllowsAdditionalDuSatzreihengliedOhneSubjekt())
                .dann(desc.isDann())
                .phorikKandidat(desc.getPhorikKandidat())
                .beendet(desc.getEndsThis());
    }

    private static StoryAddition toHauptsatzMitSpeziellemVorfeldStoryStateBuilder(
            final StructuralElement startsNew,
            @NonNull final DuDescription desc) {
        return t(startsNew,
                desc.getDescriptionHauptsatzMitSpeziellemVorfeld())
                .komma(desc.isKommaStehtAus())
                .undWartest(desc.isAllowsAdditionalDuSatzreihengliedOhneSubjekt())
                .dann(desc.isDann())
                .phorikKandidat(desc.getPhorikKandidat())
                .beendet(desc.getEndsThis());
    }

    public void setLastNarrationSource(final NarrationSource lastNarrationSource) {
        checkArgument(lastNarrationSource != NarrationSource.INITIALIZATION);
        requireStoryState();
        setLastNarrationSourceInternal(lastNarrationSource);
        requireStoryState(); // update cache
    }

    @Query("UPDATE StoryState SET lastNarrationSource = :lastNarrationSource")
    protected abstract void setLastNarrationSourceInternal(NarrationSource lastNarrationSource);

    public void add(@NonNull final StoryAddition storyAddition) {
        checkNotNull(storyAddition, "storyAddition is null");

        @Nullable final StoryState currentStoryState = requireStoryState();

        delete(currentStoryState);

        final StoryState res = currentStoryState.add(narrationSourceJustInCase,
                storyAddition);
        insert(res);
    }

    private void delete(final StoryState storyState) {
        storyStateCached = null;
        deleteInternal(storyState);
    }

    @Delete
    abstract void deleteInternal(StoryState storyState);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public void insert(final StoryState storyState) {
        storyStateCached = storyState;
        insertInternal(storyState);
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract void insertInternal(StoryState storyState);

    /**
     * Wählt einen {@link StoryAddition} aus den Alternativen -
     * versucht dabei vor allem, Wiederholgungen mit dem unmittelbar zuvor geschriebenen
     * Story-Text zu vermeiden.
     */
    private static StoryAddition
    chooseNextFrom(final StoryState initialStoryState,
                   final Collection<StoryAddition> alternatives) {
        return chooseNextFrom(initialStoryState, alternatives.toArray(new StoryAddition[0]));
    }

    /**
     * Wählt einen {@link StoryAddition} aus den Alternativen -
     * versucht dabei vor allem, Wiederholgungen mit dem unmittelbar zuvor geschriebenen
     * Story-Text zu vermeiden.
     */
    private static StoryAddition
    chooseNextFrom(final StoryState initialStoryState, final StoryAddition... alternatives) {
        return alternatives[chooseNextIndexFrom(initialStoryState, alternatives)];
    }

    /**
     * Wählt einen {@link StoryAddition} aus den Alternativen -
     * versucht dabei vor allem, Wiederholgungen mit dem unmittelbar zuvor geschriebenen
     * Story-Text zu vermeiden.
     */
    public StoryAddition
    chooseNextFrom(final StoryAddition... alternatives) {
        return alternatives[chooseNextIndexFrom(alternatives)];
    }

    /**
     * Wählt einen {@link StoryAddition} aus den Alternativen und gibt den Indes zurück -
     * versucht dabei vor allem, Wiederholgungen mit dem unmittelbar zuvor geschriebenen
     * Story-Text zu vermeiden.
     */
    private int chooseNextIndexFrom(final StoryAddition... alternatives) {
        if (alternatives.length == 1) {
            return 0;
        }

        return chooseNextIndexFrom(requireStoryState(), alternatives);
    }

    /**
     * Wählt einen {@link StoryAddition} aus den Alternativen und gibt den Indes zurück -
     * versucht dabei vor allem, Wiederholgungen mit dem unmittelbar zuvor geschriebenen
     * Story-Text zu vermeiden.
     */
    private static int chooseNextIndexFrom(final StoryState inititalStoryState,
                                           final StoryAddition... alternatives) {
        if (alternatives.length == 1) {
            return 0;
        }

        return chooseNextIndexAndScoreFrom(inititalStoryState, alternatives).getIndex();
    }

    /**
     * Wählt einen {@link StoryAddition} aus den Alternativen und gibt den Indes zurück -
     * versucht dabei vor allem, Wiederholgungen mit dem unmittelbar zuvor geschriebenen
     * Story-Text zu vermeiden.
     */
    @NonNull
    private IndexAndScore chooseNextIndexAndScoreFrom(
            final StoryAddition... alternatives) {
        return chooseNextIndexAndScoreFrom(requireStoryState(), alternatives);
    }

    /**
     * Wählt einen {@link StoryAddition} aus den Alternativen und gibt den Indes zurück -
     * versucht dabei vor allem, Wiederholgungen mit dem unmittelbar zuvor geschriebenen
     * Story-Text zu vermeiden.
     */
    @NonNull
    private static IndexAndScore chooseNextIndexAndScoreFrom(
            final StoryState initialStoryState,
            final Collection<StoryAddition> alternatives) {
        return chooseNextIndexAndScoreFrom(initialStoryState,
                alternatives.toArray(new StoryAddition[0]));
    }

    /**
     * Wählt einen {@link StoryAddition} aus den Alternativen und gibt den Indes zurück -
     * versucht dabei vor allem, Wiederholgungen mit dem unmittelbar zuvor geschriebenen
     * Story-Text zu vermeiden.
     */
    @NonNull
    private static IndexAndScore chooseNextIndexAndScoreFrom(
            final StoryState initialStoryState,
            final StoryAddition... alternatives) {
        checkArgument(alternatives.length > 0,
                "No alternatives");

        final String currentText = initialStoryState.getText();

        int bestIndex = -1;
        float bestScore = Float.NEGATIVE_INFINITY;

        for (int i = 0; i < alternatives.length; i++) {
            final StoryAddition alternative = alternatives[i];
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

    public boolean lastNarrationWasFromReaction() {
        return requireStoryState().lastNarrationWasFomReaction();
    }

    @NonNull
    public StoryState requireStoryState() {
        @Nullable final StoryState storyState = getStoryState();
        if (storyState == null) {
            throw new IllegalStateException("No current story state to add to");
        }
        return storyState;
    }

    public StoryState getStoryState() {
        if (storyStateCached != null) {
            return storyStateCached;
        }

        return loadStoryState();
    }

    @Query("SELECT * from StoryState")
    abstract StoryState loadStoryState();
}
