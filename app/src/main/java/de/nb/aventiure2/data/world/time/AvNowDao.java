package de.nb.aventiure2.data.world.time;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import de.nb.aventiure2.logger.Logger;
import de.nb.aventiure2.scaction.AbstractScAction;

import static de.nb.aventiure2.data.world.time.AvTimeSpan.noTime;

/**
 * Room DAO for {@link AvNow}.
 */
@Dao
public abstract class AvNowDao {
    private static final Logger LOGGER = Logger.getLogger();

    /**
     * Sets the current date and time in the world, adding this passed time.
     * Do NOT use this within an action implementation or a reaction.
     * {@link AbstractScAction#doAndPassTime()} deals with this.
     */
    public void passTime(final AvTimeSpan timePassed) {
        final AvDateTime now = now();
        if (timePassed.equals(noTime())) {
            return;
        }

        setNow(now.plus(timePassed));
    }

    /**
     * Sets the current date and time in the world. Do NOT use this
     * within an action implementation or a reaction.
     * {@link AbstractScAction#doAndPassTime()} deals with this.
     */
    public void setNow(final int day, final AvTime time) {
        setNow(new AvDateTime(day, time));
    }

    /**
     * Sets the current date and time in the world. Do NOT use this
     * within an action implementation or a reaction.
     * {@link AbstractScAction#doAndPassTime()} deals with this.
     */
    public void setNow(final AvDateTime now) {
        deleteAll();

        insert(new AvNow(now));
        LOGGER.d("Zeitpunkt: " + now);
    }

    @Query("DELETE FROM AvNow")
    abstract void deleteAll();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract void insert(AvNow now);

    @Query("SELECT now from AvNow")
    public abstract AvDateTime now();
}
