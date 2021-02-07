package de.nb.aventiure2.data.world.counter;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.stream.Stream;

/**
 * Room DAO for {@link Counter}s.
 */
@Dao
public abstract class CounterDao {
    /**
     * Erhöht diesen {@link Counter} und gibt das Ergebnis zurück.
     * <p>
     * Oft wird ein <code>Counter</code> hochgezählt, damit der Spieler im Rahmen der
     * Erzählung eine bestimmte Information einmal - aber nicht immer wieder - erhält.
     * In diesem Fall ist es essenziell, dass diese Beschreibung dann auch dem Spieler
     * <i>wirklich angezeigt</i> wird (und nicht unterdrückt oder durch eine ganz andere
     * Beschreibung ersetzt). Oft ist es deshalb eine gute Idee,
     * <code>#incAndGet(String)</code> in derselben Methode aufzurufen, in der auch
     * <code>narrate</code>
     * aufgerufen wird. Oft ist es sogar noch besser,  {@link #get(String)} aufzurufen und
     * den Counter außerdem in der {@link de.nb.aventiure2.german.description.TimedDescription}
     * zu übergeben.
     */
    public int incAndGet(final Enum<?> id) {
        return incAndGet(id.name());
    }

    /**
     * Erhöht diesen {@link Counter} und gibt das Ergebnis zurück.
     * <p>
     * Oft wird ein <code>Counter</code> hochgezählt, damit der Spieler im Rahmen der
     * Erzählung eine bestimmte Information einmal - aber nicht immer wieder - erhält.
     * In diesem Fall ist es essenziell, dass diese Beschreibung dann auch dem Spieler
     * <i>wirklich angezeigt</i> wird (und nicht unterdrückt oder durch eine ganz andere
     * Beschreibung ersetzt). Oft ist es deshalb eine gute Idee,
     * <code>#incAndGet(String)</code> in derselben Methode aufzurufen, in der auch
     * <code>narrate</code>
     * aufgerufen wird. Oft ist es sogar noch besser,  {@link #get(String)} aufzurufen und
     * den Counter außerdem in der {@link de.nb.aventiure2.german.description.TimedDescription}
     * zu übergeben.
     */
    private int incAndGet(final String id) {
        insert(new Counter(id, 0)); // ignore, if row already exists

        final int value = get(id);
        final int newValue = value + 1;

        set(id, newValue);

        return newValue;
    }

    /**
     * Erhöht diesen {@link Counter}.
     */
    public void inc(final Enum<?> id) {
        inc(id.name());
    }

    /**
     * Erhöht diesen {@link Counter}.
     */
    public void inc(final String id) {
        insert(new Counter(id, 0)); // ignore, if row already exists

        final int value = get(id);
        final int newValue = value + 1;

        set(id, newValue);
    }

    public void reset(final Enum<?>... ids) {
        Stream.of(ids).map(Enum::name).forEach(this::reset);
    }

    private void reset(final String id) {
        insert(new Counter(id, 0)); // ignore, if row already exists
        set(id, 0);
    }

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract void insert(Counter counter);

    @Query("UPDATE Counter SET value = :value WHERE id = :id")
    abstract void set(String id, int value);

    public int get(final Enum<?> id) {
        return get(id.name());
    }

    @Query("SELECT value from Counter where :id = id")
    public abstract int get(String id);
}