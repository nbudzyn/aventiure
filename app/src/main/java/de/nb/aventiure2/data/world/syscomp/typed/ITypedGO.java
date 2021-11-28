package de.nb.aventiure2.data.world.syscomp.typed;

import javax.annotation.Nonnull;

import de.nb.aventiure2.data.world.base.IGameObject;

/**
 * Interface für ein Objekt, das einen Typ hat. Es gibt nur eine feste Menge von Typen.
 * Der Typ erlaubt es, alle Objekte eines Typs zu finden. Typen sind hauptsächlich
 * für Objekte relevant, die on-the-fly erzeugt werden - wo es also mehrere
 * Objekte <i>desselben Typs</i> gibt.
 * </p>
 * Der Typ eines Objekts ist unveränderlich.
 */
public interface ITypedGO extends IGameObject {
    @Nonnull
    TypeComp typeComp();
}
