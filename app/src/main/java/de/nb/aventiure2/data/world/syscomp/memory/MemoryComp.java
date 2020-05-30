package de.nb.aventiure2.data.world.syscomp.memory;

import androidx.annotation.NonNull;

import java.util.Map;
import java.util.Objects;

import javax.annotation.Nullable;

import de.nb.aventiure2.data.database.AvDatabase;
import de.nb.aventiure2.data.world.base.AbstractStatefulComponent;
import de.nb.aventiure2.data.world.base.GameObject;
import de.nb.aventiure2.data.world.base.GameObjectId;
import de.nb.aventiure2.data.world.base.IGameObject;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Component for a {@link GameObject}: The game object
 * remembers things.
 */
public class MemoryComp extends AbstractStatefulComponent<MemoryPCD> {
    private final AvDatabase db;
    @NonNull
    private final Map<GameObjectId, Known> initiallyKnown;

    /**
     * Constructor for {@link MemoryComp}.
     */
    public MemoryComp(final GameObjectId gameObjectId,
                      final AvDatabase db,
                      final Map<GameObjectId, Known> initiallyKnown) {
        super(gameObjectId, db.memoryDao());
        this.db = db;
        this.initiallyKnown = initiallyKnown;
    }

    @Override
    @NonNull
    protected MemoryPCD createInitialState() {
        return new MemoryPCD(getGameObjectId(),
                // Wahrscheinlich ist das Game Objekt also letztes irgendwohin gegangen
                new Action(Action.Type.SPIELBEGINN, (GameObjectId) null),
                initiallyKnown);
    }


    public boolean lastActionWas(final Action.Type actionType,
                                 final IGameObject gameObject) {
        return lastActionWas(actionType,
                gameObject != null ? gameObject.getId() : null);
    }

    public boolean lastActionWas(final Action.Type actionType,
                                 final GameObjectId gameObjectId) {
        return lastActionWas(new Action(actionType, gameObjectId));
    }

    public boolean lastActionWas(@NonNull final Action action) {
        return Objects.equals(getLastAction(), action);
    }

    @NonNull
    public Action getLastAction() {
        return getPcd().getLastAction();
    }

    public void setLastAction(@NonNull final Action.Type actionType,
                              @Nullable final IGameObject object) {
        setLastAction(actionType, object != null ? object.getId() : null);
    }

    public void setLastAction(@NonNull final Action.Type actionType,
                              @Nullable final GameObjectId gameObjectId) {
        setLastAction(new Action(actionType, gameObjectId));
    }

    public void setLastAction(@NonNull final Action action) {
        getPcd().setLastAction(checkNotNull(action, "action"));
    }

    public boolean isKnown(final IGameObject otherGameObject) {
        return isKnown(otherGameObject.getId());
    }

    public boolean isKnown(final GameObjectId otherGameObjectId) {
        return getKnown(otherGameObjectId).isKnown();
    }

    /**
     * Sets the known value, but does not make it worse than it is.
     */
    public void upgradeKnown(final IGameObject otherGameObject,
                             @NonNull final Known minimalKnown) {
        upgradeKnown(otherGameObject.getId(), minimalKnown);
    }

    /**
     * Sets the known value, but does not make it worse than it is.
     */
    public void upgradeKnown(final GameObjectId otherGameObjectId,
                             @NonNull final Known minimalKnown) {
        setKnown(otherGameObjectId, Known.max(
                minimalKnown,
                getKnown(otherGameObjectId) // old value
        ));
    }

    @NonNull
    public Known getKnown(final IGameObject otherGameObject) {
        return getKnown(otherGameObject.getId());
    }

    @NonNull
    public Known getKnown(final GameObjectId otherGameObjectId) {
        return getPcd().getKnown(otherGameObjectId);
    }

    private void setKnown(final GameObjectId otherGameObjectId,
                          @NonNull final Known known) {
        getPcd().setKnown(otherGameObjectId, known);
    }
}
