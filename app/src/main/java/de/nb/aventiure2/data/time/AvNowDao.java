package de.nb.aventiure2.data.time;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import de.nb.aventiure2.scaction.AbstractScAction;

/**
 * Room DAO for the {@link TimeTaker}.
 */
@Dao
public abstract class AvNowDao {
    /**
     * Sets the current date and time in the world. Do NOT use this
     * within an action implementation or a reaction.
     * {@link AbstractScAction#doAndPassTime()} deals with this.
     */
    public void setNow(final AvDateTime now) {
        delete();

        insert(new NowEntity(now));
    }

    @Query("DELETE FROM NowEntity")
    abstract void delete();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract void insert(NowEntity now);

    @Query("SELECT * from NowEntity")
    public abstract NowEntity now();
}
