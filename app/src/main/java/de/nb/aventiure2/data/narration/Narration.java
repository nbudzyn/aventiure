package de.nb.aventiure2.data.narration;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import de.nb.aventiure2.data.world.base.GameObjectId;
import de.nb.aventiure2.data.world.base.IGameObject;
import de.nb.aventiure2.german.base.GermanUtil;
import de.nb.aventiure2.german.base.NumerusGenus;
import de.nb.aventiure2.german.base.Personalpronomen;
import de.nb.aventiure2.german.base.PhorikKandidat;
import de.nb.aventiure2.german.base.StructuralElement;
import de.nb.aventiure2.german.description.TextDescription;
import de.nb.aventiure2.german.string.GermanStringUtil;

import static com.google.common.base.Preconditions.checkArgument;
import static de.nb.aventiure2.german.base.StructuralElement.SENTENCE;

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
     * Ob die wörtliche Rede noch "offen" ist.  Es steht also noch ein schließendes
     * Anführungszeichen aus. Wenn ein schließendes Anführungszeichen aussteht,
     * gibt es zwei Möglichkeiten, fortzufahren:
     * <ol>
     * <li>Es folgt (wenn noch kein Satzzeichen geschrieben wurde) ein Punkt, danach
     * das schließende Anführungszeichen - damit ist der Satz beendet.
     * <li>Es folgt kein Punkt, sondern direkt  das schließende Anführungszeichen. Damit kann
     * der Satz fortgesetzt werden (es sei denn, vor dem Anfürhungszeichen wäre bereits ein
     * Punkt geschrieben).
     * </ol>
     */
    private final boolean woertlicheRedeNochOffen;

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
     * <p>
     * Wir unterstützen nur Phorik-Kandidaten in der dritten Person.
     */
    @Nullable
    private final NumerusGenus phorikKandidatNumerusGenus;

    private final boolean dann;

    private final NarrationSource lastNarrationSource;

    public Narration(@NonNull final NarrationSource lastNarrationSource,
                     @NonNull final StructuralElement endsThis,
                     @NonNull final String text,
                     final boolean woertlicheRedeNochOffen, final boolean kommaStehtAus,
                     final boolean allowsAdditionalDuSatzreihengliedOhneSubjekt,
                     final boolean dann,
                     @Nullable final PhorikKandidat phorikKandidat) {
        this(lastNarrationSource, endsThis, text, woertlicheRedeNochOffen, kommaStehtAus,
                allowsAdditionalDuSatzreihengliedOhneSubjekt, dann,
                phorikKandidat != null ?
                        ((GameObjectId) phorikKandidat.getBezugsobjekt()) : null,
                phorikKandidat != null ?
                        phorikKandidat.getNumerusGenus() : null);
    }

    Narration(@NonNull final NarrationSource lastNarrationSource,
              @NonNull final StructuralElement endsThis,
              @NonNull final String text,
              final boolean woertlicheRedeNochOffen, final boolean kommaStehtAus,
              final boolean allowsAdditionalDuSatzreihengliedOhneSubjekt,
              final boolean dann,
              @Nullable final GameObjectId phorikKandidatBezugsobjekt,
              @Nullable final NumerusGenus phorikKandidatNumerusGenus) {
        checkArgument(!allowsAdditionalDuSatzreihengliedOhneSubjekt
                        || endsThis == StructuralElement.WORD,
                "!allowsAdditionalDuSatzreihengliedOhneSubjekt "
                        + "|| endsThis == StructuralElement.WORD verletzt");
        this.woertlicheRedeNochOffen = woertlicheRedeNochOffen;
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

    boolean kommaStehtAus() {
        return kommaStehtAus;
    }

    boolean woertlicheRedeNochOffen() {
        return woertlicheRedeNochOffen;
    }

    boolean allowsAdditionalDuSatzreihengliedOhneSubjekt() {
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
    private Personalpronomen getAnaphPersPronWennMgl(final GameObjectId gameObjectId) {
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
        // IDEA Statt "dann" auch andere "Temporaladverbialia" verwenden,
        //  siehe "Grammatik der deutschen Sprache E2.3": darauf, danach,
        //  kurz danach, sofort, bald etc. Vielleicht abhängig davon, wie lang
        //  Das letzte Ereignis gedauert hat oder wie lang das aktuelle Ereignis
        //  dauert? Wenn das aktuelle Ereignis 0sek dauert, geschieht es offenbar
        //  gleichzeitig?! Also etwas schreiben wie "zugleich"?!
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
                  final TextDescription textDescription) {
        final StringBuilder resText = new StringBuilder(text.trim());

        final StructuralElement separation =
                StructuralElement.max(endsThis, textDescription.getStartsNew());

        final String text = textDescription.getText();
        switch (separation) {
            case WORD:
                resText.append(schliesseWoertlicheRedeFallsNoetig(
                        resText.toString(),
                        text,
                        false));

                if (kommaNeeded(resText.toString(),
                        text)) {
                    resText.append(",");
                }

                if (GermanUtil.spaceNeeded(resText.toString(),
                        text)) {
                    resText.append(" ");
                }
                break;
            case SENTENCE:
                resText.append(schliesseWoertlicheRedeFallsNoetig(
                        resText.toString(),
                        text,
                        true));

                if (periodNeededToStartNewSentence(resText.toString(),
                        text)) {
                    resText.append(".");
                }
                if (GermanUtil.spaceNeeded(
                        resText.toString(),
                        text)) {
                    resText.append(" ");
                }
                break;
            case PARAGRAPH:
                resText.append(schliesseWoertlicheRedeFallsNoetig(
                        resText.toString(),
                        text,
                        true));

                if (periodNeededToStartNewSentence(resText.toString(),
                        text)) {
                    resText.append(".");
                }
                if (newlineNeededToStartNewParagraph(resText.toString(),
                        text)) {
                    resText.append("\n");
                }
                break;
            case CHAPTER:
                resText.append(schliesseWoertlicheRedeFallsNoetig(
                        resText.toString(),
                        text,
                        true));

                if (periodNeededToStartNewSentence(resText.toString(),
                        text)) {
                    resText.append(".");
                }

                final int numNewlinesNeeded =
                        howManyNewlinesNeedeToStartNewChapter(resText.toString(),
                                text);
                for (int i = 0; i < numNewlinesNeeded; i++) {
                    resText.append("\n");
                }
                break;
            default:
                throw new IllegalStateException("Unexpected structural element value: "
                        + separation);
        }

        if (SENTENCE == StructuralElement.min(textDescription.getStartsNew(), SENTENCE)) {
            resText.append(GermanStringUtil.capitalize(text));
        } else {
            resText.append(text);
        }

        return new Narration(
                narrationSource,
                textDescription.getEndsThis(),
                resText.toString(),
                textDescription.isWoertlicheRedeNochOffen(), textDescription.isKommaStehtAus(),
                textDescription.isAllowsAdditionalDuSatzreihengliedOhneSubjekt(),
                textDescription.isDann(),
                textDescription.getPhorikKandidat());
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

        final String lastCharBase = base.substring(base.length() - 1);
        if (lastCharBase.equals(",")) {
            return false;
        }

        final String firstCharAdditional = addition.substring(0, 1);
        if (".,;!?\n".contains(firstCharAdditional)) {
            return false;
        }

        return true;
    }

    /**
     * Gibt den String zurück, mit dem die wörtliche Rede abgeschlossen wird - falls überhaupt
     * eine wörtliche Rede noch offen ist. Dies können ein Leerstring, "“" oder ".“" sein.
     *
     * @param satzende Ob der Satz damit beendet werden soll
     */
    private String schliesseWoertlicheRedeFallsNoetig(
            final String base, final String addition, final boolean satzende) {
        if (!woertlicheRedeNochOffen) {
            return "";
        }

        return schliesseWoertlicheRede(base, addition, satzende);
    }

    /**
     * Gibt den String zurück, mit dem die noch offene wörtliche Rede abgeschlossen wird.
     * Dies können ein Leerstring, "“" oder ".“" sein.
     *
     * @param satzende Ob der Satz damit beendet werden soll
     */
    private static String schliesseWoertlicheRede(
            final String base, final String addition, final boolean satzende) {

        if (satzende) {
            return schliesseWoertlicheRedeSatzende(base, addition);
        }

        return schliesseWoertlicheRedeNichtSatzende(base, addition);
    }

    @NonNull
    private static String schliesseWoertlicheRedeSatzende(final String base,
                                                          final String addition) {
        final String baseTrimmed = base.trim();
        final String additionTrimmed = addition.trim();

        final String lastRelevantCharBase = baseTrimmed.substring(baseTrimmed.length() - 1);
        if ("….!?:\"“".contains(lastRelevantCharBase)) {
            if (baseTrimmed.endsWith("…“") || baseTrimmed.endsWith(".“")
                    || baseTrimmed.endsWith("!“") || baseTrimmed.endsWith("?“")
                    || baseTrimmed.endsWith("…\"") || baseTrimmed.endsWith(".\"")
                    || baseTrimmed.endsWith("!\"") || baseTrimmed.endsWith("?\"")) {
                return "";
            }

            if (additionTrimmed.startsWith("“")) {
                return "";
            }

            return "“";
        }

        if (additionTrimmed.startsWith(".“")) {
            return "";
        }

        if (additionTrimmed.startsWith("“")) {
            return ".";
        }

        return ".“";
    }

    @NonNull
    private static String schliesseWoertlicheRedeNichtSatzende(final String base,
                                                               final String addition) {
        final String baseTrimmed = base.trim();
        final String additionTrimmed = addition.trim();

        if (baseTrimmed.endsWith("“")) {
            return "";
        }

        if (additionTrimmed.startsWith("“")) {
            return "";
        }

        return "“";

        // Das Komma sollte ohnehin durch kommaStehtAus gefordert sein
    }

    private static boolean periodNeededToStartNewSentence(
            final String base, final String addition) {
        final String baseTrimmed = base.trim();

        final String lastRelevantCharBase =
                baseTrimmed.substring(baseTrimmed.length() - 1);
        if ("….!?:\"“–\n".contains(lastRelevantCharBase)) {
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
