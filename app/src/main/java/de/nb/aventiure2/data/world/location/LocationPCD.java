package de.nb.aventiure2.data.world.location;

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

    LocationPCD(@NonNull final GameObjectId gameObjectId, @Nullable final GameObjectId locationId) {
        super(gameObjectId);
        this.locationId = locationId;
    }

    @Nullable
    public GameObjectId getLocationId() {
        return locationId;
    }

    public void setLocationId(@Nullable final GameObjectId locationId) {
        this.locationId = locationId;
    }
}
