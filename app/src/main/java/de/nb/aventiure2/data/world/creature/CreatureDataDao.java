package de.nb.aventiure2.data.world.creature;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import de.nb.aventiure2.data.world.room.AvRoom;

/**
 * Room DAO for {@link CreatureData}s.
 */
@Dao
public abstract class CreatureDataDao {
    public void insertInitial() {
        for (final Creature creature : Creatures.ALL) {
            insertInitial(creature);
        }
    }

    public void insertInitial(final Creature creature) {
        insert(new CreatureData(creature, creature.getInitialRoom(),
                false, creature.getInitialState()));
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void insert(CreatureData creatureData);

    public void setKnown(final Creature.Key key) {
        setKnown(Creature.get(key));
    }

    @Query("UPDATE CreatureData SET known = 1 WHERE creature = :creature")
    public abstract void setKnown(Creature creature);

    @Update
    public abstract void update(CreatureData creatureData);

    @Query("SELECT * from CreatureData where :room = room")
    public abstract List<CreatureData> getCreaturesInRoomSync(AvRoom room);

    @Query("SELECT * from CreatureData where :room = room")
    public abstract LiveData<List<CreatureData>> getCreaturesInRoom(AvRoom room);

    public void setState(final Creature.Key creatureKey, final CreatureState state) {
        setState(Creature.get(creatureKey), state);
    }

    public void setState(final Creature creature, final CreatureState state) {
        if (!creature.isStateAllowed(state)) {
            throw new IllegalArgumentException("Illegal state for " + creature.getKey() +
                    ": " + state + ". Expected states: " + creature.getAllowedStates());
        }

        setStateInternal(creature, state);
    }

    @Query("UPDATE CreatureData SET state = :state WHERE creature = :creature")
    protected abstract void setStateInternal(Creature creature, CreatureState state);
}
