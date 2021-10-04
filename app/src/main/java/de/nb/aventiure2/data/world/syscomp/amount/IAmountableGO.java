package de.nb.aventiure2.data.world.syscomp.amount;

import javax.annotation.Nonnull;

/**
 * Interface für ein Objekt, das eine (veränderliche) Menge hat.
 * Solche Objekte werden typischerweise auch
 * {@link de.nb.aventiure2.data.world.syscomp.typed.ITypedGO}s sein.
 */
public interface IAmountableGO {
    @Nonnull
    AmountComp amountComp();
}
