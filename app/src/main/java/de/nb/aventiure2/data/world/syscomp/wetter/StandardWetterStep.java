package de.nb.aventiure2.data.world.syscomp.wetter;

import androidx.annotation.NonNull;

import java.util.Objects;

import de.nb.aventiure2.data.time.AvTimeSpan;

class StandardWetterStep {
    private final WetterData wetterTo;
    private final AvTimeSpan standardDuration;

    StandardWetterStep(final WetterData wetterTo, final AvTimeSpan standardDuration) {
        this.wetterTo = wetterTo;
        this.standardDuration = standardDuration;
    }

    @NonNull
    WetterData getWetterTo() {
        return wetterTo;
    }

    @NonNull
    AvTimeSpan getStandardDuration() {
        return standardDuration;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final StandardWetterStep that = (StandardWetterStep) o;
        return wetterTo.equals(that.wetterTo) &&
                standardDuration.equals(that.standardDuration);
    }

    @Override
    public int hashCode() {
        return Objects.hash(wetterTo, standardDuration);
    }
}
