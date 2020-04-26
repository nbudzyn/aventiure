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

import de.nb.aventiure2.data.world.base.GameObjectId;
import de.nb.aventiure2.data.world.syscomp.storingplace.IHasStoringPlaceGO;
import de.nb.aventiure2.data.world.time.AvTimeSpan;
import de.nb.aventiure2.german.base.AbstractDescription;
import de.nb.aventiure2.german.base.DuDescription;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static de.nb.aventiure2.data.storystate.StoryState.StructuralElement.SENTENCE;
import static de.nb.aventiure2.data.storystate.StoryStateBuilder.t;
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

    public AvTimeSpan addAlt(final AbstractDescription... alternatives) {
        return addAlt(SENTENCE, alternatives);
    }

    public AvTimeSpan addAlt(final StoryState.StructuralElement startsNewUnlessSatzreihung,
                             final AbstractDescription... alternatives) {
        final StoryState initialStoryState = getStoryState();
        return addAlt(startsNewUnlessSatzreihung,
                asList(alternatives), initialStoryState, initialStoryState.getLastRoom());
    }

    public AvTimeSpan addAlt(final ImmutableCollection.Builder<AbstractDescription> alternatives) {
        return addAlt(alternatives.build());
    }

    public AvTimeSpan addAlt(final Collection<AbstractDescription> alternatives) {
        return addAlt(SENTENCE, alternatives);
    }

    public AvTimeSpan addAlt(final StoryState.StructuralElement startsNewUnlessSatzreihung,
                             final Collection<AbstractDescription> alternatives) {
        final StoryState initialStoryState = getStoryState();
        return addAlt(startsNewUnlessSatzreihung,
                alternatives, initialStoryState, initialStoryState.getLastRoom());
    }

    public AvTimeSpan addAlt(final @Nullable IHasStoringPlaceGO lastRoom,
                             final AbstractDescription... alternatives) {
        return addAlt(getStoryState(), lastRoom, alternatives);
    }

    public AvTimeSpan addAlt(final StoryState initialStoryState,
                             final @Nullable IHasStoringPlaceGO lastRoom,
                             final AbstractDescription... alternatives) {
        return addAlt(asList(alternatives), initialStoryState,
                lastRoom != null ? lastRoom.getId() : null);
    }

    public AvTimeSpan addAlt(final Collection<AbstractDescription> alternatives,
                             final @Nullable IHasStoringPlaceGO lastRoom) {
        return addAlt(alternatives,
                getStoryState(),
                lastRoom != null ? lastRoom.getId() : null);
    }

    public AvTimeSpan addAlt(final Collection<AbstractDescription> alternatives,
                             final StoryState initialStoryState,
                             final @Nullable GameObjectId lastRoomId) {
        return addAlt(SENTENCE, alternatives, initialStoryState, lastRoomId);
    }

    public AvTimeSpan addAlt(final StoryState.StructuralElement startsNewUnlessSatzreihung,
                             final Collection<AbstractDescription> alternatives,
                             final StoryState initialStoryState,
                             final @Nullable GameObjectId lastRoomId) {
        checkArgument(alternatives.size() > 0,
                "No alternatives");

        AbstractDescription bestDesc = null;
        float bestScore = Float.NEGATIVE_INFINITY;
        StoryStateBuilder bestStoryStateBuilder = null;
        for (final AbstractDescription descAlternative : alternatives) {
            final List<StoryStateBuilder> storyStateBuildersForAlternative =
                    toStoryStateBuilders(startsNewUnlessSatzreihung, descAlternative,
                            initialStoryState, lastRoomId);
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

    public AvTimeSpan add(final AbstractDescription desc) {
        return add(SENTENCE, desc);
    }

    public AvTimeSpan add(final StoryState.StructuralElement startsNewUnlessSatzreihung,
                          final AbstractDescription desc) {
        final StoryState initialStoryState = getStoryState();
        return add(startsNewUnlessSatzreihung, desc, initialStoryState,
                initialStoryState.getLastRoom());
    }

    public AvTimeSpan add(final StoryState.StructuralElement startsNewUnlessSatzreihung,
                          final AbstractDescription desc,
                          final @Nullable IHasStoringPlaceGO lastRoom) {
        final StoryState initialStoryState = getStoryState();
        return add(startsNewUnlessSatzreihung, desc, initialStoryState,
                lastRoom != null ? lastRoom.getId() : null);
    }

    public AvTimeSpan add(final StoryState.StructuralElement startsNewUnlessSatzreihung,
                          final AbstractDescription desc, @Nullable final GameObjectId lastRoom) {
        final StoryState initialStoryState = getStoryState();
        return add(startsNewUnlessSatzreihung, desc, initialStoryState, lastRoom);
    }

    private AvTimeSpan add(final StoryState.StructuralElement startsNewUnlessSatzreihung,
                           final AbstractDescription desc, final StoryState initialStoryState,
                           @Nullable final GameObjectId lastRoom) {
        add(chooseNextFrom(initialStoryState,
                toStoryStateBuilders(startsNewUnlessSatzreihung, desc, initialStoryState,
                        lastRoom)));

        return desc.getTimeElapsed();
    }

    private static List<StoryStateBuilder> toStoryStateBuilders(
            final StoryState.StructuralElement startsNewUnlessSatzreihung,
            final AbstractDescription desc,
            final StoryState initialStoryState,
            @Nullable final GameObjectId lastRoom) {
        if (initialStoryState.allowsAdditionalDuSatzreihengliedOhneSubjekt() &&
                desc instanceof DuDescription) {
            final DuDescription duDesc = (DuDescription) desc;
            return ImmutableList.of(t(StoryState.StructuralElement.WORD,
                    "und " +
                            duDesc.getDescriptionSatzanschlussOhneSubjekt())
                    .komma(duDesc.kommaStehtAus())
                    .dann(duDesc.dann())
                    .letzterRaum(lastRoom));
        } else if (initialStoryState.dann()) {
            return ImmutableList.of(t(startsNewUnlessSatzreihung,
                    desc.getDescriptionHauptsatzMitKonjunktionaladverbWennNoetig("dann"))
                    .komma(desc.kommaStehtAus())
                    .undWartest(desc.allowsAdditionalDuSatzreihengliedOhneSubjekt())
                    .dann(false)
                    .letzterRaum(lastRoom));
        } else {
            final ImmutableList.Builder<StoryStateBuilder> alternatives =
                    ImmutableList.builder();
            alternatives.add(toHauptsatzStoryStateBuilder(startsNewUnlessSatzreihung, desc)
                    .letzterRaum(lastRoom));

            if (desc instanceof DuDescription) {
                alternatives.add(toHauptsatzMitSpeziellemVorfeldStoryStateBuilder(
                        startsNewUnlessSatzreihung,
                        (DuDescription) desc)
                        .letzterRaum(lastRoom));
            }

            return alternatives.build();
        }
    }

    private static StoryStateBuilder toHauptsatzStoryStateBuilder(
            final StoryState.StructuralElement startsNewUnlessSatzreihung,
            @NonNull final AbstractDescription desc) {
        return t(startsNewUnlessSatzreihung,
                desc.getDescriptionHauptsatz())
                .komma(desc.kommaStehtAus())
                .undWartest(desc.allowsAdditionalDuSatzreihengliedOhneSubjekt())
                .dann(desc.dann());
    }

    private static StoryStateBuilder toHauptsatzMitSpeziellemVorfeldStoryStateBuilder(
            final StoryState.StructuralElement startsNewUnlessSatzreihung,
            @NonNull final DuDescription desc) {
        return t(startsNewUnlessSatzreihung,
                desc.getDescriptionHauptsatzMitSpeziellemVorfeld())
                .komma(desc.kommaStehtAus())
                .undWartest(desc.allowsAdditionalDuSatzreihengliedOhneSubjekt())
                .dann(desc.dann());
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
    public static StoryStateBuilder
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
