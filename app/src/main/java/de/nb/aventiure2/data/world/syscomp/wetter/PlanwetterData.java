package de.nb.aventiure2.data.world.syscomp.wetter;

import androidx.room.Embedded;

import javax.annotation.concurrent.Immutable;

import de.nb.aventiure2.data.time.AvDateTime;

/**
 * Wetter, wie es bis zu einem Zeitpunkt werden soll(te).
 */
@Immutable
class PlanwetterData {
    final AvDateTime planDateTime;

    @Embedded(prefix = "plan")
    public final WetterData wetter;

    PlanwetterData(final AvDateTime planDateTime,
                   final WetterData wetter) {
        this.planDateTime = planDateTime;
        this.wetter = wetter;
    }
}
