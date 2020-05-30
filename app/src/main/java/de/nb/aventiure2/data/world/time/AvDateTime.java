package de.nb.aventiure2.data.world.time;

import androidx.annotation.NonNull;
import androidx.room.PrimaryKey;

import org.jetbrains.annotations.Contract;

import java.util.Objects;

import javax.annotation.concurrent.Immutable;

import static de.nb.aventiure2.data.world.time.AvTime.SECS_IN_AN_HOUR;
import static de.nb.aventiure2.data.world.time.AvTime.SECS_IN_A_DAY;

/**
 * Value Object für Datum und Zeitpunkt.
 */
@Immutable
public class AvDateTime {
    /**
     * Sekunden seit einem Referenzzeitpunkt
     */
    @PrimaryKey
    private final long secsSinceBeginning;

    public static boolean isWithin(final AvDateTime dateTime,
                                   @NonNull final AvDateTime lowerBoundExclusive,
                                   final AvDateTime upperBoundInclusive) {
        return lowerBoundExclusive.isBefore(dateTime) &&
                upperBoundInclusive.isEqualOrAfter(dateTime);
    }

    public AvDateTime(final int daySinceBeginning, @NonNull final AvTime time) {
        this(daySinceBeginning * SECS_IN_A_DAY + time.getSecsSinceMidnight());
    }

    public AvDateTime(final int daySinceBeginning, final int hoursSinceDayStart,
                      final int minutesSinceHourStart) {
        this(daySinceBeginning * SECS_IN_A_DAY
                + hoursSinceDayStart * SECS_IN_AN_HOUR
                + minutesSinceHourStart * 60);
    }

    AvDateTime(final long secsSinceBeginning) {
        this.secsSinceBeginning = secsSinceBeginning;
    }

    public Tageszeit getTageszeit() {
        return getTime().getTageszeit();
    }

    public AvDateTime plus(@NonNull final AvTimeSpan add) {
        return new AvDateTime(secsSinceBeginning + add.getSecs());
    }

    public AvDateTime minus(@NonNull final AvTimeSpan sub) {
        return new AvDateTime(secsSinceBeginning - sub.getSecs());
    }

    public AvTimeSpan minus(@NonNull final AvDateTime sub) {
        return new AvTimeSpan(secsSinceBeginning - sub.secsSinceBeginning);
    }

    public AvTimeSpan timeSpanUntil(final AvTime otherTime) {
        return getTime().timeSpanUntil(otherTime);
    }

    public boolean isEqualOrBefore(@NonNull final AvDateTime other) {
        return secsSinceBeginning <= other.secsSinceBeginning;
    }

    public boolean isBefore(@NonNull final AvDateTime other) {
        return secsSinceBeginning < other.secsSinceBeginning;
    }

    public boolean isEqualOrAfter(@NonNull final AvDateTime other) {
        return secsSinceBeginning >= other.secsSinceBeginning;
    }

    public boolean isAfter(@NonNull final AvDateTime other) {
        return secsSinceBeginning > other.secsSinceBeginning;
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

    public AvTime getTime() {
        return new AvTime((int) (secsSinceBeginning % SECS_IN_A_DAY));
    }

    long getSecsSinceBeginning() {
        return secsSinceBeginning;
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
        final AvDateTime that = (AvDateTime) o;
        return secsSinceBeginning == that.secsSinceBeginning;
    }

    @Override
    public int hashCode() {
        return Objects.hash(secsSinceBeginning);
    }

    @NonNull
    @Override
    public String toString() {
        return "d" + getDay() + " " + getTime();
    }
}
