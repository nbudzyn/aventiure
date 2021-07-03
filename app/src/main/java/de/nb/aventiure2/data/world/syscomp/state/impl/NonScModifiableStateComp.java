package de.nb.aventiure2.data.world.syscomp.state.impl;

import com.google.common.collect.ImmutableList;

import de.nb.aventiure2.data.database.AvDatabase;
import de.nb.aventiure2.data.time.TimeTaker;
import de.nb.aventiure2.data.world.base.GameObject;
import de.nb.aventiure2.data.world.base.GameObjectId;
import de.nb.aventiure2.data.world.gameobject.*;
import de.nb.aventiure2.data.world.syscomp.state.AbstractStateComp;
import de.nb.aventiure2.data.world.syscomp.state.StateModification;

/**
 * Component für ein {@link GameObject}: Das Game Object hat einen Zustand (der sich
 * über die Zeit ändern kann) - allerdings kann der SC den Zustand des Objekts
 * nicht direkt ändern.
 */
public abstract class NonScModifiableStateComp<S extends Enum<S>> extends AbstractStateComp<S> {
    NonScModifiableStateComp(final GameObjectId gameObjectId, final AvDatabase db,
                             final TimeTaker timeTaker,
                             final World world,
                             final S initialState) {
        super(gameObjectId, db, timeTaker, world, initialState);
    }

    @Override
    public ImmutableList<StateModification<S>> getScStateModificationData() {
        return ImmutableList.of();
    }
}
