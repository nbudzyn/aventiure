package de.nb.aventiure2.data.world.time;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import static de.nb.aventiure2.data.world.time.AvTime.SECS_IN_AN_HOUR;
import static de.nb.aventiure2.data.world.time.AvTime.SECS_IN_A_DAY;

@Entity
public class AvDateTime {
    /**
     * Sekunden seit einem Referenzzeitpunkt
     */
    @PrimaryKey
    @NonNull
    private long secsSinceBeginning;

    public AvDateTime(final int daySinceBeginning, final AvTime time) {
        this(daySinceBeginning * SECS_IN_A_DAY + time.getSecsSinceMidnight());
    }

    public AvDateTime(final int daySinceBeginning, final int hoursSinceDayStart,
                      final int minutesSinceHourStart) {
        this(daySinceBeginning * SECS_IN_A_DAY
                + hoursSinceDayStart * SECS_IN_AN_HOUR
                + minutesSinceHourStart * 60);
    }

    AvDateTime(@NonNull final long secsSinceBeginning) {
        this.secsSinceBeginning = secsSinceBeginning;
    }

    public AvTimeSpan timeSpanUntil(final AvTime otherTime) {
        return getTime().timeSpanUntil(otherTime);
    }

    public void add(final AvTimeSpan add) {
        secsSinceBeginning += add.getSecs();
    }

    int getSec() {
        return getTime().getSec();
    }

    int getMin() {
        return getTime().getMin();
    }

    int getHour() {
        return getTime().getHour();
    }

    long getDay() {
        return secsSinceBeginning / SECS_IN_A_DAY;
    }


    private AvTime getTime() {
        return new AvTime((int) (secsSinceBeginning % SECS_IN_A_DAY));
    }

    long getSecsSinceBeginning() {
        return secsSinceBeginning;
    }

    @NonNull
    @Override
    public String toString() {
        return "d" + getDay() + " " + getTime();
    }
}
