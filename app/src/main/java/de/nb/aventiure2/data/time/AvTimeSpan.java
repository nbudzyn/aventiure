package de.nb.aventiure2.data.time;

import androidx.annotation.NonNull;
import androidx.room.PrimaryKey;

import org.jetbrains.annotations.Contract;

import java.util.Locale;
import java.util.Objects;

import javax.annotation.concurrent.Immutable;

import static com.google.common.base.Preconditions.checkArgument;
import static de.nb.aventiure2.data.time.AvTime.HOURS_IN_A_DAY;
import static de.nb.aventiure2.data.time.AvTime.SECS_IN_AN_HOUR;

/**
 * Eine Zeitspanne, immutable
 */
@Immutable
public class AvTimeSpan {
    public static final AvTimeSpan NO_TIME = secs(0);
    public static final AvTimeSpan ONE_DAY = days(1);

    /**
     * Gesamte Zeitspanne in Sekunden
     */
    @PrimaryKey
    private final long secs;

    @NonNull
    @Contract(pure = true)
    public static AvTimeSpan days(final long days) {
        return hours(days * HOURS_IN_A_DAY);
    }

    @NonNull
    @Contract(pure = true)
    public static AvTimeSpan hours(final long hours) {
        return mins(hours * 60);
    }

    @NonNull
    @Contract(pure = true)
    public static AvTimeSpan mins(final long mins) {
        return secs(mins * 60);
    }

    @NonNull
    @Contract(value = "_ -> new", pure = true)
    public static AvTimeSpan secs(final long secs) {
        return new AvTimeSpan(secs);
    }

    @NonNull
    public static AvTimeSpan max(@NonNull final AvTimeSpan one, @NonNull final AvTimeSpan other) {
        if (one.longerThan(other)) {
            return one;
        }

        return other;
    }

    @NonNull
    public static AvTimeSpan min(@NonNull final AvTimeSpan one, @NonNull final AvTimeSpan other) {
        if (one.shorterThan(other)) {
            return one;
        }

        return other;
    }

    AvTimeSpan(final long secs) {
        this.secs = secs;
    }

    public AvTimeSpan plus(@NonNull final AvTimeSpan add) {
        return new AvTimeSpan(secs + add.secs);
    }

    public AvTimeSpan minus(@NonNull final AvTimeSpan sub) {
        checkArgument(!shorterThan(sub),
                "Cannot subtract " + sub + " from " + this);

        return new AvTimeSpan(secs - sub.secs);
    }

    private int getSecPart() {
        return (int) (secs
                - (getDays() * SECS_IN_AN_HOUR * HOURS_IN_A_DAY)
                - (getHourPart() * SECS_IN_AN_HOUR)
                - (getMinPart() * 60));
    }

    private int getMinPart() {
        return (int) (secs
                - (getDays() * SECS_IN_AN_HOUR * HOURS_IN_A_DAY)
                - (getHourPart() * SECS_IN_AN_HOUR))
                / 60;
    }

    private int getHourPart() {
        return (int) (secs - (getDays() * SECS_IN_AN_HOUR * HOURS_IN_A_DAY)) / SECS_IN_AN_HOUR;
    }

    private long getDays() {
        return getSecs() / SECS_IN_AN_HOUR / HOURS_IN_A_DAY;
    }

    public double getAsHours() {
        return ((double) secs) / SECS_IN_AN_HOUR;
    }

    public long getSecs() {
        return secs;
    }

    public AvTimeSpan dividedBy(final double div) {
        return times(1.0 / div);
    }

    public AvTimeSpan times(final double factor) {
        return secs((long) (factor * secs));
    }

    public boolean isBetween(@NonNull final AvTimeSpan one, @NonNull final AvTimeSpan other) {
        if (one.longerThan(other)) {
            return isBetween(other, one);
        }

        return longerThan(one) && shorterThan(other);
    }

    public boolean isBetweenIncluding(@NonNull final AvTimeSpan one,
                                      @NonNull final AvTimeSpan other) {
        if (one.longerThan(other)) {
            return isBetween(other, one);
        }

        return longerThanOrEqual(one) && shorterThanOrEqual(other);
    }

    public boolean longerThan(@NonNull final AvTimeSpan other) {
        return secs > other.secs;
    }

    public boolean longerThanOrEqual(@NonNull final AvTimeSpan other) {
        return secs >= other.secs;
    }

    public boolean shorterThan(@NonNull final AvTimeSpan other) {
        return secs < other.secs;
    }

    public boolean shorterThanOrEqual(@NonNull final AvTimeSpan other) {
        return secs <= other.secs;
    }

    public boolean isNoTime() {
        return equals(NO_TIME);
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
        final AvTimeSpan that = (AvTimeSpan) o;
        return secs == that.secs;
    }

    @Override
    public int hashCode() {
        return Objects.hash(secs);
    }

    @NonNull
    @Override
    public String toString() {
        if (getHourPart() == 0) {
            if (getMinPart() == 0) {
                return String.format(Locale.GERMANY, "%02ds", getSecPart());
            }

            return String.format(Locale.GERMANY, "%02dmin %02ds", getMinPart(), getSecPart());
        }

        return String.format(Locale.GERMANY, "%02dh %02dmin %02ds", getHourPart(), getMinPart(),
                getSecPart());
    }
}
