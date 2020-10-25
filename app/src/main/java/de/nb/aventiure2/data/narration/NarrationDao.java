package de.nb.aventiure2.data.narration;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.google.common.collect.ImmutableList;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

import de.nb.aventiure2.data.world.time.*;
import de.nb.aventiure2.german.base.StructuralElement;
import de.nb.aventiure2.german.description.AbstractDescription;
import de.nb.aventiure2.german.description.AbstractDuDescription;
import de.nb.aventiure2.german.description.TimedDescription;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static de.nb.aventiure2.data.narration.NarrationAddition.t;
import static de.nb.aventiure2.german.base.StructuralElement.SENTENCE;
import static de.nb.aventiure2.german.base.StructuralElement.WORD;
import static de.nb.aventiure2.german.base.StructuralElement.max;

/**
 * Android Room DAO for {@link Narration}s.
 */
@Dao
public abstract class NarrationDao {
    @Nullable
    private Narration narrationCached;

    public NarrationDao() {
    }

    AvTimeSpan narrateAltTimedDescriptions(final Narration.NarrationSource narrationSource,
                                           final Collection<TimedDescription> alternatives) {
        checkArgument(alternatives.size() > 0,
                "No alternatives");

        final Narration initialNarration = requireNarration();

        TimedDescription bestDesc = null;
        float bestScore = Float.NEGATIVE_INFINITY;
        NarrationAddition bestNarrationAddition = null;

        // TODO Hier könnte es textuelle Duplikate geben - sowohl zwischen den
        //  NarrationAdditions einer AbstractDescriptions also auch zwischen den NarrationAdditions
        //  verschiedener AbstractDescriptions. Die Duplikate kosten vermutlich viel Zeit -
        //  also sollte man sie herausfiltern. Da nach den ganzen NarrationAddition-Prüfungen
        //  am Ende wieder die bestDesc relevant ist, ist das nicht trivial.

        for (final TimedDescription descAlternative : alternatives) {
            final List<NarrationAddition> narrationBuildersForAlternative =
                    toNarrationAdditions(descAlternative.getDescription(),
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

        narrate(narrationSource, bestNarrationAddition);
        return bestDesc.getTimeElapsed();
    }

    void narrateAltDescriptions(final Narration.NarrationSource narrationSource,
                                final Collection<AbstractDescription<?>> alternatives) {
        checkArgument(alternatives.size() > 0,
                "No alternatives");

        final Narration initialNarration = requireNarration();

        float bestScore = Float.NEGATIVE_INFINITY;
        NarrationAddition bestNarrationAddition = null;

        // TODO Hier könnte es textuelle Duplikate geben - sowohl zwischen den
        //  NarrationAdditions einer AbstractDescriptions also auch zwischen den NarrationAdditions
        //  verschiedener AbstractDescriptions. Die Duplikate kosten vermutlich viel Zeit -
        //  also sollte man sie herausfiltern.

        for (final AbstractDescription<?> descAlternative : alternatives) {
            final List<NarrationAddition> narrationBuildersForAlternative =
                    toNarrationAdditions(descAlternative, initialNarration);
            final IndexAndScore indexAndScore = chooseNextIndexAndScoreFrom(
                    initialNarration,
                    narrationBuildersForAlternative);
            if (indexAndScore.getScore() > bestScore) {
                bestScore = indexAndScore.getScore();
                bestNarrationAddition =
                        narrationBuildersForAlternative.get(indexAndScore.getIndex());
            }
        }

        narrate(narrationSource, bestNarrationAddition);
    }

    TimedDescription chooseBest(final Collection<TimedDescription> alternatives) {
        checkArgument(alternatives.size() > 0, "No alternatives");

        final Narration initialNarration = requireNarration();

        TimedDescription bestDesc = null;
        float bestScore = Float.NEGATIVE_INFINITY;

        // TODO Hier könnte es textuelle Duplikate geben - sowohl zwischen den
        //  NarrationAdditions einer AbstractDescriptions also auch zwischen den NarrationAdditions
        //  verschiedener AbstractDescriptions. Die Duplikate kosten vermutlich viel Zeit -
        //  also sollte man sie herausfiltern. Da nach den ganzen NarrationAddition-Prüfungen
        //  am Ende wieder die bestDesc relevant ist, ist das nicht trivial.

        for (final TimedDescription descAlternative : alternatives) {
            final List<NarrationAddition> narrationBuildersForAlternative =
                    toNarrationAdditions(descAlternative.getDescription(),
                            initialNarration);
            final IndexAndScore indexAndScore = chooseNextIndexAndScoreFrom(
                    initialNarration,
                    narrationBuildersForAlternative);
            if (indexAndScore.getScore() > bestScore) {
                bestScore = indexAndScore.getScore();
                bestDesc = descAlternative;
            }
        }

        return bestDesc;
    }

    // TODO Bei narrate() eine eingebettete Sprache erlauben:
    //  - {RAPUNZEL.std.nom) immer die Langform?
    //  - {RAPUNZEL.short.nom) immer die Langform?
    //  - {RAPUNZEL.persPron.nom) Personalprononem (kontextabhängig von dem was zuvor stand!)
    //  - {persPron.nom} (Kurzform)
    //  - {RAPUNZEL.ana.nom) Nimmt möglichst eine Anapher
    //  - {RAPUNZEL.nom): Wählt automatisch richtig (kontextabhängig!)
    //  - .phorik(..) automatisch oder heuristisch setzen?!

    private static List<NarrationAddition> toNarrationAdditions(
            final AbstractDescription<?> desc,
            final Narration initialNarration) {
        // STORY Statt "und gehst nach Norden": ", bevor du nach Norden gehst"?
        //  (Allerdings sollte der Nebensatz dann eher eine Nebensache enthalten...)

        if (initialNarration.allowsAdditionalDuSatzreihengliedOhneSubjekt() &&
                desc.getStartsNew() == WORD &&
                desc instanceof AbstractDuDescription) {
            final AbstractDuDescription<?, ?> duDesc = (AbstractDuDescription<?, ?>) desc;
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
            final ImmutableList.Builder<NarrationAddition> alternatives = ImmutableList.builder();

            final StructuralElement startsNew = startsNewAtLeastSentenceForDuDescription(desc);

            final NarrationAddition standard = toHauptsatzNarrationAddition(startsNew, desc);
            alternatives.add(standard);

            if (desc instanceof AbstractDuDescription) {
                final NarrationAddition speziellesVorfeld =
                        toHauptsatzMitSpeziellemVorfeldNarrationAddition(
                                startsNewAtLeastSentenceForDuDescription(desc),
                                (AbstractDuDescription<?, ?>) desc);
                if (!speziellesVorfeld.getText().equals(standard.getText())) {
                    alternatives.add(speziellesVorfeld);
                }
            }

            return alternatives.build();
        }
    }

    private static StructuralElement startsNewAtLeastSentenceForDuDescription(
            final AbstractDescription<?> desc) {
        return (desc instanceof AbstractDuDescription) ?
                // Bei einer AbstractDuDescription ist der Hauptsatz ein echter
                // Hauptsatz. Daher muss ein neuer Satz begonnen werden.
                max(desc.getStartsNew(), SENTENCE) :
                // Ansonsten könnte der "Hauptsatz" auch einfach ein paar Wörter sein,
                // die Vorgabe WORD soll dann erhalten bleiben
                desc.getStartsNew();
    }

    private static NarrationAddition toHauptsatzNarrationAddition(
            final StructuralElement startsNew,
            @NonNull final AbstractDescription<?> desc) {
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
            @NonNull final AbstractDuDescription<?, ?> desc) {
        return t(startsNew,
                desc.getDescriptionHauptsatzMitSpeziellemVorfeld())
                .komma(desc.isKommaStehtAus())
                .undWartest(desc.isAllowsAdditionalDuSatzreihengliedOhneSubjekt())
                .dann(desc.isDann())
                .phorikKandidat(desc.getPhorikKandidat())
                .beendet(desc.getEndsThis());
    }

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
