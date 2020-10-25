package de.nb.aventiure2.data.narration;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;
import androidx.annotation.WorkerThread;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;

import java.util.Collection;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import de.nb.aventiure2.data.database.AvDatabase;
import de.nb.aventiure2.data.world.base.GameObjectId;
import de.nb.aventiure2.data.world.base.IGameObject;
import de.nb.aventiure2.data.world.time.*;
import de.nb.aventiure2.german.base.Personalpronomen;
import de.nb.aventiure2.german.base.PhorikKandidat;
import de.nb.aventiure2.german.base.StructuralElement;
import de.nb.aventiure2.german.description.AbstractDescription;
import de.nb.aventiure2.german.description.TimedDescription;

import static de.nb.aventiure2.data.narration.Narration.NarrationSource.REACTIONS;
import static de.nb.aventiure2.data.world.time.AvTimeSpan.*;
import static java.util.Arrays.asList;

public class Narrator {
    private static volatile Narrator INSTANCE;
    @Nullable
    private TemporaryNarration temporaryNarration = null;

    private Narration.NarrationSource narrationSourceJustInCase =
            Narration.NarrationSource.INITIALIZATION;

    private final AvNowDao nowDao;

    private final NarrationDao dao;

    public static Narrator getInstance(final AvDatabase db) {
        if (INSTANCE == null) {
            synchronized (Narrator.class) {
                if (INSTANCE == null) {
                    INSTANCE = new Narrator(db);
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

    private Narrator(final AvDatabase db) {
        dao = db.narrationDao();
        nowDao = db.nowDao();
    }

    public void setNarrationSourceJustInCase(
            final Narration.NarrationSource narrationSourceJustInCase) {
        this.narrationSourceJustInCase = narrationSourceJustInCase;
    }

    public Narration.NarrationSource getNarrationSourceJustInCase() {
        return narrationSourceJustInCase;
    }

    public void narrate(final TimedDescription desc) {
        narrateAlt(desc);
    }

    public void narrateAlt(final ImmutableCollection.Builder<AbstractDescription<?>> alternatives,
                           final AvTimeSpan timeElapsed) {
        narrateAlt(alternatives.build(), timeElapsed);
    }

    public void narrateAlt(final ImmutableCollection.Builder<TimedDescription> alternatives) {
        narrateAlt(alternatives.build());
    }

    public void narrateAlt(final TimedDescription... alternatives) {
        narrateAlt(asList(alternatives));
    }

    public void narrateAlt(final Collection<AbstractDescription<?>> alternatives,
                           final AvTimeSpan timeElapsed) {
        narrateAlt(
                alternatives.stream()
                        .map(d -> new TimedDescription(d, timeElapsed))
                        .collect(ImmutableList.toImmutableList())
        );
    }

    public void narrateAlt(final Collection<TimedDescription> alternatives) {
        if (temporaryNarration != null) {
            // Hier gibt es zwei Möglichkeiten:
            // 1. Die temporary Narration (wenn es eine gibt) und die (neuen) alternatives werden
            //  gemeinsam in einen Text gegossen, etwa in der Art
            //  "Als du weiter durch den Wald gehst, kommt dir eine Frau entgegen"
            //  (Das geht nicht immer.)
            // 2.Oder es wird nur die temporary Narration erzählt und die alternatives
            //  werden temporär gespeichert:
            //  "Du gehst weiter durch den Wald".

            final AllgDescriptionWithScoreAndElapsedTime bestTemporaryNarrationAlone =
                    dao.chooseBest(
                            // Zeit spielt hier keine Rolle
                            temporaryNarration.getDescriptionAlternatives().stream()
                                    .map(d -> new TimedDescription(d, noTime()))
                                    .collect(ImmutableList.toImmutableList())
                    );
            @Nullable final AllgDescriptionWithScoreAndElapsedTime bestCombined
                    = dao.chooseBestCombination(
                    temporaryNarration.getDescriptionAlternatives(),
                    alternatives);

            if (bestCombined != null &&
                    bestCombined.score > bestTemporaryNarrationAlone.score) {
                // Time of temporaryNarration has already been accounted for.
                dao.narrate(narrationSourceJustInCase, bestCombined.allgDescription);
                temporaryNarration = null;
                nowDao.passTime(bestTemporaryNarrationAlone.timeElapsed);
                return;
            }

            // Wenn scoreCombinedDescription <= scoreTemporaryNarrationAlone:

            // Time of temporaryNarration has already been accounted for.
            dao.narrate(temporaryNarration.getNarrationSource(),
                    bestTemporaryNarrationAlone.allgDescription);
        }

        final Set<AvTimeSpan> timesElapsed = alternatives.stream()
                .map(TimedDescription::getTimeElapsed)
                .collect(Collectors.toSet());

        final Collection<TimedDescription> bestAlternatives;
        if (timesElapsed.size() != 1) {
            // Die Alternativen dauern möglicherweise unterschiedlich lange! Dann müssen wir leider sofort
            // auf Alternativen mit gleicher Dauer beschränken - ansonsten ist
            // nicht klar, wieviel Zeit jetzt (!) vergehen muss!
            final AllgDescriptionWithScoreAndElapsedTime best =
                    dao.chooseBest(alternatives);

            bestAlternatives =
                    alternatives.stream()
                            .filter(d -> d.getTimeElapsed().equals(best.timeElapsed))
                            .collect(Collectors.toSet());
        } else {
            // Wir können alle Alternativen temporär behalten!
            bestAlternatives = alternatives;
        }

        temporaryNarration = new TemporaryNarration(narrationSourceJustInCase,
                alternatives.stream()
                        .map(TimedDescription::getDescription)
                        .collect(ImmutableList.toImmutableList()));

        nowDao.passTime(timesElapsed.iterator().next());
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
    public boolean isThema(@NonNull final GameObjectId gameObjectId) {
        // STORY es gibt auch noch andere Fälle, wo das Game Object Thema sein könnte...
        //  (Auch im NarrationDao anpassen!)

        return applyToPhorikKandidat(pk ->
                pk != null && pk.getBezugsobjekt().equals(gameObjectId));
    }

    public Personalpronomen getAnaphPersPronWennMgl(final IGameObject gameObject) {
        return applyToPhorikKandidat(pk ->
                PhorikKandidat.getAnaphPersPronWennMgl(pk, gameObject.getId()));
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

    private void doTemporaryNarration() {
        if (temporaryNarration != null) {
            // Time has already been accounted for
            dao.narrateAltDescriptions(
                    temporaryNarration.getNarrationSource(),
                    temporaryNarration.getDescriptionAlternatives());
            temporaryNarration = null;
        }
    }

}
