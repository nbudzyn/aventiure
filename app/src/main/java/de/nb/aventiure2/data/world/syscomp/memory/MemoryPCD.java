package de.nb.aventiure2.data.world.syscomp.memory;

import androidx.annotation.NonNull;
import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.Ignore;

import com.google.common.collect.ImmutableMap;

import java.util.HashMap;
import java.util.Map;

import de.nb.aventiure2.data.world.base.AbstractPersistentComponentData;
import de.nb.aventiure2.data.world.base.GameObjectId;
import de.nb.aventiure2.data.world.base.Known;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static de.nb.aventiure2.data.world.base.Known.UNKNOWN;

/**
 * Mutable - and therefore persistent - data of the {@link MemoryComp} component.
 */
@Entity
public
class MemoryPCD extends AbstractPersistentComponentData {
    @NonNull
    @Ignore
    private final Map<GameObjectId, Known> knownMap;

    /**
     * Die letzte Aktion des Game Objects - oft eine Interaktion mit einem konkreten anderen
     * Game Object - jedenfalls niemals eine Interaktion mit sich selbst.
     */
    @Embedded(prefix = "last")
    @NonNull
    private Action lastAction;

    MemoryPCD(final GameObjectId gameObjectId,
              final Action lastAction) {
        this(gameObjectId, lastAction, new HashMap<>());
    }

    @Ignore
    MemoryPCD(final GameObjectId gameObjectId,
              final Action lastAction,
              final Map<GameObjectId, Known> knownMap) {
        super(gameObjectId);
        this.knownMap = new HashMap<>(knownMap);
        setLastAction(lastAction);
    }

    @NonNull
    Action getLastAction() {
        return lastAction;
    }

    void setLastAction(final Action lastAction) {
        checkArgument(!getGameObjectId().equals(lastAction.getObject()),
                "Interaktion des Game Objects " + getGameObjectId()
                        + " mit sich selbst nicht erlaubt");

        setChanged();
        this.lastAction = checkNotNull(lastAction, "lastAction");
    }

    @NonNull
    ImmutableMap<GameObjectId, Known> getKnownMap() {
        return ImmutableMap.copyOf(knownMap);
    }

    void setKnown(final Map<GameObjectId, Known> map) {
        setChanged();
        knownMap.putAll(map);
    }

    void setKnown(final GameObjectId otherGameObjectId, final Known known) {
        if (getKnown(otherGameObjectId) == known) {
            return;
        }

        setChanged();

        if (known == UNKNOWN) {
            knownMap.remove(otherGameObjectId);
        } else {
            knownMap.put(otherGameObjectId, known);
        }
    }

    public Known getKnown(final GameObjectId otherGameObjectId) {
        return knownMap.getOrDefault(otherGameObjectId, UNKNOWN);
    }
}
