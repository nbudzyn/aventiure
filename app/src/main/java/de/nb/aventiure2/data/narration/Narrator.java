package de.nb.aventiure2.data.narration;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;
import androidx.annotation.WorkerThread;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableSet;

import java.util.Collection;
import java.util.function.Function;

import de.nb.aventiure2.data.database.AvDatabase;
import de.nb.aventiure2.data.world.base.GameObjectId;
import de.nb.aventiure2.data.world.base.IGameObject;
import de.nb.aventiure2.german.base.Personalpronomen;
import de.nb.aventiure2.german.base.PhorikKandidat;
import de.nb.aventiure2.german.base.StructuralElement;
import de.nb.aventiure2.german.description.AbstractDescription;

import static java.util.Arrays.asList;

public class Narrator {
    private static volatile Narrator INSTANCE;
    @Nullable
    private TemporaryNarration temporaryNarration = null;

    private Narration.NarrationSource narrationSourceJustInCase =
            Narration.NarrationSource.INITIALIZATION;

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
    }

    public void setNarrationSourceJustInCase(
            final Narration.NarrationSource narrationSourceJustInCase) {
        this.narrationSourceJustInCase = narrationSourceJustInCase;
    }

    public Narration.NarrationSource getNarrationSourceJustInCase() {
        return narrationSourceJustInCase;
    }

    public void narrateAlt(final AbstractDescription<?>... alternatives) {
        final Narration initialNarration = dao.requireNarration();
        dao.narrateAlt(narrationSourceJustInCase, asList(alternatives), initialNarration);

        // FIXME Damit Dinge wie "Unten angekommen..." möglich werden: Der Narrator merkt sich
        //  immer einen Satz, bevor er ihn
        //  ausgibt ("rendert"). Erst mit dem nächsten Satz wird der letzte Satz "gerendert".
        //  Bevor der Benutzer handeln kann, wird aber dieser "temporäre Satz" ausgegeben.

        // STORY "Als du unten angekommen bist..."
    }

    public void narrateAlt(final ImmutableCollection.Builder<AbstractDescription<?>> alternatives) {
        narrateAlt(alternatives.build());
    }

    public void narrateAlt(final Collection<AbstractDescription<?>> alternatives) {
        final Narration initialNarration = dao.requireNarration();
        dao.narrateAlt(narrationSourceJustInCase, alternatives, initialNarration);
    }

    public void narrate(final AbstractDescription<?> desc) {
        final Narration initialNarration = dao.requireNarration();
        dao.narrate(narrationSourceJustInCase, desc, initialNarration);
    }

    public boolean lastNarrationWasFromReaction() {
        return dao.requireNarration().lastNarrationWasFomReaction();
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
        final ImmutableSet<R> alt =
                temporaryNarration.getDescriptionAlternatives().stream()
                        .map(descriptionFunction)
                        .collect(ImmutableSet.toImmutableSet());

        if (alt.size() != 1) {
            pushTempDescription();
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
        final ImmutableSet<R> alt =
                temporaryNarration.getDescriptionAlternatives().stream()
                        .map(AbstractDescription::getPhorikKandidat)
                        .map(function)
                        .collect(ImmutableSet.toImmutableSet());

        if (alt.size() != 1) {
            pushTempDescription();
            return applyToPhorikKandidat(function);
        }

        return alt.iterator().next();
    }

    @Nullable
    public String getNarrationText() {
        pushTempDescription();
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
        pushTempDescription();
    }

    private void pushTempDescription() {
        if (temporaryNarration != null) {
            final Narration initialNarration = dao.requireNarration();
            dao.narrateAlt(narrationSourceJustInCase,
                    temporaryNarration.getDescriptionAlternatives(),
                    initialNarration);
            temporaryNarration = null;
        }
    }
}
