package de.nb.aventiure2.data.world.syscomp.location;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Objects;
import java.util.function.Supplier;

import de.nb.aventiure2.data.database.AvDatabase;
import de.nb.aventiure2.data.world.base.AbstractStatefulComponent;
import de.nb.aventiure2.data.world.base.GameObject;
import de.nb.aventiure2.data.world.base.GameObjectId;
import de.nb.aventiure2.data.world.base.IGameObject;
import de.nb.aventiure2.data.world.gameobject.World;
import de.nb.aventiure2.data.world.syscomp.storingplace.ILocationGO;
import de.nb.aventiure2.data.world.time.AvTimeSpan;

import static de.nb.aventiure2.data.world.time.AvTimeSpan.noTime;

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

    public AvTimeSpan narrateAndUnsetLocation() {
        return narrateAndUnsetLocation(AvTimeSpan::noTime);
    }

    @NonNull
    public AvTimeSpan narrateAndUnsetLocation(final Supplier<AvTimeSpan> onEnter) {
        return narrateAndSetLocation((GameObjectId) null, onEnter);
    }

    @NonNull
    public AvTimeSpan narrateAndSetLocation(@Nullable final ILocationGO newLocation) {
        return narrateAndSetLocation(
                newLocation != null ? newLocation.getId() : null);
    }

    @NonNull
    public AvTimeSpan narrateAndSetLocation(@Nullable final GameObjectId newLocationId) {
        return narrateAndSetLocation(newLocationId, AvTimeSpan::noTime);
    }

    @NonNull
    public AvTimeSpan narrateAndSetLocation(@Nullable final ILocationGO newLocation,
                                            final Supplier<AvTimeSpan> onEnter) {
        return narrateAndSetLocation(
                newLocation != null ? newLocation.getId() : null, onEnter);
    }

    @NonNull
    public AvTimeSpan narrateAndSetLocation(@Nullable final GameObjectId newLocationId,
                                            final Supplier<AvTimeSpan> onEnter) {
        AvTimeSpan timeElapsed = narrateAndDoLeaveReactions(newLocationId);

        setLocation(newLocationId);

        timeElapsed = timeElapsed.plus(onEnter.get());

        return timeElapsed.plus(narrateAndDoEnterReactions(newLocationId));
    }

    @NonNull
    public AvTimeSpan narrateAndDoLeaveReactions(
            @Nullable final GameObjectId newLocationId) {
        @Nullable final ILocationGO from = getLocation();
        if (from == null) {
            return noTime();
        }

        return world.narrateAndDoReactions()
                .onLeave(getGameObjectId(), from, newLocationId);
    }

    @NonNull
    public AvTimeSpan narrateAndDoEnterReactions(@Nullable final GameObjectId newLocationId) {
        if (newLocationId == null) {
            return noTime();
        }
        return world.narrateAndDoReactions()
                .onEnter(getGameObjectId(), getLastLocation(), newLocationId);
    }

    /**
     * Gibt zurück, ob sich diese beiden Game Objects dieselbe äußerste Location haben
     * (z.B. denselben Raum).
     */
    public boolean hasSameUpperMostLocationAs(final GameObjectId otherId) {
        return hasSameUpperMostLocationAs(world.load(otherId));
    }

    /**
     * Gibt zurück, ob sich diese beiden Game Objects dieselbe äußerste Location haben
     * (z.B. denselben Raum).
     */
    public boolean hasSameUpperMostLocationAs(final IGameObject other) {
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
     * sich (ggf. rekusiv) an dieser Location befindet.
     */
    public boolean hasRecursiveLocation(final GameObjectId locationId) {
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
    public boolean hasRecursiveLocation(final ILocationGO location) {
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

    public boolean hasUpperMostLocation(final ILocationGO location) {
        return location.equals(getUpperMostLocation());
    }

    private @Nullable
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
        return Objects.equals(getPcd().getLocationId(), locationId);
    }

    @Nullable
    public ILocationGO getLocation() {
        @Nullable final GameObjectId locationId = getLocationId();
        if (locationId == null) {
            return null;
        }

        // TODO Ist es gut, wenn die Komponente GameObjects aufruft?
        //  Ideen könnten sein:
        //  - Die Komponente darf sich nur um sich selbst kümmern.
        //  - Die Komponente darf sich nur um sich selbst kümmern sowie
        //    um dieselbe Komponenten-Klasse bei anderen Game Objets
        //    (also nur um ihr "System").
        //  - Die Komponente darf sich nur um sich selbst kümmern sowie
        //    um dieselbe Komponenten-Klasse bei anderen Game Objets
        //    (also nur um ihr "System") - und sie darf ihr eigenes Game Object
        //    nach Dingen fragen (über ein dünnes Interface wie ComponentContext).
        //  - Die Komponente darf sich um sich selbst kümmern
        //    und sie darf auch andere Game Objects laden und mit ihnen interagieren,
        //    allerdings nicht direkt mit anderen Komponenten dieser
        //    Game Objects, außer diese anderen Komponenten gehören zum
        //    eigenen "System" (also: Die LocationComp darf mit anderen
        //    Game Objects interagieren sowie deren LocationComps - aber nicht
        //    deren XYZComps).
        //  - Eine Komponente darf alles, was auch eine ScAction (z.B.) darf.
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
     * Die Reacktions werden hier nicht aufgerufen!
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
    public GameObjectId getLastLocationId() {
        return getPcd().getLastLocationId();
    }

    public boolean isMovable() {
        return movable;
    }
}
