package de.nb.aventiure2.data.world.syscomp.alive;

import javax.annotation.Nonnull;

import de.nb.aventiure2.data.world.base.IGameObject;

/**
 * Game Object, bei dem es sich um ein lebendiges Wesen handelt.
 */
public interface ILivingBeingGO extends IGameObject {
    @Nonnull
    AliveComp aliveComp();
}