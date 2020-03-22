package de.nb.aventiure2.data.world.creature;

import androidx.annotation.Nullable;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

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

    public void setRoom(final Creature.Key key, final @Nullable AvRoom room) {
        setRoom(Creature.get(key), room);
    }

    @Query("UPDATE CreatureData SET room = :room WHERE creature = :creature")
    public abstract void setRoom(Creature creature, AvRoom room);

    public CreatureData getCreature(final Creature.Key creatureKey) {
        return getCreature(Creature.get(creatureKey));
    }

    @Query("SELECT * from CreatureData where :creature = creature")
    public abstract CreatureData getCreature(Creature creature);

    @Query("SELECT * from CreatureData where :room = room")
    public abstract List<CreatureData> getCreaturesInRoom(AvRoom room);

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
