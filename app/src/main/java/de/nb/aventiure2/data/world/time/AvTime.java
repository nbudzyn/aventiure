package de.nb.aventiure2.data.world.time;

import androidx.annotation.NonNull;
import androidx.room.PrimaryKey;

import java.util.Locale;
import java.util.Objects;

import javax.annotation.concurrent.Immutable;

import static com.google.common.base.Preconditions.checkState;
import static de.nb.aventiure2.data.world.time.Tageszeit.ABENDS;
import static de.nb.aventiure2.data.world.time.Tageszeit.MORGENS;
import static de.nb.aventiure2.data.world.time.Tageszeit.NACHTS;
import static de.nb.aventiure2.data.world.time.Tageszeit.TAGSUEBER;

/**
 * Wertobjekt für eine Uhrzeit in der Spielwelt
 */
@Immutable
public class AvTime {
    static final int SECS_IN_AN_HOUR = 60 * 60;
    static final int HOURS_IN_A_DAY = 24;
    static final int SECS_IN_A_DAY = SECS_IN_AN_HOUR * HOURS_IN_A_DAY;

    /**
     * Sekunden seit Mitternacht
     */
    @PrimaryKey
    private final int secsSinceMidnight;

    public static AvTime oClock(final int hoursSinceMidnight) {
        return oClock(hoursSinceMidnight, 0);
    }

    public static AvTime oClock(final int hoursSinceMidnight, final int minutesSinceHourStart) {
        return new AvTime(hoursSinceMidnight, minutesSinceHourStart);
    }

    private AvTime(final int hoursSinceMidnight, final int minutesSinceHourStart) {
        this(hoursSinceMidnight * 60 * 60 + minutesSinceHourStart * 60);
    }

    AvTime(final int secsSinceMidnight) {
        checkState(secsSinceMidnight < SECS_IN_A_DAY,
                "secsSinceMidnight >= SECS_IN_A_DAY");
        this.secsSinceMidnight = secsSinceMidnight;
    }

    Tageszeit getTageszeit() {
        if (isBefore(oClock(6))) {
            return NACHTS;
        }

        if (isBefore(oClock(8))) {
            return MORGENS;
        }

        if (isBefore(oClock(17))) {
            return TAGSUEBER;
        }

        if (isBefore(oClock(18, 30))) {
            return ABENDS;
        }

        return NACHTS;
    }

    AvTimeSpan timeSpanUntil(@NonNull final AvTime other) {
        if (!other.isBefore(this)) {
            return new AvTimeSpan(other.secsSinceMidnight - secsSinceMidnight);
        }
        return new AvTimeSpan(
                SECS_IN_A_DAY + other.secsSinceMidnight - secsSinceMidnight);
    }

    public AvTime rotate(@NonNull final AvTimeSpan add) {
        return new AvTime((int) ((secsSinceMidnight + add.getSecs()) % SECS_IN_A_DAY));
    }

    int getSec() {
        return secsSinceMidnight
                - (getHour() * SECS_IN_AN_HOUR)
                - (getMin() * 60);
    }

    int getMin() {
        return (secsSinceMidnight - getHour() * SECS_IN_AN_HOUR) / 60;
    }

    int getHour() {
        return secsSinceMidnight / SECS_IN_AN_HOUR;
    }

    /**
     * Returns whether <code>this</code> lies within a regular time interval.
     */
    public boolean isInRegularTimeIntervalIncl(
            final AvTime start,
            final AvTimeSpan intervalDuration,
            final AvTimeSpan durationBetweenIntervals,
            final AvTime end) {
        if (isBefore(start)) {
            return false;
        }

        if (isAfter(end)) {
            return false;
        }

        return ((getSecsSinceMidnight() - start.getSecsSinceMidnight()) %
                intervalDuration.plus(durationBetweenIntervals).getSecs()) <=
                intervalDuration.getSecs();
    }

    public boolean isWithin(final AvTime startExclusive, final AvTime endInclusive) {
        return startExclusive.isBefore(this) && endInclusive.isEqualOrAfter(this);
    }

    public boolean isBefore(final AvTime other) {
        return secsSinceMidnight < other.secsSinceMidnight;
    }

    public boolean isEqualOrBefore(final AvTime other) {
        return secsSinceMidnight <= other.secsSinceMidnight;
    }

    public boolean isEqualOrAfter(@NonNull final AvTime other) {
        return secsSinceMidnight >= other.secsSinceMidnight;
    }

    public boolean isAfter(@NonNull final AvTime other) {
        return secsSinceMidnight > other.secsSinceMidnight;
    }

    long getSecsSinceMidnight() {
        return secsSinceMidnight;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final AvTime avTime = (AvTime) o;
        return secsSinceMidnight == avTime.secsSinceMidnight;
    }

    @Override
    public int hashCode() {
        return Objects.hash(secsSinceMidnight);
    }

    @NonNull
    @Override
    public String toString() {
        return String.format(Locale.GERMANY, "%02d:%02d:%02d", getHour(), getMin(), getSec());
    }
}
