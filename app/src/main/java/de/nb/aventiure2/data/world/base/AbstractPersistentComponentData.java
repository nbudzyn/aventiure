package de.nb.aventiure2.data.world.base;

import androidx.annotation.NonNull;
import androidx.room.PrimaryKey;

import java.util.Objects;

/**
 * Mutable - and therefore persistent - data of an {@link IComponent}.
 */
public class AbstractPersistentComponentData {
    @PrimaryKey
    @NonNull
    private GameObjectId gameObjectId;

    private boolean changed = false;

    public AbstractPersistentComponentData(@NonNull final GameObjectId gameObjectId) {
        this.gameObjectId = gameObjectId;
    }

    public boolean isChanged() {
        return changed;
    }

    protected void setChanged() {
        setChanged(true);
    }

    public void setChanged(final boolean changed) {
        this.changed = changed;
    }

    @NonNull
    public GameObjectId getGameObjectId() {
        return gameObjectId;
    }

    void setGameObjectId(@NonNull final GameObjectId gameObjectId) {
        this.gameObjectId = gameObjectId;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final AbstractPersistentComponentData that = (AbstractPersistentComponentData) o;
        return gameObjectId.equals(that.gameObjectId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(gameObjectId);
    }

    @NonNull
    @Override
    public String toString() {
        return getClass().getSimpleName() + " for game object " + gameObjectId;
    }
}
