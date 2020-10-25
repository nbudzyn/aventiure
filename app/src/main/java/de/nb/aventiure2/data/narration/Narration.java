package de.nb.aventiure2.data.narration;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.common.base.Preconditions;

import de.nb.aventiure2.data.world.base.GameObjectId;
import de.nb.aventiure2.data.world.base.IGameObject;
import de.nb.aventiure2.german.base.GermanUtil;
import de.nb.aventiure2.german.base.NumerusGenus;
import de.nb.aventiure2.german.base.Personalpronomen;
import de.nb.aventiure2.german.base.PhorikKandidat;
import de.nb.aventiure2.german.base.StructuralElement;
import de.nb.aventiure2.german.description.AllgDescription;

/**
 * The text of the narration, together with state relevant for going on with the narration.
 * Only things that have already happened.
 */
@Entity
public class Narration {
    public enum NarrationSource {
        INITIALIZATION, SC_ACTION, REACTIONS,
    }

    /**
     * This {@link Narration} ends this ... (paragraph, e.g.)
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
     * Whether the narration can be continued by a Satzreihenglied without subject where
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

    public Narration(@NonNull final NarrationSource lastNarrationSource,
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

    Narration(@NonNull final NarrationSource lastNarrationSource,
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

    boolean allowsAdditionalDuSatzreihengliedOhneSubjekt() {
        return allowsAdditionalDuSatzreihengliedOhneSubjekt;
    }

    /**
     * Ob dieses Game Object zurzeit <i>Thema</i> ist (im Sinne von Thema - Rhema).
     */
    boolean isThema(@NonNull final GameObjectId gameObjectId) {
        if (gameObjectId.equals(phorikKandidatBezugsobjekt)) {
            return true;
        }

        // STORY es gibt auch noch andere Fälle, wo das Game Object Thema sein könnte...
        //  (Auch im Narrator anpassen!)

        return false;
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
    Personalpronomen getAnaphPersPronWennMgl(final IGameObject gameObject) {
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
    Personalpronomen getAnaphPersPronWennMgl(final GameObjectId gameObjectId) {
        return PhorikKandidat.getAnaphPersPronWennMgl(getPhorikKandidat(), gameObjectId);
    }

    /**
     * Ob ein anaphorischer Bezug (z.B. mit einem Personalpronomen) auf dieses
     * Game Object möglich ist.
     * <br/>
     * Beispiel: "Du hebst du Lampe auf..." - jetzt ist ein anaphorischer Bezug
     * auf die Lampe mittels des Personalpronomens "sie" möglich:
     * "... und nimmst sie mit."
     */
    boolean isAnaphorischerBezugMoeglich(final GameObjectId gameObjectId) {
        return PhorikKandidat.isAnaphorischerBezugMoeglich(getPhorikKandidat(), gameObjectId);
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

    boolean dann() {
        // STORY Statt dann auch andere "Temporaladverbialia" verwenden,
        //  siehe "Grammatik der deutschen Sprache E2.3": darauf, danach,
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

    Narration add(final NarrationSource narrationSource,
                  final AllgDescription allgDescription) {
        final StringBuilder resText = new StringBuilder(getText().trim());

        final StructuralElement separation =
                StructuralElement.max(endsThis, allgDescription.getStartsNew());

        switch (separation) {
            case WORD:
                if (kommaNeeded(resText.toString(), allgDescription.getDescriptionHauptsatz())) {
                    resText.append(",");
                }

                if (GermanUtil
                        .spaceNeeded(resText.toString(),
                                allgDescription.getDescriptionHauptsatz())) {
                    resText.append(" ");
                }
                break;
            case SENTENCE:
                if (periodNeededToStartNewSentence(resText.toString(),
                        allgDescription.getDescriptionHauptsatz())) {
                    resText.append(".");
                }
                if (GermanUtil
                        .spaceNeeded(resText.toString(),
                                allgDescription.getDescriptionHauptsatz())) {
                    resText.append(" ");
                }
                break;
            case PARAGRAPH:
                if (periodNeededToStartNewSentence(resText.toString(),
                        allgDescription.getDescriptionHauptsatz())) {
                    resText.append(".");
                }
                if (newlineNeededToStartNewParagraph(resText.toString(),
                        allgDescription.getDescriptionHauptsatz())) {
                    resText.append("\n");
                }
                break;
            case CHAPTER:
                if (periodNeededToStartNewSentence(resText.toString(),
                        allgDescription.getDescriptionHauptsatz())) {
                    resText.append(".");
                }

                final int numNewlinesNeeded =
                        howManyNewlinesNeedeToStartNewChapter(resText.toString(),
                                allgDescription.getDescriptionHauptsatz());
                for (int i = 0; i < numNewlinesNeeded; i++) {
                    resText.append("\n");
                }
                break;
            default:
                throw new IllegalStateException("Unexpected structural element value: "
                        + separation);
        }

        resText.append(allgDescription.getDescriptionHauptsatz());

        return new Narration(
                narrationSource,
                allgDescription.getEndsThis(),
                resText.toString(),
                allgDescription.isKommaStehtAus(),
                allgDescription.isAllowsAdditionalDuSatzreihengliedOhneSubjekt(),
                allgDescription.isDann(),
                allgDescription.getPhorikKandidat());
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

    static boolean periodNeededToStartNewSentence(
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

    NarrationSource getLastNarrationSource() {
        return lastNarrationSource;
    }
}
