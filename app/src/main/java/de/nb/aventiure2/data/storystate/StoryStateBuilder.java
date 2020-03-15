package de.nb.aventiure2.data.storystate;

import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.PrimaryKey;

import de.nb.aventiure2.data.storystate.StoryState.StartsNew;
import de.nb.aventiure2.data.world.creature.Creature;
import de.nb.aventiure2.data.world.object.AvObject;
import de.nb.aventiure2.data.world.room.AvRoom;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

/**
 * Builder for {@link StoryState}.
 */
public class StoryStateBuilder {
    /**
     * The last action (if any).
     */
    @Nullable
    private IPlayerAction lastAction;

    @NonNull
    private final StartsNew startsNew;

    @PrimaryKey
    @NonNull
    private String text;

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
            @NonNull final StartsNew startsNew,
            @NonNull final String text) {
        checkArgument(!TextUtils.isEmpty(text), "text is null or empty");
        checkNotNull(startsNew, "startsNew is null");

        return new StoryStateBuilder(lastAction, startsNew, text);
    }

    private StoryStateBuilder(@Nullable final IPlayerAction lastAction,
                              @NonNull final StartsNew startsNew,
                              @NonNull final String text) {
        this.lastAction = lastAction;
        this.startsNew = startsNew;
        this.text = text;
    }

    public StoryStateBuilder appendPeriodAndText(final String text,
                                                 final boolean allowsAdditionalDuSatzreihengliedOhneSubjekt,
                                                 final boolean dann) {
        return appendText(". ", false, false)
                .appendText(text, allowsAdditionalDuSatzreihengliedOhneSubjekt, dann);
    }

    public StoryStateBuilder appendText(final String text,
                                        final boolean allowsAdditionalDuSatzreihengliedOhneSubjekt,
                                        final boolean dann) {
        this.text += text;
        this.allowsAdditionalDuSatzreihengliedOhneSubjekt =
                allowsAdditionalDuSatzreihengliedOhneSubjekt;
        this.dann = dann;
        return this;
    }

    public StoryStateBuilder imGespraechMit(final Creature talkingTo) {
        this.talkingTo = talkingTo;
        return this;
    }

    public StoryStateBuilder letztesObject(final AvObject lastObject) {
        this.lastObject = lastObject;
        return this;
    }

    public StoryStateBuilder letzterRaum(final AvRoom lastRoom) {
        this.lastRoom = lastRoom;
        return this;
    }

    public StoryStateBuilder letzteAktion(@Nullable final IPlayerAction lastAction) {
        this.lastAction = lastAction;
        return this;
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

        return new StoryState(lastAction,
                startsNew,
                text,
                kommaStehtAus,
                allowsAdditionalDuSatzreihengliedOhneSubjekt,
                dann,
                talkingTo,
                lastObject, lastRoom);
    }
}
