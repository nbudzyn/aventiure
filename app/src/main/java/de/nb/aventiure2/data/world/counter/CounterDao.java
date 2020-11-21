package de.nb.aventiure2.data.world.counter;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import de.nb.aventiure2.data.narration.NarrationDao;
import de.nb.aventiure2.german.description.AbstractDescription;

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
     * {@link NarrationDao#narrate(AbstractDescription)}
     * aufgerufen wird.
     */
    // FIXME Alle Verwendungen prüfen - besser auf ein neues Konzept umstellen,
    //  wo erst der Narrator den Counter erhöht (wenn der Text mit Sicherheit
    //  wirklich geschrieben wird).
    public int incAndGet(final String id) {
        insert(new Counter(id, 0)); // ignore, if row already exists

        final int value = get(id);
        final int newValue = value + 1;

        set(id, newValue);

        return newValue;
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

    public void reset(final String id) {
        insert(new Counter(id, 0)); // ignore, if row already exists
        set(id, 0);
    }

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract void insert(Counter counter);

    @Query("UPDATE Counter SET value = :value WHERE id = :id")
    abstract void set(String id, int value);

//    @Query("UPDATE Counter SET value = value - 1 WHERE id = :id")
//    abstract void decrementValue(String id);

    @Query("SELECT value from Counter where :id = id")
    public abstract int get(String id);
}