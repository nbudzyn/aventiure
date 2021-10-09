package de.nb.aventiure2.scaction.impl;

import static java.util.Objects.requireNonNull;
import static de.nb.aventiure2.german.description.DescriptionUmformulierer.drueckeAusTimed;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.common.collect.ImmutableList;

import org.jetbrains.annotations.Contract;

import java.util.Collection;
import java.util.List;

import de.nb.aventiure2.data.narration.Narrator;
import de.nb.aventiure2.data.time.TimeTaker;
import de.nb.aventiure2.data.world.gameobject.*;
import de.nb.aventiure2.data.world.syscomp.description.IDescribableGO;
import de.nb.aventiure2.data.world.syscomp.memory.Action;
import de.nb.aventiure2.data.world.syscomp.spatialconnection.CardinalDirection;
import de.nb.aventiure2.data.world.syscomp.state.IHasStateGO;
import de.nb.aventiure2.data.world.syscomp.state.StateModification;
import de.nb.aventiure2.scaction.AbstractScAction;
import de.nb.aventiure2.scaction.stepcount.SCActionStepCountDao;

/**
 * Der SC ändert den Zustand eines Gegenstands (z.B. zerbricht er Holz in handliche Stücke).
 */
public class ZustandVeraendernAction<S extends Enum<S>, GO extends IDescribableGO & IHasStateGO<S>>
        extends AbstractScAction {
    @NonNull
    private final GO object;

    @NonNull
    private final StateModification<S> modification;

    /**
     * Erzeugt alle Aktionen, mit denen der SC dieses <code>gameObject</code>
     * in seinem Zustand ändern kann.
     * <p>
     * Beispiel: Erzeugt die Aktion, mit denen der Benutzer das Holz in handliche Stücke
     * brechen kann.
     */
    public static <S extends Enum<S>, GO extends IDescribableGO & IHasStateGO<S>>
    Collection<ZustandVeraendernAction<S, GO>> buildActions(
            final SCActionStepCountDao scActionStepCountDao,
            final TimeTaker timeTaker,
            final Narrator n, final World world,
            final GO gameObject) {
        final ImmutableList.Builder<ZustandVeraendernAction<S, GO>> res = ImmutableList.builder();

        final List<StateModification<S>> modifications =
                gameObject.stateComp().getScStateModificationData();

        for (final StateModification<S> modification : modifications) {
            res.add(new ZustandVeraendernAction<>(scActionStepCountDao, timeTaker,
                    n, world, gameObject, modification));
        }

        return res.build();
    }

    private ZustandVeraendernAction(final SCActionStepCountDao scActionStepCountDao,
                                    final TimeTaker timeTaker,
                                    final Narrator n, final World world,
                                    final GO object,
                                    final StateModification<S> modification) {
        super(scActionStepCountDao, timeTaker, n, world);
        this.object = object;
        this.modification = modification;
    }

    @Override
    public String getType() {
        return "actionZustandVeraendern";
    }

    @Nullable
    @Override
    public CardinalDirection getCardinalDirection() {
        return null;
    }

    @Override
    @NonNull
    public String getName() {
        return requireNonNull(modification.getActionName());
    }

    @Override
    protected void narrateAndDo() {
        n.narrateAlt(drueckeAusTimed(getKohaerenzrelationFuerUmformulierung(),
                modification.altTimedDescriptions()));

        object.stateComp().narrateAndSetState(modification.getNewState());

        sc.memoryComp().setLastAction(buildMemorizedAction());
    }

    @Override
    protected boolean isDefinitivWiederholung() {
        // Wir gehen hier erst einmal davon aus, dass eine State-Modifikation (wenn sie überhaupt
        // möglich ist) immer gelingt. Deshalb ist eine weitere State-Modfikation danach
        // (auch für dasselbe Objekt) etwas neues und keine Wiederholung.
        return false;
    }

    @Override
    protected boolean isDefinitivFortsetzung() {
        // Wir gehen hier erst einmal davon aus, dass eine State-Modifikation (wenn sie überhaupt
        // möglich ist) immer vollständig durchgeführt. Deshalb ist eine weitere
        // State-Modfikation danach
        // (auch für dasselbe Objekt) niemals eine Fortsetzung.
        return false;
    }

    @Override
    protected boolean isDefinitivDiskontinuitaet() {
        // Derzeit gibt es kein "hin und wieder zurück"-Modifikationen. Deshalb kann es zu
        // keinen Diskontinuitäten kommen ("Du öffnest die Kiste, aber dann schließt du sie
        // wieder.")
        return false;
    }

    @Contract(" -> new")
    @NonNull
    private Action buildMemorizedAction() {
        return new Action(Action.Type.STATE_MODIFICATION, object);
    }

}