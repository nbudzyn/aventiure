package de.nb.aventiure2.data.storystate;

import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.PrimaryKey;

import de.nb.aventiure2.data.storystate.StoryState.StructuralElement;
import de.nb.aventiure2.data.world.base.GameObjectId;
import de.nb.aventiure2.data.world.entity.creature.Creature;
import de.nb.aventiure2.data.world.entity.object.AvObject;
import de.nb.aventiure2.data.world.room.AvRoom;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

/**
 * Builder for {@link StoryState}.
 */
public class StoryStateBuilder {
    /**
     * The class name of the last action (if any).
     */
    @Nullable
    private String lastActionClassName;

    /**
     * This {@link StoryState} starts a new ... (paragraph, e.g.)
     */
    private final StructuralElement startsNew;

    /**
     * This {@link StoryState} ends this ... (paragraph, e.g.)
     */
    private StructuralElement endsThis = StructuralElement.WORD;

    @PrimaryKey
    @NonNull
    private final String text;

    /**
     * Ob ein Komma aussteht. Wenn ein Komma aussteht, muss als nächstes ein Komma folgen -
     * oder das Satzende.
     */
    private boolean kommaStehtAus;

    /**
     * Whether the story can be continued by a Satzreihenglied without subject where
     * the player character is the implicit subject (such as " und gehst durch die Tür.")
     */
    private boolean allowsAdditionalDuSatzreihengliedOhneSubjekt = false;

    private boolean dann = false;

    /**
     * The creature the user is / has recently been talking to.
     */
    @Nullable
    private Creature talkingTo;

    /**
     * The last object.
     */
    @Nullable
    private AvObject lastObject;

    /**
     * The room the user was in at the beginning of the last action.
     * <p>Needs to be set before calling {@link #build()}.</p>
     */
    @Nullable
    private AvRoom lastRoom;

    public static StoryStateBuilder t(
            @Nullable final IPlayerAction lastAction,
            @NonNull final StructuralElement startsNew,
            @NonNull final String text) {
        checkArgument(!TextUtils.isEmpty(text), "text is null or empty");
        checkNotNull(startsNew, "startsNew is null");

        return t(lastAction == null ? null : lastAction.getClass(), startsNew, text);
    }

    public static StoryStateBuilder t(
            @Nullable final Class<? extends IPlayerAction> lastActionClass,
            @NonNull final StoryState.StructuralElement startsNew,
            @NonNull final String text) {
        checkArgument(!TextUtils.isEmpty(text), "text is null or empty");
        checkNotNull(startsNew, "startsNew is null");

        return t(toLastActionClassName(lastActionClass), startsNew, text);
    }

    public static StoryStateBuilder t(
            @Nullable final String lastActionClassName,
            @NonNull final StoryState.StructuralElement startsNew,
            @NonNull final String text) {
        checkArgument(!TextUtils.isEmpty(text), "text is null or empty");
        checkNotNull(startsNew, "startsNew is null");

        return new StoryStateBuilder(lastActionClassName, startsNew, text);
    }


    private StoryStateBuilder(@Nullable final String lastActionClassName,
                              @NonNull final StoryState.StructuralElement startsNew,
                              @NonNull final String text) {
        this.lastActionClassName = lastActionClassName;
        this.startsNew = startsNew;
        this.text = text;
    }

    public StoryStateBuilder imGespraechMit(final Creature talkingTo) {
        this.talkingTo = talkingTo;
        return this;
    }

    public StoryStateBuilder letztesObject(final AvObject lastObject) {
        this.lastObject = lastObject;
        return this;
    }

    public StoryStateBuilder letzterRaum(final GameObjectId lastRoom) {
        return letzterRaum(AvRoom.get(lastRoom));
    }

    public StoryStateBuilder letzterRaum(final AvRoom lastRoom) {
        this.lastRoom = lastRoom;
        return this;
    }

    public StoryStateBuilder letzteAktion(@Nullable final IPlayerAction lastAction) {
        return letzteAktion(lastAction == null ? null : lastAction.getClass());
    }

    public StoryStateBuilder letzteAktion(final Class<? extends IPlayerAction> lastActionClass) {
        return letzteAktion(toLastActionClassName(lastActionClass));
    }

    public StoryStateBuilder letzteAktion(@Nullable final String lastActionClassName) {
        this.lastActionClassName = lastActionClassName;
        return this;
    }

    public StoryStateBuilder beendet(final StructuralElement structuralElement) {
        endsThis = structuralElement;
        return this;
    }

    private static String toLastActionClassName(
            @Nullable final Class<? extends IPlayerAction> lastActionClass) {
        return lastActionClass == null ? null : lastActionClass.getCanonicalName();
    }

    public StoryStateBuilder komma() {
        return komma(true);
    }

    public StoryStateBuilder komma(final boolean kommaStehtAus) {
        this.kommaStehtAus = kommaStehtAus;
        return this;
    }


    /**
     * Sets a flag that the text can be continued by a Satzreihenglied without subject where
     * the player character is the implicit subject
     */
    public StoryStateBuilder undWartest() {
        return undWartest(true);
    }

    public StoryStateBuilder undWartest(
            final boolean allowsAdditionalPlayerSatzreihengliedOhneSubjekt) {
        allowsAdditionalDuSatzreihengliedOhneSubjekt =
                allowsAdditionalPlayerSatzreihengliedOhneSubjekt;
        return this;
    }

    public StoryStateBuilder dann() {
        return dann(true);
    }

    public StoryStateBuilder dann(final boolean dann) {
        this.dann = dann;
        return this;
    }

    public StoryState build() {
        checkState(lastRoom != null, "lastRoom is null");

        return new StoryState(lastActionClassName,
                startsNew,
                endsThis,
                text,
                kommaStehtAus,
                allowsAdditionalDuSatzreihengliedOhneSubjekt,
                dann,
                talkingTo,
                lastObject, lastRoom);
    }
}
