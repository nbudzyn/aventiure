package de.nb.aventiure2.data.time;

import static de.nb.aventiure2.data.time.AvTime.SECS_IN_A_DAY;

import java.util.Objects;

import javax.annotation.Nonnull;

import de.nb.aventiure2.data.world.base.Change;

/**
 * Wertobjekt für ein Zeit-Intervall in der Spielwelt. Das Zeitintervall
 * kann innerhalb eines Kalendertags liegen - oder 00:00 überschreiten.
 */
public class AvTimeInterval {
    /**
     * Beginn des Zeitintervalls - gehört nicht mit in das Intervall ("exklusiv").
     */
    @Nonnull
    private final AvTime startExclusive;

    /**
     * Ende des Zeitintervalls - gehört gerade noch in das Intervall ("inklusiv").
     * Das Ende kann auch gleich dem Anfang sein (Intervall von 24 Stunden)
     * oder vor dem Anfang liegen (Intervall überschreitet 00:00).
     */
    @Nonnull
    private final AvTime endInclusive;

    static AvTimeInterval fromExclusiveToInclusive(final Change<? extends AvTime> change) {
        return fromExclusiveToInclusive(change.getVorher(), change.getNachher());
    }

    public static AvTimeInterval fromExclusiveToInclusive(final AvTime startExclusive,
                                                          final AvTime endInclusive) {
        return new AvTimeInterval(startExclusive, endInclusive);
    }

    private AvTimeInterval(final AvTime startExclusive, final AvTime endInclusive) {
        this.startExclusive = startExclusive;
        this.endInclusive = endInclusive;
    }

    public AvTime getStartExclusive() {
        return startExclusive;
    }

    public AvTime getEndInclusive() {
        return endInclusive;
    }

    public boolean contains(final AvTime time) {
        if (startExclusive.isBefore(endInclusive)) {
            // Zeitintervall innerhalb eines Kalendertags
            return startExclusive.isBefore(time) && endInclusive.isEqualOrAfter(time);
        }

        // Zeitintervall überschreitet 00:00

        return startExclusive.isBefore(time) || endInclusive.isEqualOrAfter(time);
    }

    AvTimeSpan toTimeSpan() {
        if (startExclusive.isBefore(endInclusive)) {
            // Zeitintervall innerhalb eines Kalendertags
            return new AvTimeSpan(endInclusive.getSecsSinceMidnight() -
                    startExclusive.getSecsSinceMidnight());
        }

        // Zeitintervall überschreitet 00:00
        return new AvTimeSpan(
                SECS_IN_A_DAY + endInclusive.getSecsSinceMidnight() -
                        startExclusive.getSecsSinceMidnight());
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final AvTimeInterval that = (AvTimeInterval) o;
        return startExclusive.equals(that.startExclusive) && endInclusive.equals(that.endInclusive);
    }

    @Override
    public int hashCode() {
        return Objects.hash(startExclusive, endInclusive);
    }

    @Override
    public String toString() {
        return "]" + startExclusive + ", " + endInclusive + "]";
    }
}
