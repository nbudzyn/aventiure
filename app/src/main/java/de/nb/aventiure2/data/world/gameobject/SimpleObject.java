package de.nb.aventiure2.data.world.gameobject;

import androidx.annotation.NonNull;

import javax.annotation.Nonnull;

import de.nb.aventiure2.data.world.base.GameObject;
import de.nb.aventiure2.data.world.base.GameObjectId;
import de.nb.aventiure2.data.world.syscomp.description.AbstractDescriptionComp;
import de.nb.aventiure2.data.world.syscomp.description.IDescribableGO;
import de.nb.aventiure2.data.world.syscomp.location.ILocatableGO;
import de.nb.aventiure2.data.world.syscomp.location.LocationComp;

class SimpleObject extends GameObject
        implements IDescribableGO, ILocatableGO {
    private final AbstractDescriptionComp descriptionComp;
    private final LocationComp locationComp;

    SimpleObject(final GameObjectId id,
                 final AbstractDescriptionComp descriptionComp,
                 final LocationComp locationComp) {
        super(id);
        // Jede Komponente muss registiert werden!
        this.descriptionComp = addComponent(descriptionComp);
        this.locationComp = addComponent(locationComp);
    }

    @NonNull
    @Override
    public AbstractDescriptionComp descriptionComp() {
        return descriptionComp;
    }

    @Nonnull
    @Override
    public LocationComp locationComp() {
        return locationComp;
    }
}
