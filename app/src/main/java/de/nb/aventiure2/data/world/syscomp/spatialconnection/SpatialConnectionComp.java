package de.nb.aventiure2.data.world.syscomp.spatialconnection;

import de.nb.aventiure2.data.world.base.AbstractStatelessComponent;
import de.nb.aventiure2.data.world.base.GameObject;
import de.nb.aventiure2.data.world.base.GameObjectId;

/**
 * Component für ein {@link GameObject}: Das Game Object (z.B. ein Raum) ist räumlich mit
 * anderen Game Objects (z.B. anderen Räumen) verbunden ist, so dass sich der
 * Spielercharakter oder jemand anderes entlang diesen Verbindungen bewegen kann.
 */
public class SpatialConnectionComp extends AbstractStatelessComponent {
    public SpatialConnectionComp(final GameObjectId id) {
        super(id);
    }
}
