package de.nb.aventiure2.scaction.stepcount;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import de.nb.aventiure2.logger.Logger;
import de.nb.aventiure2.scaction.AbstractScAction;

/**
 * Room DAO for {@link SCActionStepCount}.
 */
@Dao
public abstract class SCActionStepCountDao {
    private static final Logger LOGGER = Logger.getLogger();

    /**
     * Zählt den Schrittzähler eins höher.
     * Do NOT use this within an action implementation or a reaction.
     * {@link AbstractScAction#doAndPassTime()} deals with this.
     */
    public void inc() {
        setStepCount(stepCount() + 1);
    }

    /**
     * Setzt den Schrittzähler auf 0.
     */
    public void resetStepCount() {
        setStepCount(0);
    }

    /**
     * Sets the current step count.
     */
    private void setStepCount(final int stepCount) {
        deleteAll();

        insert(new SCActionStepCount(stepCount));
        LOGGER.d("Schritt: " + stepCount);
    }

    @Query("DELETE FROM SCActionStepCount")
    abstract void deleteAll();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract void insert(SCActionStepCount stepCount);

    @Query("SELECT stepCount from SCActionStepCount")
    public abstract int stepCount();
}
