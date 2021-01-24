package de.nb.aventiure2.data.world.syscomp.alive;

import com.google.common.collect.ImmutableList;

import java.util.List;
import java.util.function.Predicate;

import de.nb.aventiure2.data.world.base.IGameObject;

import static com.google.common.collect.ImmutableList.toImmutableList;

/**
 * Functionality concerned with Location that might span several game objects:
 * Game Object Queries etc.
 */
public class AliveSystem {
    public AliveSystem() {
    }

    public static <GO extends IGameObject> ImmutableList<GO>
    filterNoLivingBeing(final List<GO> gameObjects) {
        return gameObjects.stream()
                .filter(((Predicate<GO>) ILivingBeingGO.class::isInstance).negate())
                .collect(toImmutableList());
    }
}
