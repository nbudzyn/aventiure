package de.nb.aventiure2.data.world.syscomp.movement;

import de.nb.aventiure2.data.world.syscomp.spatialconnection.ISpatiallyConnectedGO;
import de.nb.aventiure2.data.world.syscomp.storingplace.ILocationGO;
import de.nb.aventiure2.data.world.time.AvTimeSpan;

public interface IMovementNarrator extends ILeavingStartedNarrator {
    <FROM extends ILocationGO & ISpatiallyConnectedGO>
    AvTimeSpan narrateAndDoMovementAsExperiencedBySC_StartsEntering(FROM from, ILocationGO to);
}
