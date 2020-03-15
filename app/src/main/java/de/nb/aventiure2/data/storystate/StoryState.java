package de.nb.aventiure2.data.storystate;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import de.nb.aventiure2.data.world.creature.Creature;
import de.nb.aventiure2.data.world.object.AvObject;
import de.nb.aventiure2.data.world.room.AvRoom;

/**
 * The text of the story, together with state relevant for going on with the story. Only things that have already happened.
 */
@Entity
public class StoryState {
    public enum StartsNew {
        PARAGRAPH, SENTENCE, WORD
    }

    private final StartsNew startsNew;

    /**
     * Canonical class name of the last action (if any).
     */
    @Nullable
    private final String lastActionClassName;

    @PrimaryKey
    @NonNull
    private final String text;

    /**
     * Ob ein Komma aussteht. Wenn ein Komma aussteht, muss als nächstes ein Komma folgen -
     * oder das Satzende.
     */
    private final boolean kommaStehtAus;

    /**
     * Whether the story can be continued by a Satzreihenglied without subject where
     * the player character is the implicit subject (such as " und gehst durch die Tür.")
     */
    private final boolean allowsAdditionalDuSatzreihengliedOhneSubjekt;

    /**
     * The creature the user has been talking to
     * recently - if any.
     */
    @Nullable
    private final Creature talkingTo;

    /**
     * The last object the user interacted with
     * recently - if any.
     */
    @Nullable
    private final AvObject lastObject;

    /**
     * The room the user was in before the last action.
     */
    @NonNull
    private final AvRoom lastRoom;

    private final boolean dann;

    StoryState butWithText(final String newText) {
        return new StoryState(lastActionClassName,
                startsNew,
                newText,
                kommaStehtAus,
                allowsAdditionalDuSatzreihengliedOhneSubjekt, dann,
                talkingTo,
                lastObject,
                lastRoom);
    }

    StoryState(@Nullable final IPlayerAction lastAction,
               @NonNull final StartsNew startsNew,
               @NonNull final String text,
               final boolean kommaStehtAus,
               final boolean allowsAdditionalDuSatzreihengliedOhneSubjekt,
               final boolean dann,
               @Nullable final Creature talkingTo,
               @Nullable final AvObject lastObject,
               @NonNull final AvRoom lastRoom) {
        this(lastAction == null ? null : lastAction.getClass().getCanonicalName(),
                startsNew,
                text,
                kommaStehtAus,
                allowsAdditionalDuSatzreihengliedOhneSubjekt, dann,
                talkingTo,
                lastObject,
                lastRoom);
    }

    StoryState(@Nullable final String lastActionClassName,
               @NonNull final StartsNew startsNew,
               @NonNull final String text,
               final boolean kommaStehtAus,
               final boolean allowsAdditionalDuSatzreihengliedOhneSubjekt,
               final boolean dann,
               @Nullable final Creature talkingTo,
               @Nullable final AvObject lastObject,
               @NonNull final AvRoom lastRoom) {
        this.lastActionClassName = lastActionClassName;
        this.startsNew = startsNew;
        this.text = text;
        this.kommaStehtAus = kommaStehtAus;
        this.allowsAdditionalDuSatzreihengliedOhneSubjekt =
                allowsAdditionalDuSatzreihengliedOhneSubjekt;
        this.dann = dann;
        this.lastRoom = lastRoom;
        this.talkingTo = talkingTo;
        this.lastObject = lastObject;
    }

    StartsNew getStartsNew() {
        return startsNew;
    }

    public String getText() {
        return text;
    }

    protected boolean kommaStehtAus() {
        return kommaStehtAus;
    }

    public boolean allowsAdditionalDuSatzreihengliedOhneSubjekt() {
        return allowsAdditionalDuSatzreihengliedOhneSubjekt;
    }

    public boolean talkingToAnyone() {
        return talkingTo != null;
    }

    public boolean talkingTo(final Creature.Key creatureKey) {
        return talkingTo(Creature.get(creatureKey));
    }

    private boolean talkingTo(final Creature creature) {
        return creature.equals(talkingTo);
    }

    @Nullable
    Creature getTalkingTo() {
        return talkingTo;
    }

    public boolean noLastObject() {
        return lastObject == null;
    }

    public boolean lastObjectWas(final AvObject object) {
        return object.equals(lastObject);
    }

    @Nullable
    AvObject getLastObject() {
        return lastObject;
    }

    public boolean lastRoomWas(final AvRoom room) {
        return room == lastRoom;
    }

    @NonNull
    public AvRoom getLastRoom() {
        return lastRoom;
    }

    public boolean lastActionWas(final Class<? extends IPlayerAction> actionClass) {
        return actionClass.getCanonicalName().equals(lastActionClassName);
    }

    @Nullable
    String getLastActionClassName() {
        return lastActionClassName;
    }

    public boolean dann() {
        return dann;
    }

    StoryState prependTo(final StoryState other) {
        String res = getText().trim();

        switch (other.startsNew) {
            case WORD:
                if (kommaNeeded(res, other.getText())) {
                    res += ",";
                }

                if (spaceNeeded(res, other.getText())) {
                    res += " ";
                }
                break;
            case SENTENCE:
                if (periodNeededToStartNewSentence(res, other.getText())) {
                    res += ".";
                }
                if (spaceNeeded(res, other.getText())) {
                    res += " ";
                }
                break;
            case PARAGRAPH:
                if (periodNeededToStartNewSentence(res, other.getText())) {
                    res += ".";
                }
                if (newlineNeededToStartNewParagraph(res, other.getText())) {
                    res += "\n";
                }
                break;
            default:
                throw new IllegalStateException("Unexpected Starts-New value: " + other.startsNew);
        }

        res += other.getText();

        return other.butWithText(res);
    }

    private static boolean newlineNeededToStartNewParagraph(
            final String base, final String addition) {
        final String baseTrimmed =
                base.trim();

        final String lastRelevantCharCurrent =
                baseTrimmed.substring(baseTrimmed.length() - 1);
        if (lastRelevantCharCurrent.equals("\n")) {
            return false;
        }

        final String firstCharAdditional =
                addition.trim().substring(0, 1);

        return !firstCharAdditional.equals("\n");
    }

    private static boolean periodNeededToStartNewSentence(
            final String base, final String addition) {
        final String baseTrimmed =
                base.trim();

        final String lastRelevantCharCurrent =
                baseTrimmed.substring(baseTrimmed.length() - 1);
        if (".!?\"“\n".contains(lastRelevantCharCurrent)) {
            return false;
        }

        final String firstCharAdditional =
                addition.trim().substring(0, 1);
        return !".!?".contains(firstCharAdditional);
    }

    private boolean kommaNeeded(final String base, final String addition) {
        if (!kommaStehtAus) {
            return false;
        }

        final String lastCharCurrent =
                base.substring(base.length() - 1);
        if (lastCharCurrent.equals(",")) {
            return false;
        }

        final String firstCharAdditional = addition.substring(0, 1);
        if (".,;!?“\n".contains(firstCharAdditional)) {
            return false;
        }

        return true;
    }

    private static boolean spaceNeeded(final String base, final String addition) {
        final String lastCharCurrent =
                base.substring(base.length() - 1);
        if (" „\n".contains(lastCharCurrent)) {
            return false;
        }

        final String firstCharAdditional = addition.substring(0, 1);
        if (" .,;!?“\n".contains(firstCharAdditional)) {
            return false;
        }

        return true;
    }
}
