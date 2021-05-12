package de.nb.aventiure2.data.world.syscomp.movement;

import java.util.Collection;

import javax.annotation.Nullable;

import de.nb.aventiure2.data.world.base.SpatialConnection;
import de.nb.aventiure2.data.world.syscomp.spatialconnection.ISpatiallyConnectedGO;
import de.nb.aventiure2.data.world.syscomp.spatialconnection.NumberOfWays;
import de.nb.aventiure2.data.world.syscomp.storingplace.ILocationGO;
import de.nb.aventiure2.german.description.TimedDescription;

/**
 * Beschreibt dem Spieler die Bewegung eines {@link IMovingGO}. Beschreibt
 * auch die Bewegung des SC in Interaktion mit diesem <code>IMovingGO</code>.
 */
interface IMovementNarrator {
    // IDEA MovementSystem fasst alle Movement-Beschreibungen
    //     FIXME internes Logging (in die Datenbank?! in Datenbank und evtl. Console?!) mit der
    //      Möglichkeit dieses Log - inkl. dem erzeugten Story Text - jederzeit
    //      zu sharen, d.h. zb per E-Mail zu versenden
    //     FIXME Loggen, wie lange der SC für die einzelnen Story-Steps gebraucht
    //      hat - inkl. der Differenz zur vorgesehenen Schrittzahl.
    //      Klug einberechnen, dass mehrere Story Steps gleichzeit offen sind!
    //      (Also addieren o.Ä.) zusammen, auch bei mehreren NPCs am selben Ort. (VOR den
    //      Reactions.)

    void narrateScTrifftStehendesMovingGO(ILocationGO location);

    <FROM extends ILocationGO & ISpatiallyConnectedGO>
    void
    narrateScTrifftMovingGOImDazwischen(
            @Nullable ILocationGO scFrom,
            ILocationGO to,
            FROM movingGOFrom);

    void narrateScUeberholtMovingGO(@Nullable SpatialConnection conn);

    void narrateScGehtMovingGOEntgegenUndLaesstEsHinterSich();

    /**
     * @param spatialConnection Die {@link SpatialConnection}, über die das
     *                          {@link IMovingGO} (höchstwahrscheinlich) gegangen kommt.
     *                          Könnte in sehr seltenen Fällen <code>null</code> sein
     *                          (z.B. wenn der Spieler diese {@link SpatialConnection}
     *                          nicht (mehr) erkennen kann o.Ä.
     */
    <FROM extends ILocationGO & ISpatiallyConnectedGO>
    void narrateAndDoEnters(FROM from,
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
    void narrateAndDoLeaves(FROM from, ILocationGO to,
                            @Nullable SpatialConnection spatialConnection,
                            NumberOfWays numberOfPossibleWaysToLeave);

    /**
     * Beschreibt, wie der SC diesem {@link IMovingGO} folgt.
     *
     * @param normalTimedDescriptions Die alternativen normalen Beschreibungen, die eigentlich
     *                                für diese
     *                                Bewegung vorgesehen wären
     */
    void narrateScFolgtMovingGO(final Collection<TimedDescription<?>> normalTimedDescriptions);
}
