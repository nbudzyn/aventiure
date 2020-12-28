package de.nb.aventiure2.data.world.syscomp.mentalmodel;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Map;
import java.util.Objects;

import javax.annotation.CheckReturnValue;

import de.nb.aventiure2.data.database.AvDatabase;
import de.nb.aventiure2.data.world.base.AbstractStatefulComponent;
import de.nb.aventiure2.data.world.base.GameObjectId;
import de.nb.aventiure2.data.world.gameobject.*;
import de.nb.aventiure2.data.world.syscomp.location.ILocatableGO;
import de.nb.aventiure2.data.world.syscomp.storingplace.ILocationGO;

/**
 * Componente für eine {@link de.nb.aventiure2.data.world.base.GameObject}:
 * Das Game Object (z.B. ein NSC) hat ein mentales Modell
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
     * Gibt zurück, ob im mentalen Modell gespeichert ist, dass sich das
     * <i>Locatable</i> an einer dieser <code>locations</code> befindet
     * (<i>nicht</i> rekursiv, also <i>nicht</i> auf einem Tisch in diesem Raum).
     */
    @CheckReturnValue
    public boolean hasAssumedLocation(final GameObjectId locatableId,
                                      final ILocationGO... locations) {
        for (@Nullable final ILocationGO location : locations) {
            if (location == null) {
                throw new NullPointerException("location was null");
            }
            if (hasAssumedLocation(locatableId, location)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Gibt zurück, ob im mentalen Modell gespeichert ist, dass
     * sich das Game Object an dieser <code>location</code> befindet (<i>nicht</i>
     * rekursiv, also <i>nicht</i> auf einem Tisch in diesem Raum).
     */
    @CheckReturnValue
    private boolean hasAssumedLocation(final GameObjectId locatableId,
                                       final ILocationGO location) {
        return hasAssumedLocation(locatableId, location.getId());
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
    public ILocationGO getAssumedLocation(final GameObjectId locatableId) {
        @Nullable final GameObjectId locationId = getAssumedLocationId(locatableId);
        if (locationId == null) {
            return null;
        }

        return (ILocationGO) world.load(locationId);
    }

    @Nullable
    @CheckReturnValue
    private GameObjectId getAssumedLocationId(final GameObjectId locatableId) {
        if (getGameObjectId().equals(locatableId)) {
            throw new IllegalArgumentException("No assumptions about yourself!");
        }

        return getPcd().getAssumedLocation(locatableId);
    }

    public void unsetAssumedLocation(final GameObjectId locatableId) {
        setAssumedLocation(locatableId, (GameObjectId) null);
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

        getPcd().setAssumedLocation(locatableId, locationId);
    }
}

