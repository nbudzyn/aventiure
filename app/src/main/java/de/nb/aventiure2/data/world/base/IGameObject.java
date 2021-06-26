package de.nb.aventiure2.data.world.base;

import androidx.annotation.Nullable;

/**
 * Interface for any object within in the game. An <i>entity</i> in the
 * entity-component-system pattern.
 */
public interface IGameObject {
    boolean is(@Nullable IGameObject... someAlternatives);

    boolean is(@Nullable GameObjectId... someIdAlternatives);

    GameObjectId getId();

    @Override
    boolean equals(Object o);

    @Override
    int hashCode();
}
