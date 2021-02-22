package de.nb.aventiure2.data.narration;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import de.nb.aventiure2.data.world.base.GameObjectId;
import de.nb.aventiure2.german.base.NumerusGenus;
import de.nb.aventiure2.german.base.PhorikKandidat;
import de.nb.aventiure2.german.base.StructuralElement;
import de.nb.aventiure2.german.description.TextDescription;

import static com.google.common.base.Preconditions.checkArgument;
import static de.nb.aventiure2.german.base.GermanUtil.beginnDecktKommaAb;
import static de.nb.aventiure2.german.base.GermanUtil.endeDecktKommaAb;
import static de.nb.aventiure2.german.base.GermanUtil.spaceNeeded;
import static de.nb.aventiure2.german.base.StructuralElement.SENTENCE;
import static de.nb.aventiure2.german.base.StructuralElement.WORD;
import static de.nb.aventiure2.german.string.GermanStringUtil.beginnStehtCapitalizeNichtImWeg;
import static de.nb.aventiure2.german.string.GermanStringUtil.breakToString;
import static de.nb.aventiure2.german.string.GermanStringUtil.capitalizeFirstLetter;
import static de.nb.aventiure2.german.string.GermanStringUtil.schliesseWoertlicheRede;

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

    public Narration(@NonNull final NarrationSource lastNarrationSource,
                     @NonNull final StructuralElement endsThis,
                     @NonNull final String text,
                     final boolean woertlicheRedeNochOffen, final boolean kommaStehtAus,
                     final boolean allowsAdditionalDuSatzreihengliedOhneSubjekt,
                     final boolean dann,
                     @Nullable final GameObjectId phorikKandidatBezugsobjekt,
                     @Nullable final NumerusGenus phorikKandidatNumerusGenus) {
        checkArgument(!allowsAdditionalDuSatzreihengliedOhneSubjekt
                        || endsThis == WORD,
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
                  final TextDescription additionDesc) {
        final StringBuilder resText = new StringBuilder(text.trim());

        final StructuralElement brreak =
                StructuralElement.max(endsThis, additionDesc.getStartsNew());

        final String addition = additionDesc.getText();

        resText.append(schliesseWoertlicheRedeFallsNoetig(
                resText.toString(),
                addition,
                brreak != WORD));

        boolean capitalize = false;

        if (kommaNeeded(resText.toString(), addition) && brreak == WORD) {
            resText.append(",");
            if (spaceNeeded(",", addition)) {
                resText.append(" ");
            }
        } else {
            resText.append(breakToString(resText.toString(),
                    brreak,
                    addition));

            if (brreak != WORD) {
                capitalize = true;
            }
        }

        final StructuralElement resEndsThis;
        if (capitalize && beginnStehtCapitalizeNichtImWeg(addition)) {
            final String capitalizedAddition = capitalizeFirstLetter(addition);
            resText.append(capitalizedAddition);

            if (!capitalizedAddition.equals(addition)) {
                resEndsThis = additionDesc.getEndsThis();
            } else {
                // Diese TextDescription war nur etwas wie "„".
                // Der Text der folgenden TextDescription muss großgeschrieben werden.
                resEndsThis = StructuralElement.min(SENTENCE,
                        additionDesc.getEndsThis());
            }
        } else {
            resText.append(addition);
            resEndsThis = additionDesc.getEndsThis();
        }

        return new Narration(
                narrationSource,
                resEndsThis,
                resText.toString().trim(),
                additionDesc.isWoertlicheRedeNochOffen(), additionDesc.isKommaStehtAus(),
                additionDesc.isAllowsAdditionalDuSatzreihengliedOhneSubjekt(),
                additionDesc.isDann(),
                additionDesc.getPhorikKandidat());
    }

    private boolean kommaNeeded(final String base, final String addition) {
        if (!kommaStehtAus) {
            return false;
        }

        if (endeDecktKommaAb(base)) {
            return false;
        }

        return !beginnDecktKommaAb(addition);
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

    NarrationSource getLastNarrationSource() {
        return lastNarrationSource;
    }
}
