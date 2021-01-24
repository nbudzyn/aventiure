package de.nb.aventiure2.data.world.syscomp.mentalmodel;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.Entity;
import androidx.room.Ignore;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import javax.annotation.CheckReturnValue;

import de.nb.aventiure2.data.world.base.AbstractPersistentComponentData;
import de.nb.aventiure2.data.world.base.GameObjectId;
import de.nb.aventiure2.data.world.syscomp.location.ILocatableGO;

import static com.google.common.collect.ImmutableList.toImmutableList;

/**
 * Mutable - and therefore persistent - data of the {@link MentalModelComp} component.
 */
@Entity
public class MentalModelPCD extends AbstractPersistentComponentData {
    /**
     * Map der angenommen Locations.
     * <ul>
     * <li>Key (nie {{@code null}}) ist die IDs des Game Objects
     * <li>Value (auch nie {{@code null}}) die ID der Location, wo es sich (laut Annahme) befindet
     * </ul>
     */
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

    /**
     * Darf nur zur Initialisierung aufgerufen werden, nicht zur Änderung!
     */
    void initAssumedLocations(final Map<GameObjectId, GameObjectId> map) {
        Preconditions.checkState(assumedLocations.isEmpty(), "Already initialized!");

        // Kein setChanged() !
        assumedLocations.putAll(map);
    }


    /**
     * Gibt die IDs aller {@link ILocatableGO}s zurück, die sich
     * - gemäß mentalem Modell - an dieser Location befinden (nicht rekursiv).
     */
    ImmutableList<GameObjectId> getAssumedInventory(final GameObjectId locationId) {
        return assumedLocations.entrySet().stream()
                .filter(e -> e.getValue().equals(locationId))
                .map(Map.Entry::getKey)
                .collect(toImmutableList());
    }

    void setAssumedLocation(final GameObjectId locatableId,
                            @Nullable final GameObjectId locationId) {
        if (Objects.equals(getAssumedLocation(locatableId), locationId)) {
            return;
        }

        setChanged();

        if (locationId == null) {
            assumedLocations.remove(locatableId);
            return;
        }

        assumedLocations.put(locatableId, locationId);
    }

    @Nullable
    @CheckReturnValue
    GameObjectId getAssumedLocation(final GameObjectId locatableId) {
        return assumedLocations.get(locatableId);
    }
}
