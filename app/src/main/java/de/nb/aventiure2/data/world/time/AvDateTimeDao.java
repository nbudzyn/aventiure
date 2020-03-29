package de.nb.aventiure2.data.world.time;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import de.nb.aventiure2.playeraction.AbstractPlayerAction;

/**
 * Room DAO for {@link AvDateTime}.
 */
@Dao
public abstract class AvDateTimeDao {
    /**
     * Sets the current date and time in the world. Do NOT use this
     * within an action implementation or an reaction.
     * {@link AbstractPlayerAction#doAndPassTime()} deals with this.
     */
    public void setDateTime(final int day, final AvTime time) {
        setDateTime(new AvDateTime(day, time));
    }

    /**
     * Sets the current date and time in the world. Do NOT use this
     * within an action implementation or an reaction.
     * {@link AbstractPlayerAction#doAndPassTime()} deals with this.
     */
    public void setDateTime(final AvDateTime dateTime) {
        deleteAll();

        insert(dateTime);
    }

    @Query("DELETE FROM AvDateTime")
    abstract void deleteAll();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract void insert(AvDateTime dateTime);

    @Query("SELECT * from AvDateTime")
    public abstract AvDateTime getDateTime();
}
