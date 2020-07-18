package de.nb.aventiure2.data.world.syscomp.spatialconnection.impl;

import androidx.annotation.Nullable;

import java.util.HashMap;

import de.nb.aventiure2.data.world.base.GameObjectId;
import de.nb.aventiure2.data.world.time.AvTimeSpan;

import static de.nb.aventiure2.data.world.time.AvTimeSpan.noTime;

/**
 * Priority queue for the A* algorithm.
 * <p>
 * We cannot use a JDK {@link java.util.PriorityQueue}, because the totalCost might be changing
 * after insertion, but the {@link java.util.PriorityQueue} does not re-sort.
 */
public class AStarPriorityQueue {
    private final HashMap<GameObjectId, AStarNode> map = new HashMap<>();

    public void add(final AStarNode node) {
        map.put(node.getLocation().getId(), node);
    }

    public boolean contains(final GameObjectId locationId) {
        return map.containsKey(locationId);
    }

    @Nullable
    public AStarNode get(final GameObjectId locationId) {
        return map.get(locationId);
    }

    @Nullable
    public AStarNode removeSmallest() {
        AStarNode smallestNode = null;
        AvTimeSpan smallestTotalCost = noTime();

        for (final AStarNode node : map.values()) {
            if (smallestNode == null ||
                    smallestTotalCost.longerThan(node.getTotalCost())) {
                smallestNode = node;
                smallestTotalCost = node.getTotalCost();
            }
        }

        map.remove(smallestNode.getLocation().getId());

        return smallestNode;
    }
}
