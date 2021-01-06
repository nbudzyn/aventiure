package de.nb.aventiure2.data.world.base;

import androidx.annotation.NonNull;

import java.util.Objects;

import javax.annotation.concurrent.Immutable;

import de.nb.aventiure2.german.base.IBezugsobjekt;

/**
 * ID of an {@link GameObject}
 */
@Immutable
public class GameObjectId implements IBezugsobjekt {
    private final long value;

    public GameObjectId(final long value) {
        this.value = value;
    }

    public long toLong() {
        return value;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final GameObjectId that = (GameObjectId) o;
        return value == that.value;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @NonNull
    @Override
    public String toString() {
        return Long.toString(value);
    }
}
