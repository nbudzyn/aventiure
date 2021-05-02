package de.nb.aventiure2.data.world.syscomp.wetter;

import androidx.room.Embedded;

import java.util.Objects;

import javax.annotation.concurrent.Immutable;

/**
 * Wetter, wie es bis zu einem Zeitpunkt werden soll(te).
 */
@Immutable
class PlanwetterData {
    /**
     * Relative Geschwindigkeit der Wetteränderung. 1 = normale Geschwindigkeit
     */
    private final float relativeVelocity;

    @Embedded(prefix = "plan")
    private final WetterData wetter;

    PlanwetterData(final float relativeVelocity, final WetterData wetter) {
        this.relativeVelocity = relativeVelocity;
        this.wetter = wetter;
    }

    @SuppressWarnings("WeakerAccess")
    float getRelativeVelocity() {
        return relativeVelocity;
    }

    @SuppressWarnings("WeakerAccess")
    WetterData getWetter() {
        return wetter;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final PlanwetterData that = (PlanwetterData) o;
        return Float.compare(that.relativeVelocity, relativeVelocity) == 0 &&
                wetter.equals(that.wetter);
    }

    @Override
    public int hashCode() {
        return Objects.hash(relativeVelocity, wetter);
    }
}
