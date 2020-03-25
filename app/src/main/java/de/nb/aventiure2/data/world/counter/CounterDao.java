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
    public boolean isFirstTime(final String id) {
        return isFirstSecondThird_orNthTime(id, 1);
    }

    public boolean isFirstOrSecdondTime(final String id) {
        return isFirstSecondThird_orNthTime(id, 2);
    }

    public boolean isFirstSecdondOrThirdTime(final String id) {
        return isFirstSecondThird_orNthTime(id, 3);
    }

    public boolean isFirstSecondThird_orNthTime(final String id, final int n) {
        insert(new Counter(id, n)); // ignore, if row already exists

        final int value = getValue(id);
        if (value == 0) {
            return false;
        }

        setValue(id, value - 1);

        return true;
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