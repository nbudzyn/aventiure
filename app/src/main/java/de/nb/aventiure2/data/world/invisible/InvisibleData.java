package de.nb.aventiure2.data.world.invisible;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import de.nb.aventiure2.data.world.base.AbstractKeyData;

/**
 * Changeable data for an {@link Invisible} in the world.
 */
@Entity
public class InvisibleData extends AbstractKeyData<Invisible.Key> {
    @PrimaryKey
    @NonNull
    private final Invisible invisible;

    @NonNull
    private final InvisibleState state;

    InvisibleData(@NonNull final Invisible invisible, final InvisibleState state) {
        this.invisible = invisible;
        this.state = state;
    }

    public boolean invisibleIs(final Invisible.Key someKey) {
        return getKey() == someKey;
    }

    @NonNull
    public Invisible getInvisible() {
        return invisible;
    }

    public boolean hasState(final InvisibleState... alternatives) {
        for (final InvisibleState test : alternatives) {
            if (state == test) {
                return true;
            }
        }

        return false;
    }

    public InvisibleState getState() {
        return state;
    }

    @Override
    public Invisible.Key getKey() {
        return invisible.getKey();
    }
}
