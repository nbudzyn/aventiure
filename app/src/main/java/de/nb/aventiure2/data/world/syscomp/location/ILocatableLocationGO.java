package de.nb.aventiure2.data.world.syscomp.location;

import androidx.annotation.Nullable;

import de.nb.aventiure2.data.world.base.IGameObject;
import de.nb.aventiure2.data.world.syscomp.storingplace.ILocationGO;

/**
 * Game Object, das die Möglichkeit bietet, etwas abzulegen / zu platzieren und zugleich auch
 * selbst an einem gewissen Ort platziert werden kann (z.B. eine Kiste).
 */
public interface ILocatableLocationGO extends ILocatableGO, ILocationGO {
    @Nullable
    @Override
    default ILocationGO getOuterMostLocation() {
        if (locationComp().getLocation() == null) {
            return this;
        }

        return ILocatableGO.super.getOuterMostLocation();
    }

    @Nullable
    @Override
    default ILocationGO getVisibleOuterMostLocation() {
        if (locationComp().getLocation() == null) {
            return this;
        }

        return ILocatableGO.super.getVisibleOuterMostLocation();
    }

    @Override
    default boolean isOrHasRecursiveLocation(@Nullable final IGameObject location) {
        return ILocatableGO.super.isOrHasRecursiveLocation(location);
    }
}
