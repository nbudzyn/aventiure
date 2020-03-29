package de.nb.aventiure2.data.world.entity.creature;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import de.nb.aventiure2.data.world.entity.base.AbstractEntityData;
import de.nb.aventiure2.data.world.room.AvRoom;
import de.nb.aventiure2.german.base.Nominalphrase;

/**
 * Changeable data for a creature in the world.
 */
@Entity
public class CreatureData extends AbstractEntityData<Creature.Key> {
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

    public boolean creatureIs(final Creature.Key someKey) {
        return getKey() == someKey;
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

    @Override
    public Creature.Key getKey() {
        return creature.getKey();
    }
}
