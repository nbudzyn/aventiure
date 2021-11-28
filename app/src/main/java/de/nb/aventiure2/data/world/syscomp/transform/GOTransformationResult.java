package de.nb.aventiure2.data.world.syscomp.transform;

import com.google.common.collect.ImmutableCollection;

import java.util.Objects;

import de.nb.aventiure2.data.world.syscomp.amount.IAmountableGO;
import de.nb.aventiure2.german.description.AltTimedDescriptionsBuilder;
import de.nb.aventiure2.german.description.TimedDescription;

public class GOTransformationResult {
    private final ImmutableCollection<? extends TimedDescription<?>> altTimedDescriptions;
    private final ImmutableCollection<IAmountableGO> output;

    private GOTransformationResult(
            final ImmutableCollection<? extends TimedDescription<?>> altTimedDescriptions,
            final ImmutableCollection<IAmountableGO> output) {
        this.altTimedDescriptions = altTimedDescriptions;
        this.output = output;
    }

    public GOTransformationResult(final AltTimedDescriptionsBuilder alt,
                                  final ImmutableCollection<IAmountableGO> output) {
        this(alt.build(), output);
    }

    public ImmutableCollection<? extends TimedDescription<?>> altTimedDescriptions() {
        return altTimedDescriptions;
    }

    public ImmutableCollection<IAmountableGO> getOutput() {
        return output;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final GOTransformationResult that = (GOTransformationResult) o;
        return altTimedDescriptions.equals(that.altTimedDescriptions) && output.equals(that.output);
    }

    @Override
    public int hashCode() {
        return Objects.hash(altTimedDescriptions, output);
    }

    @Override
    public String toString() {
        return "GOTransformationResult{" +
                "altTimedDescriptions=" + altTimedDescriptions +
                ", output=" + output +
                '}';
    }
}
