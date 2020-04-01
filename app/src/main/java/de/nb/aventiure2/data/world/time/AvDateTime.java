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

    public static boolean isWithin(final AvDateTime dateTime,
                                   final AvDateTime lowerBoundExclusive,
                                   final AvDateTime upperBoundInclusive) {
        return lowerBoundExclusive.isBefore(dateTime) && !upperBoundInclusive.isBefore(dateTime);
    }

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

    public Tageszeit getTageszeit() {
        return getTime().getTageszeit();
    }

    public AvDateTime plus(final AvTimeSpan add) {
        return new AvDateTime(secsSinceBeginning + add.getSecs());
    }

    public AvDateTime minus(final AvTimeSpan sub) {
        return new AvDateTime(secsSinceBeginning - sub.getSecs());
    }

    public AvTimeSpan minus(final AvDateTime sub) {
        return new AvTimeSpan(secsSinceBeginning - sub.secsSinceBeginning);
    }

    public AvTimeSpan timeSpanUntil(final AvTime otherTime) {
        return getTime().timeSpanUntil(otherTime);
    }

    public boolean isBefore(final AvDateTime other) {
        return secsSinceBeginning < other.secsSinceBeginning;
    }

    public boolean isAfter(final AvDateTime other) {
        return secsSinceBeginning > other.secsSinceBeginning;
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
