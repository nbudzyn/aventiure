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
     * Map der angenommenen Locations.
     * <ul>
     * <li>Key (nie {{@code null}}) ist die IDs des Game Objects
     * <li>Value (auch nie {{@code null}}) die ID der Location, wo es sich (laut Annahme) befindet
     * </ul>
     */
    @NonNull
    @Ignore
    private final Map<GameObjectId, GameObjectId> assumedLocations;

    /**
     * Map der angenommenen States.
     * <ul>
     * <li>Key (nie {{@code null}}) ist die IDs des
     * {@link de.nb.aventiure2.data.world.syscomp.state.IHasStateGO}s
     * <li>Value (auch nie {{@code null}}) der String des State-Enums, in dem sich das
     *  {@link de.nb.aventiure2.data.world.syscomp.state.IHasStateGO} (laut Annahme) befindet
     * </ul>
     * Dies sind jeweils die letzten States, von denen das {@link IHasMentalModelGO} "weiß" -
     * wobei klar ist, dass das {@link IHasMentalModelGO} vermutlich den State des
     * {@link de.nb.aventiure2.data.world.syscomp.state.IHasStateGO} nicht im Detail kennt.
     */
    @NonNull
    @Ignore
    private final Map<GameObjectId, String> assumedStates;

    MentalModelPCD(final GameObjectId gameObjectId) {
        this(gameObjectId, new HashMap<>(), ImmutableMap.of());
    }

    @Ignore
    MentalModelPCD(final GameObjectId gameObjectId,
                   final Map<GameObjectId, GameObjectId> assumedLocations,
                   final Map<GameObjectId, ? extends Enum<?>> assumedStates) {
        super(gameObjectId);
        this.assumedLocations = new HashMap<>(assumedLocations);

        this.assumedStates = new HashMap<>();
        Preconditions.checkState(this.assumedStates.isEmpty(), "Already initialized!");

        // Kein setChanged() !
        for (final Map.Entry<GameObjectId, ? extends Enum<?>> entry : assumedStates.entrySet()) {
            this.assumedStates.put(entry.getKey(), entry.getValue().name());
        }
    }

    @NonNull
    ImmutableMap<GameObjectId, GameObjectId> getAssumedLocations() {
        return ImmutableMap.copyOf(assumedLocations);
    }

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

    @NonNull
    ImmutableMap<GameObjectId, String> getAssumedStateStrings() {
        return ImmutableMap.copyOf(assumedStates);
    }

    void initAssumedStateStrings(final Map<GameObjectId, String> map) {
        Preconditions.checkState(assumedStates.isEmpty(), "Already initialized!");

        // Kein setChanged() !
        for (final Map.Entry<GameObjectId, String> entry : map.entrySet()) {
            assumedStates.put(entry.getKey(), entry.getValue());
        }
    }

    void setAssumedState(final GameObjectId gameObjectId,
                         @Nullable final Enum<?> state) {
        if (Objects.equals(getAssumedStateString(gameObjectId),
                state != null ? state.name() : null)) {
            return;
        }

        setChanged();

        if (state == null) {
            assumedStates.remove(gameObjectId);
            return;
        }

        assumedStates.put(gameObjectId, state.name());
    }

    @Nullable
    @CheckReturnValue
    String getAssumedStateString(final GameObjectId gameObjectId) {
        return assumedStates.get(gameObjectId);
    }
}
