package de.nb.aventiure2.data.world.time;

import androidx.annotation.NonNull;
import androidx.room.PrimaryKey;

import static com.google.common.base.Preconditions.checkState;

/**
 * Uhrzeit in der Spielwelt
 */
public class AvTime {
    static final int SECS_IN_AN_HOUR = 60 * 60;
    static final int HOURS_IN_A_DAY = 24;
    static final int SECS_IN_A_DAY = SECS_IN_AN_HOUR * HOURS_IN_A_DAY;

    /**
     * Sekunden seit Mitternacht
     */
    @PrimaryKey
    @NonNull
    private int secsSinceMidnight;

    public static AvTime oClock(final int hoursSinceMidnight, final int minutesSinceHourStart) {
        return new AvTime(hoursSinceMidnight, minutesSinceHourStart);
    }

    public AvTime(final int hoursSinceMidnight, final int minutesSinceHourStart) {
        this(hoursSinceMidnight * 60 * 60 + minutesSinceHourStart * 60);
    }

    AvTime(@NonNull final int secsSinceMidnight) {
        checkState(secsSinceMidnight < SECS_IN_A_DAY,
                "secsSinceMidnight >= SECS_IN_A_DAY");
        this.secsSinceMidnight = secsSinceMidnight;
    }

    public AvTimeSpan timeSpanUntil(final AvTime other) {
        if (!other.isBefore(this)) {
            return new AvTimeSpan(other.secsSinceMidnight - secsSinceMidnight);
        }
        return new AvTimeSpan(
                SECS_IN_A_DAY + other.secsSinceMidnight - secsSinceMidnight);
    }

    public void rotate(final AvTimeSpan add) {
        secsSinceMidnight = (int) ((secsSinceMidnight + add.getSecs()) % SECS_IN_A_DAY);
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

    public boolean isBefore(final AvTime other) {
        return secsSinceMidnight < other.secsSinceMidnight;
    }

    public boolean isAfter(final AvTime other) {
        return secsSinceMidnight > other.secsSinceMidnight;
    }

    long getSecsSinceMidnight() {
        return secsSinceMidnight;
    }

    @NonNull
    @Override
    public String toString() {
        return String.format("%02d:%02d:%02d", getHour(), getMin(), getSec());
    }
}
