package de.nb.aventiure2.data.world.syscomp.movement;

import androidx.annotation.Nullable;

import de.nb.aventiure2.data.database.AvDatabase;
import de.nb.aventiure2.data.world.base.GameObjectId;

/**
 * Functionality concerned with NPC movement that might span several game objects:
 * Game Object Queries etc.
 */
public class MovementSystem {
    private final MovementDao dao;

    public MovementSystem(final AvDatabase db) {
        dao = db.movementDao();
    }

    /**
     * Ermittelt ein {@link IMovingGO}, das den SC gerade verlassen hat
     * (oder ihm gerade auf dem Weg entgegengekommen ist).
     * <p>
     * Vor jedem Aufruf muss sichergestellt sein, dass alle Änderungen an Game Objects
     * gespeichert sind!
     *
     * @param toId Richtung, in der das {@link IMovingGO} gegangen ist.
     */
    @Nullable
    public GameObjectId findWerDenSCGeradeVerlassenHat(final GameObjectId toId) {
        return dao.findWerDenSCGeradeVerlassenHat(toId);
    }
}
