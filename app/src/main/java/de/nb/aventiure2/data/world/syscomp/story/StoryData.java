package de.nb.aventiure2.data.world.syscomp.story;

import androidx.annotation.NonNull;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static de.nb.aventiure2.data.world.syscomp.story.StoryData.State.AKTIV;
import static de.nb.aventiure2.data.world.syscomp.story.StoryData.State.BEENDET;
import static de.nb.aventiure2.data.world.syscomp.story.StoryData.State.NICHT_BEGONNEN;

/**
 * Mutable persistente Daten für eine {@link Story}. Änderbar - nicht als Schlüssel in Maps
 * oder in Sets verwenden!
 */
class StoryData {
    enum State {
        NICHT_BEGONNEN,
        AKTIV,
        BEENDET
    }

    @NonNull
    private final Story story;

    @NonNull
    private State state;

    @NonNull
    private final Map<IStoryNode, Integer> reachedNodes;

    StoryData(final Story story, final State state) {
        this(story, state, ImmutableMap.of());
    }

    StoryData(final Story story, final State state, final Map<IStoryNode, Integer> reachedNodes) {
        this.story = story;
        this.state = state;
        this.reachedNodes = new HashMap<>(reachedNodes);
    }

    int getNumSuccessfulNodes() {
        if (state == BEENDET) {
            return story.getNodes().size();
        }

        return reachedNodes.size();
    }

    Iterable<? extends IStoryNode> getReachableNodes() {
        final ImmutableSet.Builder<IStoryNode> res = ImmutableSet.builder();
        if (state == AKTIV) {
            for (final IStoryNode node : story.getNodes()) {
                if (isReachable(node)) {
                    res.add(node);
                }
            }
        }

        return res.build();
    }

    private boolean isReachable(final IStoryNode node) {
        return state != BEENDET && !isReached(node) && allNodesReachedRequiredFor(node) &&
                // der Node wird auch nicht automatisch freigeschaltet
                node.getExpAchievementSteps() != null;
    }

    private boolean allNodesReachedRequiredFor(final IStoryNode node) {
        for (final IStoryNode nodeRequired : node.getPreconditions()) {
            if (!isReached(nodeRequired)) {
                return false;
            }
        }

        return true;
    }

    int getLastAchievementStepCount() {
        int res = 0;
        if (state == NICHT_BEGONNEN) {
            return 0;
        }

        for (final Integer achievementStepCount : reachedNodes.values()) {
            res = Math.max(res, achievementStepCount);
        }

        return res;
    }

    boolean reachStoryNode(final IStoryNode storyNode, final int scActionStepCount) {
        if (state == BEENDET) {
            return false;
        }

        if (reachedNodes.containsKey(storyNode)) {
            // Es wird nur der Schrittzähler gespeichert, wenn der Spieler die Node das erste Mal
            // erreicht! (Ein späteres nochmaliges Erreichen ist irrelevant.)
            return false;
        }

        if (state == NICHT_BEGONNEN) {
            state = AKTIV;
        }

        reachedNodes.put(storyNode, scActionStepCount);

        if (storyNode.beendetStory()) {
            state = BEENDET;

            // STORY Wenn alle Storys abgeschlossen sind, wird das Spiel
            //  beendet. ("Du lebst noch lange glücklich und vergnügt.")
        }

        return true;
    }

    /**
     * Gibt {@code true} zurück, wenn
     * <ul>
     *     <li>dieser Story Node erreicht wurde
     *     <li>oder die Story bereits beendet ist.
     * </ul>
     */
    boolean isReachedOrStoryBeendet(final IStoryNode storyNode) {
        if (state == BEENDET) {
            return true;
        }

        return isReached(storyNode);
    }

    private boolean isReached(final IStoryNode storyNode) {
        return reachedNodes.containsKey(storyNode);
    }

    @NonNull
    State getState() {
        return state;
    }

    @NonNull
    ImmutableMap<IStoryNode, Integer> getReachedNodes() {
        return ImmutableMap.copyOf(reachedNodes);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final StoryData storyData = (StoryData) o;
        return story == storyData.story &&
                state == storyData.state &&
                reachedNodes.equals(storyData.reachedNodes);
    }

    @Override
    public int hashCode() {
        return Objects.hash(story);
    }

    @NonNull
    @Override
    public String toString() {
        return "StoryData{" +
                "story=" + story +
                ", state=" + state +
                ", reachedNodes=" + reachedNodes +
                '}';
    }
}
