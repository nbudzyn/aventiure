package de.nb.aventiure2.data.storystate;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.common.base.Preconditions;

import de.nb.aventiure2.data.world.base.GameObjectId;
import de.nb.aventiure2.data.world.base.IGameObject;
import de.nb.aventiure2.german.base.StructuralElement;

/**
 * The text of the story, together with state relevant for going on with the story. Only things that have already happened.
 */
@Entity
public class StoryState {

    /**
     * This {@link StoryState} starts a new ... (paragraph, e.g.)
     */
    private final StructuralElement startsNew;

    /**
     * This {@link StoryState} ends this ... (paragraph, e.g.)
     */
    private final StructuralElement endsThis;

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
    private final GameObjectId persPronKandidat;

    private final boolean dann;

    private StoryState butWithText(final String newText) {
        return new StoryState(
                startsNew,
                endsThis,
                newText,
                kommaStehtAus,
                allowsAdditionalDuSatzreihengliedOhneSubjekt, dann,
                persPronKandidat);
    }

    StoryState(@NonNull final StructuralElement startsNew,
               @NonNull final StructuralElement endsThis,
               @NonNull final String text,
               final boolean kommaStehtAus,
               final boolean allowsAdditionalDuSatzreihengliedOhneSubjekt,
               final boolean dann,
               @Nullable final GameObjectId persPronKandidat) {
        Preconditions.checkArgument(!allowsAdditionalDuSatzreihengliedOhneSubjekt
                        || endsThis == StructuralElement.WORD,
                "!allowsAdditionalDuSatzreihengliedOhneSubjekt "
                        + "|| endsThis == StructuralElement.WORD verletzt");

        this.startsNew = startsNew;
        this.endsThis = endsThis;
        this.text = text;
        this.kommaStehtAus = kommaStehtAus;
        this.allowsAdditionalDuSatzreihengliedOhneSubjekt =
                allowsAdditionalDuSatzreihengliedOhneSubjekt;
        this.dann = dann;
        this.persPronKandidat = persPronKandidat;
    }

    StructuralElement getStartsNew() {
        return startsNew;
    }

    StructuralElement getEndsThis() {
        return endsThis;
    }

    @NonNull
    public String getText() {
        return text;
    }

    protected boolean kommaStehtAus() {
        return kommaStehtAus;
    }

    public boolean allowsAdditionalDuSatzreihengliedOhneSubjekt() {
        return allowsAdditionalDuSatzreihengliedOhneSubjekt;
    }

    /**
     * Es gibt keinen Kandidaten für ein Personalpronomen.
     *
     * @see #persPronKandidat
     */
    public boolean noPersPronKandidat() {
        return persPronKandidat == null;
    }

    /**
     * Legt den Kandidaten für ein Personalpronomen fest.
     *
     * @see #persPronKandidat
     */
    public boolean persPronKandidatIs(final IGameObject object) {
        return object.is(persPronKandidat);
    }

    /**
     * Gibt den Kandidaten für ein Personalpronomen zurück.
     *
     * @see #persPronKandidat
     */
    @Nullable
    GameObjectId getPersPronKandidat() {
        return persPronKandidat;
    }

    public boolean dann() {
        return dann;
    }

    StoryState prependTo(final StoryState other) {
        String res = getText().trim();

        final StructuralElement separation =
                StructuralElement.max(endsThis, other.startsNew);

        switch (separation) {
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
            case CHAPTER:
                if (periodNeededToStartNewSentence(res, other.getText())) {
                    res += ".";
                }

                final int numNewlinesNeeded =
                        howManyNewlinesNeedeToStartNewChapter(res, other.getText());
                for (int i = 0; i < numNewlinesNeeded; i++) {
                    res += "\n";
                }
                break;
            default:
                throw new IllegalStateException("Unexpected structural element value: "
                        + separation);
        }

        res += other.getText();

        return other.butWithText(res);
    }

    private static int howManyNewlinesNeedeToStartNewChapter(
            final String base, final String addition) {
        final String baseTrimmed = base.trim();
        final String additionTrimmed = addition.trim();

        if (baseTrimmed.endsWith("\n\n")) {
            return 0;
        }

        if (additionTrimmed.startsWith("\n\n")) {
            return 0;
        }

        if (baseTrimmed.endsWith("\n")) {
            if (additionTrimmed.startsWith("\n")) {
                return 0;
            }
            return 1;
        }

        if (additionTrimmed.startsWith("\n")) {
            return 1;
        }

        return 2;
    }

    private static boolean newlineNeededToStartNewParagraph(
            final String base, final String addition) {
        final String baseTrimmed = base.trim();
        if (baseTrimmed.endsWith("\n")) {
            return false;
        }

        final String additionTrimmed = addition.trim();
        return !additionTrimmed.startsWith("\n");
    }

    private boolean kommaNeeded(final String base, final String addition) {
        if (!kommaStehtAus) {
            return false;
        }

        final String lastCharBase =
                base.substring(base.length() - 1);
        if (lastCharBase.equals(",")) {
            return false;
        }

        final String firstCharAdditional = addition.substring(0, 1);
        if (".,;!?“\n".contains(firstCharAdditional)) {
            return false;
        }

        return true;
    }

    public static boolean periodNeededToStartNewSentence(
            final String base, final String addition) {
        final String baseTrimmed =
                base.trim();

        final String lastRelevantCharBase =
                baseTrimmed.substring(baseTrimmed.length() - 1);
        if ("….!?\"“\n".contains(lastRelevantCharBase)) {
            return false;
        }

        final String firstCharAdditional =
                addition.trim().substring(0, 1);
        return !".!?".contains(firstCharAdditional);
    }

    private static boolean spaceNeeded(final String base, final String addition) {
        final String lastCharBase =
                base.substring(base.length() - 1);
        if (" „\n".contains(lastCharBase)) {
            return false;
        }

        final String firstCharAdditional = addition.substring(0, 1);
        return !" .,;!?“\n".contains(firstCharAdditional);
    }
}
