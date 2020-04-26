package de.nb.aventiure2.data.world.gameobjectstate;

import javax.annotation.Nonnull;

import de.nb.aventiure2.data.world.base.IGameObject;

/**
 * Game Object, das stets einen von mehreren Zuständen
 * hat.
 */
public interface IHasStateGO extends IGameObject {
    @Nonnull
    public StateComp stateComp();
}
