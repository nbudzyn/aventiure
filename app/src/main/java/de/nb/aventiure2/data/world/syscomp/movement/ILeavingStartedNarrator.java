package de.nb.aventiure2.data.world.syscomp.movement;

import de.nb.aventiure2.data.world.syscomp.spatialconnection.ISpatiallyConnectedGO;
import de.nb.aventiure2.data.world.syscomp.storingplace.ILocationGO;
import de.nb.aventiure2.data.world.time.AvTimeSpan;

public interface ILeavingStartedNarrator {
    <FROM extends ILocationGO & ISpatiallyConnectedGO>
    AvTimeSpan narrateAndDoMovementAsExperiencedBySCStartsLeaving(FROM from, ILocationGO to);
}
