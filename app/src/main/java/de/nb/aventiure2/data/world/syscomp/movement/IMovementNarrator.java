package de.nb.aventiure2.data.world.syscomp.movement;

import javax.annotation.Nullable;

import de.nb.aventiure2.data.world.base.SpatialConnection;
import de.nb.aventiure2.data.world.syscomp.spatialconnection.ISpatiallyConnectedGO;
import de.nb.aventiure2.data.world.syscomp.spatialconnection.NumberOfWays;
import de.nb.aventiure2.data.world.syscomp.storingplace.ILocationGO;

/**
 * Beschreibt dem Spieler die Bewegung eines {@link IMovingGO}. Beschreibt
 * auch die Bewegung des SC in Interaktion mit diesem <code>IMovingGO</code>.
 */
public interface IMovementNarrator {
    // STORY MovementSystem fasst alle Movement-Besxhreibungen
    //  zusammen, auch bei mehreren NPCs am selben Ort. (VOR den Reactions.)

    void narrateScTrifftStehendesMovingGO(ILocationGO location);

    <FROM extends ILocationGO & ISpatiallyConnectedGO>
    void
    narrateScTrifftEnteringMovingGO(
            @Nullable ILocationGO scFrom,
            ILocationGO to,
            FROM movingGOFrom);

    void narrateScUeberholtMovingGO();

    <SC_FROM extends ILocationGO & ISpatiallyConnectedGO>
    void narrateScTrifftLeavingMovingGO(@Nullable final ILocationGO scFrom,
                                        final SC_FROM scToAndMovingGoFrom,
                                        final ILocationGO movingGOTo);

    void narrateScGehtMovingGOEntgegenUndLaesstEsHinterSich();

    /**
     * @param spatialConnection Die {@link SpatialConnection}, über die das
     *                          {@link IMovingGO} (höchstwahrscheinlich) gegangen kommt.
     *                          Könnte in sehr seltenen Fällen <code>null</code> sein
     *                          (z.B. wenn der Spieler diese {@link SpatialConnection}
     *                          nicht (mehr) erkennen kann o.Ä.
     */
    <FROM extends ILocationGO & ISpatiallyConnectedGO>
    void narrateAndDoStartsEntering(FROM from,
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
    void narrateAndDoStartsLeaving(FROM from, ILocationGO to,
                                   @Nullable SpatialConnection spatialConnection,
                                   NumberOfWays numberOfPossibleWaysToLeave);
}
