package de.nb.aventiure2.data.world.player.stats;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import de.nb.aventiure2.data.world.time.AvDateTime;
import de.nb.aventiure2.data.world.time.AvTimeSpan;

import static de.nb.aventiure2.data.world.time.AvTimeSpan.hours;

/**
 * The player character's stats.
 */
@Entity
public class ScStats {
    /**
     * Zeit, die es braucht, bis der Spieler nach dem Essen wieder hungrig wird
     */
    public static AvTimeSpan ZEITSPANNE_NACH_ESSEN_BIS_WIEDER_HUNGRIG =
            hours(6);

    @PrimaryKey // Something has to be the primary key
    @NonNull
    private final ScStateOfMind stateOfMind;

    private final ScHunger hunger;

    private final AvDateTime zuletztGegessen;

    public ScStats(@NonNull final ScStateOfMind stateOfMind,
                   final ScHunger hunger,
                   final AvDateTime zuletztGegessen) {
        this.stateOfMind = stateOfMind;
        this.hunger = hunger;
        this.zuletztGegessen = zuletztGegessen;
    }

    @NonNull
    public ScStateOfMind getStateOfMind() {
        return stateOfMind;
    }

    public ScHunger getHunger() {
        return hunger;
    }

    public AvDateTime getZuletztGegessen() {
        return zuletztGegessen;
    }
}
