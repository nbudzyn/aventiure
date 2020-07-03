package de.nb.aventiure2.data.world.gameobject;

import androidx.annotation.Nullable;

import java.util.HashMap;

import de.nb.aventiure2.data.world.base.GameObject;
import de.nb.aventiure2.data.world.base.GameObjectId;

/**
 * Maps {@link de.nb.aventiure2.data.world.base.GameObjectId}s to
 * {@link de.nb.aventiure2.data.world.base.GameObject}s.
 */
class GameObjectIdMap extends HashMap<GameObjectId, GameObject> {
    @Nullable
    public void putAll(final GameObject... gameObjects) {
        for (final GameObject gameObject : gameObjects) {
            put(gameObject);
        }
    }

    @Nullable
    public GameObject put(final GameObject gameObject) {
        return super.put(gameObject.getId(), gameObject);
    }
}
