package de.nb.aventiure2.data.storystate;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.common.base.Preconditions;

import de.nb.aventiure2.data.world.base.GameObjectId;
import de.nb.aventiure2.data.world.base.IGameObject;
import de.nb.aventiure2.german.base.NumerusGenus;
import de.nb.aventiure2.german.base.Personalpronomen;
import de.nb.aventiure2.german.base.PhorikKandidat;
import de.nb.aventiure2.german.base.StructuralElement;

import static de.nb.aventiure2.data.storystate.StoryState.NarrationSource.REACTIONS;

/**
 * The text of the story, together with state relevant for going on with the story. Only things that have already happened.
 */
@Entity
public class StoryState {
    public enum NarrationSource {
        INITIALIZATION, SC_ACTION, REACTIONS,
    }

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
     * Hierauf könnte sich ein Pronomen (z.B. ein Personalpronomen) unmittelbar
     * danach (<i>anaphorisch</i>) beziehen.
     * <p>
     * Dieses Feld nur gesetzt werden wenn man sich sicher ist, wenn es also keine
     * Fehlreferenzierungen, Doppeldeutigkeiten
     * oder unerwünschten Wiederholungen geben kann. Typische Fälle wären "Du nimmst die Lampe und
     * zündest sie an." oder "Du stellst die Lampe auf den Tisch und zündest sie an."
     * <p>
     * Negatitvbeispiele wäre:
     * <ul>
     *     <li>"Du stellst die Lampe auf die Theke und zündest sie an." (Fehlreferenzierung)
     *     <li>"Du nimmst den Ball und den Schuh und wirfst ihn in die Luft." (Doppeldeutigkeit)
     *     <li>"Du nimmst die Lampe und zündest sie an. Dann stellst du sie wieder ab,
     *     schaust sie dir aber dann noch einmal genauer an: Sie ... sie ... sie" (Unerwünschte
     *     Wiederholung)
     *     <li>"Du stellst die Lampe auf den Tisch. Der Tisch ist aus Holz und hat viele
     *     schöne Gravuren - er muss sehr wertvoll sein. Dann nimmst du sie wieder in die Hand."
     *     (Referenziertes Objekt zu weit entfernt.)
     * </ul>
     */
    @Nullable
    private final GameObjectId phorikKandidatBezugsobjekt;

    /**
     * Damit sich ein Pronomen (z.B. ein Personalpronomen) auf das
     * {@link #phorikKandidatBezugsobjekt} beziehen kann, müssen
     * diese grammatikalischen Merkmale übereinstimmen.
     */
    @Nullable
    private final NumerusGenus phorikKandidatNumerusGenus;

    private final boolean dann;

    private final NarrationSource lastNarrationSource;

    public StoryState(@NonNull final NarrationSource lastNarrationSource,
                      @NonNull final StructuralElement endsThis,
                      @NonNull final String text,
                      final boolean kommaStehtAus,
                      final boolean allowsAdditionalDuSatzreihengliedOhneSubjekt,
                      final boolean dann,
                      @Nullable final PhorikKandidat phorikKandidat) {
        this(lastNarrationSource, endsThis, text, kommaStehtAus,
                allowsAdditionalDuSatzreihengliedOhneSubjekt, dann,
                phorikKandidat != null ?
                        ((GameObjectId) phorikKandidat.getBezugsobjekt()) : null,
                phorikKandidat != null ?
                        phorikKandidat.getNumerusGenus() : null);
    }

    StoryState(@NonNull final NarrationSource lastNarrationSource,
               @NonNull final StructuralElement endsThis,
               @NonNull final String text,
               final boolean kommaStehtAus,
               final boolean allowsAdditionalDuSatzreihengliedOhneSubjekt,
               final boolean dann,
               @Nullable final GameObjectId phorikKandidatBezugsobjekt,
               @Nullable final NumerusGenus phorikKandidatNumerusGenus) {
        Preconditions.checkArgument(!allowsAdditionalDuSatzreihengliedOhneSubjekt
                        || endsThis == StructuralElement.WORD,
                "!allowsAdditionalDuSatzreihengliedOhneSubjekt "
                        + "|| endsThis == StructuralElement.WORD verletzt");
        this.lastNarrationSource = lastNarrationSource;
        this.endsThis = endsThis;
        this.text = text;
        this.kommaStehtAus = kommaStehtAus;
        this.allowsAdditionalDuSatzreihengliedOhneSubjekt =
                allowsAdditionalDuSatzreihengliedOhneSubjekt;
        this.dann = dann;
        this.phorikKandidatBezugsobjekt = phorikKandidatBezugsobjekt;
        this.phorikKandidatNumerusGenus = phorikKandidatNumerusGenus;
    }

    public StructuralElement getEndsThis() {
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
     * Gibt das Personalpronomen zurück, mit dem ein
     * anaphorischer Bezug auf dieses
     * Game Object möglich ist.
     * <br/>
     * Beispiel: "Du hebst du Lampe auf..." - jetzt ist ein anaphorischer Bezug
     * auf die Lampe möglich und diese Methode gibt "sie" zurück.
     */
    @Nullable
    public Personalpronomen getAnaphPersPronWennMgl(final IGameObject gameObject) {
        return getAnaphPersPronWennMgl(gameObject.getId());
    }

    /**
     * Gibt das Personalpronomen zurück, mit dem ein
     * anaphorischer (rückgreifender) Bezug auf dieses
     * Game Object möglich ist.
     * <br/>
     * Beispiel: "Du hebst du Lampe auf..." - jetzt ist ein anaphorischer Bezug
     * auf die Lampe möglich und diese Methode gibt "sie" zurück.
     */
    @Nullable
    public Personalpronomen getAnaphPersPronWennMgl(final GameObjectId gameObjectId) {
        @Nullable final PhorikKandidat phorikKandidat = getPhorikKandidat();
        if (phorikKandidat == null) {
            return null;
        }

        if (!phorikKandidat.getBezugsobjekt().equals(gameObjectId)) {
            return null;
        }

        return Personalpronomen.get(phorikKandidat.getNumerusGenus());
    }

    /**
     * Ob ein anaphorischer Bezug (z.B. mit einem Personalpronomen) auf dieses
     * Game Object möglich ist.
     * <br/>
     * Beispiel: "Du hebst du Lampe auf..." - jetzt ist ein anaphorischer Bezug
     * auf die Lampe mittels des Personalpronomens "sie" möglich:
     * "... und nimmst sie mit."
     */
    public boolean isAnaphorischerBezugMoeglich(final NumerusGenus numerusGenus,
                                                final IGameObject gameObject) {
        return isAnaphorischerBezugMoeglich(numerusGenus, gameObject.getId());
    }

    /**
     * Ob ein anaphorischer Bezug (z.B. mit einem Personalpronomen) auf dieses
     * Game Object möglich ist.
     * <br/>
     * Beispiel: "Du hebst du Lampe auf..." - jetzt ist ein anaphorischer Bezug
     * auf die Lampe mittels des Personalpronomens "sie" möglich:
     * "... und nimmst sie mit."
     */
    public boolean isAnaphorischerBezugMoeglich(final NumerusGenus numerusGenus,
                                                final GameObjectId gameObjectId) {
        @Nullable final PhorikKandidat phorikKandidat = getPhorikKandidat();
        if (phorikKandidat == null) {
            return false;
        }

        return phorikKandidat.isBezugMoeglich(numerusGenus, gameObjectId);
    }

    @Nullable
    PhorikKandidat getPhorikKandidat() {
        if (phorikKandidatBezugsobjekt == null ||
                phorikKandidatNumerusGenus == null) {
            return null;
        }

        return new PhorikKandidat(phorikKandidatNumerusGenus,
                phorikKandidatBezugsobjekt);
    }

    public boolean dann() {
        // TODO "Dann" nicht bei statischen Verben (du hast Glück, du hast Hunger) verwenden

        // TODO "Dann" nur verwenden, wenn der es einen Aktor gibt und der Aktor im letzten
        //  Satz gleich war. (Nach der Logik kann man dann auch für Beschreibungen in
        //  der dritten Person verwenden!)

        // STORY Statt dann auch andere "Temporaladverbialia" verwenden,
        //  siehe "Grammatik der deutschen Sprache E2.3": darauf,
        //  kurz danach, sofort, bald etc. Vielleicht abhängig davon, wie lang
        //  Das letzte Ereignis gedauert hat oder wie lang das aktuelle Ereignis
        //  dauert? Wenn das aktuelle Ereignis 0sek dauert, geschieht es offenbar
        //  gleichzeitig?! Also etwas schreiben wie "zugleich"?!

        // TODO Idee könnte sein, dass erst mehrere Alternativen erstellt werden
        //  und auf Basis des bisherigen Textes ermittelt wird, welche Alternative
        //  besser passt?
        return dann;
    }

    @Nullable
    GameObjectId getPhorikKandidatBezugsobjekt() {
        return phorikKandidatBezugsobjekt;
    }

    @Nullable
    NumerusGenus getPhorikKandidatNumerusGenus() {
        return phorikKandidatNumerusGenus;
    }

    StoryState add(final NarrationSource narrationSource, final StoryAddition storyAddition) {
        String resText = getText().trim();

        final StructuralElement separation =
                StructuralElement.max(endsThis, storyAddition.getStartsNew());

        switch (separation) {
            case WORD:
                if (kommaNeeded(resText, storyAddition.getText())) {
                    resText += ",";
                }

                if (spaceNeeded(resText, storyAddition.getText())) {
                    resText += " ";
                }
                break;
            case SENTENCE:
                if (periodNeededToStartNewSentence(resText, storyAddition.getText())) {
                    resText += ".";
                }
                if (spaceNeeded(resText, storyAddition.getText())) {
                    resText += " ";
                }
                break;
            case PARAGRAPH:
                if (periodNeededToStartNewSentence(resText, storyAddition.getText())) {
                    resText += ".";
                }
                if (newlineNeededToStartNewParagraph(resText, storyAddition.getText())) {
                    resText += "\n";
                }
                break;
            case CHAPTER:
                if (periodNeededToStartNewSentence(resText, storyAddition.getText())) {
                    resText += ".";
                }

                final int numNewlinesNeeded =
                        howManyNewlinesNeedeToStartNewChapter(resText, storyAddition.getText());
                for (int i = 0; i < numNewlinesNeeded; i++) {
                    resText += "\n";
                }
                break;
            default:
                throw new IllegalStateException("Unexpected structural element value: "
                        + separation);
        }

        resText += storyAddition.getText();

        return new StoryState(
                narrationSource,
                storyAddition.getEndsThis(),
                resText,
                storyAddition.isKommaStehtAus(),
                storyAddition.isAllowsAdditionalDuSatzreihengliedOhneSubjekt(),
                storyAddition.isDann(),
                storyAddition.getPhorikKandidat());
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

    public boolean lastNarrationWasFomReaction() {
        return isLastNarrationSource(REACTIONS);
    }

    private boolean isLastNarrationSource(final NarrationSource narrationSource) {
        return getLastNarrationSource() == narrationSource;
    }

    NarrationSource getLastNarrationSource() {
        return lastNarrationSource;
    }
}
