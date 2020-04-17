package de.nb.aventiure2.data.world.invisible;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import de.nb.aventiure2.data.world.base.AbstractGameData;
import de.nb.aventiure2.data.world.base.GameObjectId;

/**
 * Changeable data for an {@link Invisible} in the world.
 */
@Entity
public class InvisibleData extends AbstractGameData {
    @PrimaryKey
    @NonNull
    private final Invisible invisible;

    @NonNull
    private final InvisibleState state;

    InvisibleData(@NonNull final Invisible invisible, final InvisibleState state) {
        super(invisible.getId());
        this.invisible = invisible;
        this.state = state;
    }

    public boolean invisibleIs(final GameObjectId someGameObjectId) {
        return getGameObjectId().equals(someGameObjectId);
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
}
