package de.nb.aventiure2.data.world.syscomp.location;

import androidx.annotation.Nullable;

import com.google.common.collect.ImmutableList;

import java.util.Collection;
import java.util.List;

import de.nb.aventiure2.data.database.AvDatabase;
import de.nb.aventiure2.data.world.base.GameObjectId;
import de.nb.aventiure2.data.world.base.IGameObject;
import de.nb.aventiure2.data.world.base.Lichtverhaeltnisse;
import de.nb.aventiure2.data.world.syscomp.description.IDescribableGO;
import de.nb.aventiure2.data.world.syscomp.storingplace.ICanHaveOuterMostLocation;
import de.nb.aventiure2.data.world.syscomp.storingplace.ILocationGO;

import static com.google.common.collect.ImmutableList.toImmutableList;

/**
 * Functionality concerned with Location that might delta several game objects:
 * Game Object Queries etc.
 */
public class LocationSystem {
    private final LocationDao dao;

    public LocationSystem(final AvDatabase db) {
        dao = db.locationDao();
    }

    /**
     * Ob bei einer solchen Bewegung eine <i>Sichtschranke</i> überschritten
     * wird: Die Bewegung geht an einen Ort, der vom Ursprungsort aus nicht einzusehen war.
     */
    public static boolean isBewegungUeberSichtschranke(@Nullable final ILocationGO from,
                                                       @Nullable final IGameObject to) {
        if (from == null) {
            return to != null;
        }

        return !from.is(to)
                && !LocationSystem.manKannHinsehenUndLichtScheintHineinUndHinaus(to);
    }

    /**
     * Gibt <code>true</code> zurück falls
     * <ul>
     * <li><code>one</code>  <code>other</code> ist
     * <li>oder sich <code>one</code> an der Location <code>other</code> befindet
     * (ggf. rekusiv) - soweit die Sichtbarkeit reicht
     * </ul>
     */
    public static boolean isOrHasVisiblyRecursiveLocation(@Nullable final IGameObject one,
                                                          @Nullable final IGameObject other) {
        if (one == null) {
            return other == null;
        }

        if (one.equals(other)) {
            return true;
        }

        if (!(one instanceof ICanHaveOuterMostLocation)) {
            return false;
        }

        return ((ICanHaveOuterMostLocation) one).isOrHasVisiblyRecursiveLocation(other);
    }

    /**
     * Gibt <code>true</code> zurück falls
     * <ul>
     * <li><code>one</code>  <code>other</code> ist
     * <li>oder sich <code>one</code> an der Location <code>other</code> befindet
     * (ggf. rekusiv).
     * </ul>
     */
    public static boolean isOrHasRecursiveLocation(@Nullable final IGameObject one,
                                                   @Nullable final IGameObject other) {
        if (one == null) {
            return other == null;
        }

        if (one.equals(other)) {
            return true;
        }

        if (!(one instanceof ICanHaveOuterMostLocation)) {
            return false;
        }


        return ((ICanHaveOuterMostLocation) one).isOrHasRecursiveLocation(other);
    }

    public static boolean manKannHinsehenUndLichtScheintHineinUndHinaus(
            @Nullable final IGameObject gameObject) {
        if (gameObject == null) {
            return false;
        }

        return (gameObject instanceof ILocationGO)
                && ((ILocationGO) gameObject).storingPlaceComp()
                .manKannHineinsehenUndLichtScheintHineinUndHinaus();
    }

    /**
     * Gibt die Lichtverhältnisse an diesem Ort zurück.
     */
    public static Lichtverhaeltnisse getLichtverhaeltnisse(@Nullable final ILocationGO location) {
        if (location == null) {
            return Lichtverhaeltnisse.HELL;
        }

        return location.storingPlaceComp().getLichtverhaeltnisse();
    }

    /**
     * Vor jedem Aufruf muss sichergestellt sein, dass alle Änderungen an Game Objects
     * gespeichert sind!
     */
    public List<GameObjectId> findByLocation(final GameObjectId locationId) {
        return dao.findByLocation(locationId);
    }

    public static <DESC_OBJ extends ILocatableGO & IDescribableGO>
    ImmutableList<DESC_OBJ> filterMovable(final Collection<DESC_OBJ> gameObjects) {
        return filterMovable(gameObjects, true);
    }

    public static <DESC_OBJ extends ILocatableGO & IDescribableGO> ImmutableList<DESC_OBJ>
    filterNotMovable(final Collection<DESC_OBJ> gameObjects) {
        return filterMovable(gameObjects, false);
    }

    private static <DESC_OBJ extends ILocatableGO & IDescribableGO> ImmutableList<DESC_OBJ>
    filterMovable(final Collection<DESC_OBJ> gameObjects, final boolean movable) {
        return gameObjects.stream()
                .filter(go -> go.locationComp().isMovable() == movable)
                .collect(toImmutableList());
    }

    public static boolean haveSameOuterMostLocation(
            final @Nullable ILocationGO one, final @Nullable ILocationGO other) {
        if (one == null || other == null) {
            return false;
        }

        return one.getOuterMostLocation().is(other.getOuterMostLocation());
    }

}
