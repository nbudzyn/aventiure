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

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static de.nb.aventiure2.data.world.syscomp.memory.Known.UNKNOWN;

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

    MemoryPCD(@NonNull final GameObjectId gameObjectId,
              @NonNull final Action lastAction) {
        this(gameObjectId, lastAction, new HashMap<>());
    }

    @Ignore
    MemoryPCD(@NonNull final GameObjectId gameObjectId,
              @NonNull final Action lastAction,
              @NonNull final Map<GameObjectId, Known> knownMap) {
        super(gameObjectId);
        this.knownMap = new HashMap<>(knownMap);
        setLastAction(lastAction);
    }

    @NonNull
    Action getLastAction() {
        return lastAction;
    }

    void setLastAction(@NonNull final Action lastAction) {
        checkArgument(!getGameObjectId().equals(lastAction.getObject()),
                "Interaktion des Game Objects " + getGameObjectId()
                        + " mit sich selbst nicht erlaubt");

        this.lastAction = checkNotNull(lastAction, "lastAction");
    }

    @NonNull
    ImmutableMap<GameObjectId, Known> getKnownMap() {
        return ImmutableMap.copyOf(knownMap);
    }

    void setKnown(final Map<GameObjectId, Known> toMap) {
        knownMap.putAll(toMap);
    }

    public Known getKnown(final GameObjectId otherGameObjectId) {
        return knownMap.getOrDefault(otherGameObjectId, UNKNOWN);
    }

    void setKnown(final GameObjectId otherGameObjectId, final Known known) {
        if (known == UNKNOWN) {
            knownMap.remove(otherGameObjectId);
        } else {
            knownMap.put(otherGameObjectId, known);
        }
    }
}
