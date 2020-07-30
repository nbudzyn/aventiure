package de.nb.aventiure2.data.world.gameobject;

import androidx.annotation.NonNull;

import de.nb.aventiure2.data.world.base.GameObjectId;
import de.nb.aventiure2.data.world.syscomp.description.AbstractDescriptionComp;
import de.nb.aventiure2.data.world.syscomp.location.LocationComp;
import de.nb.aventiure2.data.world.syscomp.storingplace.ILocationGO;
import de.nb.aventiure2.data.world.syscomp.storingplace.StoringPlaceComp;

class StoringPlaceObject extends SimpleObject
        implements ILocationGO {
    private final StoringPlaceComp storingPlaceComp;

    StoringPlaceObject(final GameObjectId id,
                       final AbstractDescriptionComp descriptionComp,
                       final LocationComp locationComp,
                       final StoringPlaceComp storingPlaceComp) {
        super(id, descriptionComp, locationComp);
        // Jede Komponente muss registiert werden!
        this.storingPlaceComp = addComponent(storingPlaceComp);
    }

    @NonNull
    @Override
    public StoringPlaceComp storingPlaceComp() {
        return storingPlaceComp;
    }
}
