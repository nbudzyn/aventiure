package de.nb.aventiure2.playeraction.action;

import androidx.annotation.NonNull;

import de.nb.aventiure2.data.database.AvDatabase;
import de.nb.aventiure2.data.storystate.StoryState;
import de.nb.aventiure2.data.storystate.StoryStateBuilder;
import de.nb.aventiure2.data.world.entity.object.AvObject;
import de.nb.aventiure2.data.world.entity.object.ObjectData;
import de.nb.aventiure2.playeraction.AbstractPlayerAction;

/**
 * An action the player could choose that deals with an object.
 */
abstract class AbstractObjectAction extends AbstractPlayerAction {
    @NonNull
    private final ObjectData objectData;

    public AbstractObjectAction(final AvDatabase db,
                                final StoryState initialStoryState,
                                @NonNull final ObjectData objectData) {
        super(db, initialStoryState);
        this.objectData = objectData;
    }

    public AvObject getObject() {
        return objectData.getObject();
    }

    public ObjectData getObjectData() {
        return objectData;
    }

    @Override
    protected StoryStateBuilder t(@NonNull final StoryState.StructuralElement startsNew,
                                  @NonNull final String text) {
        return super.t(startsNew, text)
                .letztesObject(objectData.getObject());
    }
}
