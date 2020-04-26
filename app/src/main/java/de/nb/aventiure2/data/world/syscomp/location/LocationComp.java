package de.nb.aventiure2.data.world.syscomp.location;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Objects;

import de.nb.aventiure2.data.database.AvDatabase;
import de.nb.aventiure2.data.world.base.AbstractStatefulComponent;
import de.nb.aventiure2.data.world.base.GameObject;
import de.nb.aventiure2.data.world.base.GameObjectId;
import de.nb.aventiure2.data.world.gameobjects.GameObjects;
import de.nb.aventiure2.data.world.syscomp.storingplace.IHasStoringPlaceGO;

/**
 * Component for a {@link GameObject}: The game object
 * can be situated in a location in the world.
 */
public class LocationComp extends AbstractStatefulComponent<LocationPCD> {
    private final AvDatabase db;
    private final GameObjectId initialLocation;

    /**
     * Constructor for a {@link LocationComp}.
     */
    public LocationComp(final GameObjectId gameObjectId,
                        final AvDatabase db,
                        final GameObjectId initialLocation) {
        super(gameObjectId, db.locationDao());
        this.db = db;
        this.initialLocation = initialLocation;
    }

    @Override
    @NonNull
    protected LocationPCD createInitialState() {
        return new LocationPCD(getGameObjectId(), initialLocation);
    }

    public boolean hasLocation(final @Nullable IHasStoringPlaceGO gameObject) {
        return hasLocation(gameObject != null ? gameObject.getId() : null);
    }

    public boolean hasLocation(final @Nullable GameObjectId locationId) {
        return Objects.equals(getPcd().getLocationId(), locationId);
    }

    @Nullable
    public IHasStoringPlaceGO getLocation() {
        @Nullable final GameObjectId locationId = getLocationId();
        if (locationId == null) {
            return null;
        }

        // TODO Ist es gut, wenn die Komponente GameObjects aufruft?
        //  Vielleicht wäre es besser, wenn sich die Komponente
        //  nur ihr eigenes DAO merken würde und sich weder
        //  um andere Komponente noch andere Game Objects kümmern würde?
        return (IHasStoringPlaceGO) GameObjects.load(db, locationId);
    }

    @Nullable
    public GameObjectId getLocationId() {
        return getPcd().getLocationId();
    }

    /**
     * Sets the location to <code>null</code>.
     */
    public void unsetLocation() {
        getPcd().setLocationId(null);
    }

    public void setLocation(final IHasStoringPlaceGO storingPlace) {
        setLocation(storingPlace.getId());
    }

    public void setLocation(final GameObjectId locationId) {
        if (getGameObjectId().equals(locationId)) {
            throw new IllegalStateException("A game object cannot contain itself.");
        }

        getPcd().setLocationId(locationId);
    }
}
