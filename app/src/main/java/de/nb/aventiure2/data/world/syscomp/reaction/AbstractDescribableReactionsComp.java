package de.nb.aventiure2.data.world.syscomp.reaction;

import de.nb.aventiure2.data.narration.Narrator;
import de.nb.aventiure2.data.world.base.GameObjectId;
import de.nb.aventiure2.data.world.counter.CounterDao;
import de.nb.aventiure2.data.world.gameobject.*;
import de.nb.aventiure2.data.world.syscomp.description.IDescribableGO;

/**
 * Component für ein {@link IDescribableGO}: The game object might
 * react to certain events.
 */
public abstract class AbstractDescribableReactionsComp extends AbstractReactionsComp {
    protected final CounterDao counterDao;

    protected AbstractDescribableReactionsComp(final GameObjectId id,
                                               final CounterDao counterDao,
                                               final Narrator n, final World world) {
        super(id, n, world);
        this.counterDao = counterDao;
    }
}
