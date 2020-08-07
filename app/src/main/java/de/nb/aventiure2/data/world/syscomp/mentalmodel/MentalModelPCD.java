package de.nb.aventiure2.data.world.syscomp.mentalmodel;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.Entity;
import androidx.room.Ignore;

import com.google.common.collect.ImmutableMap;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import de.nb.aventiure2.data.world.base.AbstractPersistentComponentData;
import de.nb.aventiure2.data.world.base.GameObjectId;

/**
 * Mutable - and therefore persistent - data of the {@link MentalModelComp} component.
 */
@Entity
public class MentalModelPCD extends AbstractPersistentComponentData {
    @NonNull
    @Ignore
    private final Map<GameObjectId, GameObjectId> assumedLocations;

    MentalModelPCD(final GameObjectId gameObjectId) {
        this(gameObjectId, new HashMap<>());
    }

    @Ignore
    MentalModelPCD(final GameObjectId gameObjectId,
                   final Map<GameObjectId, GameObjectId> assumedLocations) {
        super(gameObjectId);
        this.assumedLocations = new HashMap<>(assumedLocations);
    }

    @NonNull
    ImmutableMap<GameObjectId, GameObjectId> getAssumedLocations() {
        return ImmutableMap.copyOf(assumedLocations);
    }

    void assumeLocations(final Map<GameObjectId, GameObjectId> map) {
        setChanged();
        assumedLocations.putAll(map);
    }

    @Nullable
    public GameObjectId getAssumedLocation(final GameObjectId locatableId) {
        return assumedLocations.get(locatableId);
    }

    public void assumeLocation(final GameObjectId locatableId,
                               @Nullable final GameObjectId locationId) {
        if (Objects.equals(
                getAssumedLocation(locatableId),
                locationId)) {
            return;
        }

        setChanged();

        if (locationId == null) {
            assumedLocations.remove(locatableId);
            return;
        }

        assumedLocations.put(locatableId, locationId);
    }
}
