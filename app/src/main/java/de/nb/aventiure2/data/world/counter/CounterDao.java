package de.nb.aventiure2.data.world.counter;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

/**
 * Room DAO for {@link Counter}s.
 */
@Dao
public abstract class CounterDao {
    public void inc(final String id) {
        incAndGet(id);
    }

    public int incAndGet(final String id) {
        insert(new Counter(id, 0)); // ignore, if row already exists

        final int value = getValue(id);
        final int newValue = value + 1;

        setValue(id, newValue);

        return newValue;
    }

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract void insert(Counter counter);

    @Query("UPDATE Counter SET value = :value WHERE id = :id")
    abstract void setValue(String id, int value);

//    @Query("UPDATE Counter SET value = value - 1 WHERE id = :id")
//    abstract void decrementValue(String id);

    @Query("SELECT value from Counter where :id = id")
    abstract int getValue(String id);
}