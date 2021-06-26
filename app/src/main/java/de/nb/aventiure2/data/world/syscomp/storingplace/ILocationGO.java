package de.nb.aventiure2.data.world.syscomp.storingplace;

import androidx.annotation.Nullable;

import javax.annotation.Nonnull;

import de.nb.aventiure2.data.world.base.IGameObject;
import de.nb.aventiure2.data.world.syscomp.location.ILocatableGO;
import de.nb.aventiure2.data.world.syscomp.location.ILocatableLocationGO;

/**
 * Game Object (z.B. ein Raum) das die Möglichkeit bietet, etwas abzulegen / zu platzieren - und
 * zwar
 * an einem bestimmten Platz (z.B. "auf dem Boden" oder "auf einem Tisch"). Die Dinge, die man dort
 * platziert, müssen {@link ILocatableGO}s sein.
 * <p>
 * Game Objects, die gleichzeitig auch {@link ILocatableGO implementieren,
 * sollen {@link ILocatableLocationGO} implementieren!
 */
public interface ILocationGO extends ICanHaveOuterMostLocation {
    @Nonnull
    StoringPlaceComp storingPlaceComp();

    @Override
    @Nonnull
    default ILocationGO getOuterMostLocation() {
        return this;
    }

    @Override
    @Nonnull
    default ILocationGO getVisibleOuterMostLocation() {
        return this;
    }

    @Override
    default boolean isOrHasRecursiveLocation(@Nullable final IGameObject location) {
        return equals(location);
    }
}
