package de.nb.aventiure2.data.world.syscomp.spatialconnection.system.pathfinder;

import androidx.annotation.Nullable;

import java.util.Objects;

import de.nb.aventiure2.data.time.AvTimeSpan;
import de.nb.aventiure2.data.world.base.IGameObject;

/**
 * Node datastructure for the A* pathfinding algorithm
 */
class AStarNode {
    /**
     * The location (room) this node represents
     */
    private final IGameObject location;

    /**
     * Distance from the start (measured in the time for a standard movement)
     */
    private AvTimeSpan dist;

    /**
     * Estimated distance (measured in the time for a standard movement) for the node
     * to the target
     */
    private final AvTimeSpan estimate;

    private AvTimeSpan totalCost;

    @Nullable
    private AStarNode parent;

    AStarNode(final IGameObject location,
              final AvTimeSpan dist,
              final AvTimeSpan estimateToTarget) {
        this(location, dist, estimateToTarget, null);
    }

    AStarNode(final IGameObject location,
              final AvTimeSpan dist,
              final AvTimeSpan estimateToTarget,
              @Nullable final AStarNode parent) {
        this.location = location;
        estimate = estimateToTarget;
        this.parent = parent;
        setDist(dist);
    }

    public IGameObject getLocation() {
        return location;
    }

    void setDist(final AvTimeSpan dist) {
        this.dist = dist;
        totalCost = dist.plus(estimate);
    }

    AvTimeSpan getDist() {
        return dist;
    }

    AvTimeSpan getTotalCost() {
        return totalCost;
    }

    void setParent(@Nullable final AStarNode parent) {
        this.parent = parent;
    }

    boolean hasParent() {
        return getParent() != null;
    }

    @Nullable
    AStarNode getParent() {
        return parent;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final AStarNode aStarNode = (AStarNode) o;
        return Objects.equals(location, aStarNode.location) &&
                // FIXME Prüfen: Hier sind einige Felder nicht final - könnte
                //  probleme mit Sets o.Ä. ergeben...
                Objects.equals(dist, aStarNode.dist) &&
                Objects.equals(estimate, aStarNode.estimate) &&
                Objects.equals(totalCost, aStarNode.totalCost) &&
                Objects.equals(parent, aStarNode.parent);
    }

    @Override
    public int hashCode() {
        // Only values that do not change!
        return Objects.hash(location);
    }
}
