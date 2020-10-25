package de.nb.aventiure2.data.narration;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;
import androidx.annotation.WorkerThread;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;

import com.google.common.collect.ImmutableCollection;

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

import static de.nb.aventiure2.data.narration.Narration.NarrationSource.REACTIONS;
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


    public void narrate(final AbstractDescription<?> desc) {
        narrateAlt(desc);
    }

    public void narrateAlt(final ImmutableCollection.Builder<AbstractDescription<?>> alternatives) {
        narrateAlt(alternatives.build());
    }

    public void narrateAlt(final AbstractDescription<?>... alternatives) {
        narrateAlt(asList(alternatives));
    }

    public void narrateAlt(final Collection<AbstractDescription<?>> alternatives) {
        // FIXME Den vorherigen Satz manchmal für die Textausgabe
        //  berücksichtigen. Z.B. "Unten angekommen..." oder
        //  "Du kommst, siehst und siegst"

        // STORY "Als du unten angekommen bist..."

        final Set<AvTimeSpan> timesElapsed = alternatives.stream()
                .map(AbstractDescription::getTimeElapsed)
                .collect(Collectors.toSet());
        if (timesElapsed.size() != 1) {
            // Die Alternativen dauern unterschiedlich lange! Leider müssen wir sofort
            // eine Alternative auswählen - ansonsten ist nicht klar, wieviel Zeit
            // vergehen muss!
            doTemporaryNarration();

            final AvTimeSpan timeElapsed = dao.narrateAlt(
                    narrationSourceJustInCase,
                    alternatives);
            nowDao.passTime(timeElapsed);
            return;
        }

        doTemporaryNarration();

        temporaryNarration = new TemporaryNarration(narrationSourceJustInCase,
                alternatives);

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
            dao.narrateAlt(
                    temporaryNarration.getNarrationSource(),
                    temporaryNarration.getDescriptionAlternatives());
            temporaryNarration = null;
        }
    }

}
