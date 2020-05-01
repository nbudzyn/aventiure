package de.nb.aventiure2.data.world.base;

/**
 * Abstract super-class for components. An {@link GameObject}
 * can be linked to several components according to the entity-component-system pattern.
 */
public abstract class AbstractComponent implements IComponent {
    private final GameObjectId gameObjectId;

    protected AbstractComponent(final GameObjectId gameObjectId) {
        this.gameObjectId = gameObjectId;
    }

    public GameObjectId getGameObjectId() {
        return gameObjectId;
    }
}
