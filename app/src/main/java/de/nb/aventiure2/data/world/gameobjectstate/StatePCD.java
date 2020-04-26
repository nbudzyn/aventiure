package de.nb.aventiure2.data.world.gameobjectstate;

import androidx.annotation.NonNull;
import androidx.room.Entity;

import de.nb.aventiure2.data.world.base.AbstractPersistentComponentData;
import de.nb.aventiure2.data.world.base.GameObjectId;

/**
 * Mutable - and therefore persistent - data of the {@link StateComp} component.
 */
@Entity
public
class StatePCD extends AbstractPersistentComponentData {
    @NonNull
    private GameObjectState state;

    StatePCD(@NonNull final GameObjectId gameObjectId, final GameObjectState state) {
        super(gameObjectId);
        this.state = state;
    }

    public boolean hasState(final GameObjectState... alternatives) {
        for (final GameObjectState test : alternatives) {
            if (state == test) {
                return true;
            }
        }

        return false;
    }

    public GameObjectState getState() {
        return state;
    }

    public void setState(final GameObjectState state) {
        this.state = state;
    }
}
