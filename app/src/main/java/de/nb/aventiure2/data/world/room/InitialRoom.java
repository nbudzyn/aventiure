package de.nb.aventiure2.data.world.room;

import de.nb.aventiure2.data.world.base.AbstractComponent;
import de.nb.aventiure2.data.world.base.GameObject;
import de.nb.aventiure2.data.world.base.GameObjectId;

/**
 * Component for a {@link GameObject}: The game object
 * has an initial room where it is placed when the game starts.
 */
public class InitialRoom extends AbstractComponent {
    public InitialRoom(final GameObjectId gameObjectId) {
        super(gameObjectId);
    }

    // TODO Verwenden oder löschen
}
