package de.nb.aventiure2.data.narration;

import static com.google.common.base.Preconditions.checkArgument;
import static de.nb.aventiure2.german.base.GermanUtil.beginnDecktKommaAb;
import static de.nb.aventiure2.german.base.GermanUtil.endeDecktKommaAb;
import static de.nb.aventiure2.german.base.GermanUtil.spaceNeeded;
import static de.nb.aventiure2.german.base.StructuralElement.SENTENCE;
import static de.nb.aventiure2.german.base.StructuralElement.WORD;
import static de.nb.aventiure2.german.string.GermanStringUtil.appendBreak;
import static de.nb.aventiure2.german.string.GermanStringUtil.beginnStehtCapitalizeNichtImWeg;
import static de.nb.aventiure2.german.string.GermanStringUtil.capitalizeFirstLetter;
import static de.nb.aventiure2.german.string.GermanStringUtil.schliesseWoertlicheRede;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import de.nb.aventiure2.data.world.base.GameObjectId;
import de.nb.aventiure2.german.base.PhorikKandidat;
import de.nb.aventiure2.german.base.StructuralElement;
import de.nb.aventiure2.german.description.TextDescription;
import de.nb.aventiure2.german.string.NoLetterException;

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
     * Das {@link StructuralElement}, mit dem diese Narration endet: Ein Kapitelende,
     * ein Absatzende, ...
     */
    private final StructuralElement endedBy;
    
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
     * Hierauf könnte sich ein Pronomen (z.B. ein Personalpronomen) beziehen kann (wenn
     * die grammatikalischen Merkmale übereinstimmen). Der PhorikKandidat muss eine
     * {@link GameObjectId} als {@link de.nb.aventiure2.german.base.IBezugsobjekt}
     * enthalten.
     */
    @Embedded
    @Nullable
    private final PhorikKandidat phorikKandidat;

    private final boolean dann;

    private final NarrationSource lastNarrationSource;

    public Narration(@NonNull final NarrationSource lastNarrationSource,
                     @NonNull final StructuralElement endedBy,
                     @NonNull final String text,
                     final boolean woertlicheRedeNochOffen, final boolean kommaStehtAus,
                     final boolean allowsAdditionalDuSatzreihengliedOhneSubjekt,
                     final boolean dann,
                     @Nullable final PhorikKandidat phorikKandidat) {
        checkArgument(!allowsAdditionalDuSatzreihengliedOhneSubjekt
                        || endedBy == WORD,
                "!allowsAdditionalDuSatzreihengliedOhneSubjekt "
                        + "|| endsThis == StructuralElement.WORD verletzt");

        checkArgument(phorikKandidat == null
                        || phorikKandidat.getBezugsobjekt() instanceof GameObjectId,
                "PhorikKandidat muss sich hier auf eine GameObjecktId beziehen - keine "
                        + "anderen Bezugsobjekte erlaubt");

        this.woertlicheRedeNochOffen = woertlicheRedeNochOffen;
        this.lastNarrationSource = lastNarrationSource;
        this.endedBy = endedBy;
        this.text = text;
        this.kommaStehtAus = kommaStehtAus;
        this.allowsAdditionalDuSatzreihengliedOhneSubjekt =
                allowsAdditionalDuSatzreihengliedOhneSubjekt;
        this.dann = dann;
        this.phorikKandidat = phorikKandidat;
    }

    StructuralElement getEndedBy() {
        return endedBy;
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
        return phorikKandidat;
    }

    boolean dann() {
        // IDEA Statt "dann" auch andere "Temporaladverbialia" verwenden,
        //  siehe "Grammatik der deutschen Sprache E2.3": darauf, danach,
        //  kurz danach, sofort, bald etc. Vielleicht abhängig davon, wie lang
        //  Das letzte Ereignis gedauert hat oder wie lang das aktuelle Ereignis
        //  dauert? Wenn das aktuelle Ereignis 0sek dauert, geschieht es offenbar
        //  gleichzeitig?! Also etwas schreiben wie "zugleich"?!
        //  Ggf. schonLaenger() berücksichtigen.
        return dann;
    }

    Narration add(final NarrationSource narrationSource,
                  final TextDescription additionDesc) {
        final StringBuilder resText = new StringBuilder(text.trim());

        final StructuralElement brreak =
                StructuralElement.max(endedBy, additionDesc.getStartsNew());

        final String addition = additionDesc.getText();

        resText.append(schliesseWoertlicheRedeFallsNoetig(
                resText.toString(),
                addition,
                brreak != WORD));

        boolean capitalize = false;

        if (brreak == WORD && kommaNeeded(resText.toString(), addition)) {
            resText.append(",");
            if (spaceNeeded(",", addition)) {
                resText.append(" ");
            }
        } else {
            appendBreak(resText, brreak, addition);

            if (brreak != WORD) {
                capitalize = true;
            }
        }

        StructuralElement resEndedBy;
        if (capitalize && beginnStehtCapitalizeNichtImWeg(addition)) {
            try {
                resText.append(capitalizeFirstLetter(addition));
                resEndedBy = additionDesc.getEndsThis();
            } catch (final NoLetterException e) {
                // Diese TextDescription war nur etwas wie "„".
                // Der Text der folgenden TextDescription muss großgeschrieben werden.
                resText.append(addition);
                resEndedBy = StructuralElement.max(SENTENCE, additionDesc.getEndsThis());
            }
        } else {
            resText.append(addition);
            resEndedBy = additionDesc.getEndsThis();
        }

        return new Narration(
                narrationSource,
                resEndedBy,
                resText.toString().trim(),
                additionDesc.toSingleKonstituente().woertlicheRedeNochOffen(),
                additionDesc.isKommaStehtAus(),
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
