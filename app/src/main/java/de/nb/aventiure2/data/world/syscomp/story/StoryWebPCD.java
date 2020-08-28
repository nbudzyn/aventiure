package de.nb.aventiure2.data.world.syscomp.story;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.Entity;
import androidx.room.Ignore;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

import java.util.HashMap;
import java.util.Map;

import de.nb.aventiure2.data.world.base.AbstractPersistentComponentData;
import de.nb.aventiure2.data.world.base.GameObjectId;
import de.nb.aventiure2.data.world.syscomp.mentalmodel.MentalModelComp;

import static de.nb.aventiure2.data.world.syscomp.story.StoryData.State.AKTIV;

/**
 * Mutable - and therefore persistent - data of the {@link MentalModelComp} component.
 */
@Entity
public class StoryWebPCD extends AbstractPersistentComponentData {
    private static final int STEPS_BETWEEN_HINTS = 10;

    @NonNull
    @Ignore
    private final Map<Story, StoryData> storyDataMap;

    /**
     * Step Count (gerechnet in Spieler-Aktionen), zu dem zuletzt eine Tipp
     * o.Ä. ausgegeben wurde
     */
    private int lastHintActionStepCount;

    @Ignore
    StoryWebPCD(final GameObjectId gameObjectId,
                final Map<Story, StoryData> storyDataMap) {
        this(gameObjectId, Integer.MIN_VALUE, storyDataMap);
    }

    StoryWebPCD(final GameObjectId gameObjectId, final int lastHintActionStepCount) {
        this(gameObjectId, lastHintActionStepCount, new HashMap<>());
    }

    @Ignore
    StoryWebPCD(final GameObjectId gameObjectId,
                final int lastHintActionStepCount,
                final Map<Story, StoryData> storyDataMap) {
        super(gameObjectId);
        this.lastHintActionStepCount = lastHintActionStepCount;
        this.storyDataMap = new HashMap<>(storyDataMap);
    }

    void reachStoryNode(final IStoryNode storyNode, final int scActionStepCount) {
        final Story story = storyNode.getStory();
        @Nullable StoryData storyData = storyDataMap.get(story);
        if (storyData == null) {
            storyData = new StoryData(story, AKTIV);
            storyDataMap.put(story, storyData);
            setChanged();
        }
        if (storyData.reachStoryNode(storyNode, scActionStepCount)) {
            setChanged();
        }
    }

    ImmutableSet<IStoryNode> getStoryNodesForHintAction(final int scActionStepCount) {
        final ImmutableSet<IStoryNode> res = getReachableStoryNodes();
        if (res.isEmpty()) {
            // Zeit sparen
            return ImmutableSet.of();
        }

        final int lastAchievementStepCount = getLastAchievementStepCount();
        final int lastHintActionStepCount = getLastHintActionStepCount();

        // Was war zuletzt:
        if (lastAchievementStepCount >= lastHintActionStepCount) {
            // Das Letzte war: Der Benutzer hat einen Story Node erreicht!
            if (scActionStepCount <
                    lastAchievementStepCount + IStoryNode.calcExpAchievementSteps(res)) {
                // Der Benutzer soll noch eine Weile lang rätseln
                return ImmutableSet.of();
            }
        } else {
            // Das Letzte war: Eine Hint Action
            if (scActionStepCount < lastHintActionStepCount + STEPS_BETWEEN_HINTS) {
                // Der Benutzer soll noch eine Weile lang rätseln
                return ImmutableSet.of();
            }
        }

        return res;
    }

    private ImmutableSet<IStoryNode> getReachableStoryNodes() {
        final ImmutableSet.Builder<IStoryNode> res = ImmutableSet.builder();

        for (final StoryData storyData : storyDataMap.values()) {
            res.addAll(storyData.getReachableNodes());
        }

        return res.build();
    }

    private int getLastAchievementStepCount() {
        int res = 0;

        for (final StoryData storyData : storyDataMap.values()) {
            res = Math.max(res, storyData.getLastAchievementStepCount());
        }

        return res;
    }

    int getLastHintActionStepCount() {
        return lastHintActionStepCount;
    }


    void setLastHintActionStepCount(final int lastHintActionStepCount) {
        if (this.lastHintActionStepCount == lastHintActionStepCount) {
            return;
        }

        this.lastHintActionStepCount = lastHintActionStepCount;

        setChanged();
    }

    @NonNull
    ImmutableMap<Story, StoryData> getStoryDataMap() {
        return ImmutableMap.copyOf(storyDataMap);
    }

    /**
     * Darf nur zur Initialisierung aufgerufen werden, nicht zur Änderung!
     */
    void initStoryDataMap(final Map<Story, StoryData> map) {
        Preconditions.checkState(storyDataMap.isEmpty(), "Already initialized!");

        // Kein setChanged() !
        storyDataMap.putAll(map);
    }
}
