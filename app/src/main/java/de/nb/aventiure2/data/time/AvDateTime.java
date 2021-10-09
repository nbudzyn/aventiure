package de.nb.aventiure2.data.time;

import static java.util.Objects.requireNonNull;
import static de.nb.aventiure2.data.time.AvTime.SECS_IN_AN_HOUR;
import static de.nb.aventiure2.data.time.AvTime.SECS_IN_A_DAY;

import androidx.annotation.NonNull;
import androidx.room.PrimaryKey;

import org.jetbrains.annotations.Contract;

import java.util.Objects;

import javax.annotation.ParametersAreNonnullByDefault;
import javax.annotation.concurrent.Immutable;

import de.nb.aventiure2.data.world.base.Change;

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

    public AvDateTime(final long daySinceBeginning, final AvTime time) {
        this(daySinceBeginning * SECS_IN_A_DAY + time.getSecsSinceMidnight());
    }

    public AvDateTime(final long daySinceBeginning, final int hoursSinceDayStart,
                      final int minutesSinceHourStart) {
        this(daySinceBeginning * SECS_IN_A_DAY
                + ((long) hoursSinceDayStart) * SECS_IN_AN_HOUR
                + minutesSinceHourStart * 60L);
    }

    public AvDateTime(final long secsSinceBeginning) {
        this.secsSinceBeginning = secsSinceBeginning;
    }

    public static AvDateTime earliest(final AvDateTime one, final AvDateTime other) {
        if (one.isBefore(other)) {
            return one;
        }

        return other;
    }

    public static AvDateTime latest(final AvDateTime one, final AvDateTime other) {
        if (one.isAfter(other)) {
            return one;
        }

        return other;
    }

    public Tageszeit getTageszeit() {
        return getTime().getTageszeit();
    }

    public AvDateTime plus(final AvTimeSpan add) {
        requireNonNull(add, "add is null");

        return new AvDateTime(secsSinceBeginning + add.getSecs());
    }

    public AvDateTime minus(final AvTimeSpan sub) {
        requireNonNull(sub, "sub is null");

        return new AvDateTime(secsSinceBeginning - sub.getSecs());
    }

    public AvTimeSpan minus(final AvDateTime sub) {
        requireNonNull(sub, "sub is null");

        return new AvTimeSpan(secsSinceBeginning - sub.secsSinceBeginning);
    }

    public AvTimeSpan timeSpanUntil(final AvTime otherTime) {
        requireNonNull(otherTime, "otherTime is null");

        return getTime().timeSpanUntil(otherTime);
    }

    public AvDateTime goBackTo(final AvTime otherTime) {
        requireNonNull(otherTime, "otherTime is null");

        if (otherTime.isAfter(getTime())) {
            return new AvDateTime(getDay() - 1, otherTime);
        }

        return new AvDateTime(getDay(), otherTime);
    }

    public boolean isWithin(final Change<AvDateTime> change) {
        return isWithin(change.getVorher(), change.getNachher());
    }

    private boolean isWithin(final AvDateTime lowerBoundExclusive,
                             final AvDateTime upperBoundInclusive) {
        requireNonNull(lowerBoundExclusive, "lowerBoundExclusive is null");
        requireNonNull(upperBoundInclusive, "upperBoundInclusive is null");

        return lowerBoundExclusive.isBefore(this) &&
                upperBoundInclusive.isEqualOrAfter(this);
    }

    public boolean isEqualOrBefore(final AvDateTime other) {
        requireNonNull(other, "other is null");

        return secsSinceBeginning <= other.secsSinceBeginning;
    }

    public boolean isBefore(final AvDateTime other) {
        requireNonNull(other, "other is null");

        return secsSinceBeginning < other.secsSinceBeginning;
    }

    public boolean isEqualOrAfter(final AvDateTime other) {
        requireNonNull(other, "other is null");

        return secsSinceBeginning >= other.secsSinceBeginning;
    }

    public boolean isAfter(final AvDateTime other) {
        requireNonNull(other, "other is null");

        return secsSinceBeginning > other.secsSinceBeginning;
    }

    private long getDay() {
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

    @SuppressWarnings("StringConcatenationMissingWhitespace")
    @NonNull
    @Override
    public String toString() {
        return "d" + getDay() + " " + getTime();
    }

}
