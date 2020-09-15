package de.nb.aventiure2.data.world.syscomp.mentalmodel;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Map;
import java.util.Objects;

import de.nb.aventiure2.data.database.AvDatabase;
import de.nb.aventiure2.data.world.base.AbstractStatefulComponent;
import de.nb.aventiure2.data.world.base.GameObjectId;
import de.nb.aventiure2.data.world.gameobject.World;
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
    public boolean assumesLocation(final GameObjectId locatableId,
                                   final ILocationGO... locations) {
        for (@Nullable final ILocationGO location : locations) {
            if (location == null) {
                throw new NullPointerException("location was null");
            }
            if (assumesLocation(locatableId, location)) {
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
    public boolean assumesLocation(final GameObjectId locatableId,
                                   final ILocationGO location) {
        return assumesLocation(locatableId, location.getId());
    }

    /**
     * Gibt zurück, ob im mentalen Modell gespeichert ist, dass sich das
     * <i>Locatable</i> an einer dieser <i>Locations</i> befindet
     * (<i>nicht</i> rekursiv, also <i>nicht</i> auf einem Tisch in diesem Raum).
     */
    public boolean assumesLocation(final GameObjectId locatableId,
                                   final GameObjectId... locationIds) {
        for (@Nullable final GameObjectId locationId : locationIds) {
            if (assumesLocation(locatableId, locationId)) {
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
    // TODO Es gibt Verwirrung mit assume / assumes. Besser
    //  getAssumedLocation(), setAssumedLocation()
    public boolean assumesLocation(final GameObjectId locatableId,
                                   final @Nullable GameObjectId locationId) {
        return Objects.equals(getAssumedLocationId(locatableId), locationId);
    }

    @Nullable
    public ILocationGO getAssumedLocation(final GameObjectId locatableId) {
        @Nullable final GameObjectId locationId = getAssumedLocationId(locatableId);
        if (locationId == null) {
            return null;
        }

        return (ILocationGO) world.load(locationId);
    }

    @Nullable
    public GameObjectId getAssumedLocationId(final GameObjectId locatableId) {
        if (getGameObjectId().equals(locatableId)) {
            throw new IllegalArgumentException("No assumptions about yourself!");
        }

        return getPcd().getAssumedLocation(locatableId);
    }

    public void unassumeLocation(final GameObjectId locatableId) {
        assumeLocation(locatableId, (GameObjectId) null);
    }

    public void assumeLocation(final ILocatableGO locatable,
                               @Nullable final ILocationGO location) {
        assumeLocation(locatable.getId(), location);
    }

    public void assumeLocation(final GameObjectId locatableId,
                               @Nullable final ILocationGO location) {
        assumeLocation(locatableId,
                location != null ? location.getId() : null);
    }

    public void assumeLocation(final GameObjectId locatableId,
                               @Nullable final GameObjectId locationId) {
        if (getGameObjectId().equals(locatableId)) {
            throw new IllegalArgumentException("No assumptions about yourself!");
        }

        if (locatableId.equals(locationId)) {
            throw new IllegalStateException("A game object cannot contain itself.");
        }

        getPcd().assumeLocation(locatableId, locationId);
    }
}

