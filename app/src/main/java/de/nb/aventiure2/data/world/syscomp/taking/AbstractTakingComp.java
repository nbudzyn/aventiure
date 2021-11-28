package de.nb.aventiure2.data.world.syscomp.taking;

import de.nb.aventiure2.data.narration.Narrator;
import de.nb.aventiure2.data.world.base.AbstractStatelessComponent;
import de.nb.aventiure2.data.world.base.GameObject;
import de.nb.aventiure2.data.world.base.GameObjectId;
import de.nb.aventiure2.data.world.base.IGameObjectDescriptionMixin;
import de.nb.aventiure2.data.world.gameobject.*;
import de.nb.aventiure2.data.world.syscomp.description.IDescribableGO;
import de.nb.aventiure2.data.world.syscomp.location.ILocatableGO;

/**
 * Component for a {@link GameObject}: The game object can take something -
 * or at least react to an offer.
 */
public abstract class AbstractTakingComp extends AbstractStatelessComponent
        implements IWorldLoaderMixin, IWorldDescriptionMixin, IGameObjectDescriptionMixin {
    protected Narrator n;
    protected final World world;

    protected AbstractTakingComp(
            final GameObjectId id,
            final Narrator n, final World world) {
        super(id);

        this.n = n;
        this.world = world;
    }

    /**
     * Das {@link ITakerGO} bekommt etwas angeboten.
     */
    public abstract <GIVEN extends IDescribableGO & ILocatableGO>
    SCTakeAction<GIVEN> getAction(GIVEN offered);

    @Override
    public World getWorld() {
        return world;
    }
}
