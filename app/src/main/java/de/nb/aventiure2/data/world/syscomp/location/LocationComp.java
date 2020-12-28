package de.nb.aventiure2.data.world.syscomp.location;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Objects;

import de.nb.aventiure2.data.database.AvDatabase;
import de.nb.aventiure2.data.world.base.AbstractStatefulComponent;
import de.nb.aventiure2.data.world.base.GameObject;
import de.nb.aventiure2.data.world.base.GameObjectId;
import de.nb.aventiure2.data.world.base.IGameObject;
import de.nb.aventiure2.data.world.gameobject.*;
import de.nb.aventiure2.data.world.syscomp.storingplace.ILocationGO;

/**
 * Component for a {@link GameObject}: The game object
 * can be situated in a location in the world.
 */
public class LocationComp extends AbstractStatefulComponent<LocationPCD> {
    private final World world;

    @Nullable
    private final GameObjectId initialLocationId;

    @Nullable
    private final GameObjectId initialLastLocationId;

    /**
     * Ob das Game Object (z.B. vom Spieler) von einem Ort in der Welt zu einem
     * anderen bewegt werden kann. (Die goldene Kugel z.B. ist <i>movable</i>,
     * ein langer Holztisch nicht.)
     */
    private final boolean movable;

    /**
     * Constructor for a {@link LocationComp}.
     */
    public LocationComp(final GameObjectId gameObjectId,
                        final AvDatabase db,
                        final World world,
                        @Nullable final GameObjectId initialLocationId,
                        @Nullable final GameObjectId initialLastLocationId,
                        final boolean movable) {
        super(gameObjectId, db.locationDao());
        this.world = world;
        this.initialLocationId = initialLocationId;
        this.initialLastLocationId = initialLastLocationId;
        this.movable = movable;
    }

    @Override
    @NonNull
    protected LocationPCD createInitialState() {
        return new LocationPCD(getGameObjectId(), initialLocationId, initialLastLocationId);
    }

    public void narrateAndUnsetLocation() {
        narrateAndUnsetLocation(() -> {});
    }

    public void narrateAndUnsetLocation(final Runnable onEnter) {
        narrateAndSetLocation((GameObjectId) null, onEnter);
    }

    public void narrateAndSetLocation(@Nullable final ILocationGO newLocation) {
        narrateAndSetLocation(
                newLocation != null ? newLocation.getId() : null);
    }

    public void narrateAndSetLocation(@Nullable final GameObjectId newLocationId) {
        narrateAndSetLocation(newLocationId, () -> {});
    }

    public void narrateAndSetLocation(@Nullable final ILocationGO newLocation,
                                      final Runnable onEnter) {
        narrateAndSetLocation(
                newLocation != null ? newLocation.getId() : null, onEnter);
    }

    public void narrateAndSetLocation(@Nullable final GameObjectId newLocationId,
                                      final Runnable onEnter) {
        narrateAndDoLeaveReactions(newLocationId);

        setLocation(newLocationId);

        onEnter.run();

        narrateAndDoEnterReactions(newLocationId);
    }

    public void narrateAndDoLeaveReactions(
            @Nullable final GameObjectId newLocationId) {
        @Nullable final ILocationGO from = getLocation();
        if (from == null) {
            return;
        }

        world.narrateAndDoReactions()
                .onLeave(getGameObjectId(), from, newLocationId);
    }

    private void narrateAndDoEnterReactions(@Nullable final GameObjectId newLocationId) {
        narrateAndDoEnterReactions(getLastLocationId(), newLocationId);
    }

    public void narrateAndDoEnterReactions(
            @Nullable final GameObjectId conceptualLastLocationId,
            @Nullable final GameObjectId newLocationId) {
        if (newLocationId == null) {
            return;
        }
        world.narrateAndDoReactions()
                .onEnter(getGameObjectId(), conceptualLastLocationId, newLocationId);
    }

    /**
     * Gibt zurück, ob sich diese beiden Game Objects dieselbe äußerste Location haben
     * (z.B. denselben Raum). Wenn <code>otherId</code> <code>null</code> ist, gibt
     * * diese Methode immer <code>null</code> zurück.
     */
    public boolean hasSameUpperMostLocationAs(@Nullable final GameObjectId otherId) {
        if (otherId == null) {
            return false;
        }

        return hasSameUpperMostLocationAs(world.load(otherId));
    }

    /**
     * Gibt zurück, ob sich diese beiden Game Objects dieselbe äußerste Location haben
     * (z.B. denselben Raum). Wenn <code>other</code> <code>null</code> ist, gibt
     * diese Methode immer <code>null</code> zurück.
     */
    public boolean hasSameUpperMostLocationAs(@Nullable final IGameObject other) {
        ILocationGO otherUpperMostLocation = null;
        if (other instanceof ILocationGO) {
            otherUpperMostLocation = (ILocationGO) other;
        }

        if (other instanceof ILocatableGO) {
            otherUpperMostLocation = ((ILocatableGO) other).locationComp().getUpperMostLocation();
        }

        if (otherUpperMostLocation == null) {
            return false;
        }

        return otherUpperMostLocation.equals(getUpperMostLocation());
    }

    /**
     * Gibt <code>true</code> zurück, falls das Game Object
     * sich (ggf. rekusiv) an einer dieser Locations befindet.
     */
    public boolean hasRecursiveLocation(final GameObjectId... locationIds) {
        for (final GameObjectId locationId : locationIds) {
            if (hasRecursiveLocation(locationId)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Gibt <code>true</code> zurück, falls das Game Object
     * sich (ggf. rekusiv) an dieser Location befindet.
     */
    public boolean hasRecursiveLocation(@Nullable final GameObjectId locationId) {
        if (locationId == null) {
            return false;
        }

        final GameObject location = world.load(locationId);

        if (!(location instanceof ILocationGO)) {
            return false;
        }

        return hasRecursiveLocation((ILocationGO) location);
    }

    /**
     * Gibt <code>true</code> zurück, falls das Game Object sich (ggf. rekusiv)
     * an dieser Location befindet.
     */
    public boolean hasRecursiveLocation(@Nullable final ILocationGO location) {
        if (location == null) {
            return false;
        }

        @Nullable final ILocationGO myLocation = getLocation();
        if (myLocation == null) {
            return false;
        }

        if (myLocation.equals(location)) {
            return true;
        }

        if (!(myLocation instanceof ILocatableGO)) {
            return false;
        }

        return ((ILocatableGO) myLocation).locationComp().hasRecursiveLocation(location);
    }

    public boolean hasUpperMostLocation(final GameObjectId locationId) {
        final GameObject location = world.load(locationId);

        if (!(location instanceof ILocationGO)) {
            return false;
        }

        return hasUpperMostLocation((ILocationGO) location);
    }

    private boolean hasUpperMostLocation(final ILocationGO location) {
        return location.equals(getUpperMostLocation());
    }

    public @Nullable
    ILocationGO getUpperMostLocation() {
        @Nullable final ILocationGO res = getLocation();
        if (res instanceof ILocatableGO) {
            return ((ILocatableGO) res).locationComp().getUpperMostLocation();
        }

        return res;
    }

    /**
     * Gibt zurück, ob sich das Game Object derzeit an <i>keiner</i> <code>location</code>.
     */
    public boolean hasNoLocation() {
        return hasLocation((ILocationGO) null);
    }

    /**
     * Gibt zurück, ob sich das Game Object an einer dieser <code>locations</code> befindet
     * (<i>nicht</i> rekursiv, also <i>nicht</i> auf einem Tisch in diesem Raum).
     */
    public boolean hasLocation(final ILocationGO... locations) {
        for (@Nullable final ILocationGO location : locations) {
            if (hasLocation(location)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Gibt zurück, ob sich das Game Object an dieser <code>location</code> befindet (<i>nicht</i>
     * rekursiv, also <i>nicht</i> auf einem Tisch in diesem Raum).
     */
    public boolean hasLocation(final @Nullable ILocationGO location) {
        return hasLocation(location != null ? location.getId() : null);
    }

    public boolean hasLocation(final GameObjectId... locationIds) {
        for (@Nullable final GameObjectId locationId : locationIds) {
            if (hasLocation(locationId)) {
                return true;
            }
        }

        return false;
    }

    public boolean hasLocation(final @Nullable GameObjectId locationId) {
        return Objects.equals(getLocationId(), locationId);
    }

    @Nullable
    public ILocationGO getLocation() {
        @Nullable final GameObjectId locationId = getLocationId();
        if (locationId == null) {
            return null;
        }

        return (ILocationGO) world.load(locationId);
    }

    @Nullable
    public GameObjectId getLocationId() {
        return getPcd().getLocationId();
    }

    /**
     * /**
     * ACHTUNG! Diese Methode sollte nur in seltenen Ausnahmefällen verwendet werden!
     * Die Reacktions werden hier nicht aufgerufen!
     */
    public void unsetLocation() {
        setLocation(null);
    }

    /**
     * ACHTUNG! Diese Methode sollte nur in seltenen Ausnahmefällen verwendet werden!
     * Die Reactions werden hier nicht aufgerufen!
     */
    public void setLocation(@Nullable final GameObjectId locationId) {
        if (getGameObjectId().equals(locationId)) {
            throw new IllegalStateException("A game object cannot contain itself.");
        }

        getPcd().setLastLocationId(getLocationId());

        getPcd().setLocationId(locationId);
    }

    public boolean lastLocationWas(final @Nullable ILocationGO gameObject) {
        return lastLocationWas(gameObject != null ? gameObject.getId() : null);
    }

    public boolean lastLocationWas(final @Nullable GameObjectId locationId) {
        return Objects.equals(getPcd().getLastLocationId(), locationId);
    }

    @Nullable
    public ILocationGO getLastLocation() {
        @Nullable final GameObjectId lastLocationId = getLastLocationId();
        if (lastLocationId == null) {
            return null;
        }

        return (ILocationGO) world.load(lastLocationId);
    }

    @Nullable
    private GameObjectId getLastLocationId() {
        return getPcd().getLastLocationId();
    }

    public boolean isMovable() {
        return movable;
    }
}
