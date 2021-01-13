package de.nb.aventiure2.data.world.syscomp.spatialconnection.system.pathfinder;

import androidx.annotation.Nullable;

import java.util.HashMap;

import de.nb.aventiure2.data.time.AvTimeSpan;
import de.nb.aventiure2.data.world.base.GameObjectId;

/**
 * Priority queue for the A* algorithm.
 * <p>
 * We cannot use a JDK {@link java.util.PriorityQueue}, because the totalCost might be changing
 * after insertion, but the {@link java.util.PriorityQueue} does not re-sort.
 */
class AStarPriorityQueue {
    private final HashMap<GameObjectId, AStarNode> map = new HashMap<>();

    void add(final AStarNode node) {
        map.put(node.getLocation().getId(), node);
    }

    @Nullable
    AStarNode get(final GameObjectId locationId) {
        return map.get(locationId);
    }

    @Nullable
    AStarNode removeSmallest() {
        AStarNode smallestNode = null;
        AvTimeSpan smallestTotalCost = AvTimeSpan.NO_TIME;

        for (final AStarNode node : map.values()) {
            if (smallestNode == null ||
                    smallestTotalCost.longerThan(node.getTotalCost())) {
                smallestNode = node;
                smallestTotalCost = node.getTotalCost();
            }
        }

        if (smallestNode != null) {
            map.remove(smallestNode.getLocation().getId());
        }

        return smallestNode;
    }
}
