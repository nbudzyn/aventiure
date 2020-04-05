package de.nb.aventiure2.scaction.action;

import androidx.annotation.NonNull;

import de.nb.aventiure2.data.database.AvDatabase;
import de.nb.aventiure2.data.storystate.StoryState;
import de.nb.aventiure2.data.storystate.StoryStateBuilder;
import de.nb.aventiure2.data.world.entity.base.AbstractEntityData;
import de.nb.aventiure2.data.world.entity.object.ObjectData;
import de.nb.aventiure2.scaction.AbstractScAction;

public abstract class AbstractEntityAction extends AbstractScAction {
    @NonNull
    private final AbstractEntityData entityData;

    public AbstractEntityAction(final AvDatabase db,
                                final StoryState initialStoryState,
                                @NonNull final AbstractEntityData entityData) {
        super(db, initialStoryState);
        this.entityData = entityData;
    }

    @NonNull
    protected AbstractEntityData getEntityData() {
        return entityData;
    }

    @Override
    protected StoryStateBuilder t(@NonNull final StoryState.StructuralElement startsNew,
                                  @NonNull final String text) {
        return super.t(startsNew, text)
                .letztesObject(
                        entityData instanceof ObjectData ? ((ObjectData) entityData).getObject() :
                                null);
    }
}
