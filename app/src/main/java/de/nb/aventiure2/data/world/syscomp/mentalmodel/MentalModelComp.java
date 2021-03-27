package de.nb.aventiure2.data.world.syscomp.mentalmodel;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.common.collect.ImmutableList;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;

import javax.annotation.CheckReturnValue;

import de.nb.aventiure2.data.database.AvDatabase;
import de.nb.aventiure2.data.world.base.AbstractStatefulComponent;
import de.nb.aventiure2.data.world.base.GameObjectId;
import de.nb.aventiure2.data.world.gameobject.*;
import de.nb.aventiure2.data.world.syscomp.location.ILocatableGO;
import de.nb.aventiure2.data.world.syscomp.storingplace.ILocationGO;

import static de.nb.aventiure2.util.StreamUtil.*;

/**
 * Componente für eine {@link de.nb.aventiure2.data.world.base.GameObject}:
 * Das Game Object (SC oder ein NSC) hat ein mentales Modell
 * der Welt. Der NSC geht also von gewissen Dingen aus, z.B.
 * dass sich jemand oder etwas irgendwo befindet.
 */
public class MentalModelComp extends AbstractStatefulComponent<MentalModelPCD> {
    private final World world;

    @NonNull
    private final Map<GameObjectId, GameObjectId> initiallyAssumedLocations;

    public MentalModelComp(final GameObjectId gameObjectId,
                           final AvDatabase db,
                           final World world,
                           final Map<GameObjectId, GameObjectId> initiallyAssumedLocations) {
        super(gameObjectId, db.mentalModelDao());
        this.world = world;
        this.initiallyAssumedLocations = initiallyAssumedLocations;
    }

    @Override
    @NonNull
    protected MentalModelPCD createInitialState() {
        return new MentalModelPCD(getGameObjectId(), initiallyAssumedLocations);
    }

    /**
     * Gibt alle {@link ILocatableGO}s zurück, die sich
     * <i>gemäß mentalem Modell dieses
     * {@link de.nb.aventiure2.data.world.syscomp.mentalmodel.IHasMentalModelGO}s</i>
     * an dieser Location befinden, auch rekursiv
     * (ebenfalls gemäß dem mentalen Modell: wenn das {@code IHasMentalModelGO}
     * davon ausgeht, dass hier ein Tisch steht, dann wird auch die Kugel zurückgeben, von der
     * er ausgeht, dass sie auf dem Tisch liegt).
     */
    public ImmutableList<ILocatableGO> getAssumedRecursiveInventory(
            final GameObjectId locationId) {
        final ImmutableList.Builder<ILocatableGO> res = ImmutableList.builder();

        final ImmutableList<ILocatableGO> directlyAssumedList = getAssumedInventory(locationId);
        res.addAll(directlyAssumedList);

        for (final ILocatableGO directlyAssumed : directlyAssumedList) {
            if (directlyAssumed instanceof ILocationGO) {
                res.addAll(getAssumedRecursiveInventory(directlyAssumed.getId()));
            }
        }

        return res.build();
    }

    /**
     * Gibt alle {@link ILocatableGO}s zurück, die sich
     * - gemäß mentalem Modell - an dieser Location befinden (nicht rekursiv).
     */
    @SuppressWarnings("unchecked")
    private ImmutableList<ILocatableGO> getAssumedInventory(final GameObjectId locationId) {
        return (ImmutableList<ILocatableGO>) (ImmutableList<?>) world
                .load(requirePcd().getAssumedInventory(locationId));
    }

    /**
     * Gibt zurück, ob im mentalen Modell gespeichert ist, dass sich das
     * <i>Locatable</i> an einer dieser <i>Locations</i> befindet
     * (<i>nicht</i> rekursiv, also <i>nicht</i> auf einem Tisch in diesem Raum).
     */
    @CheckReturnValue
    public boolean hasAssumedLocation(final GameObjectId locatableId,
                                      final GameObjectId... locationIds) {
        for (@Nullable final GameObjectId locationId : locationIds) {
            if (hasAssumedLocation(locatableId, locationId)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Gibt zurück, ob im mentalen Modell gespeichert ist, dass sich das
     * <i>Locatable</i> an dieser <i>Location</i> befindet
     * (<i>nicht</i> rekursiv, also <i>nicht</i> auf einem Tisch in diesem Raum).
     */
    @CheckReturnValue
    public boolean hasAssumedLocation(final GameObjectId locatableId,
                                      final @Nullable GameObjectId locationId) {
        return Objects.equals(getAssumedLocationId(locatableId), locationId);
    }

    @Nullable
    @CheckReturnValue
    private GameObjectId getAssumedLocationId(final GameObjectId locatableId) {
        if (getGameObjectId().equals(locatableId)) {
            throw new IllegalArgumentException("No assumptions about yourself!");
        }

        return requirePcd().getAssumedLocation(locatableId);
    }

    public void unsetAssumedLocations(final GameObjectId... locatables) {
        unsetAssumedLocations(Arrays.asList(locatables));
    }

    public void unsetAssumedLocations(final Collection<? extends ILocatableGO> locatables) {
        setAssumedLocations(locatables, null);
    }

    private void unsetAssumedLocations(final Iterable<? extends GameObjectId> locatableIds) {
        setAssumedLocations(locatableIds, null);
    }

    public void unsetAssumedLocation(final ILocatableGO locatable) {
        unsetAssumedLocation(locatable.getId());
    }

    public void unsetAssumedLocation(final GameObjectId locatableId) {
        setAssumedLocation(locatableId, (GameObjectId) null);
    }

    public void setAssumedLocations(
            final Collection<? extends ILocatableGO> locatables,
            @Nullable final ILocationGO location) {
        setAssumedLocations(
                mapToList(locatables, ILocatableGO::getId),
                location);
    }

    private void setAssumedLocations(
            final Iterable<? extends GameObjectId> locatableIds,
            @Nullable final ILocationGO location) {
        for (final GameObjectId locatableId : locatableIds) {
            setAssumedLocation(locatableId, location);
        }
    }

    public void setAssumedLocationToActual(final ILocatableGO locatable) {
        setAssumedLocation(locatable.getId(), getActualLocationId(locatable));
    }

    public void setAssumedLocation(final ILocatableGO locatable,
                                   @Nullable final ILocationGO location) {
        setAssumedLocation(locatable.getId(), location);
    }

    public void setAssumedLocationToActual(final GameObjectId locatableId) {
        setAssumedLocation(locatableId, getActualLocationId(locatableId));
    }

    public void setAssumedLocation(final GameObjectId locatableId,
                                   @Nullable final ILocationGO location) {
        setAssumedLocation(locatableId,
                location != null ? location.getId() : null);
    }

    private void setAssumedLocation(final GameObjectId locatableId,
                                    @Nullable final GameObjectId locationId) {
        if (getGameObjectId().equals(locatableId)) {
            throw new IllegalArgumentException("No assumptions about yourself!");
        }

        if (locatableId.equals(locationId)) {
            throw new IllegalStateException("A game object cannot contain itself.");
        }

        requirePcd().setAssumedLocation(locatableId, locationId);
    }

    private GameObjectId getActualLocationId(final ILocatableGO locatable) {
        return getAssumedLocationId(locatable.getId());
    }

    private GameObjectId getActualLocationId(final GameObjectId locatableId) {
        return ((ILocatableGO) world.load(locatableId)).locationComp().getLocationId();
    }
}

