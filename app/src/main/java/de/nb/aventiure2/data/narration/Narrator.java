package de.nb.aventiure2.data.narration;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;
import androidx.annotation.WorkerThread;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import de.nb.aventiure2.data.database.AvDatabase;
import de.nb.aventiure2.data.time.AvTimeSpan;
import de.nb.aventiure2.data.time.TimeTaker;
import de.nb.aventiure2.data.world.base.GameObjectId;
import de.nb.aventiure2.data.world.base.IGameObject;
import de.nb.aventiure2.data.world.counter.CounterDao;
import de.nb.aventiure2.german.base.Personalpronomen;
import de.nb.aventiure2.german.base.PhorikKandidat;
import de.nb.aventiure2.german.base.StructuralElement;
import de.nb.aventiure2.german.description.AbstractDescription;
import de.nb.aventiure2.german.description.AltDescriptionsBuilder;
import de.nb.aventiure2.german.description.AltTimedDescriptionsBuilder;
import de.nb.aventiure2.german.description.TextDescription;
import de.nb.aventiure2.german.description.TimedDescription;
import de.nb.aventiure2.german.stemming.StemmedWords;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.collect.ImmutableList.toImmutableList;
import static de.nb.aventiure2.data.narration.Narration.NarrationSource.REACTIONS;
import static de.nb.aventiure2.data.narration.TextDescriptionBuilder.distinctByKey;
import static de.nb.aventiure2.data.narration.TextDescriptionBuilder.toTextDescriptions;
import static de.nb.aventiure2.german.description.TimedDescription.toTimed;
import static de.nb.aventiure2.german.description.TimedDescription.toUntimed;
import static java.util.Arrays.asList;
import static java.util.Objects.requireNonNull;

@ParametersAreNonnullByDefault
public class Narrator {
    private static volatile Narrator INSTANCE;

    @Nullable
    private TemporaryNarration temporaryNarration = null;

    @Nonnull
    private Narration.NarrationSource narrationSourceJustInCase =
            Narration.NarrationSource.INITIALIZATION;

    @Nonnull
    private final TimeTaker timeTaker;

    @Nonnull
    private final CounterDao counterDao;

    @Nonnull
    private final NarrationDao dao;

    public static Narrator getInstance(final AvDatabase db,
                                       final TimeTaker timeTaker) {
        if (INSTANCE == null) {
            synchronized (Narrator.class) {
                if (INSTANCE == null) {
                    INSTANCE = new Narrator(db, timeTaker);
                }
            }
        }
        return INSTANCE;
    }

    @VisibleForTesting
    @WorkerThread
    public static void reset() {
        INSTANCE = null;
    }

    private Narrator(final AvDatabase db, final TimeTaker timeTaker) {
        dao = db.narrationDao();
        counterDao = db.counterDao();
        this.timeTaker = timeTaker;
    }

    public void setNarrationSourceJustInCase(
            final Narration.NarrationSource narrationSourceJustInCase) {
        this.narrationSourceJustInCase = narrationSourceJustInCase;
    }

    @NonNull
    public Narration.NarrationSource getNarrationSourceJustInCase() {
        return narrationSourceJustInCase;
    }

    public <D extends AbstractDescription<?>>
    boolean narrateIfCounterIs(final int counterValue,
                               final TimedDescription<D> desc) {
        Preconditions.checkArgument(
                desc.getCounterIdIncrementedIfTextIsNarrated() != null,
                "No counter given in TimedDescription %s", desc.getDescription());

        if (counterDao.get(desc.getCounterIdIncrementedIfTextIsNarrated()) == counterValue) {
            narrate(desc);
            return true;
        }

        return false;
    }

    public <D extends AbstractDescription<?>>
    void narrate(final TimedDescription<D> desc) {
        narrateAlt(desc);
    }

    public void narrateAlt(final AltTimedDescriptionsBuilder timeAlternatives) {
        narrateAlt(timeAlternatives.build());
    }

    public void narrateAlt(final AltDescriptionsBuilder alternatives,
                           final AvTimeSpan timeElapsed) {
        narrateAlt(alternatives, timeElapsed, null);
    }

    public void narrateAlt(final AltDescriptionsBuilder alternatives,
                           final AvTimeSpan timeElapsed,
                           @Nullable final Enum<?> counterIdIncrementedIfTextIs) {
        narrateAlt(alternatives.build(), timeElapsed, counterIdIncrementedIfTextIs);
    }

    public void narrateAlt(final TimedDescription<?>... alternatives) {
        narrateAlt(Arrays.asList(alternatives));
    }

    @SafeVarargs
    public final <D extends AbstractDescription<?>>
    void narrateAlt(final AvTimeSpan timeElapsed,
                    final D... alternatives) {
        narrateAlt(asList(alternatives), timeElapsed);
    }

    @SafeVarargs
    public final <D extends AbstractDescription<?>>
    void narrateAlt(final AvTimeSpan timeElapsed,
                    final Enum<?> counterIdIncrementedIfTextIsNarrated,
                    final D... alternatives) {
        narrateAlt(asList(alternatives), timeElapsed, counterIdIncrementedIfTextIsNarrated);
    }

    public <D extends AbstractDescription<?>>
    void narrateAlt(final Stream<D> alternativesStream, final AvTimeSpan timeElapsed) {
        narrateAlt(alternativesStream.collect(Collectors.toSet()), timeElapsed);
    }

    public <D extends AbstractDescription<?>>
    void narrateAlt(final Collection<D> alternatives,
                    final AvTimeSpan timeElapsed) {
        narrateAlt(alternatives, timeElapsed, null);
    }

    public <D extends AbstractDescription<?>>
    void narrateAlt(final Collection<D> alternatives,
                    final AvTimeSpan timeElapsed,
                    @Nullable final Enum<?> counterIdIncrementedIfTextIs) {
        narrateAlt(toTimed(alternatives, timeElapsed, counterIdIncrementedIfTextIs));
    }

    public <D extends TimedDescription<?>>
    void narrateAlt(final Stream<D> alternativesStream) {
        narrateAlt(alternativesStream.collect(Collectors.toSet()));
    }

    public void narrateAlt(final Collection<? extends TimedDescription<?>> alternatives) {
        // IDEA hier könnte ein Adressatenmodell auf Basis des SC (Interface?)
        //  entscheiden, ob SC z.B. schläft.

        if (temporaryNarration != null) {
            if (narrateTemporaryNarrationAndTryCombiningWithAlternative(alternatives)) {
                return;
            }
        }

        // Die Alternativen dauern möglicherweise unterschiedlich oder haben
        // unterschiedliche Counter! Dann müssten wir uns sofort auf die
        // "gleichen" Alternativen beschränken - ansonsten ist
        // nicht klar, wieviel Zeit jetzt (!) vergehen muss oder welcher Counter
        // jetzt (!) hochgezählt werden muss!
        final Collection<? extends TimedDescription<?>> bestAlternatives =
                chooseBestAlternativesWithSameElapsedTimeAndCounterId(alternatives);

        temporaryNarration = new TemporaryNarration(narrationSourceJustInCase,
                bestAlternatives.stream()
                        .map(TimedDescription::getDescription)
                        .collect(ImmutableList.toImmutableList()));

        passTimeAndIncCounter(bestAlternatives.iterator().next());
    }

    private void passTimeAndIncCounter(final TimedDescription<?> timedDescription) {
        timeTaker.passTime(timedDescription.getTimeElapsed());
        if (timedDescription.getCounterIdIncrementedIfTextIsNarrated() != null) {
            counterDao.inc(timedDescription.getCounterIdIncrementedIfTextIsNarrated());
        }
    }

    /**
     * Wählt aus den Alternativen die beste aus. Gibt es zusätzlich noch andere Alternativen
     * <i>mit derselben Dauer und Counter-ID wie diese Beste</i>, werden sie auch mit zurückgegeben.
     */
    @NonNull
    private Collection<? extends TimedDescription<?>> chooseBestAlternativesWithSameElapsedTimeAndCounterId(
            final Collection<? extends TimedDescription<?>> alternatives) {
        final long numberTimesElapsed = alternatives.stream()
                .map(TimedDescription::getTimeElapsed)
                .distinct()
                .count();

        final long numberCounters = alternatives.stream()
                .map(TimedDescription::getCounterIdIncrementedIfTextIsNarrated)
                .distinct()
                .count();

        if (numberTimesElapsed == 1 && numberCounters == 1) {
            // Wir können alle Alternativen temporär behalten!
            return alternatives;
        }

        // Die Alternativen dauern unterschiedlich lang oder haben verschiedene
        // Counter, die hochgezählt werden sollen! Dann müssen wir uns leider sofort
        // auf "gleiche" Alternativen beschränken - ansonsten ist
        // nicht klar, wieviel Zeit jetzt (!) vergehen muss oder welcher
        // Counter hochgezählt werden muss!
        final TimedTextDescriptionWithScore best = dao.chooseBest(alternatives);

        return alternatives.stream()
                .filter(d -> d.getTimeElapsed().equals(best.timedTextDescription.getTimeElapsed()))
                .filter(d -> Objects.equal(
                        d.getCounterIdIncrementedIfTextIsNarrated(),
                        best.timedTextDescription.getCounterIdIncrementedIfTextIsNarrated()))
                .collect(Collectors.toSet());
    }

    /**
     * Versucht, die {@link #temporaryNarration} mit einer der Alternativen
     * in einen möglichst guten Text zu gießen und diese zu erzählen.
     * <ul>
     *     <li>Wenn das gelingt, gibt diese Methode <code>true</code> zurück. Die
     *     Alternativen wurden also erzählt.
     *     <li>Wenn das nicht gelingt, erzählt diese Methode nur die
     *     {@link #temporaryNarration} und gibt <code>false</code> zurück. Die
     *     Alternativen wurden also <i>noch nicht</i> erzählt und müssen noch
     *     gespeichert werden.
     * </ul>
     */
    private boolean narrateTemporaryNarrationAndTryCombiningWithAlternative(
            final Collection<? extends TimedDescription<?>> timedAlternatives) {
        requireNonNull(temporaryNarration, "temporaryNarration is null");
        checkArgument(!timedAlternatives.isEmpty(), "No timedAlternatives!");

        // Hier gibt es zwei Möglichkeiten:
        // 1. Die temporary Narration und die (neuen) Alternatives werden
        //  gemeinsam in einen Text gegossen, etwa in der Art
        //  "Als du weiter durch den Wald gehst, kommt dir eine Frau entgegen"
        //  (Das geht nicht immer - oder ist nicht immer optimal.)
        // 2.Oder es wird nur die temporary Narration erzählt und die Alternatives
        //  werden temporär gespeichert:
        //  "Du gehst weiter durch den Wald".

        final Narration initialNarration = dao.requireNarration();
        final ImmutableList<TextDescription> allGeneratedDescriptionsAlone =
                toTextDescriptions(temporaryNarration.getDescriptionAlternatives(),
                        initialNarration);

        final List<TimedDescription<TextDescription>> alternativeCombinations =
                dao.altCombinations(
                        temporaryNarration.getDescriptionAlternatives(), timedAlternatives);

        // Optimierung, Zeit sparen!
        if (allGeneratedDescriptionsAlone.size() == 1 && alternativeCombinations.isEmpty()) {
            // Es wird nur die temporary Narration erzählt:
            //  "Du gehst weiter durch den Wald".

            // Time of temporaryNarration has already been accounted for.
            dao.narrateAndConsume(allGeneratedDescriptionsAlone,
                    temporaryNarration.getNarrationSource(),
                    allGeneratedDescriptionsAlone.get(0));

            // Die  Alternatives müssen noch temporär gespeichert werden
            return false;
        }

        final StemmedWords baseStems =
                StemmedWords.stemEnd(initialNarration.getText(), 100);

        final NarrationDao.IndexAndScore indexAndScoreAlone =
                dao.calcBestIndexAndScore(allGeneratedDescriptionsAlone, baseStems);

        if (!alternativeCombinations.isEmpty()) {
            // Hier könnte es ganz theoretisch Duplikate geben.
            //  Um Zeit zu sparen, filtern wir die Duplikate heraus.
            //  Löschen, wenn es keine Duplikate geben kann
            final ImmutableList<TimedDescription<TextDescription>>
                    alternativeCombinationsOhneDuplikate =
                    alternativeCombinations.stream()
                            .filter(distinctByKey(t -> t.getDescription().getText()))
                            .collect(toImmutableList());

            final NarrationDao.IndexAndScore indexAndScoreCombined =
                    dao.calcBestIndexAndScore(
                            toUntimed(alternativeCombinationsOhneDuplikate),
                            baseStems);

            if (indexAndScoreCombined.getScore() >= indexAndScoreAlone.getScore()) {
                // Die temporary Narration und die (neuen) Alternatives werden
                //  gemeinsam in einen optimalen Text gegossen, etwa in der Art
                //  "Als du weiter durch den Wald gehst, kommt dir eine Frau entgegen"
                temporaryNarration = null;

                narratePassTimeAndIncrementCounter(
                        alternativeCombinationsOhneDuplikate,
                        alternativeCombinationsOhneDuplikate.get(indexAndScoreCombined.getIndex()));
                return true;
            }
        }

        // Wenn scoreCombinedDescription < scoreTemporaryNarrationAlone:
        // Es wird nur die temporary Narration erzählt:
        //  "Du gehst weiter durch den Wald".

        // Time of temporaryNarration has already been accounted for.
        dao.narrateAndConsume(allGeneratedDescriptionsAlone,
                temporaryNarration.getNarrationSource(),
                allGeneratedDescriptionsAlone.get(indexAndScoreAlone.getIndex()));

        // Die  Alternatives müssen noch temporär gespeichert werden
        return false;
    }

    private void narratePassTimeAndIncrementCounter(
            final List<TimedDescription<TextDescription>> alternativeCombinations,
            final TimedDescription<TextDescription> timedTextDescription) {
        // Time of temporaryNarration has already been accounted for.
        dao.narrateAndConsume(toUntimed(alternativeCombinations),
                narrationSourceJustInCase, timedTextDescription.getDescription());
        passTimeAndIncCounter(timedTextDescription);
    }

    private void doTemporaryNarration() {
        if (temporaryNarration != null) {
            // Time has already been accounted for
            dao.narrateAltDescriptions(
                    temporaryNarration.getNarrationSource(),
                    temporaryNarration.getDescriptionAlternatives());
            temporaryNarration = null;
        }
    }

    public boolean lastNarrationWasFromReaction() {
        return getLastNarrationSource() == REACTIONS;
    }

    private Narration.NarrationSource getLastNarrationSource() {
        if (temporaryNarration != null) {
            return temporaryNarration.getNarrationSource();
        }

        return dao.requireNarration().getLastNarrationSource();
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public void saveInitialNarration(final Narration narration) {
        dao.insert(narration);
    }


    /**
     * Ob dieses Game Object zurzeit <i>Thema</i> ist (im Sinne von Thema - Rhema).
     */
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public boolean isThema(@NonNull final GameObjectId gameObjectId) {
        // IDEA es gibt auch noch andere Fälle, wo das Game Object Thema sein könnte...
        //  (Auch im NarrationDao anpassen, dort noch analog zu finden.)

        return applyToPhorikKandidat(pk ->
                pk != null && pk.getBezugsobjekt().equals(gameObjectId));
    }

    @Nullable
    public Personalpronomen getAnaphPersPronWennMgl(final IGameObject gameObject) {
        return getAnaphPersPronWennMgl(gameObject.getId());
    }

    @Nullable
    public Personalpronomen getAnaphPersPronWennMgl(final GameObjectId gameObjectId) {
        return applyToPhorikKandidat(pk ->
                PhorikKandidat.getAnaphPersPronWennMgl(pk, gameObjectId));
    }

    /**
     * Ob ein anaphorischer Bezug (z.B. mit einem Personalpronomen) auf dieses
     * Game Objekt möglich ist.
     * <br/>
     * Beispiel: "Du hebst du Lampe auf..." - jetzt ist ein anaphorischer Bezug
     * auf die Lampe mittels des Personalpronomens "sie" möglich:
     * "... und nimmst sie mit."
     */
    public boolean isAnaphorischerBezugMoeglich(final GameObjectId gameObjectId) {
        return applyToPhorikKandidat(pk -> PhorikKandidat
                .isAnaphorischerBezugMoeglich(pk, gameObjectId));
    }

    public boolean endsThisIsExactly(final StructuralElement structuralElement) {
        return applyToNarration(
                d -> d.getEndsThis() == structuralElement,
                n -> n.getEndsThis() == structuralElement
        );
    }

    /**
     * Whether the narration can be continued by a Satzreihenglied without subject where
     * the player character is the implicit subject (such as " und gehst durch die Tür.")
     */
    public boolean allowsAdditionalDuSatzreihengliedOhneSubjekt() {
        return applyToNarration(
                AbstractDescription::isAllowsAdditionalDuSatzreihengliedOhneSubjekt,
                Narration::allowsAdditionalDuSatzreihengliedOhneSubjekt
        );
    }

    public boolean dann() {
        return applyToNarration(
                AbstractDescription::isDann,
                Narration::dann
        );
    }

    private <R> R applyToNarration(final Function<AbstractDescription<?>, R> descriptionFunction,
                                   final Function<Narration, R> narrationFunction) {
        if (temporaryNarration == null) {
            return narrationFunction.apply(dao.requireNarration());
        }

        // Idee: Wenn alle temp-Alternativen zum selben Ergebnis führen, können alle
        // Alternativen möglich bleiben. Wenn nicht - dann jetzt für eine entscheiden!
        final Set<R> alt =
                temporaryNarration.getDescriptionAlternatives().stream()
                        .map(descriptionFunction)
                        .collect(Collectors.toSet());

        if (alt.size() != 1) {
            doTemporaryNarration();
            return applyToNarration(descriptionFunction, narrationFunction);
        }

        return alt.iterator().next();
    }

    private <R> R applyToPhorikKandidat(final Function<PhorikKandidat, R> function) {
        if (temporaryNarration == null) {
            return function.apply(dao.requireNarration().getPhorikKandidat());
        }

        // Idee: Wenn alle temp-Alternativen zum selben Ergebnis führen, können alle
        // Alternativen möglich bleiben. Wenn nicht - dann jetzt für eine entscheiden!
        final Set<R> alt =
                temporaryNarration.getDescriptionAlternatives().stream()
                        .map(AbstractDescription::getPhorikKandidat)
                        .map(function)
                        .collect(Collectors.toSet());

        if (alt.size() != 1) {
            doTemporaryNarration();
            return applyToPhorikKandidat(function);
        }

        return alt.iterator().next();
    }

    @Nullable
    public String getNarrationText() {
        doTemporaryNarration();
        final Narration narration = dao.getNarration();
        if (narration == null) {
            return null;
        }

        return narration.getText();
    }

    /**
     * Saves all temporary data.
     */
    public void saveAll() {
        doTemporaryNarration();
    }
}
