package de.nb.aventiure2.data.world.base;

/**
 * Interface for any object within in the game. An <i>entity</i> in the
 * entity-component-system pattern.
 */
public interface IGameObject {
    boolean is(IGameObject... someAlternatives);

    boolean is(GameObjectId... someIdAlternatives);

    GameObjectId getId();

    @Override
    boolean equals(Object o);

    @Override
    int hashCode();
}
