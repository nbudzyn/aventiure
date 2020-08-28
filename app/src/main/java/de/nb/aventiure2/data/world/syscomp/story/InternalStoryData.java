package de.nb.aventiure2.data.world.syscomp.story;

import androidx.annotation.NonNull;
import androidx.room.Entity;

import de.nb.aventiure2.data.world.base.GameObjectId;

@Entity(primaryKeys = {"storyWeb", "story"})
public class InternalStoryData {
    @NonNull
    private GameObjectId storyWeb;

    @NonNull
    private Story story;

    @NonNull
    private StoryData.State state;

    public InternalStoryData(final GameObjectId storyWeb, final Story story,
                             final StoryData.State state) {
        this.storyWeb = storyWeb;
        this.story = story;
        this.state = state;
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
    public StoryData.State getState() {
        return state;
    }

    public void setState(final StoryData.State state) {
        this.state = state;
    }
}
