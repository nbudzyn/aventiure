package de.nb.aventiure2.data.world.syscomp.movement;

import javax.annotation.Nullable;

import de.nb.aventiure2.data.world.base.SpatialConnection;
import de.nb.aventiure2.data.world.syscomp.spatialconnection.ISpatiallyConnectedGO;
import de.nb.aventiure2.data.world.syscomp.spatialconnection.NumberOfWays;
import de.nb.aventiure2.data.world.syscomp.storingplace.ILocationGO;
import de.nb.aventiure2.data.world.time.AvTimeSpan;

/**
 * Beschreibt dem Spieler die Bewegung eines {@link IMovingGO}. Beschreibt
 * auch die Bewegung des SC in Interaktion mit diesem <code>IMovingGO</code>.
 */
public interface IMovementNarrator {
    AvTimeSpan narrateScTrifftStehendesMovingGO(ILocationGO location);

    <FROM extends ILocationGO & ISpatiallyConnectedGO> AvTimeSpan
    narrateScTrifftEnteringMovingGO(
            @Nullable ILocationGO scFrom,
            ILocationGO to,
            FROM movingGOFrom);

    AvTimeSpan narrateScUeberholtMovingGO();

    <SC_FROM extends ILocationGO & ISpatiallyConnectedGO>
    AvTimeSpan narrateScTrifftLeavingMovingGO(@Nullable final ILocationGO scFrom,
                                              final SC_FROM scToAndMovingGoFrom,
                                              final ILocationGO movingGOTo);

    AvTimeSpan narrateScGehtMovingGOEntgegenUndLaesstEsHinterSich();

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

    /**
     * @param spatialConnection Die {@link SpatialConnection}, über die das
     *                          {@link IMovingGO} (höchstwahrscheinlich) weggeht.
     *                          Könnte in sehr seltenen Fällen <code>null</code> sein
     *                          (z.B. wenn der Spieler diese {@link SpatialConnection}
     *                          nicht (mehr) erkennen kann o.Ä.
     */
    <FROM extends ILocationGO & ISpatiallyConnectedGO>
    AvTimeSpan narrateAndDoStartsLeaving(FROM from, ILocationGO to,
                                         @Nullable SpatialConnection spatialConnection,
                                         NumberOfWays numberOfPossibleWaysToLeave);
}
