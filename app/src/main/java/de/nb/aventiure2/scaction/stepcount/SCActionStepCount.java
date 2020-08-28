package de.nb.aventiure2.scaction.stepcount;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * Entity, zählt die Aktionen, die der Benutzer durchgeführt hat.
 * (Erreicht der Benutzer länger keine Zeile, könnte er z.B. Tipps erhalten.)
 */
@Entity
public class SCActionStepCount {
    /**
     * Anzahl der Aktionen (Schritte), die der Benutzer durchgeführt hat
     */
    @PrimaryKey
    private final int stepCount;

    public SCActionStepCount(final int stepCount) {
        this.stepCount = stepCount;
    }

    public int getStepCount() {
        return stepCount;
    }

    @NonNull
    @Override
    public String toString() {
        return Integer.toString(stepCount);
    }
}
