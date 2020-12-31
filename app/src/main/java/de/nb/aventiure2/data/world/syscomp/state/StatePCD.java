package de.nb.aventiure2.data.world.syscomp.state;

import androidx.annotation.NonNull;
import androidx.room.Entity;

import de.nb.aventiure2.data.time.AvDateTime;
import de.nb.aventiure2.data.world.base.AbstractPersistentComponentData;
import de.nb.aventiure2.data.world.base.GameObjectId;

/**
 * Mutable - and therefore persistent - data of the {@link AbstractStateComp} component.
 */
@Entity
public class StatePCD extends AbstractPersistentComponentData {
    @NonNull
    private String state;

    @NonNull
    private AvDateTime stateDateTime;

    StatePCD(@NonNull final GameObjectId gameObjectId,
             @NonNull final String state,
             @NonNull final AvDateTime stateDateTime) {
        super(gameObjectId);
        this.state = state;
        this.stateDateTime = stateDateTime;
    }

    @NonNull
    String getState() {
        return state;
    }

    void setState(@NonNull final String state) {
        setChanged();
        this.state = state;
    }

    @NonNull
    AvDateTime getStateDateTime() {
        return stateDateTime;
    }

    void setStateDateTime(@NonNull final AvDateTime stateDateTime) {
        setChanged();
        this.stateDateTime = stateDateTime;
    }
}
