package de.nb.aventiure2.data.world.spatialconnection;

import javax.annotation.Nonnull;

import de.nb.aventiure2.data.world.base.IGameObject;

/**
 * Game Object (z.B. ein Raum) das räumlich mit anderen Game Objects (z.B. anderen Räumen)
 * verbunden ist, so dass sich der Spielercharakter oder jemand anderes entlang diesen
 * Verbindungen bewegen kann.
 */
public interface ISpatiallyConnectedGO extends IGameObject {
    @Nonnull
    public SpatialConnectionComp spatialConnectionComp();
}
