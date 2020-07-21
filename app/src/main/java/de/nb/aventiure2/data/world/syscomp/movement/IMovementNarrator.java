package de.nb.aventiure2.data.world.syscomp.movement;

import de.nb.aventiure2.data.world.syscomp.spatialconnection.ISpatiallyConnectedGO;
import de.nb.aventiure2.data.world.syscomp.spatialconnection.NumberOfWays;
import de.nb.aventiure2.data.world.syscomp.spatialconnection.impl.SpatialConnection;
import de.nb.aventiure2.data.world.syscomp.storingplace.ILocationGO;
import de.nb.aventiure2.data.world.time.AvTimeSpan;

/**
 * Beschreibt dem Spieler die Bewegung eines {@link IMovingGO}
 */
public interface IMovementNarrator extends ILeavingStartedNarrator {
    /**
     * @param spatialConnection Die {@link SpatialConnection}, über die das
     *                          {@link IMovingGO} (höchstwahrscheinlich) gegangen kommt.
     *                          Könnte in sehr seltenen Fällen <code>null</code> sein
     *                          (z.B. wenn der Spieler diese {@link SpatialConnection}
     *                          nicht (mehr) erkennen kann o.Ä.
     */
    <FROM extends ILocationGO & ISpatiallyConnectedGO>
    AvTimeSpan narrateAndDoStartsEntering(FROM from,
                                          ILocationGO to,
                                          SpatialConnection spatialConnection,
                                          NumberOfWays numberOfWaysIn);
}
