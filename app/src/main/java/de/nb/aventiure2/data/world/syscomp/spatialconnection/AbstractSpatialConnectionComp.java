package de.nb.aventiure2.data.world.syscomp.spatialconnection;

import androidx.annotation.NonNull;

import java.util.List;

import javax.annotation.ParametersAreNonnullByDefault;

import de.nb.aventiure2.data.database.AvDatabase;
import de.nb.aventiure2.data.world.base.AbstractStatelessComponent;
import de.nb.aventiure2.data.world.base.GameObject;
import de.nb.aventiure2.data.world.base.GameObjectId;
import de.nb.aventiure2.data.world.gameobjects.GameObjects;
import de.nb.aventiure2.data.world.lichtverhaeltnisse.Lichtverhaeltnisse;
import de.nb.aventiure2.data.world.syscomp.memory.Known;
import de.nb.aventiure2.data.world.syscomp.spatialconnection.impl.SpatialConnection;
import de.nb.aventiure2.data.world.time.Tageszeit;

/**
 * Component für ein {@link GameObject}: Das Game Object (z.B. ein Raum) ist räumlich mit
 * anderen Game Objects (z.B. anderen Räumen) verbunden ist, so dass sich der
 * Spielercharakter oder jemand anderes entlang diesen Verbindungen bewegen kann.
 */
@ParametersAreNonnullByDefault
public abstract class AbstractSpatialConnectionComp extends AbstractStatelessComponent {
    protected final AvDatabase db;

    public AbstractSpatialConnectionComp(final GameObjectId id,
                                         final AvDatabase db) {
        super(id);
        this.db = db;
    }

    @NonNull
    public abstract List<SpatialConnection> getConnections();

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
    public abstract boolean isAlternativeMovementDescriptionAllowed(final GameObjectId to,
                                                                    Known newRoomKnown,
                                                                    Lichtverhaeltnisse lichtverhaeltnisseInNewRoom);

    @NonNull
    protected ISpatiallyConnectedGO getFrom() {
        return (ISpatiallyConnectedGO) GameObjects.load(db, getGameObjectId());
    }

    @NonNull
    protected Lichtverhaeltnisse getLichtverhaeltnisseFrom() {
        final Tageszeit tageszeit = db.dateTimeDao().now().getTageszeit();
        return Lichtverhaeltnisse.getLichtverhaeltnisse(tageszeit, getFrom().getId());
    }
}