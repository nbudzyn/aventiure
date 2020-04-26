package de.nb.aventiure2.data.world.syscomp.description;

import javax.annotation.Nonnull;

import de.nb.aventiure2.data.world.base.IGameObject;

/**
 * Game Object (z.B. ein Objekt oder eine Kreatur), das eine oder mehrere Beschreibungen hat.
 */
public interface IDescribableGO extends IGameObject {
    @Nonnull
    public DescriptionComp descriptionComp();
}
