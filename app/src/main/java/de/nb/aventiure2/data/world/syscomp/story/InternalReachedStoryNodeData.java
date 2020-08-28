package de.nb.aventiure2.data.world.syscomp.story;

import androidx.annotation.NonNull;
import androidx.room.Entity;

import de.nb.aventiure2.data.world.base.GameObjectId;

@Entity(primaryKeys = {"storyWeb", "story", "storyNode"})
public class InternalReachedStoryNodeData {
    @NonNull
    private GameObjectId storyWeb;

    @NonNull
    private Story story;

    @NonNull
    private String storyNode;

    private int stepReached;

    public InternalReachedStoryNodeData(final GameObjectId storyWeb,
                                        final Story story, final String storyNode,
                                        final int stepReached) {
        this.storyWeb = storyWeb;
        this.story = story;
        this.storyNode = storyNode;
        this.stepReached = stepReached;
    }

    @NonNull
    public GameObjectId getStoryWeb() {
        return storyWeb;
    }

    public void setStoryWeb(final GameObjectId storyWeb) {
        this.storyWeb = storyWeb;
    }

    @NonNull
    public Story getStory() {
        return story;
    }

    public void setStory(final Story story) {
        this.story = story;
    }

    @NonNull
    public String getStoryNode() {
        return storyNode;
    }

    public void setStoryNode(final String storyNode) {
        this.storyNode = storyNode;
    }

    public int getStepReached() {
        return stepReached;
    }

    public void setStepReached(final int stepReached) {
        this.stepReached = stepReached;
    }
}
