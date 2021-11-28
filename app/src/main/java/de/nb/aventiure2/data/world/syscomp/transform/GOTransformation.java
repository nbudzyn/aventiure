package de.nb.aventiure2.data.world.syscomp.transform;

import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

import javax.annotation.Nonnull;

import de.nb.aventiure2.data.world.syscomp.typed.GameObjectType;

/**
 * Daten für die Umwandlung von einem (oder mehreren) Objekten,
 * jeweils in einer gewissen Menge, in ein anderes (oder mehrere andere), auch jeweils in
 * gewisser Menge - durch den SC.
 *
 * @see de.nb.aventiure2.data.world.syscomp.state.StateModification
 */
public class GOTransformation {
    /**
     * Name der {@link de.nb.aventiure2.scaction.impl.TransformAction} für den SC.
     */
    private final String actionName;

    private final Map<GameObjectType, Integer> input;

    private final Function<Map<GameObjectType, Integer>, GOTransformationResult> transform;

    public GOTransformation(final String actionName,
                            final Map<GameObjectType, Integer> input,
                            final Function<Map<GameObjectType, Integer>, GOTransformationResult> transform) {
        this.actionName = actionName;
        this.input = input;
        this.transform = transform;
    }

    @Nonnull
    public String getActionName() {
        return actionName;
    }

    @Nonnull
    public Map<GameObjectType, Integer> getInput() {
        return input;
    }

    public GOTransformationResult transform() {
        return transform.apply(input);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final GOTransformation that = (GOTransformation) o;
        return actionName.equals(that.actionName) && input.equals(that.input) && transform
                .equals(that.transform);
    }

    @Override
    public int hashCode() {
        return Objects.hash(actionName, input, transform);
    }

    @Override
    public String toString() {
        return "GOTransformation{" +
                "actionName='" + actionName + '\'' +
                ", input=" + input +
                ", transform=" + transform +
                '}';
    }
}
