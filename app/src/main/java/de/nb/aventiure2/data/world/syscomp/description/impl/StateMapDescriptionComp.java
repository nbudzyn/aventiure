package de.nb.aventiure2.data.world.syscomp.description.impl;

import com.google.common.collect.ImmutableMap;

import java.util.Map;

import de.nb.aventiure2.data.world.base.GameObjectId;
import de.nb.aventiure2.data.world.syscomp.state.AbstractStateComp;

public class StateMapDescriptionComp<S extends Enum<S>> extends MultiDescriptionComp {
    private final AbstractStateComp<S> stateComp;
    private final ImmutableMap<S, DescriptionTriple> descriptionsByState;

    StateMapDescriptionComp(final GameObjectId gameObjectId,
                            final AbstractStateComp<S> stateComp,
                            final Map<S, DescriptionTriple> descriptionsByState) {
        super(gameObjectId);
        this.stateComp = stateComp;
        this.descriptionsByState = ImmutableMap.copyOf(descriptionsByState);
    }
    
    @Override
    protected DescriptionTriple chooseDescriptionTriple() {
        return descriptionsByState.get(stateComp.getState());
    }
}
