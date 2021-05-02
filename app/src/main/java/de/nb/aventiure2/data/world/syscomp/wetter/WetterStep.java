package de.nb.aventiure2.data.world.syscomp.wetter;

import androidx.room.Embedded;

import java.util.Objects;

import javax.annotation.concurrent.Immutable;

import de.nb.aventiure2.data.time.AvDateTime;
import de.nb.aventiure2.data.time.AvTimeSpan;

/**
 * Ein Schritt im Rahmen einer Wetteränderung.
 */
@Immutable
class WetterStep {
    @Embedded(prefix = "to")
    private final WetterData wetterTo;

    private final AvDateTime startTime;
    private final AvTimeSpan expDuration;

    WetterStep(final WetterData wetterTo, final AvDateTime startTime,
               final AvTimeSpan expDuration) {
        this.wetterTo = wetterTo;
        this.startTime = startTime;
        this.expDuration = expDuration;
    }

    AvDateTime getExpDoneTime() {
        return startTime.plus(expDuration);
    }

    WetterData getWetterTo() {
        return wetterTo;
    }

    public AvDateTime getStartTime() {
        return startTime;
    }

    AvTimeSpan getExpDuration() {
        return expDuration;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final WetterStep that = (WetterStep) o;
        return wetterTo.equals(that.wetterTo) &&
                startTime.equals(that.startTime) &&
                expDuration.equals(that.expDuration);
    }

    @Override
    public int hashCode() {
        return Objects.hash(wetterTo, startTime, expDuration);
    }
}
