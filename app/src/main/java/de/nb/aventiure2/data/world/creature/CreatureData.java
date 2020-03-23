package de.nb.aventiure2.data.world.creature;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.List;

import de.nb.aventiure2.data.world.entity.AbstractEntityData;
import de.nb.aventiure2.data.world.room.AvRoom;
import de.nb.aventiure2.german.base.Nominalphrase;

/**
 * Changeable data for a creature in the world.
 */
@Entity
public class CreatureData extends AbstractEntityData {
    @PrimaryKey
    @NonNull
    private final Creature creature;

    @Nullable
    private final AvRoom room;

    private final boolean known;

    private final CreatureState state;

    CreatureData(@NonNull final Creature creature, @Nullable final AvRoom room,
                 final boolean known, final CreatureState state) {
        this.creature = creature;
        this.room = room;
        this.known = known;
        this.state = state;
    }

    public boolean creatureIs(final Creature.Key key) {
        return creature.getKey() == key;
    }

    public static boolean contains(final List<CreatureData> creatureDataList,
                                   final Creature.Key key) {
        for (final CreatureData creatureData : creatureDataList) {
            if (creatureData.creature.getKey().equals(key)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public Nominalphrase getDescription(final boolean shortIfKnown) {
        if (isKnown()) {
            return shortIfKnown ? creature.getShortDescriptionWhenKnown() :
                    creature.getNormalDescriptionWhenKnown();
        }

        return creature.getDescriptionAtFirstSight();
    }

    @NonNull
    public Creature getCreature() {
        return creature;
    }

    @NonNull
    public AvRoom getRoom() {
        return room;
    }

    public boolean isKnown() {
        return known;
    }

    public boolean hasState(final CreatureState... alternatives) {
        for (final CreatureState test : alternatives) {
            if (state == test) {
                return true;
            }
        }

        return false;
    }

    public CreatureState getState() {
        return state;
    }


}
