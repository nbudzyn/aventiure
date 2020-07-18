package de.nb.aventiure2.data.world.syscomp.spatialconnection.impl;

import androidx.annotation.Nullable;

import java.util.Objects;

import de.nb.aventiure2.data.world.base.IGameObject;
import de.nb.aventiure2.data.world.time.AvTimeSpan;

/**
 * Node datastructure for the A* pathfinding algorithm
 */
class AStarNode {
    /**
     * The location (room) this node represents
     */
    private final IGameObject location;

    /**
     * Distance from the start (measured in seconds for a standard movement)
     */
    private AvTimeSpan dist;

    /**
     * Extimated distance (measured in seconds for a standard movement) for the node
     * the the target
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

    public void setDist(final AvTimeSpan dist) {
        this.dist = dist;
        totalCost = dist.plus(estimate);
    }

    public AvTimeSpan getDist() {
        return dist;
    }

    AvTimeSpan getTotalCost() {
        return totalCost;
    }

    public void setParent(@Nullable final AStarNode parent) {
        this.parent = parent;
    }

    public boolean hasParent() {
        return getParent() != null;
    }

    @Nullable
    public AStarNode getParent() {
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
