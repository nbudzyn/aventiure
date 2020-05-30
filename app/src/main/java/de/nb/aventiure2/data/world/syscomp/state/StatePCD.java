package de.nb.aventiure2.data.world.syscomp.state;

import androidx.annotation.NonNull;
import androidx.room.Entity;

import de.nb.aventiure2.data.world.base.AbstractPersistentComponentData;
import de.nb.aventiure2.data.world.base.GameObjectId;
import de.nb.aventiure2.data.world.time.AvDateTime;

/**
 * Mutable - and therefore persistent - data of the {@link StateComp} component.
 */
@Entity
public
class StatePCD extends AbstractPersistentComponentData {
    @NonNull
    private GameObjectState state;

    @NonNull
    private AvDateTime stateDateTime;

    StatePCD(@NonNull final GameObjectId gameObjectId,
             @NonNull final GameObjectState state,
             @NonNull final AvDateTime stateDateTime) {
        super(gameObjectId);
        this.state = state;
        this.stateDateTime = stateDateTime;
    }

    public boolean hasState(final GameObjectState... alternatives) {
        for (final GameObjectState test : alternatives) {
            if (state == test) {
                return true;
            }
        }

        return false;
    }

    @NonNull
    public GameObjectState getState() {
        return state;
    }

    public void setState(@NonNull final GameObjectState state) {
        this.state = state;
    }

    @NonNull
    AvDateTime getStateDateTime() {
        return stateDateTime;
    }

    void setStateDateTime(@NonNull final AvDateTime stateDateTime) {
        this.stateDateTime = stateDateTime;
    }
}
