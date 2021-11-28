package de.nb.aventiure2.data.world.gameobject;

import javax.annotation.Nonnull;

import de.nb.aventiure2.data.world.base.GameObjectId;
import de.nb.aventiure2.data.world.syscomp.description.AbstractDescriptionComp;
import de.nb.aventiure2.data.world.syscomp.location.ILocatableGO;
import de.nb.aventiure2.data.world.syscomp.location.LocationComp;

class SimpleObject extends DescriptionObject
        implements ILocatableGO {
    private final LocationComp locationComp;

    SimpleObject(final GameObjectId id,
                 final AbstractDescriptionComp descriptionComp,
                 final LocationComp locationComp) {
        super(id, descriptionComp);
        // Jede Komponente muss registiert werden!
        this.locationComp = addComponent(locationComp);
    }

    @Nonnull
    @Override
    public LocationComp locationComp() {
        return locationComp;
    }
}
