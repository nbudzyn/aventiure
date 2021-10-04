package de.nb.aventiure2.data.world.syscomp.typed;

import androidx.annotation.NonNull;

import de.nb.aventiure2.data.database.AvDatabase;
import de.nb.aventiure2.data.world.base.AbstractStatefulComponent;
import de.nb.aventiure2.data.world.base.GameObjectId;

/**
 * Komponente für den Typ eines Objekts. Es gibt nur eine feste Menge von Typen.
 * Der Typ erlaubt es, alle Objekte eines Typs zu finden. Typen sind hauptsächlich
 * für Objekte relevant, die on-the-fly erzeugt werden - wo es also mehrere
 * Objekte <i>desselben Typs</i> gibt.
 * </p>
 * Der Typ eines Objekts ist unveränderlich.
 */
public class TypeComp extends AbstractStatefulComponent<TypePCD> {
    private final GameObjectType type;

    public TypeComp(final GameObjectId gameObjectId,
                    final AvDatabase db,
                    final GameObjectType type) {
        super(gameObjectId, db.typeDao());
        this.type = type;
    }

    @Override
    @NonNull
    protected TypePCD createInitialState() {
        return new TypePCD(getGameObjectId(), type);
    }

    public GameObjectType getType() {
        return type;
    }
}
