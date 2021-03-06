package de.nb.aventiure2.data.world.base;

import androidx.annotation.NonNull;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.util.Objects;

/**
 * Mutable - and therefore persistent - data of an {@link IComponent}.
 */
public class AbstractPersistentComponentData {
    @PrimaryKey
    @NonNull
    private final GameObjectId gameObjectId;

    @Ignore
    private boolean changed = false;

    protected AbstractPersistentComponentData(@NonNull final GameObjectId gameObjectId) {
        this.gameObjectId = gameObjectId;
    }

    @Ignore
    boolean isChanged() {
        return changed;
    }

    @Ignore
    protected void setChanged() {
        setChanged(true);
    }

    @Ignore
    public void setChanged(final boolean changed) {
        this.changed = changed;
    }

    @NonNull
    public GameObjectId getGameObjectId() {
        return gameObjectId;
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
