package de.nb.aventiure2.german.description;

import androidx.annotation.Nullable;

import de.nb.aventiure2.data.narration.Narration;
import de.nb.aventiure2.data.world.base.GameObjectId;
import de.nb.aventiure2.data.world.base.IGameObject;
import de.nb.aventiure2.german.base.NumerusGenus;
import de.nb.aventiure2.german.base.PhorikKandidat;
import de.nb.aventiure2.german.base.StructuralElement;
import de.nb.aventiure2.german.base.SubstantivischePhrase;

/**
 * Parameter einer {@link AbstractDescription} - mutable!
 */
public class DescriptionParams {
    /**
     * This {@link Narration} starts a new ... (paragraph, e.g.)
     */
    private StructuralElement startsNew;
    /**
     * This {@link Narration} ends this ... (paragraph, e.g.)
     */
    private StructuralElement endsThis;
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

    public DescriptionParams copy() {
        return new DescriptionParams(startsNew, endsThis, kommaStehtAus,
                allowsAdditionalDuSatzreihengliedOhneSubjekt, dann, phorikKandidat);
    }

    private DescriptionParams(final StructuralElement startsNew, final StructuralElement endsThis,
                              final boolean kommaStehtAus,
                              final boolean allowsAdditionalDuSatzreihengliedOhneSubjekt,
                              final boolean dann, final PhorikKandidat phorikKandidat) {
        this.startsNew = startsNew;
        this.endsThis = endsThis;
        this.kommaStehtAus = kommaStehtAus;
        this.allowsAdditionalDuSatzreihengliedOhneSubjekt =
                allowsAdditionalDuSatzreihengliedOhneSubjekt;
        this.dann = dann;
        this.phorikKandidat = phorikKandidat;
    }

    public DescriptionParams(final StructuralElement startsNew) {
        this.startsNew = startsNew;
        endsThis = StructuralElement.WORD;
    }

    public void setStartsNew(final StructuralElement startsNew) {
        this.startsNew = startsNew;
    }

    public void phorikKandidat(final SubstantivischePhrase substantivischePhrase,
                               final IGameObject gameObject) {
        phorikKandidat(substantivischePhrase.getNumerusGenus(), gameObject.getId());
    }

    public void phorikKandidat(final NumerusGenus numerusGenus,
                               final IGameObject gameObject) {
        phorikKandidat(numerusGenus, gameObject.getId());
    }

    public void phorikKandidat(final NumerusGenus numerusGenus,
                               final GameObjectId gameObjectId) {
        phorikKandidat(new PhorikKandidat(numerusGenus, gameObjectId));
    }

    public void phorikKandidat(
            @Nullable final PhorikKandidat phorikKandidat) {
        this.phorikKandidat = phorikKandidat;
    }

    public void beendet(final StructuralElement structuralElement) {
        endsThis = structuralElement;
    }

    public void komma() {
        komma(true);
    }

    public void komma(final boolean kommaStehtAus) {
        this.kommaStehtAus = kommaStehtAus;
    }

    /**
     * Sets a flag that the text can be continued by a Satzreihenglied without subject where
     * the player character is the implicit subject
     */
    public void undWartest() {
        undWartest(true);
    }

    public void undWartest(
            final boolean allowsAdditionalPlayerSatzreihengliedOhneSubjekt) {
        allowsAdditionalDuSatzreihengliedOhneSubjekt =
                allowsAdditionalPlayerSatzreihengliedOhneSubjekt;
    }

    public void dann() {
        dann(true);
    }

    public void dann(final boolean dann) {
        this.dann = dann;
    }

    public StructuralElement getStartsNew() {
        return startsNew;
    }

    public StructuralElement getEndsThis() {
        return endsThis;
    }

    public boolean isKommaStehtAus() {
        return kommaStehtAus;
    }

    public boolean isAllowsAdditionalDuSatzreihengliedOhneSubjekt() {
        return allowsAdditionalDuSatzreihengliedOhneSubjekt;
    }

    public boolean isDann() {
        return dann;
    }

    @Nullable
    public PhorikKandidat getPhorikKandidat() {
        return phorikKandidat;
    }
}