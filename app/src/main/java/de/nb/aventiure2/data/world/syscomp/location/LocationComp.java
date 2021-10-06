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
import de.nb.aventiure2.data.world.syscomp.storingplace.ICanHaveOuterMostLocation;
import de.nb.aventiure2.data.world.syscomp.storingplace.ILocationGO;
import de.nb.aventiure2.german.praedikat.PraedikatMitEinerObjektleerstelle;

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
     * Ob das Game Object aus vielen unverbunden Einzelteilen besteht (z.B. viele Äste).
     */
    private final boolean vielteilig;

    /**
     * Konstruktor für {@link LocationComp}, bei dem das Objekt <i>nicht vielteilig</i> ist.
     */
    public LocationComp(final GameObjectId gameObjectId,
                        final AvDatabase db,
                        final World world,
                        @Nullable final GameObjectId initialLocationId,
                        @Nullable final GameObjectId initialLastLocationId,
                        final boolean movable) {
        this(gameObjectId, db, world, initialLocationId, initialLastLocationId, movable, false);
    }

    public LocationComp(final GameObjectId gameObjectId,
                        final AvDatabase db,
                        final World world,
                        @Nullable final GameObjectId initialLocationId,
                        @Nullable final GameObjectId initialLastLocationId,
                        final boolean movable,
                        final boolean vielteilig) {
        super(gameObjectId, db.locationDao());
        this.world = world;
        this.initialLocationId = initialLocationId;
        this.initialLastLocationId = initialLastLocationId;
        this.movable = movable;
        this.vielteilig = vielteilig;
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
        narrateAndSetLocation(newLocation != null ? newLocation.getId() : null);
    }

    public void narrateAndSetLocation(@Nullable final GameObjectId newLocationId) {
        narrateAndSetLocation(newLocationId, () -> {});
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
    public boolean hasSameOuterMostLocationAs(@Nullable final GameObjectId otherId) {
        return hasSameOuterMostLocationAs((IGameObject) world.load(otherId));
    }


    /**
     * Gibt zurück, ob sich diese beiden Game Objects dieselbe äußerste Location haben
     * (z.B. denselben Raum - soweit sichtbar). Wenn <code>otherId</code> <code>null</code> ist,
     * gibt
     * * diese Methode immer <code>null</code> zurück.
     */
    public boolean hasSameVisibleOuterMostLocationAs(@Nullable final GameObjectId otherId) {
        return hasSameVisibleOuterMostLocationAs((IGameObject) world.load(otherId));
    }


    /**
     * Gibt zurück, ob sich diese beiden Game Objects dieselbe äußerste Location haben
     * (z.B. denselben Raum). Wenn <code>other</code> <code>null</code> ist, gibt
     * diese Methode immer <code>null</code> zurück.
     */
    public boolean hasSameOuterMostLocationAs(@Nullable final IGameObject other) {
        ILocationGO otherOuterMostLocation = null;
        if (other instanceof ICanHaveOuterMostLocation) {
            otherOuterMostLocation = ((ICanHaveOuterMostLocation) other).getOuterMostLocation();
        }

        if (otherOuterMostLocation == null) {
            return false;
        }

        return otherOuterMostLocation.equals(getOuterMostLocation());
    }


    /**
     * Gibt zurück, ob sich diese beiden Game Objects dieselbe äußerste Location haben
     * (z.B. denselben Raum - soweit man sehen kann). Wenn <code>other</code> <code>null</code>
     * ist, gibt diese Methode immer <code>null</code> zurück.
     */
    public boolean hasSameVisibleOuterMostLocationAs(@Nullable final IGameObject other) {
        if (!(other instanceof ICanHaveOuterMostLocation)) {
            return false;
        }

        @Nullable final ILocationGO otherVisibleOuterMostLocation =
                ((ICanHaveOuterMostLocation) other).getVisibleOuterMostLocation();

        if (otherVisibleOuterMostLocation == null) {
            return false;
        }

        return otherVisibleOuterMostLocation.equals(getVisibleOuterMostLocation());
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
        @Nullable final GameObject location = world.load(locationId);

        if (!(location instanceof ILocationGO)) {
            return false;
        }

        return hasRecursiveLocation((ILocationGO) location);
    }


    /**
     * Gibt <code>true</code> zurück, falls das Game Object
     * sich (ggf. rekusiv - soweit man sehen kann) an dieser Location befindet.
     */
    public boolean hasVisiblyRecursiveLocation(@Nullable final GameObjectId locationId) {
        @Nullable final GameObject location = world.load(locationId);

        @Nullable final ILocationGO myLocation = getLocation();
        if (myLocation == null || !myLocation.storingPlaceComp()
                .manKannHineinsehenUndLichtScheintHineinUndHinaus()) {
            return false;
        }

        return myLocation.isOrHasVisiblyRecursiveLocation(location);
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

        return myLocation.isOrHasRecursiveLocation(location);
    }

    public @Nullable
    ILocationGO getOuterMostLocation() {
        @Nullable final ILocationGO location = getLocation();

        if (location == null) {
            return null;
        }

        return location.getOuterMostLocation();
    }

    @Nullable
    ILocationGO getVisibleOuterMostLocation() {
        @Nullable final ILocationGO location = getLocation();

        if (location == null
                || !location.storingPlaceComp()
                .manKannHineinsehenUndLichtScheintHineinUndHinaus()) {
            return location;
        }

        return location.getVisibleOuterMostLocation();
    }

    /**
     * Gibt zurück, ob sich das Game Object derzeit an <i>keiner</i> <code>location</code>.
     */
    public boolean hasNoLocation() {
        return hasLocation((ILocationGO) null);
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

    public boolean isNiedrig() {
        return getLocation() != null && getLocation().storingPlaceComp().isNiedrig();
    }

    /**
     * Gibt ein Prädikat für das Mitnehmen von der Location zurück.
     *
     * @param vielteilig Ob das Objekt, das mitgenommen wird, vielteilig ist
     *                   (z.B. viele Äste)
     */
    @Nullable
    public PraedikatMitEinerObjektleerstelle getMitnehmenPraedikat(final boolean vielteilig) {
        if (getLocation() == null) {
            return null;
        }

        return getLocation().storingPlaceComp().getLocationMode().getMitnehmenPraedikat(vielteilig);
    }

    @Nullable
    public ILocationGO getLocation() {
        return world.load(getLocationId());
    }

    @Nullable
    public GameObjectId getLocationId() {
        return requirePcd().getLocationId();
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

        requirePcd().setLastLocationId(getLocationId());

        requirePcd().setLocationId(locationId);
    }

    public boolean lastLocationWas(final @Nullable ILocationGO gameObject) {
        return lastLocationWas(gameObject != null ? gameObject.getId() : null);
    }

    public boolean lastLocationWas(final @Nullable GameObjectId locationId) {
        return Objects.equals(requirePcd().getLastLocationId(), locationId);
    }

    @Nullable
    public ILocationGO getLastLocation() {
        return world.load(getLastLocationId());
    }

    @Nullable
    private GameObjectId getLastLocationId() {
        return requirePcd().getLastLocationId();
    }

    public boolean isMovable() {
        return movable;
    }

    public boolean isVielteilig() {
        return vielteilig;
    }
}
