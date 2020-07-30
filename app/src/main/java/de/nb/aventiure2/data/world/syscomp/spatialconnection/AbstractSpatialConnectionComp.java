package de.nb.aventiure2.data.world.syscomp.spatialconnection;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

import javax.annotation.ParametersAreNonnullByDefault;

import de.nb.aventiure2.data.database.AvDatabase;
import de.nb.aventiure2.data.world.base.AbstractStatelessComponent;
import de.nb.aventiure2.data.world.base.GameObject;
import de.nb.aventiure2.data.world.base.GameObjectId;
import de.nb.aventiure2.data.world.base.Known;
import de.nb.aventiure2.data.world.base.Lichtverhaeltnisse;
import de.nb.aventiure2.data.world.base.SpatialConnection;
import de.nb.aventiure2.data.world.base.SpatialConnectionData;
import de.nb.aventiure2.data.world.gameobject.World;

/**
 * Component für ein {@link GameObject}: Das Game Object (z.B. ein Raum) ist räumlich mit
 * anderen Game Objects (z.B. anderen Räumen) verbunden ist, so dass sich der
 * Spielercharakter oder jemand anderes entlang diesen Verbindungen bewegen kann.
 */
@ParametersAreNonnullByDefault
public abstract class AbstractSpatialConnectionComp extends AbstractStatelessComponent {
    protected final AvDatabase db;
    protected final World world;

    public AbstractSpatialConnectionComp(final GameObjectId id,
                                         final AvDatabase db,
                                         final World world) {
        super(id);
        this.db = db;
        this.world = world;
    }

    @Nullable
    public SpatialConnection getConnection(final GameObjectId to) {
        final List<SpatialConnection> connections = getConnections();
        return getConnection(connections, to);
    }

    public NumberOfWays getNumberOfWaysOut() {
        return NumberOfWays.get(getConnections().size());
    }

    @Nullable
    private static SpatialConnection getConnection(
            final List<SpatialConnection> connections,
            final GameObjectId to) {
        for (final SpatialConnection spatialConnection : connections) {
            if (spatialConnection.getTo().equals(to)) {
                return spatialConnection;
            }
        }

        return null;
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
     * {@link SpatialConnectionData.SCMoveDescriptionProvider#getSCMoveDescription(Known, Lichtverhaeltnisse)}-Methode
     * aufgerufen wird!
     */
    public abstract boolean isAlternativeMovementDescriptionAllowed(final GameObjectId to,
                                                                    Known newLocationKnown,
                                                                    Lichtverhaeltnisse lichtverhaeltnisseInNewLocation);

}
