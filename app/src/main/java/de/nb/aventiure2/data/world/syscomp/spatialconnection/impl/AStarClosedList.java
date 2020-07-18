package de.nb.aventiure2.data.world.syscomp.spatialconnection.impl;

import androidx.annotation.Nullable;

import java.util.HashMap;

import de.nb.aventiure2.data.world.base.GameObjectId;

public class AStarClosedList {
    final HashMap<GameObjectId, AStarNode> map = new HashMap<>();

    AStarClosedList() {
    }

    public void add(final AStarNode node) {
        map.put(node.getLocation().getId(), node);
    }

    @Nullable
    public AStarNode get(final GameObjectId locationId) {
        return map.get(locationId);
    }

    public void remove(final AStarNode nextNode) {
        map.remove(nextNode.getLocation().getId());
    }
}
