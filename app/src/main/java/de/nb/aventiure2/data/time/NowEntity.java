package de.nb.aventiure2.data.time;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import de.nb.aventiure2.data.world.base.AbstractPersistentComponentData;

/**
 * {@link AbstractPersistentComponentData} containing the current date and time in
 * the world.
 */
@Entity
public
class NowEntity {
    /**
     * Aktuelles Datum und Zeit in der Welt
     */
    @NonNull
    @PrimaryKey
    private AvDateTime now;

    NowEntity(final AvDateTime now) {
        this.now = now;
    }

    void setNow(final AvDateTime now) {
        this.now = now;
    }

    @NonNull
    AvDateTime getNow() {
        return now;
    }

    @NonNull
    @Override
    public String toString() {
        return now.toString();
    }
}
