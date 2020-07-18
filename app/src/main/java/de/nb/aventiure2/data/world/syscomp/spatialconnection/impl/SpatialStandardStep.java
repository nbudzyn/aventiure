package de.nb.aventiure2.data.world.syscomp.spatialconnection.impl;

import androidx.annotation.NonNull;

import org.jetbrains.annotations.Contract;

import java.util.Objects;

import de.nb.aventiure2.data.world.base.GameObjectId;
import de.nb.aventiure2.data.world.time.AvTimeSpan;

public class SpatialStandardStep {
    @NonNull
    private final GameObjectId to;

    @NonNull
    private final AvTimeSpan standardDuration;

    SpatialStandardStep(final GameObjectId to, final AvTimeSpan standardDuration) {
        this.to = to;
        this.standardDuration = standardDuration;
    }

    @NonNull
    public GameObjectId getTo() {
        return to;
    }

    @NonNull
    public AvTimeSpan getStandardDuration() {
        return standardDuration;
    }

    @Contract(value = "null -> false", pure = true)
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final SpatialStandardStep that = (SpatialStandardStep) o;
        return to.equals(that.to) &&
                standardDuration.equals(that.standardDuration);
    }

    @Override
    public int hashCode() {
        return Objects.hash(to, standardDuration);
    }
}
