package de.nb.aventiure2.data.world.syscomp.typed;

import androidx.room.Entity;

import javax.annotation.Nonnull;

import de.nb.aventiure2.data.world.base.AbstractPersistentComponentData;
import de.nb.aventiure2.data.world.base.GameObjectId;

/**
 * PCD ({@link de.nb.aventiure2.data.world.base.AbstractPersistentComponentData})
 * für den Typ eines Objekts. Der Typ eines Objekts ist unveränderlich, er
 * erlaubt es, alle Objekte eines Typs zu finden. Typen sind hauptsächlich
 * für Objekte relevant, die on-the-fly erzeugt werden - wo es also mehrere
 * Objekte <i>desselben Typs</i> gibt.
 */
@Entity
public
class TypePCD extends AbstractPersistentComponentData {
    @Nonnull
    private final GameObjectType type;

    TypePCD(@Nonnull final GameObjectId gameObjectId,
            @Nonnull final GameObjectType type) {
        super(gameObjectId);
        this.type = type;
    }

    @Nonnull
    public GameObjectType getType() {
        return type;
    }

    // Der Typ ist unveränderlich. Deshalb haben wir keinen Setter und
    // brauchen auch nie setChanged() aufzurufen.
}
