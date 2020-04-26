package de.nb.aventiure2.data.world.syscomp.location;

import javax.annotation.Nonnull;

import de.nb.aventiure2.data.world.base.IGameObject;

/**
 * Game Object, das sich in der Welt an einem Ort befinden kann.
 */
public interface ILocatableGO extends IGameObject {
    @Nonnull
    public LocationComp locationComp();
}