package de.nb.aventiure2.data.world.time;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * Entity, enthält aktuelles Datum und Zeit in der Welt.
 */
@Entity
public class AvNow {
    /**
     * Aktuelles Datum und Zeit in der Welt
     */
    @PrimaryKey
    @NonNull
    private final AvDateTime now;

    public AvNow(@NonNull final AvDateTime now) {
        this.now = now;
    }

    @NonNull
    public AvDateTime getNow() {
        return now;
    }

    @NonNull
    @Override
    public String toString() {
        return now.toString();
    }
}
