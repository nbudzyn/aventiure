package de.nb.aventiure2.data.storystate;

import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.PrimaryKey;

import de.nb.aventiure2.data.storystate.StoryState.StructuralElement;
import de.nb.aventiure2.data.world.base.GameObjectId;
import de.nb.aventiure2.data.world.base.IGameObject;
import de.nb.aventiure2.data.world.syscomp.alive.ILivingBeingGO;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Builder for {@link StoryState}.
 */
public class StoryStateBuilder {
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
    private GameObjectId talkingTo;

    /**
     * Wenn dieses Game Object als unmittelbar nächstes verwendet werden soll, kann man
     * ein Personalpronomen verwenden.
     * <p>
     * Darf nur gesetzt werden wenn man sich sicher ist, wenn es also keine
     * Fehlreferenzierungen, Doppeldeutigkeiten
     * oder unerwünschten Wiederholungen geben kann. Typische Fälle wären "Du nimmst du Lampe und
     * zündest sie an." oder "Du stellst die Lampe auf den Tisch und zündest sie an."
     * <p>
     * Negatitvbeispiele wäre:
     * <ul>
     *     <li>"Du stellst die Lampe auf die Theke und zündest sie an." (Fehlreferenzierung)
     *     <li>"Du nimmst den Ball und den Schuh und wirfst ihn in die Luft." (Doppeldeutigkeit)
     *     <li>"Du nimmst die Lampe, und zündest sie an. Dann stellst du sie wieder ab,
     *     schaust sie dir aber dann noch einmal genauer an: Sie ... sie ... sie" (Unerwünschte
     *     Wiederholung)
     *     <li>"Du stellst die Lampe auf den Tisch. Der Tisch ist aus Holz und hat viele
     *     schöne Gravuren - er muss sehr wertvoll sein. Dann nimmst du sie wieder in die Hand."
     *     (Referenziertes Objekt zu weit entfernt.)
     * </ul>
     */
    @Nullable
    private GameObjectId persPronKandidat;

    public static StoryStateBuilder t(
            @NonNull final StructuralElement startsNew,
            @NonNull final String text) {
        checkArgument(!TextUtils.isEmpty(text), "text is null or empty");
        checkNotNull(startsNew, "startsNew is null");

        return new StoryStateBuilder(startsNew, text);
    }


    private StoryStateBuilder(@NonNull final StoryState.StructuralElement startsNew,
                              @NonNull final String text) {
        this.startsNew = startsNew;
        this.text = text;
    }

    public StoryStateBuilder imGespraechMit(final ILivingBeingGO talkingTo) {
        return imGespraechMit(talkingTo.getId());
    }

    public StoryStateBuilder imGespraechMit(final GameObjectId talkingTo) {
        this.talkingTo = talkingTo;
        return this;
    }

    public StoryStateBuilder imGespraechMitNiemandem() {
        talkingTo = null;
        return this;
    }

    /**
     * Legt den Kandidaten für ein Personalpronomen fest.
     *
     * @see #persPronKandidat
     */
    public StoryStateBuilder persPronKandidat(@Nullable final IGameObject lastObject) {
        return persPronKandidat(lastObject == null ? null : lastObject.getId());
    }

    /**
     * Legt den Kandidaten für ein Personalpronomen fest.
     *
     * @see #persPronKandidat
     */
    public StoryStateBuilder persPronKandidat(@Nullable final GameObjectId lastObject) {
        persPronKandidat = lastObject;
        return this;
    }

    public StoryStateBuilder beendet(final StructuralElement structuralElement) {
        endsThis = structuralElement;
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
        return new StoryState(
                startsNew,
                endsThis,
                text,
                kommaStehtAus,
                allowsAdditionalDuSatzreihengliedOhneSubjekt,
                dann,
                talkingTo,
                persPronKandidat);
    }
}
