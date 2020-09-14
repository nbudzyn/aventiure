package de.nb.aventiure2.data.narration;

import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.PrimaryKey;

import javax.annotation.ParametersAreNonnullByDefault;

import de.nb.aventiure2.data.world.base.GameObjectId;
import de.nb.aventiure2.data.world.base.IGameObject;
import de.nb.aventiure2.german.base.NumerusGenus;
import de.nb.aventiure2.german.base.PhorikKandidat;
import de.nb.aventiure2.german.base.StructuralElement;
import de.nb.aventiure2.german.base.SubstantivischePhrase;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Builder for {@link Narration}.
 */
@ParametersAreNonnullByDefault
public class NarrationAddition {
    /**
     * This {@link Narration} starts a new ... (paragraph, e.g.)
     */
    private final StructuralElement startsNew;

    /**
     * This {@link Narration} ends this ... (paragraph, e.g.)
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
     * Whether the narration can be continued by a Satzreihenglied without subject where
     * the player character is the implicit subject (such as " und gehst durch die Tür.")
     */
    private boolean allowsAdditionalDuSatzreihengliedOhneSubjekt = false;
    // STORY Das Konzept könnte man verallgemeinern: Die NarrationAddition könnte am Ende
    //  Koordination (d.h. und-Verbindungen) auf verschiedenen Ebenen erlauben:
    //  - Du nimmst die Lampe UND DAS GLAS: Koordination im AkkObj des NEHMEN-Prädikat
    //  - Den Weg hinunter kommen eine Frau UND EIN MANN: Koordination im Subj des KOMMEN-Prädikats
    //  - Du hast gute Laune und GEHST WEITER: Koordination zweier Verben zum selben Subj (P2)
    //  - Die Frau hat gute Laune und GEHT WEITER: Koordination zweier Verben zum selben Subj (P3)
    //  - Die Frau geht den Weg hinunten UND DU GEHST HINTERHER: Koordination zweier Hauptsätze
    //  Dazu bräuchte man wohl eine Kontextinfo in der Art "Womit endet die NarrationAddition?"

    private boolean dann = false;

    /**
     * Hierauf könnte sich ein Pronomen (z.B. ein Personalpronomen) unmittelbar
     * danach (<i>anaphorisch</i>) beziehen. Dazu müssen (in aller Regel) die grammatischen
     * Merkmale übereinstimmen und es muss mit dem Pronomen dieses Bezugsobjekt
     * gemeint sein.
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
    private PhorikKandidat phorikKandidat;

    public static NarrationAddition t(
            final StructuralElement startsNew,
            final String text) {
        checkArgument(!TextUtils.isEmpty(text), "text is null or empty");
        checkNotNull(startsNew, "startsNew is null");

        return new NarrationAddition(startsNew, text);
    }


    private NarrationAddition(final StructuralElement startsNew,
                              final String text) {
        this.startsNew = startsNew;
        this.text = text;
    }

    public NarrationAddition phorikKandidat(final SubstantivischePhrase substantivischePhrase,
                                            final IGameObject gameObject) {
        phorikKandidat(substantivischePhrase.getNumerusGenus(), gameObject.getId());
        return this;
    }

    public NarrationAddition phorikKandidat(final NumerusGenus numerusGenus,
                                            final IGameObject gameObject) {
        phorikKandidat(numerusGenus, gameObject.getId());
        return this;
    }

    public NarrationAddition phorikKandidat(final NumerusGenus numerusGenus,
                                            final GameObjectId gameObjectId) {
        phorikKandidat(new PhorikKandidat(numerusGenus, gameObjectId));
        return this;
    }

    public NarrationAddition phorikKandidat(
            @Nullable final PhorikKandidat phorikKandidat) {
        this.phorikKandidat = phorikKandidat;
        return this;
    }

    public NarrationAddition beendet(final StructuralElement structuralElement) {
        endsThis = structuralElement;
        return this;
    }

    public NarrationAddition komma() {
        return komma(true);
    }

    public NarrationAddition komma(final boolean kommaStehtAus) {
        this.kommaStehtAus = kommaStehtAus;
        return this;
    }

    /**
     * Sets a flag that the text can be continued by a Satzreihenglied without subject where
     * the player character is the implicit subject
     */
    public NarrationAddition undWartest() {
        return undWartest(true);
    }

    public NarrationAddition undWartest(
            final boolean allowsAdditionalPlayerSatzreihengliedOhneSubjekt) {
        allowsAdditionalDuSatzreihengliedOhneSubjekt =
                allowsAdditionalPlayerSatzreihengliedOhneSubjekt;
        return this;
    }

    public NarrationAddition dann() {
        return dann(true);
    }

    public NarrationAddition dann(final boolean dann) {
        this.dann = dann;
        return this;
    }

    StructuralElement getStartsNew() {
        return startsNew;
    }

    StructuralElement getEndsThis() {
        return endsThis;
    }

    @NonNull
    String getText() {
        return text;
    }

    boolean isKommaStehtAus() {
        return kommaStehtAus;
    }

    boolean isAllowsAdditionalDuSatzreihengliedOhneSubjekt() {
        return allowsAdditionalDuSatzreihengliedOhneSubjekt;
    }

    boolean isDann() {
        return dann;
    }

    @Nullable
    PhorikKandidat getPhorikKandidat() {
        return phorikKandidat;
    }
}
