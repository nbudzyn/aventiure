package de.nb.aventiure2.data.world.syscomp.spatialconnection.system;

import androidx.annotation.Nullable;

import org.jetbrains.annotations.Contract;

import de.nb.aventiure2.data.time.AvTimeSpan;
import de.nb.aventiure2.data.world.gameobject.*;
import de.nb.aventiure2.data.world.syscomp.spatialconnection.ISpatiallyConnectedGO;
import de.nb.aventiure2.data.world.syscomp.spatialconnection.impl.SpatialStandardStep;
import de.nb.aventiure2.data.world.syscomp.spatialconnection.system.pathfinder.AStarPathfinder;
import de.nb.aventiure2.data.world.syscomp.storingplace.ILocationGO;

import static de.nb.aventiure2.data.time.AvTimeSpan.NO_TIME;

/**
 * Functionality concerned with spatial connections that might delta several game objects.
 */
public class SpatialConnectionSystem {
    private final AStarPathfinder pathfinder;

    public SpatialConnectionSystem(final World world) {
        pathfinder = new AStarPathfinder(world);
    }

    @Contract("null, _ -> null; !null, null -> null")
    @Nullable
    public SpatialStandardStep findFirstStep(
            @Nullable final ISpatiallyConnectedGO start,
            @Nullable final ILocationGO target) {
        if (start == null) {
            return null;
        }

        if (target == null) {
            return null;
        }

        if (start.is(target)) {
            return null;
        }

        return pathfinder.findFirstStep(start, target);
    }

    @Nullable
    public AvTimeSpan findDistance(@Nullable final ISpatiallyConnectedGO start,
                                   @Nullable final ILocationGO target) {
        if (start == null) {
            return null;
        }

        if (target == null) {
            return null;
        }

        if (start.is(target)) {
            return NO_TIME;
        }

        return pathfinder.findDistance(start, target);
    }
}
