package de.nb.aventiure2.data.world.time;

import androidx.annotation.NonNull;
import androidx.room.PrimaryKey;

import java.util.Objects;

import javax.annotation.concurrent.Immutable;

import static de.nb.aventiure2.data.world.time.AvTime.HOURS_IN_A_DAY;
import static de.nb.aventiure2.data.world.time.AvTime.SECS_IN_AN_HOUR;

/**
 * Eine Zeitspanne, immutable
 */
@Immutable
public class AvTimeSpan {
    public static final AvTimeSpan ONE_DAY = days(1);

    /**
     * Gesamte Zeitspanne in Sekunden
     */
    @PrimaryKey
    @NonNull
    private final long secs;

    public static AvTimeSpan noTime() {
        return secs(0);
    }

    public static AvTimeSpan days(final long days) {
        return hours(days * HOURS_IN_A_DAY);
    }

    public static AvTimeSpan hours(final long hours) {
        return mins(hours * 60);
    }

    public static AvTimeSpan mins(final long mins) {
        return secs(mins * 60);
    }

    public static AvTimeSpan secs(final long secs) {
        return new AvTimeSpan(secs);
    }

    AvTimeSpan(@NonNull final long secs) {
        this.secs = secs;
    }

    public AvTimeSpan plus(final AvTimeSpan add) {
        return new AvTimeSpan(secs + add.secs);
    }

    int getSecPart() {
        return (int) (secs
                - (getDays() * SECS_IN_AN_HOUR * HOURS_IN_A_DAY)
                - (getHourPart() * SECS_IN_AN_HOUR)
                - (getMinPart() * 60));
    }

    int getMinPart() {
        return (int) (secs
                - (getDays() * SECS_IN_AN_HOUR * HOURS_IN_A_DAY)
                - (getHourPart() * SECS_IN_AN_HOUR))
                / 60;
    }

    public int getHourPart() {
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

    public AvTimeSpan times(final double factor) {
        return secs((long) factor * secs);
    }

    public boolean longerThan(final AvTimeSpan other) {
        return secs > other.secs;
    }

    public boolean smallerThan(final AvTimeSpan other) {
        return secs < other.secs;
    }

    public boolean isNoTime() {
        return equals(noTime());
    }

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
                return String.format("%02ds", getSecPart());
            }

            return String.format("%02dmin %02ds", getMinPart(), getSecPart());
        }

        return String.format("%02dh %02dmin %02ds", getHourPart(), getMinPart(), getSecPart());
    }
}
