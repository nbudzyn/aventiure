package de.nb.aventiure2.data.world.gameobject;

import androidx.annotation.NonNull;

import de.nb.aventiure2.data.world.base.GameObjectId;
import de.nb.aventiure2.data.world.syscomp.description.AbstractDescriptionComp;
import de.nb.aventiure2.data.world.syscomp.location.LocationComp;
import de.nb.aventiure2.data.world.syscomp.state.AbstractStateComp;
import de.nb.aventiure2.data.world.syscomp.state.IHasStateGO;

public class StateObject<S extends Enum<S>> extends SimpleObject implements IHasStateGO<S> {
    protected final AbstractStateComp<S> stateComp;

    public StateObject(final GameObjectId id,
                       final AbstractDescriptionComp descriptionComp,
                       final LocationComp locationComp,
                       final AbstractStateComp<S> stateComp) {
        super(id, descriptionComp, locationComp);
        this.stateComp = addComponent(stateComp);
    }

    @NonNull
    @Override
    public AbstractStateComp<S> stateComp() {
        return stateComp;
    }
}
