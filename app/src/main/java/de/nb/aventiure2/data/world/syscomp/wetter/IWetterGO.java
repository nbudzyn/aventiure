package de.nb.aventiure2.data.world.syscomp.wetter;

import javax.annotation.Nonnull;

import de.nb.aventiure2.data.world.base.IGameObject;

/**
 * Game Object für das Wetter
 */
public interface IWetterGO extends IGameObject {
    @Nonnull
    WetterComp wetterComp();
}