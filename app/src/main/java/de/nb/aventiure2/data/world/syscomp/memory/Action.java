package de.nb.aventiure2.data.world.syscomp.memory;

import static java.util.Arrays.asList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.Ignore;

import com.google.common.base.Joiner;

import org.jetbrains.annotations.Contract;

import java.util.Objects;

import javax.annotation.concurrent.Immutable;

import de.nb.aventiure2.data.world.base.GameObjectId;
import de.nb.aventiure2.data.world.base.IGameObject;

/**
 * Eine Aktion des {@link IHasMemoryGO} - oft eine Interaktion mit einem konkreten anderen
 * Game Object.
 */
@Immutable
public class Action {
    public enum Type {
        /**
         * Diese Typ-Konstante steht dafür, dass das Spiel zuvor begonnen hat und es also
         * keine letzte Aktion gab (Null Object Pattern).
         */
        SPIELBEGINN,
        ABLEGEN,
        /**
         * Das {@link de.nb.aventiure2.data.world.syscomp.memory.IHasMemoryGO} bewegt sich, z.B. der
         * Spielercharakter geht irgendwo hin.
         */
        BEWEGEN,
        CREATE_NEHMEN,
        ESSEN, GEBEN, HEULEN, HOCHWERFEN, NEHMEN, REDEN, RUFEN,
        SCHLAFEN_ODER_VERGEBLICHER_EINSCHLAF_VERSUCH, STATE_MODIFICATION, RASTEN, WARTEN
    }

    @NonNull
    private final Type type;

    /**
     * Das vorrangige Objekt der Aktion: Der genommene Gegenstand, der Ort, zu dem sich der
     * Benutzer bewegt hat o.Ä.
     */
    @Nullable
    private final GameObjectId object;

    /**
     * Ein zusätzliches "Adverbial" der Aktion, z.B. der Ort, <i>von dem</i>
     * ein Gegenstand ({@link #object}) genommen wurde etc.
     */
    @Nullable
    private final GameObjectId adverbial;

    @Ignore
    public Action(final Type type) {
        this(type, (GameObjectId) null);
    }

    @Ignore
    public Action(final Type type,
                  @Nullable final IGameObject object) {
        this(type, object, (IGameObject) null);
    }

    public Action(final Type type,
                  @Nullable final IGameObject object,
                  @Nullable final IGameObject adverbial) {
        this(type,
                object != null ? object.getId() : null,
                adverbial != null ? adverbial.getId() : null);
    }

    public Action(final Type type,
                  @Nullable final IGameObject object,
                  @Nullable final GameObjectId adverbial) {
        this(type,
                object != null ? object.getId() : null,
                adverbial);
    }

    @Ignore
    public Action(final Type type,
                  @Nullable final GameObjectId object) {
        this(type, object, null);
    }

    @SuppressWarnings("WeakerAccess")
    Action(final Type type,
           @Nullable final GameObjectId object,
           @Nullable final GameObjectId adverbial) {
        this.type = type;
        this.object = object;
        this.adverbial = adverbial;
    }

    public boolean is(final Type... someTypes) {
        return asList(someTypes).contains(type);
    }

    @NonNull
    public Type getType() {
        return type;
    }

    /**
     * Prüft, ob die Aktion <hashCode>someGameObject</hashCode> als vorrangige Objekt hat.
     * (<i>Prüft nicht das {@link #adverbial}!</i>
     */
    public boolean hasObject(@Nullable final IGameObject someGameObject) {


        return hasObject(
                someGameObject != null ? someGameObject.getId() : object);
    }

    /**
     * Prüft, ob die Aktion <hashCode>someGameObjectId</hashCode> als vorrangige Objekt hat.
     * (<i>Prüft nicht das {@link #adverbial}!</i>
     */
    @Contract(pure = true)
    private boolean hasObject(@Nullable final GameObjectId someGameObjectId) {
        return Objects.equals(object, someGameObjectId);
    }

    @Nullable
    public GameObjectId getObject() {
        return object;
    }

    @Nullable
    GameObjectId getAdverbial() {
        return adverbial;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final Action action = (Action) o;
        return type == action.type &&
                Objects.equals(object, action.object) &&
                Objects.equals(adverbial, action.adverbial);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, object, adverbial);
    }

    @Override
    @NonNull
    public String toString() {
        if (object == null) {
            return type.toString();
        }

        return type
                + "("
                + Joiner.on(",").skipNulls().join(object, adverbial)
                + ")";
    }
}
