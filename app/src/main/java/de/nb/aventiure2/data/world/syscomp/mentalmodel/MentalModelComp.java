package de.nb.aventiure2.data.world.syscomp.mentalmodel;

import static com.google.common.base.Preconditions.checkArgument;
import static de.nb.aventiure2.util.StreamUtil.*;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.Ignore;

import com.google.common.collect.ImmutableList;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;

import javax.annotation.CheckReturnValue;

import de.nb.aventiure2.data.database.AvDatabase;
import de.nb.aventiure2.data.world.base.AbstractStatefulComponent;
import de.nb.aventiure2.data.world.base.GameObject;
import de.nb.aventiure2.data.world.base.GameObjectId;
import de.nb.aventiure2.data.world.gameobject.*;
import de.nb.aventiure2.data.world.syscomp.location.ILocatableGO;
import de.nb.aventiure2.data.world.syscomp.location.LocationSystem;
import de.nb.aventiure2.data.world.syscomp.state.IHasStateGO;
import de.nb.aventiure2.data.world.syscomp.storingplace.ILocationGO;

/**
 * Componente für eine {@link de.nb.aventiure2.data.world.base.GameObject}:
 * Das Game Object (SC oder ein NSC) hat ein mentales Modell
 * der Welt. Der NSC geht also von gewissen Dingen aus, z.B.
 * dass sich jemand oder etwas irgendwo befindet.
 */
public class MentalModelComp extends AbstractStatefulComponent<MentalModelPCD>
        implements IWorldLoaderMixin {
    private final World world;

    @NonNull
    private final Map<GameObjectId, GameObjectId> initiallyAssumedLocations;

    /**
     * Map der initial angenommenen States.
     * <ul>
     * <li>Key (nie {{@code null}}) ist die IDs des
     * {@link de.nb.aventiure2.data.world.syscomp.state.IHasStateGO}s
     * <li>Value (auch nie {{@code null}}) der String des State-Enums, in dem sich das
     *  {@link de.nb.aventiure2.data.world.syscomp.state.IHasStateGO} (laut Annahme) befindet
     * </ul>
     * Es ist klar, dass das {@link IHasMentalModelGO} vermutlich den State des
     * {@link de.nb.aventiure2.data.world.syscomp.state.IHasStateGO} nicht im Detail kennt.
     */
    @NonNull
    @Ignore
    private final Map<GameObjectId, Enum<?>> initiallyAssumedStates;

    public MentalModelComp(final GameObjectId gameObjectId,
                           final AvDatabase db,
                           final World world,
                           final Map<GameObjectId, GameObjectId> initiallyAssumedLocations,
                           final Map<GameObjectId, Enum<?>> initiallyAssumedStates) {
        super(gameObjectId, db.mentalModelDao());
        this.world = world;
        this.initiallyAssumedLocations = initiallyAssumedLocations;
        this.initiallyAssumedStates = initiallyAssumedStates;
    }

    @Override
    @NonNull
    protected MentalModelPCD createInitialState() {
        return new MentalModelPCD(getGameObjectId(), initiallyAssumedLocations,
                initiallyAssumedStates);
    }

    /**
     * Speichert die aktuellen Werte (Ort, Zustand) des Game Objects als angenommene
     * Werte.
     */
    public void setAssumptionsToActual(final GameObjectId gameObjectId) {
        final GameObject gameObject = load(gameObjectId);

        if (gameObject instanceof ILocatableGO) {
            setAssumedLocation(gameObjectId,
                    getActualLocationId((ILocatableGO) gameObject));
        }
        if (gameObject instanceof IHasStateGO<?>) {
            setAssumedState(gameObjectId, getActualState((IHasStateGO<?>) gameObject));
        }
    }

    /**
     * Gibt alle {@link ILocatableGO}s zurück, die sich
     * <i>gemäß mentalem Modell dieses
     * {@link de.nb.aventiure2.data.world.syscomp.mentalmodel.IHasMentalModelGO}s</i>
     * an dieser Location befinden, auch rekursiv, soweit man in Dinge
     * hineinsehen kann.
     * (Rekursion ebenfalls gemäß dem mentalen Modell: wenn das {@code IHasMentalModelGO}
     * davon ausgeht, dass hier ein Tisch steht, dann wird auch die Kugel zurückgeben, von der
     * er ausgeht, dass sie auf dem Tisch liegt).
     */
    public ImmutableList<ILocatableGO> getAssumedVisiblyRecursiveInventory(
            final GameObjectId locationId) {
        final ImmutableList.Builder<ILocatableGO> res = ImmutableList.builder();

        final ImmutableList<ILocatableGO> directlyAssumedList = getAssumedInventory(locationId);
        res.addAll(directlyAssumedList);

        for (final ILocatableGO directlyAssumed : directlyAssumedList) {
            if (LocationSystem.manKannHinsehenUndLichtScheintHineinUndHinaus(directlyAssumed)) {
                res.addAll(getAssumedVisiblyRecursiveInventory(directlyAssumed.getId()));
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
    public GameObjectId getAssumedLocationId(final GameObjectId locatableId) {
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

    private static GameObjectId getActualLocationId(final ILocatableGO locatable) {
        return locatable.locationComp().getLocationId();
    }

    /**
     * Gibt zurück, ob im mentalen Modell gespeichert ist, dass sich das
     * {@code IHasStateGO} in einem dieser Zustände befindet.
     */
    @CheckReturnValue
    public boolean hasAssumedState(final GameObjectId gameObjectId, final Enum<?>... states) {
        for (@Nullable final Enum<?> state : states) {
            if (hasAssumedState(gameObjectId, state)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Gibt zurück, ob im mentalen Modell gespeichert ist, dass sich das
     * {@code IHasStateGO} in diesem State befindet.
     */
    @CheckReturnValue
    private boolean hasAssumedState(final GameObjectId gameObjectId,
                                    final @Nullable Enum<?> state) {
        return Objects.equals(getAssumedStateString(gameObjectId),
                state != null ? state.name() : null);
    }

    @Nullable
    @CheckReturnValue
    @SuppressWarnings("unchecked")
    public <S extends Enum<S>> Enum<?> getAssumedState(final GameObjectId gameObjectId) {
        @Nullable final String assumedStateString = getAssumedStateString(gameObjectId);
        if (assumedStateString == null) {
            return null;
        }

        final GameObject gameObject = loadRequired(gameObjectId);
        checkArgument(gameObject instanceof IHasStateGO<?>,
                "No IHasStateGO: " + gameObject);

        final Class<S> stateEnumClass =
                ((IHasStateGO<S>) gameObject).stateComp().getStateEnumClass();
        return Enum.valueOf(stateEnumClass, assumedStateString);
    }

    @Nullable
    @CheckReturnValue
    private String getAssumedStateString(final GameObjectId gameObjectId) {
        return requirePcd().getAssumedStateString(gameObjectId);
    }

    public <S extends Enum<S>> void setAssumedStateToActual(final GameObjectId gameObjectId) {
        setAssumedStateToActual((IHasStateGO<S>) loadRequired(gameObjectId));
    }

    public <S extends Enum<S>> void setAssumedStateToActual(final IHasStateGO<S> gameObject) {
        setAssumedState(gameObject.getId(), getActualState(gameObject));
    }

    public <S extends Enum<S>> void setAssumedState(final IHasStateGO<S> gameObject,
                                                    @Nullable final S state) {
        setAssumedState(gameObject.getId(), state);
    }

    private <S extends Enum<S>> void setAssumedState(final GameObjectId gameObjectId,
                                                     @Nullable final S state) {
        requirePcd().setAssumedState(gameObjectId, state);
    }

    private static <S extends Enum<S>> S getActualState(final IHasStateGO<S> gameObject) {
        return gameObject.stateComp().getState();
    }

    @Override
    public World getWorld() {
        return world;
    }
}

