package de.nb.aventiure2.data.world.syscomp.movement;

import javax.annotation.Nonnull;

import de.nb.aventiure2.data.world.base.IGameObject;

/**
 * Game Object, das sich eigenständig in der Welt umherbewegt
 */
public interface IMovingGO extends IGameObject {
    @Nonnull
    MovementComp movementComp();
}