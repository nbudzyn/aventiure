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

import java.util.Collection;
import java.util.List;
import java.util.Objects;

import de.nb.aventiure2.data.narration.Narration.NarrationSource;
import de.nb.aventiure2.data.world.time.AvTimeSpan;
import de.nb.aventiure2.german.base.StructuralElement;
import de.nb.aventiure2.german.description.AbstractDescription;
import de.nb.aventiure2.german.description.DuDescription;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static de.nb.aventiure2.data.narration.NarrationAddition.t;
import static de.nb.aventiure2.german.base.StructuralElement.SENTENCE;
import static de.nb.aventiure2.german.base.StructuralElement.WORD;
import static de.nb.aventiure2.german.base.StructuralElement.max;
import static java.util.Arrays.asList;

/**
 * Android Room DAO for {@link Narration}s.
 */
@Dao
public abstract class NarrationDao {
    private NarrationSource narrationSourceJustInCase = NarrationSource.INITIALIZATION;

    @Nullable
    private Narration narrationCached;

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
        final Narration initialNarration = requireNarration();
        return addAlt(
                asList(alternatives), initialNarration);
    }

    public AvTimeSpan addAlt(
            final ImmutableCollection.Builder<AbstractDescription<?>> alternatives) {
        return addAlt(alternatives.build());
    }

    public AvTimeSpan addAlt(final Collection<AbstractDescription<?>> alternatives) {
        final Narration initialNarration = requireNarration();
        return addAlt(
                alternatives, initialNarration);
    }

    private AvTimeSpan addAlt(final Narration initialNarration,
                              final AbstractDescription<?>... alternatives) {
        return addAlt(asList(alternatives), initialNarration);
    }

    private AvTimeSpan addAlt(final Collection<AbstractDescription<?>> alternatives,
                              final Narration initialNarration) {
        checkArgument(alternatives.size() > 0,
                "No alternatives");

        AbstractDescription<?> bestDesc = null;
        float bestScore = Float.NEGATIVE_INFINITY;
        NarrationAddition bestNarrationAddition = null;
        for (final AbstractDescription<?> descAlternative : alternatives) {
            final List<NarrationAddition> narrationBuildersForAlternative =
                    toNarrationAdditions(descAlternative,
                            initialNarration);
            final IndexAndScore indexAndScore = chooseNextIndexAndScoreFrom(
                    initialNarration,
                    narrationBuildersForAlternative);
            if (indexAndScore.getScore() > bestScore) {
                bestScore = indexAndScore.getScore();
                bestDesc = descAlternative;
                bestNarrationAddition =
                        narrationBuildersForAlternative.get(indexAndScore.getIndex());
            }
        }

        add(bestNarrationAddition);

        return bestDesc.getTimeElapsed();
    }

    public AvTimeSpan add(final AbstractDescription<?> desc) {
        final Narration initialNarration = requireNarration();
        return add(desc, initialNarration);
    }

    private AvTimeSpan add(final AbstractDescription<?> desc,
                           final Narration initialNarration) {
        add(chooseNextFrom(initialNarration,
                toNarrationAdditions(desc, initialNarration)));

        return desc.getTimeElapsed();
    }

    private static List<NarrationAddition> toNarrationAdditions(
            final AbstractDescription<?> desc,
            final Narration initialNarration) {
        // STORY Statt "und gehst nach Norden": ", bevor du nach Norden gehst"?
        //  (Allerdings sollte der Nebensatz dann eher eine Nebensache enthalten...)

        if (initialNarration
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
        } else if (initialNarration.dann()) {
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
            final ImmutableList.Builder<NarrationAddition> alternatives =
                    ImmutableList.builder();

            final StructuralElement startsNew = startsNewAtLeastSentenceForDuDescription(desc);

            final NarrationAddition standard = toHauptsatzNarrationAddition(startsNew, desc);
            alternatives.add(standard);

            if (desc instanceof DuDescription) {
                final NarrationAddition speziellesVorfeld =
                        toHauptsatzMitSpeziellemVorfeldNarrationAddition(
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

    private static NarrationAddition toHauptsatzNarrationAddition(
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

    private static NarrationAddition toHauptsatzMitSpeziellemVorfeldNarrationAddition(
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
        requireNarration();
        setLastNarrationSourceInternal(lastNarrationSource);
        requireNarration(); // update cache
    }

    @Query("UPDATE Narration SET lastNarrationSource = :lastNarrationSource")
    protected abstract void setLastNarrationSourceInternal(NarrationSource lastNarrationSource);

    public void add(@NonNull final NarrationAddition narrationAddition) {
        checkNotNull(narrationAddition, "narrationAddition is null");

        @Nullable final Narration currentNarration = requireNarration();

        delete(currentNarration);

        final Narration res = currentNarration.add(narrationSourceJustInCase,
                narrationAddition);
        insert(res);
    }

    private void delete(final Narration narration) {
        narrationCached = null;
        deleteInternal(narration);
    }

    @Delete
    abstract void deleteInternal(Narration narration);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public void insert(final Narration narration) {
        narrationCached = narration;
        insertInternal(narration);
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract void insertInternal(Narration narration);

    /**
     * Wählt einen {@link NarrationAddition} aus den Alternativen -
     * versucht dabei vor allem, Wiederholgungen mit der unmittelbar zuvor geschriebenen
     * Narration zu vermeiden.
     */
    private static NarrationAddition
    chooseNextFrom(final Narration initialNarration,
                   final Collection<NarrationAddition> alternatives) {
        return chooseNextFrom(initialNarration, alternatives.toArray(new NarrationAddition[0]));
    }

    /**
     * Wählt einen {@link NarrationAddition} aus den Alternativen -
     * versucht dabei vor allem, Wiederholgungen mit der unmittelbar zuvor geschriebenen
     * Narration zu vermeiden.
     */
    private static NarrationAddition
    chooseNextFrom(final Narration initialNarration, final NarrationAddition... alternatives) {
        return alternatives[chooseNextIndexFrom(initialNarration, alternatives)];
    }

    /**
     * Wählt einen {@link NarrationAddition} aus den Alternativen -
     * versucht dabei vor allem, Wiederholgungen mit der unmittelbar zuvor geschriebenen
     * Narration zu vermeiden.
     */
    public NarrationAddition
    chooseNextFrom(final NarrationAddition... alternatives) {
        return alternatives[chooseNextIndexFrom(alternatives)];
    }

    /**
     * Wählt einen {@link NarrationAddition} aus den Alternativen und gibt den Indes zurück -
     * versucht dabei vor allem, Wiederholgungen mit der unmittelbar zuvor geschriebenen
     * Narration zu vermeiden.
     */
    private int chooseNextIndexFrom(final NarrationAddition... alternatives) {
        if (alternatives.length == 1) {
            return 0;
        }

        return chooseNextIndexFrom(requireNarration(), alternatives);
    }

    /**
     * Wählt einen {@link NarrationAddition} aus den Alternativen und gibt den Indes zurück -
     * versucht dabei vor allem, Wiederholgungen mit der unmittelbar zuvor geschriebenen
     * Narration zu vermeiden.
     */
    private static int chooseNextIndexFrom(final Narration inititalNarration,
                                           final NarrationAddition... alternatives) {
        if (alternatives.length == 1) {
            return 0;
        }

        return chooseNextIndexAndScoreFrom(inititalNarration, alternatives).getIndex();
    }

    /**
     * Wählt einen {@link NarrationAddition} aus den Alternativen und gibt den Indes zurück -
     * versucht dabei vor allem, Wiederholgungen mit der unmittelbar zuvor geschriebenen
     * Narration zu vermeiden.
     */
    @NonNull
    private IndexAndScore chooseNextIndexAndScoreFrom(
            final NarrationAddition... alternatives) {
        return chooseNextIndexAndScoreFrom(requireNarration(), alternatives);
    }

    /**
     * Wählt einen {@link NarrationAddition} aus den Alternativen und gibt den Indes zurück -
     * versucht dabei vor allem, Wiederholgungen mit der unmittelbar zuvor geschriebenen
     * Narration zu vermeiden.
     */
    @NonNull
    private static IndexAndScore chooseNextIndexAndScoreFrom(
            final Narration initialNarration,
            final Collection<NarrationAddition> alternatives) {
        return chooseNextIndexAndScoreFrom(initialNarration,
                alternatives.toArray(new NarrationAddition[0]));
    }

    /**
     * Wählt einen {@link NarrationAddition} aus den Alternativen und gibt den Indes zurück -
     * versucht dabei vor allem, Wiederholgungen mit der unmittelbar zuvor geschriebenen
     * Narration zu vermeiden.
     */
    @NonNull
    private static IndexAndScore chooseNextIndexAndScoreFrom(
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

    public boolean lastNarrationWasFromReaction() {
        return requireNarration().lastNarrationWasFomReaction();
    }

    @NonNull
    public Narration requireNarration() {
        @Nullable final Narration narration = getNarration();
        if (narration == null) {
            throw new IllegalStateException("No current narration to add to");
        }
        return narration;
    }

    public Narration getNarration() {
        if (narrationCached != null) {
            return narrationCached;
        }

        return loadNarration();
    }

    @Query("SELECT * from Narration")
    abstract Narration loadNarration();
}
