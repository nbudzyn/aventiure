package de.nb.aventiure2.data.world.syscomp.spatialconnection.builder;

import java.util.List;

import de.nb.aventiure2.data.database.AvDatabase;
import de.nb.aventiure2.data.world.base.GameObjectId;
import de.nb.aventiure2.data.world.lichtverhaeltnisse.Lichtverhaeltnisse;
import de.nb.aventiure2.data.world.syscomp.memory.Known;
import de.nb.aventiure2.data.world.syscomp.spatialconnection.ISpatiallyConnectedGO;
import de.nb.aventiure2.data.world.time.Tageszeit;

import static de.nb.aventiure2.data.world.gameobjects.GameObjects.load;

/**
 * Builds all {@link SpatialConnection}s starting from
 * one {@link ISpatiallyConnectedGO}.
 */
abstract class AbstractSpatialConnectionBuilder {
    protected final AvDatabase db;
    private final GameObjectId gameObjectId;

    AbstractSpatialConnectionBuilder(
            final AvDatabase db, final GameObjectId gameObjectId) {
        this.db = db;
        this.gameObjectId = gameObjectId;
    }

    abstract List<SpatialConnection> getConnections();

    /**
     * Gibt zurück, ob bei einer Bewegung zu <code>to</code> hin
     * auch eine andere Beschreibung gezeigt werden kann als aus der entsprechenden
     * {@link SpatialConnection} - oder ob die Beschreibung verpflichtend ist und nur
     * <i>ergänzt oder umformuliert</i> werden darf.
     * <p>
     * Immer wenn beim Erzeugen einer {@link de.nb.aventiure2.german.base.AbstractDescription} ein
     * {@link de.nb.aventiure2.data.world.counter.Counter} hochgezählt wurde, wird diese
     * Description wohl verpflichtend sein. Vermutlich enthält sie essentielle Informationen,
     * die der Spieler nicht verpassen soll.
     * <p>
     * Bei den übrigen Descriptions kann auch ein ganz anderer Text gewählt werden, in der
     * Art "Du springst damit fort" oder "Du kehrst zurück".
     * <p>
     * Diese Methode muss aufgerufen werden, <i>bevor</i> die
     * {@link SpatialConnection.SCMoveDescriptionProvider#getSCMoveDescription(Known, Lichtverhaeltnisse)}-Methode
     * aufgerufen wird!
     */
    abstract boolean isAlternativeMovementDescriptionAllowed(final GameObjectId to);

    protected ISpatiallyConnectedGO getFrom() {
        return (ISpatiallyConnectedGO) load(db, gameObjectId);
    }

    protected Lichtverhaeltnisse getLichtverhaeltnisseFrom() {
        final Tageszeit tageszeit = db.dateTimeDao().now().getTageszeit();
        return Lichtverhaeltnisse.getLichtverhaeltnisse(tageszeit, getFrom().getId());
    }
}
