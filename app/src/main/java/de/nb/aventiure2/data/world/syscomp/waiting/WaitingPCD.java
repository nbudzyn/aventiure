package de.nb.aventiure2.data.world.syscomp.waiting;

import androidx.annotation.NonNull;
import androidx.room.Entity;

import de.nb.aventiure2.data.world.base.AbstractPersistentComponentData;
import de.nb.aventiure2.data.world.base.GameObjectId;
import de.nb.aventiure2.data.world.time.*;

/**
 * Mutable - and therefore persistent - data of the {@link WaitingComp} component.
 */
@Entity
public class WaitingPCD extends AbstractPersistentComponentData {
    @NonNull
    private AvDateTime endTime;

    WaitingPCD(final GameObjectId gameObjectId,
               final AvDateTime endTime) {
        super(gameObjectId);
        this.endTime = endTime;
    }

    void setEndTime(final AvDateTime endTime) {
        setChanged();
        this.endTime = endTime;
    }

    @NonNull
    AvDateTime getEndTime() {
        return endTime;
    }
}
