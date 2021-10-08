package de.nb.aventiure2.data.world.syscomp.state;

import javax.annotation.Nonnull;

import de.nb.aventiure2.data.world.base.IGameObject;

/**
 * Game Object, das stets einen von mehreren Zuständen
 * hat.
 */
public interface IHasStateGO<S extends Enum<S>> extends IGameObject {
    @Nonnull
    AbstractStateComp<S> stateComp();
}
