package de.nb.aventiure2.data.world.time;

import androidx.annotation.NonNull;
import androidx.room.PrimaryKey;

import org.jetbrains.annotations.Contract;

import java.util.Objects;

import javax.annotation.ParametersAreNonnullByDefault;
import javax.annotation.concurrent.Immutable;

import static com.google.common.base.Preconditions.checkNotNull;
import static de.nb.aventiure2.data.world.time.AvTime.SECS_IN_AN_HOUR;
import static de.nb.aventiure2.data.world.time.AvTime.SECS_IN_A_DAY;

/**
 * Value Object für Datum und Zeitpunkt.
 */
@Immutable
@ParametersAreNonnullByDefault
public class AvDateTime {
    /**
     * Sekunden seit einem Referenzzeitpunkt
     */
    @PrimaryKey
    private final long secsSinceBeginning;

    public AvDateTime(final int daySinceBeginning, final AvTime time) {
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

    public AvDateTime plus(final AvTimeSpan add) {
        checkNotNull(add, "add is null");

        return new AvDateTime(secsSinceBeginning + add.getSecs());
    }

    public AvDateTime minus(final AvTimeSpan sub) {
        checkNotNull(sub, "sub is null");

        return new AvDateTime(secsSinceBeginning - sub.getSecs());
    }

    public AvTimeSpan minus(final AvDateTime sub) {
        checkNotNull(sub, "sub is null");

        return new AvTimeSpan(secsSinceBeginning - sub.secsSinceBeginning);
    }

    public AvTimeSpan timeSpanUntil(final AvTime otherTime) {
        checkNotNull(otherTime, "otherTime is null");

        return getTime().timeSpanUntil(otherTime);
    }

    public boolean isWithin(final AvDateTime lowerBoundExclusive,
                            final AvDateTime upperBoundInclusive) {
        checkNotNull(lowerBoundExclusive, "lowerBoundExclusive is null");
        checkNotNull(upperBoundInclusive, "upperBoundInclusive is null");

        return lowerBoundExclusive.isBefore(this) &&
                upperBoundInclusive.isEqualOrAfter(this);
    }

    public boolean isEqualOrBefore(final AvDateTime other) {
        checkNotNull(other, "other is null");

        return secsSinceBeginning <= other.secsSinceBeginning;
    }

    public boolean isBefore(final AvDateTime other) {
        checkNotNull(other, "other is null");

        return secsSinceBeginning < other.secsSinceBeginning;
    }

    public boolean isEqualOrAfter(final AvDateTime other) {
        checkNotNull(other, "other is null");

        return secsSinceBeginning >= other.secsSinceBeginning;
    }

    public boolean isAfter(final AvDateTime other) {
        checkNotNull(other, "other is null");

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
