package de.nb.aventiure2.data.world.syscomp.location;

import androidx.annotation.Nullable;

import javax.annotation.Nonnull;

import de.nb.aventiure2.data.world.base.IGameObject;
import de.nb.aventiure2.data.world.syscomp.storingplace.ICanHaveOuterMostLocation;
import de.nb.aventiure2.data.world.syscomp.storingplace.ILocationGO;

/**
 * Game Object, das sich in der Welt an einem Ort befinden kann.
 * <p>
 * Game Objects, die gleichzeitig auch {@link ILocationGO} implementieren,
 * sollen {@link ILocatableLocationGO} implementieren!
 */
public interface ILocatableGO extends ICanHaveOuterMostLocation {
    @Nonnull
    LocationComp locationComp();

    @Nullable
    @Override
    default ILocationGO getOuterMostLocation() {
        return locationComp().getOuterMostLocation();
    }

    @Nullable
    @Override
    default ILocationGO getVisibleOuterMostLocation() {
        return locationComp().getVisibleOuterMostLocation();
    }

    @Override
    default boolean isOrHasRecursiveLocation(@Nullable final IGameObject location) {
        if (equals(location)) {
            return true;
        }

        if (!(location instanceof ILocationGO)) {
            return false;
        }

        return locationComp().hasRecursiveLocation((ILocationGO) location);
    }

    @Override
    default boolean isOrHasVisiblyRecursiveLocation(@Nullable final IGameObject location) {
        if (equals(location)) {
            return true;
        }

        if (!(location instanceof ILocationGO)) {
            return false;
        }

        return locationComp().hasVisiblyRecursiveLocation(location.getId());
    }
}