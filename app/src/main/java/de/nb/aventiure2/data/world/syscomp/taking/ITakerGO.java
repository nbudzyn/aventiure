package de.nb.aventiure2.data.world.syscomp.taking;

import javax.annotation.Nonnull;

import de.nb.aventiure2.data.world.base.IGameObject;

/**
 * Game Object, dem man etwas geben kann.
 */
public interface ITakerGO<C extends AbstractTakingComp> extends IGameObject {
    @Nonnull
    C takingComp();
}