package de.nb.aventiure2.data.world.base;

/**
 * Abstract super-class for {@link AbstractComponent}s that does not have any mutable - and
 * therefore persistent - data.
 */
public abstract class AbstractStatelessComponent extends AbstractComponent {
    protected AbstractStatelessComponent(final GameObjectId gameObjectId) {
        super(gameObjectId);
    }

    @Override
    public final void saveInitialState() {
        // Component doesn't have any mutable state to be saved.
    }

    @Override
    public final void load() {
        // Component doesn't have any mutable state to be loaded.
    }

    @Override
    public boolean isChanged() {
        return false;
    }

    @Override
    public void save(final boolean unload) {
        // Component doesn't have any mutable state to be loaded.
    }
}
