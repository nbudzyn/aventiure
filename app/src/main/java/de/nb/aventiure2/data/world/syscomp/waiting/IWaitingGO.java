package de.nb.aventiure2.data.world.syscomp.waiting;

import javax.annotation.Nonnull;

import de.nb.aventiure2.data.world.base.IGameObject;

/**
 * Game Object, das auf etwas warten kann
 */
public interface IWaitingGO extends IGameObject {
    @Nonnull
    WaitingComp waitingComp();
}