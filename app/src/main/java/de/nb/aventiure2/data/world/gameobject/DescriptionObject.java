package de.nb.aventiure2.data.world.gameobject;

import androidx.annotation.NonNull;

import de.nb.aventiure2.data.world.base.GameObject;
import de.nb.aventiure2.data.world.base.GameObjectId;
import de.nb.aventiure2.data.world.syscomp.description.AbstractDescriptionComp;
import de.nb.aventiure2.data.world.syscomp.description.IDescribableGO;

public class DescriptionObject extends GameObject implements IDescribableGO {
    private final AbstractDescriptionComp descriptionComp;

    DescriptionObject(final GameObjectId id,
                      final AbstractDescriptionComp descriptionComp) {
        super(id);
        this.descriptionComp = addComponent(descriptionComp);
    }

    @NonNull
    @Override
    public AbstractDescriptionComp descriptionComp() {
        return descriptionComp;
    }
}
