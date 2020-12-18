package de.nb.aventiure2.data.world.base;

import androidx.annotation.NonNull;

import de.nb.aventiure2.german.base.Person;

import static de.nb.aventiure2.data.world.gameobject.World.*;
import static de.nb.aventiure2.german.base.Person.P2;
import static de.nb.aventiure2.german.base.Person.P3;

/**
 * Abstract super-class for components. An {@link GameObject}
 * can be linked to several components according to the entity-component-system pattern.
 */
public abstract class AbstractComponent implements IComponent {
    private final GameObjectId gameObjectId;

    AbstractComponent(final GameObjectId gameObjectId) {
        this.gameObjectId = gameObjectId;
    }

    @NonNull
    protected Person getGameObjectPerson() {
        if (getGameObjectId().equals(SPIELER_CHARAKTER)) {
            return P2;
        }

        return P3;
    }

    public GameObjectId getGameObjectId() {
        return gameObjectId;
    }
}
