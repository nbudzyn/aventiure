package de.nb.aventiure2.german.description;

import androidx.annotation.Nullable;

import de.nb.aventiure2.data.world.base.GameObjectId;
import de.nb.aventiure2.data.world.base.IGameObject;
import de.nb.aventiure2.german.base.NumerusGenus;
import de.nb.aventiure2.german.base.PhorikKandidat;
import de.nb.aventiure2.german.base.SubstantivischePhrase;

import static com.google.common.base.Preconditions.checkArgument;
import static de.nb.aventiure2.german.base.Person.P3;

/**
 * Parameter einer {@link AbstractDescription} - mutable!
 */
public class DescriptionParams {
    /**
     * Whether the narration can be continued by a Satzreihenglied without subject where
     * the player character is the implicit subject (such as " und gehst durch die Tür.")
     */
    private boolean allowsAdditionalDuSatzreihengliedOhneSubjekt;
    private boolean dann;
    /**
     * Hierauf könnte sich ein Pronomen (z.B. ein Personalpronomen) unmittelbar
     * danach (<i>anaphorisch</i>) beziehen. Dazu müssen (in aller Regel) die grammatischen
     * Merkmale übereinstimmen und es muss mit dem Pronomen dieses Bezugsobjekt
     * gemeint sein.
     * <p>
     * Dieses Feld sollte nur gesetzt werden wenn man sich sicher ist, wenn es also keine
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

    DescriptionParams copy() {
        return new DescriptionParams(
                allowsAdditionalDuSatzreihengliedOhneSubjekt, dann, phorikKandidat);
    }

    DescriptionParams(@Nullable final PhorikKandidat phorikKandidat) {
        this(false, false, phorikKandidat);
    }

    private DescriptionParams(final boolean allowsAdditionalDuSatzreihengliedOhneSubjekt,
                              final boolean dann,
                              @Nullable final PhorikKandidat phorikKandidat) {
        this.allowsAdditionalDuSatzreihengliedOhneSubjekt =
                allowsAdditionalDuSatzreihengliedOhneSubjekt;
        this.dann = dann;
        this.phorikKandidat = phorikKandidat;
    }

    /**
     * Erzeugt einen {@link PhorikKandidat}en. Wir unterstützen nur
     * Phorik-Kandidaten in der dritten Person!
     *
     * @param substantivischePhrase Substantivische Phrase in der dritten Person
     */
    public void phorikKandidat(final SubstantivischePhrase substantivischePhrase,
                               final IGameObject gameObject) {
        checkArgument(substantivischePhrase.getPerson() == P3,
                "Substantivische Phrase %s hat falsche "
                        + "Person: %s. Für Phorik-Kandiaten "
                        + "ist nur 3. Person zugelassen.", substantivischePhrase,
                substantivischePhrase.getPerson());

        phorikKandidat(substantivischePhrase.getNumerusGenus(), gameObject.getId());
    }

    /**
     * Erzeugt einen {@link PhorikKandidat}en. Wir unterstützen nur
     * Phorik-Kandidaten in der dritten Person!
     */
    public void phorikKandidat(final NumerusGenus numerusGenus,
                               final IGameObject gameObject) {
        phorikKandidat(numerusGenus, gameObject.getId());
    }

    /**
     * Erzeugt einen {@link PhorikKandidat}en. Wir unterstützen nur
     * Phorik-Kandidaten in der dritten Person.
     */
    public void phorikKandidat(final NumerusGenus numerusGenus,
                               final GameObjectId gameObjectId) {
        phorikKandidat(new PhorikKandidat(numerusGenus, gameObjectId));
    }

    public void phorikKandidat(
            @Nullable final PhorikKandidat phorikKandidat) {
        this.phorikKandidat = phorikKandidat;
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