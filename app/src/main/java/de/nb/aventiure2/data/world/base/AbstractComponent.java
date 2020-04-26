package de.nb.aventiure2.data.world.base;

import androidx.annotation.NonNull;

/**
 * Abstract super-class for components. An {@link GameObject}
 * can be linked to several components according to the entity-component-system pattern.
 */
public abstract class AbstractComponent implements IComponent {
    private final GameObjectId gameObjectId;

    /**
     * Kontext der Komponente - letztlich das Game Object, das diese Komponente enthält.
     * Über diesen Kontext kann die Komponente mit ihrem Game Object
     * interagieren und z.B. "Nachrichten schicken" oder "Fragen stellen",
     * die ggf. auch andere Components desselben Game Objects betreffen können.
     * <p>
     * Nach die Komponente ihrem Game Object hinzugefügt wurde, ist der context gesetzt und
     * nicht mehr <code>null</code>.
     */
    private ComponentContext context;

    protected AbstractComponent(final GameObjectId gameObjectId) {
        this.gameObjectId = gameObjectId;
    }

    @Override
    public final void setContext(final ComponentContext context) {
        this.context = context;
    }

    public GameObjectId getGameObjectId() {
        return gameObjectId;
    }

    /**
     * Gibt den Kontext der Komponente zurück - letztlich das Game Object, das diese Komponente
     * enthält.
     * Über den Kontext kann die Komponente mit ihrem Game Object
     * interagieren und z.B. "Nachrichten schicken" oder "Fragen stellen",
     * die ggf. auch andere Components desselben Game Objects betreffen können.
     */
    @NonNull
    public ComponentContext getContext() {
        if (context == null) {
            throw new IllegalStateException("getContext() called before adding component "
                    + "to the game object.");
        }

        return context;
    }
}
