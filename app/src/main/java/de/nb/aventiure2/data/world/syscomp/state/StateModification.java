package de.nb.aventiure2.data.world.syscomp.state;

import androidx.annotation.NonNull;

import com.google.common.collect.ImmutableCollection;

import java.util.function.Supplier;

import de.nb.aventiure2.german.description.TimedDescription;
import de.nb.aventiure2.scaction.impl.ZustandVeraendernAction;

/**
 * Daten für eine Zustandsänderung, die der SC an einem {@link IHasStateGO} vornehmen kann (z.B.
 * er zerbricht das Holz in handliche Stücke).
 */
public class StateModification<S extends Enum<S>> {
    /**
     * Name der {@link ZustandVeraendernAction} für den SC.
     */
    private final String actionName;

    private final Supplier<ImmutableCollection<? extends TimedDescription<?>>> describer;

    private final S newState;

    public StateModification(final String actionName,
                             final Supplier<ImmutableCollection<? extends TimedDescription<?>>> describer,
                             final S newState) {
        this.actionName = actionName;
        this.describer = describer;
        this.newState = newState;
    }

    /**
     * Gibt den Namen der {@link ZustandVeraendernAction} für den SC zurück.
     */
    @NonNull
    public String getActionName() {
        return actionName;
    }

    public S getNewState() {
        return newState;
    }

    public ImmutableCollection<? extends TimedDescription<?>> altTimedDescriptions() {
        return describer.get();
    }
}
