package de.nb.aventiure2.data.world.syscomp.amount;

import javax.annotation.Nonnull;

import de.nb.aventiure2.data.world.base.IGameObject;

/**
 * Interface für ein Objekt, das eine (veränderliche) Menge hat.
 * Solche Objekte werden typischerweise auch
 * {@link de.nb.aventiure2.data.world.syscomp.typed.ITypedGO}s sein.
 */
public interface IAmountableGO extends IGameObject {
    @Nonnull
    AmountComp amountComp();
}
