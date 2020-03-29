package de.nb.aventiure2.data.world.invisible;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

/**
 * Room DAO for {@link InvisibleData}s.
 */
@Dao
public abstract class InvisibleDataDao {
    public void insertInitial() {
        for (final Invisible invisible : Invisibles.ALL) {
            insertInitial(invisible);
        }
    }

    public void insertInitial(final Invisible invisible) {
        insert(new InvisibleData(invisible, invisible.getInitialState()));
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void insert(InvisibleData invisbleData);

    public InvisibleData getInvisible(final Invisible.Key invisibleKey) {
        return getInvisible(Invisible.get(invisibleKey));
    }

    @Query("SELECT * from InvisibleData where :invisible = invisible")
    public abstract InvisibleData getInvisible(Invisible invisible);

    public void setState(final Invisible.Key invisibleKey, final InvisibleState state) {
        setState(Invisible.get(invisibleKey), state);
    }

    public void setState(final Invisible invisible, final InvisibleState state) {
        if (!invisible.isStateAllowed(state)) {
            throw new IllegalArgumentException("Illegal state for " + invisible.getKey() +
                    ": " + state + ". Expected states: " + invisible.getAllowedStates());
        }

        setStateInternal(invisible, state);
    }

    @Query("UPDATE InvisibleData SET state = :state WHERE invisible = :invisible")
    protected abstract void setStateInternal(Invisible invisible, InvisibleState state);

    @Query("SELECT * from InvisibleData")
    public abstract List<InvisibleData> getAll();
}
