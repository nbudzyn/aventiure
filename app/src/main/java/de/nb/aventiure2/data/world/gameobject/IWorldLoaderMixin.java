package de.nb.aventiure2.data.world.gameobject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import javax.annotation.Nonnull;

import de.nb.aventiure2.data.world.base.GameObjectId;
import de.nb.aventiure2.data.world.base.IGameObject;
import de.nb.aventiure2.data.world.gameobject.player.*;
import de.nb.aventiure2.data.world.gameobject.wetter.*;

/**
 * Mixin with helper methods for loading game objects from the {@link World}.
 */
public interface IWorldLoaderMixin {
    @NonNull
    default SpielerCharakter loadSC() {
        return getWorld().loadSC();
    }

    @NonNull
    default Wetter loadWetter() {
        return getWorld().loadWetter();
    }

    @Nonnull
    default <T extends IGameObject> T loadRequired(@Nonnull final GameObjectId id) {
        return getWorld().loadRequired(id);
    }

    @Nullable
    default <T extends IGameObject> T load(@Nullable final GameObjectId id) {
        return getWorld().load(id);
    }

    World getWorld();
}
