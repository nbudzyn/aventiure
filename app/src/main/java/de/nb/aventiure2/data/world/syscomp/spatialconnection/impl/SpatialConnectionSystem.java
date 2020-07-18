package de.nb.aventiure2.data.world.syscomp.spatialconnection.impl;

import androidx.annotation.Nullable;

import com.google.common.collect.ImmutableList;

import org.jetbrains.annotations.Contract;

import de.nb.aventiure2.data.world.base.GameObjectId;
import de.nb.aventiure2.data.world.base.IGameObject;
import de.nb.aventiure2.data.world.gameobject.World;
import de.nb.aventiure2.data.world.syscomp.spatialconnection.ISpatiallyConnectedGO;
import de.nb.aventiure2.data.world.syscomp.storingplace.ILocationGO;
import de.nb.aventiure2.data.world.time.AvTimeSpan;

import static de.nb.aventiure2.data.world.time.AvTimeSpan.mins;
import static de.nb.aventiure2.data.world.time.AvTimeSpan.noTime;
import static de.nb.aventiure2.data.world.time.AvTimeSpan.secs;

/**
 * Functionality concerned with spatial connections that might span several game objects.
 */
public class SpatialConnectionSystem {
    private final World world;

    public SpatialConnectionSystem(final World world) {
        this.world = world;
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

        return findFirstStepUsingAStar(start, target);
    }

    @Nullable
    private SpatialStandardStep findFirstStepUsingAStar(
            final ISpatiallyConnectedGO startGO,
            final ILocationGO targetGO) {
        // Based on Ahlquist / Novak: Game Artificial Intelligence
        final AStarPriorityQueue priorityQueue = new AStarPriorityQueue();
        final AStarClosedList closedList = new AStarClosedList();

        final AStarNode startNode =
                new AStarNode(startGO, noTime(), estimateDistToTarget(startGO, targetGO));
        priorityQueue.add(startNode);

        while (true) {
            @Nullable final AStarNode currentNode = priorityQueue.removeSmallest();
            if (currentNode == null) {
                return null;
            }

            closedList.add(currentNode);

            if (currentNode.getLocation().is(targetGO)) {
                return getFirstStep(currentNode);
            }

            for (final SpatialStandardStep nextStep : findNextSteps(currentNode)) {
                final AvTimeSpan curDistance = nextStep.getStandardDuration();

                @Nullable
                AStarNode nextNode = priorityQueue.get(nextStep.getTo());
                if (nextNode == null) {
                    nextNode = closedList.get(nextStep.getTo());
                }

                if (nextNode == null) {
                    nextNode = new AStarNode(
                            world.load(nextStep.getTo()),
                            curDistance.plus(currentNode.getDist()),
                            estimateDistToTarget(nextStep.getTo(), targetGO),
                            currentNode);
                    priorityQueue.add(nextNode);
                } else {
                    if (curDistance.plus(currentNode.getDist()).shorterThan(
                            nextNode.getDist())) {
                        nextNode.setDist(curDistance.plus(currentNode.getDist()));
                        nextNode.setParent(currentNode);
                        closedList.remove(nextNode);
                        priorityQueue.add(nextNode);
                    }
                }
            }
        }

    }

    private static ImmutableList<SpatialStandardStep> findNextSteps(final AStarNode node) {
        return findNextSteps(node.getLocation());
    }

    private static ImmutableList<SpatialStandardStep> findNextSteps(final IGameObject from) {
        if (!(from instanceof ISpatiallyConnectedGO)) {
            return ImmutableList.of();
        }

        return
                ((ISpatiallyConnectedGO) from).spatialConnectionComp().getConnections().stream()
                        .map(SpatialConnectionSystem::toSpatialStandardStep)
                        .collect(ImmutableList.toImmutableList());
    }

    private static SpatialStandardStep toSpatialStandardStep(
            final SpatialConnection spatialConnection) {
        return new SpatialStandardStep(
                spatialConnection.getTo(),
                // TODO use a standard movement duration based on the SpatialConnections
                mins(10)
        );
    }

    private static SpatialStandardStep getFirstStep(final AStarNode node) {
        if (node.getParent().hasParent()) {
            return getFirstStep(node.getParent());
        }

        return toSpatialStandardStep(node);
    }

    private static SpatialStandardStep toSpatialStandardStep(final AStarNode secondNode) {
        return new SpatialStandardStep(
                secondNode.getLocation().getId(),
                secondNode.getTotalCost());
    }

    private static AvTimeSpan estimateDistToTarget(
            final ISpatiallyConnectedGO startGO,
            final ILocationGO targetGO) {
        return estimateDistToTarget(startGO.getId(), targetGO);
    }

    private static AvTimeSpan estimateDistToTarget(
            final GameObjectId startGOId,
            final ILocationGO targetGO) {
        if (targetGO.is(startGOId)) {
            return noTime();
        }

        return
                // "estimates for A* must [be] less than or equal to the
                // actual distance of the shortest path"
                secs(30)
                        .times(
                                Math.abs(
                                        startGOId.toLong() - targetGO.getId().toLong()
                                ));
    }
}
