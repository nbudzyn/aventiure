package de.nb.aventiure2.data.world.syscomp.location;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Objects;
import java.util.function.Supplier;

import de.nb.aventiure2.data.database.AvDatabase;
import de.nb.aventiure2.data.world.base.AbstractStatefulComponent;
import de.nb.aventiure2.data.world.base.GameObject;
import de.nb.aventiure2.data.world.base.GameObjectId;
import de.nb.aventiure2.data.world.gameobjects.GameObjects;
import de.nb.aventiure2.data.world.syscomp.storingplace.IHasStoringPlaceGO;
import de.nb.aventiure2.data.world.time.AvTimeSpan;

import static de.nb.aventiure2.data.world.time.AvTimeSpan.noTime;

/**
 * Component for a {@link GameObject}: The game object
 * can be situated in a location in the world.
 */
public class LocationComp extends AbstractStatefulComponent<LocationPCD> {
    private final AvDatabase db;
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
                        final GameObjectId initialLocationId,
                        @Nullable final GameObjectId initialLastLocationId,
                        final boolean movable) {
        super(gameObjectId, db.locationDao());
        this.db = db;
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
        return narrateAndSetLocation((GameObjectId) null);
    }

    public AvTimeSpan narrateAndSetLocation(@Nullable final IHasStoringPlaceGO newLocation) {
        return narrateAndSetLocation(
                newLocation != null ? newLocation.getId() : null);
    }

    public AvTimeSpan narrateAndSetLocation(@Nullable final GameObjectId newLocationId) {
        return narrateAndSetLocation(newLocationId, () -> noTime());
    }

    public AvTimeSpan narrateAndSetLocation(@Nullable final GameObjectId newLocationId,
                                            final Supplier<AvTimeSpan> onEnter) {
        AvTimeSpan timeElapsed = narrateAndDoLeaveReactions(newLocationId);

        setLocation(newLocationId);

        timeElapsed = timeElapsed.plus(onEnter.get());

        return timeElapsed.plus(narrateAndDoEnterReactions(newLocationId));
    }

    public AvTimeSpan narrateAndDoLeaveReactions(
            @Nullable final GameObjectId newLocationId) {
        @Nullable final IHasStoringPlaceGO from = getLocation();
        if (from == null) {
            return noTime();
        }

        return GameObjects.narrateAndDoReactions()
                .onLeave(getGameObjectId(), from, newLocationId);
    }

    public AvTimeSpan narrateAndDoEnterReactions(@Nullable final GameObjectId newLocationId) {
        if (newLocationId == null) {
            return noTime();
        }
        return GameObjects.narrateAndDoReactions()
                .onEnter(getGameObjectId(), getLastLocation(), newLocationId);
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
        setLocation((GameObjectId) null);
    }

    private void setLocation(@Nullable final GameObjectId locationId) {
        if (Objects.equals(getLocationId(), locationId)) {
            return;
        }

        if (getGameObjectId().equals(locationId)) {
            throw new IllegalStateException("A game object cannot contain itself.");
        }


        getPcd().setLastLocationId(getLocationId());

        getPcd().setLocationId(locationId);
    }

    public boolean lastLocationWas(final @Nullable IHasStoringPlaceGO gameObject) {
        return lastLocationWas(gameObject != null ? gameObject.getId() : null);
    }

    public boolean lastLocationWas(final @Nullable GameObjectId locationId) {
        return Objects.equals(getPcd().getLastLocationId(), locationId);
    }

    @Nullable
    private IHasStoringPlaceGO getLastLocation() {
        @Nullable final GameObjectId lastLocationId = getLastLocationId();
        if (lastLocationId == null) {
            return null;
        }

        // TODO Ist es gut, wenn die Komponente GameObjects aufruft?
        //  Vielleicht wäre es besser, wenn sich die Komponente
        //  nur ihr eigenes DAO merken würde und sich weder
        //  um andere Komponente noch andere Game Objects kümmern würde?
        return (IHasStoringPlaceGO) GameObjects.load(db, lastLocationId);
    }

    @Nullable
    private GameObjectId getLastLocationId() {
        return getPcd().getLastLocationId();
    }

    public boolean isMovable() {
        return movable;
    }
}
