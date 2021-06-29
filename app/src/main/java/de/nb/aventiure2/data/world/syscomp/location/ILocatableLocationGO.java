package de.nb.aventiure2.data.world.syscomp.location;

import androidx.annotation.Nullable;

import javax.annotation.Nonnull;

import de.nb.aventiure2.data.world.base.IGameObject;
import de.nb.aventiure2.data.world.syscomp.storingplace.ILocationGO;

import static java.util.Objects.requireNonNull;

/**
 * Game Object, das die Möglichkeit bietet, etwas abzulegen / zu platzieren und zugleich auch
 * selbst an einem gewissen Ort platziert werden kann (z.B. eine Kiste).
 */
public interface ILocatableLocationGO extends ILocatableGO, ILocationGO {
    @Override
    @Nonnull
    default ILocationGO getOuterMostLocation() {
        if (locationComp().getLocation() == null) {
            return this;
        }

        return requireNonNull(ILocatableGO.super.getOuterMostLocation());
    }

    @Override
    @Nonnull
    default ILocationGO getVisibleOuterMostLocation() {
        if (locationComp().getLocation() == null) {
            return this;
        }

        return requireNonNull(ILocatableGO.super.getVisibleOuterMostLocation());
    }

    @Override
    default boolean isOrHasRecursiveLocation(@Nullable final IGameObject location) {
        return ILocatableGO.super.isOrHasRecursiveLocation(location);
    }

    @Override
    default boolean isOrHasVisiblyRecursiveLocation(@Nullable final IGameObject location) {
        return ILocatableGO.super.isOrHasVisiblyRecursiveLocation(location);
    }
}
