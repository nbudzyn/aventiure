package de.nb.aventiure2.data.world.syscomp.memory;

import androidx.annotation.NonNull;

import com.google.common.collect.ImmutableList;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

import javax.annotation.Nullable;

import de.nb.aventiure2.data.database.AvDatabase;
import de.nb.aventiure2.data.world.base.AbstractStatefulComponent;
import de.nb.aventiure2.data.world.base.GameObject;
import de.nb.aventiure2.data.world.base.GameObjectId;
import de.nb.aventiure2.data.world.base.IGameObject;
import de.nb.aventiure2.data.world.base.Known;
import de.nb.aventiure2.data.world.gameobject.*;
import de.nb.aventiure2.data.world.syscomp.description.IDescribableGO;
import de.nb.aventiure2.data.world.syscomp.location.ILocatableGO;
import de.nb.aventiure2.data.world.syscomp.location.LocationSystem;

import static com.google.common.collect.ImmutableList.toImmutableList;
import static java.util.Objects.requireNonNull;

/**
 * Component for a {@link GameObject}: The game object
 * remembers things.
 */
public class MemoryComp extends AbstractStatefulComponent<MemoryPCD> {
    @NonNull
    private final Map<GameObjectId, Known> initiallyKnown;

    private final World world;

    /**
     * Constructor for {@link MemoryComp}.
     */
    public MemoryComp(final GameObjectId gameObjectId,
                      final AvDatabase db,
                      final World world,
                      final Map<GameObjectId, Known> initiallyKnown) {
        super(gameObjectId, db.memoryDao());
        this.world = world;
        this.initiallyKnown = initiallyKnown;
    }

    @Override
    @NonNull
    protected MemoryPCD createInitialState() {
        return new MemoryPCD(getGameObjectId(),
                // Wahrscheinlich ist das Game Objekt also letztes irgendwohin gegangen
                new Action(Action.Type.SPIELBEGINN),
                initiallyKnown);
    }

    public boolean lastActionWas(final Action.Type actionType,
                                 @Nullable final IGameObject gameObject) {
        return lastActionWas(actionType,
                gameObject != null ? gameObject.getId() : null);
    }

    private boolean lastActionWas(final Action.Type actionType,
                                  @Nullable final GameObjectId gameObjectId) {
        return lastActionWas(new Action(actionType, gameObjectId));
    }

    public boolean lastActionWas(final Action action) {
        return Objects.equals(getLastAction(), action);
    }

    @NonNull
    public Action getLastAction() {
        return requirePcd().getLastAction();
    }

    public void setLastAction(final Action.Type actionType,
                              @Nullable final IGameObject object) {
        setLastAction(actionType, object, (IGameObject) null);
    }

    public void setLastAction(final Action.Type actionType,
                              @Nullable final IGameObject object,
                              @Nullable final IGameObject adverbial) {
        setLastAction(new Action(actionType, object, adverbial));
    }

    public void setLastAction(final Action.Type actionType,
                              @Nullable final IGameObject object,
                              @Nullable final GameObjectId adverbial) {
        setLastAction(new Action(actionType, object, adverbial));
    }

    public void setLastAction(final Action.Type actionType,
                              @Nullable final GameObjectId gameObjectId) {
        setLastAction(new Action(actionType, gameObjectId));
    }

    public void setLastAction(final Action action) {
        requirePcd().setLastAction(requireNonNull(action, "action"));
    }

    public boolean isKnown(final IGameObject otherGameObject) {
        return isKnown(otherGameObject.getId());
    }

    public boolean areAllKnown(final GameObjectId... otherGameObjectIds) {
        return Stream.of(otherGameObjectIds).allMatch(this::isKnown);
    }

    public boolean isKnown(final GameObjectId otherGameObjectId) {
        return getKnown(otherGameObjectId).isKnown();
    }

    public boolean isKnownFromLight(final GameObjectId otherGameObjectId) {
        return getKnown(otherGameObjectId) == Known.KNOWN_FROM_LIGHT;
    }

    public void narretAndForget(final GameObjectId... otherGameObjectIds) {
        for (final GameObjectId otherGameObjectId : otherGameObjectIds) {
            narrateAndSetKnown(otherGameObjectId, Known.UNKNOWN);
        }
    }

    public void narrateAndUpgradeKnown(final Iterable<? extends IGameObject> objects) {
        for (final IGameObject object : objects) {
            narrateAndUpgradeKnown(object);
        }
    }

    /**
     * Sets the known value (based on the game object's location, but does not make
     * it worse than it is.
     */
    public void narrateAndUpgradeKnown(final IGameObject otherGameObject) {
        narrateAndUpgradeKnown(otherGameObject.getId());
    }

    public void narrateAndUpgradeKnown(final GameObjectId otherGameObjectId) {
        narrateAndUpgradeKnown(otherGameObjectId, getKnownForLocation(otherGameObjectId));
    }

    private Known getKnownForLocation(final GameObjectId otherGameObjectId) {
        final GameObject otherGameObject = world.load(otherGameObjectId);
        if (!(otherGameObject instanceof ILocatableGO)) {
            // Anscheinend ist andere Game Object ein nichtmaterielles Ding, das keinen wirklichen
            // Aufenthaltsort in der Welt hat.
            return Known.KNOWN_FROM_LIGHT;
        }

        return Known.getKnown(
                LocationSystem.getLichtverhaeltnisse(
                        ((ILocatableGO) otherGameObject).locationComp().getLocation()));
    }

    public <LOC_DESC extends ILocatableGO & IDescribableGO> ImmutableList<LOC_DESC> filterKnown(
            final Collection<? extends LOC_DESC> gameObjects) {
        return gameObjects.stream().filter(this::isKnown).collect(toImmutableList());
    }

    public void narrateAndUpgradeKnown(
            final Iterable<? extends IGameObject> objects,
            final Known minimalKnown) {
        for (final IGameObject object : objects) {
            narrateAndUpgradeKnown(object, minimalKnown);
        }
    }

    /**
     * Sets the known value, but does not make it worse than it is.
     * And tells all listeners to
     * {@link de.nb.aventiure2.data.world.syscomp.reaction.interfaces.IKnownChangedReactions}
     * about any change.
     */
    private void narrateAndUpgradeKnown(final IGameObject otherGameObject,
                                        final Known minimalKnown) {
        narrateAndUpgradeKnown(otherGameObject.getId(), minimalKnown);
    }

    /**
     * Sets the known value, but does not make it worse than it is.
     * And tells all listeners to
     * {@link de.nb.aventiure2.data.world.syscomp.reaction.interfaces.IKnownChangedReactions}
     * about any change.
     */
    public void narrateAndUpgradeKnown(final GameObjectId otherGameObjectId,
                                       final Known minimalKnown) {
        narrateAndSetKnown(otherGameObjectId, Known.max(
                minimalKnown,
                getKnown(otherGameObjectId) // old value
        ));
    }

    private void narrateAndSetKnown(final GameObjectId otherGameObjectId,
                                    final Known known) {
        if (known.equals(getKnown(otherGameObjectId))) {
            return;
        }

        final Known oldKnown = getKnown(otherGameObjectId);

        requirePcd().setKnown(otherGameObjectId, known);

        world.narrateAndDoReactions().onKnownChanged(
                getGameObjectId(), otherGameObjectId, oldKnown, known);
    }

    @NonNull
    public Known getKnown(final GameObjectId otherGameObjectId) {
        return requirePcd().getKnown(otherGameObjectId);
    }
}
