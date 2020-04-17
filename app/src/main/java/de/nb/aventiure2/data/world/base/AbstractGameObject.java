package de.nb.aventiure2.data.world.base;

import androidx.annotation.NonNull;

import java.util.Objects;

/**
 * Any object within in the game. An <i>entity</i> in the entity-component-system pattern.
 */
public class AbstractGameObject {
    private final GameObjectId id;

    public AbstractGameObject(final GameObjectId id) {
        this.id = id;
    }

    public boolean is(final GameObjectId someId) {
        return getId().equals(someId);
    }

    public GameObjectId getId() {
        return id;
    }


    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final AbstractGameObject that = (AbstractGameObject) o;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @NonNull
    @Override
    public String toString() {
        return id.toString();
    }
}
