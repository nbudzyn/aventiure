package de.nb.aventiure2.data.world.time;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

/**
 * Room DAO for {@link AvDateTime}.
 */
@Dao
public abstract class AvDateTimeDao {
    public void passTime(final AvTimeSpan timeElapsed) {
        final AvDateTime dateTime = getDateTime();
        dateTime.add(timeElapsed);
        setDateTime(dateTime);
    }

    public void setDateTime(final int day, final AvTime time) {
        setDateTime(new AvDateTime(day, time));
    }

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
