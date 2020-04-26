package de.nb.aventiure2.data.world.syscomp.alive;

import de.nb.aventiure2.data.world.base.AbstractStatelessComponent;
import de.nb.aventiure2.data.world.base.GameObject;
import de.nb.aventiure2.data.world.base.GameObjectId;

/**
 * Component for a {@link GameObject}: The game object is alive.
 */
public class AliveComp extends AbstractStatelessComponent {
    // STORY LivingBeings könnten sterben.

    public AliveComp(final GameObjectId gameObjectId) {
        super(gameObjectId);
    }
}
