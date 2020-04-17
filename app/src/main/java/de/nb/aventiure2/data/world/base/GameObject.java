package de.nb.aventiure2.data.world.base;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Objects;

import de.nb.aventiure2.data.world.room.StoringPlace;

/**
 * Any object within in the game. An <i>entity</i> in the entity-component-system pattern.
 */
public class GameObject {
    private final GameObjectId id;

    // Components
    @Nullable
    private StoringPlace storingPlace;

    public GameObject(final GameObjectId id) {
        this.id = id;
    }

    public void setStoringPlace(final @Nullable StoringPlace storingPlace) {
        this.storingPlace = storingPlace;
    }

    public @Nullable
    StoringPlace getStoringPlace() {
        return storingPlace;
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
        final GameObject that = (GameObject) o;
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
