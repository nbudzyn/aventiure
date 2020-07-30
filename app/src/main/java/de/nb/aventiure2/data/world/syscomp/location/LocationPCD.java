package de.nb.aventiure2.data.world.syscomp.location;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.Entity;

import de.nb.aventiure2.data.world.base.AbstractPersistentComponentData;
import de.nb.aventiure2.data.world.base.GameObjectId;

/**
 * Mutable - and therefore persistent - data of the {@link LocationComp} component.
 */
@Entity
public
class LocationPCD extends AbstractPersistentComponentData {
    @Nullable
    private GameObjectId locationId;

    @Nullable
    private GameObjectId lastLocationId;

    LocationPCD(@NonNull final GameObjectId gameObjectId,
                @Nullable final GameObjectId locationId,
                @Nullable final GameObjectId lastLocationId) {
        super(gameObjectId);
        this.locationId = locationId;
        this.lastLocationId = lastLocationId;
    }

    @Nullable
    GameObjectId getLocationId() {
        return locationId;
    }

    void setLocationId(@Nullable final GameObjectId locationId) {
        setChanged();
        this.locationId = locationId;
    }

    @Nullable
    GameObjectId getLastLocationId() {
        return lastLocationId;
    }

    void setLastLocationId(@Nullable final GameObjectId lastLocationId) {
        setChanged();
        this.lastLocationId = lastLocationId;
    }
}
