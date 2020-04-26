package de.nb.aventiure2.data.world.syscomp.memory;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.jetbrains.annotations.Contract;

import java.util.Objects;

import de.nb.aventiure2.data.world.base.GameObjectId;
import de.nb.aventiure2.data.world.base.IGameObject;

import static java.util.Arrays.asList;

/**
 * Eine Aktion des {@link IHasMemoryGO} - oft eine Interaktion mit einem konkreten anderen
 * Game Object.
 */
public class Action {
    public enum Type {
        // Diese Typ-Konstante steht dafür, dass das Spiel zuvor begonnen hat und es also
        // keine letzte Aktion gab (Null Object Pattern).
        SPIELBEGINN,
        ABLEGEN,
        /**
         * Das {@link de.nb.aventiure2.data.world.syscomp.memory.IHasMemoryGO} bewegt sich, z.B. der
         * Spielercharakter geht irgendwo hin.
         */
        BEWEGEN, ESSEN, HEULEN, HOCHWERFEN, KLETTERN, NEHMEN, REDEN,
        SCHLAFEN_ODER_VERGEBLICHER_EINSCHLAF_VERSUCH
    }

    @NonNull
    private final Type type;

    public Action(@NonNull final Type type,
                  @Nullable final IGameObject object) {
        this(type, object != null ? object.getId() : null);
    }

    public Action(@NonNull final Type type,
                  @Nullable final GameObjectId gameObjectId) {
        this.type = type;
        this.gameObjectId = gameObjectId;
    }

    @Nullable
    private final GameObjectId gameObjectId;

    public boolean is(final Type... someTypes) {
        return asList(someTypes).contains(type);
    }

    @NonNull
    public Type getType() {
        return type;
    }

    public boolean hasObject(@Nullable final IGameObject gameObject) {
        return hasObject(
                gameObject != null ? gameObject.getId() : gameObjectId);
    }

    @Contract(pure = true)
    private boolean hasObject(@Nullable final GameObjectId otherGameObjectId) {
        return Objects.equals(gameObjectId, otherGameObjectId);
    }

    @Nullable
    public GameObjectId getGameObjectId() {
        return gameObjectId;
    }

    @Contract(value = "null -> false", pure = true)
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final Action that = (Action) o;
        return type == that.type &&
                Objects.equals(gameObjectId, that.gameObjectId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, gameObjectId);
    }

    @Override
    @NonNull
    public String toString() {
        final StringBuilder res = new StringBuilder();
        res.append(type);
        if (gameObjectId != null) {
            res.append("(")
                    .append(gameObjectId)
                    .append(")");
        }

        return res.toString();
    }
}
