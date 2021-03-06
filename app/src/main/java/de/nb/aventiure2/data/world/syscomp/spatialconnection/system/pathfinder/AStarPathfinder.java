package de.nb.aventiure2.data.world.syscomp.spatialconnection.system.pathfinder;

import androidx.annotation.Nullable;

import com.google.common.collect.ImmutableList;

import de.nb.aventiure2.data.time.AvTimeSpan;
import de.nb.aventiure2.data.world.base.GameObjectId;
import de.nb.aventiure2.data.world.base.IGameObject;
import de.nb.aventiure2.data.world.base.SpatialConnection;
import de.nb.aventiure2.data.world.gameobject.*;
import de.nb.aventiure2.data.world.syscomp.spatialconnection.ISpatiallyConnectedGO;
import de.nb.aventiure2.data.world.syscomp.spatialconnection.impl.SpatialStandardStep;
import de.nb.aventiure2.data.world.syscomp.storingplace.ILocationGO;

import static de.nb.aventiure2.data.time.AvTimeSpan.NO_TIME;
import static de.nb.aventiure2.data.time.AvTimeSpan.secs;
import static de.nb.aventiure2.util.StreamUtil.*;

public class AStarPathfinder {
    private final World world;

    public AStarPathfinder(final World world) {
        this.world = world;
    }

    @Nullable
    public SpatialStandardStep findFirstStep(
            final ISpatiallyConnectedGO startGO,
            final ILocationGO targetGO) {
        @Nullable final AStarNode pathTargetNode = findPath(startGO, targetGO);

        if (pathTargetNode == null) {
            return null;
        }

        return getFirstStep(pathTargetNode);
    }

    @Nullable
    public AvTimeSpan findDistance(
            final ISpatiallyConnectedGO startGO,
            final ILocationGO targetGO) {
        @Nullable final AStarNode pathTargetNode = findPath(startGO, targetGO);

        if (pathTargetNode == null) {
            return null;
        }

        return pathTargetNode.getDist();
    }

    @Nullable
    private AStarNode findPath(
            final ISpatiallyConnectedGO startGO,
            final ILocationGO targetGO) {
        // Based on Ahlquist / Novak: Game Artificial Intelligence
        final AStarPriorityQueue priorityQueue = new AStarPriorityQueue();
        final AStarClosedList closedList = new AStarClosedList();

        final AStarNode startNode =
                new AStarNode(startGO, NO_TIME, estimateDistToTarget(startGO, targetGO));
        priorityQueue.add(startNode);

        while (true) {
            @Nullable final AStarNode currentNode = priorityQueue.removeSmallest();
            if (currentNode == null) {
                return null;
            }

            closedList.add(currentNode);

            if (currentNode.getLocation().is(targetGO)) {
                return currentNode;
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
                mapToList(
                        ((ISpatiallyConnectedGO) from).spatialConnectionComp().getConnections(),
                        AStarPathfinder::toSpatialStandardStep);
    }

    private static SpatialStandardStep toSpatialStandardStep(
            final SpatialConnection spatialConnection) {
        return new SpatialStandardStep(
                spatialConnection.getTo(),
                spatialConnection.getStandardDuration()
        );
    }

    @Nullable
    private static SpatialStandardStep getFirstStep(final AStarNode node) {
        if (!node.hasParent()) {
            return null;
        }

        assert node.getParent() != null;
        if (node.getParent().hasParent()) {
            return getFirstStep(node.getParent());
        }

        return toSpatialStandardStep(node);
    }

    private static SpatialStandardStep toSpatialStandardStep(final AStarNode secondNode) {
        return new SpatialStandardStep(
                secondNode.getLocation().getId(),
                secondNode.getDist());
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
            return NO_TIME;
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
