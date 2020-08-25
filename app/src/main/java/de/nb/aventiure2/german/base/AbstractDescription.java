package de.nb.aventiure2.german.base;

import androidx.annotation.Nullable;

import de.nb.aventiure2.data.narration.Narration;
import de.nb.aventiure2.data.world.time.AvTimeSpan;

/**
 * Abstract superclass for a description.
 */
public abstract class AbstractDescription<SELF extends AbstractDescription<SELF>> {
    /**
     * This {@link Narration} starts a new ... (paragraph, e.g.)
     */
    private final StructuralElement startsNew;

    /**
     * This {@link Narration} ends this ... (paragraph, e.g.)
     */
    private StructuralElement endsThis = StructuralElement.WORD;

    /**
     * Ob ein Komma aussteht. Wenn ein Komma aussteht, muss als Nächstes ein Komma folgen -
     * oder das Satzende.
     */
    private boolean kommaStehtAus = false;

    private boolean allowsAdditionalDuSatzreihengliedOhneSubjekt = false;

    private boolean dann = false;

    private final AvTimeSpan timeElapsed;

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

    public AbstractDescription(final StructuralElement startsNew,
                               final AvTimeSpan timeElapsed) {
        this.startsNew = startsNew;
        this.timeElapsed = timeElapsed;
    }

    public StructuralElement getStartsNew() {
        return startsNew;
    }

    /**
     * Gibt die Beschreibung zurück, beginnend mit einem Hauptsatz
     */
    public abstract String getDescriptionHauptsatz();

    /**
     * Gibt die Beschreibung als Hauptsatz zurück, wenn nötig mit dem angegebenen
     * <code>konjunktionaladverb</code> ("dann", "darauf") im Vorfeld.
     */
    public abstract String
    getDescriptionHauptsatzMitKonjunktionaladverbWennNoetig(String konjunktionaladverb);

    public SELF komma() {
        return komma(true);
    }

    public SELF komma(final boolean kommaStehtAus) {
        this.kommaStehtAus = kommaStehtAus;
        return (SELF) this;
    }

    public boolean isKommaStehtAus() {
        return kommaStehtAus;
    }

    /**
     * Sets a flag that the text can be continued by a Satzreihenglied without subject where
     * the player character is the implicit subject
     */
    public SELF undWartest() {
        return undWartest(true);
    }

    public SELF undWartest(
            final boolean allowsAdditionalPlayerSatzreihengliedOhneSubjekt) {
        allowsAdditionalDuSatzreihengliedOhneSubjekt =
                allowsAdditionalPlayerSatzreihengliedOhneSubjekt;
        return (SELF) this;
    }

    public boolean isAllowsAdditionalDuSatzreihengliedOhneSubjekt() {
        return allowsAdditionalDuSatzreihengliedOhneSubjekt;
    }

    public SELF dann() {
        return dann(true);
    }

    public SELF dann(final boolean dann) {
        this.dann = dann;
        return (SELF) this;
    }

    public boolean isDann() {
        return dann;
    }

    public SELF beendet(final StructuralElement structuralElement) {
        endsThis = structuralElement;
        return (SELF) this;
    }

    public StructuralElement getEndsThis() {
        return endsThis;
    }

    public SELF phorikKandidat(final SubstantivischePhrase substantivischePhrase,
                               final IBezugsobjekt bezugsobjekt) {
        return phorikKandidat(substantivischePhrase.getNumerusGenus(), bezugsobjekt);
    }

    public SELF phorikKandidat(final NumerusGenus numerusGenus,
                               final IBezugsobjekt bezugsobjekt) {
        return phorikKandidat(new PhorikKandidat(numerusGenus, bezugsobjekt));
    }


    public SELF phorikKandidat(@Nullable final PhorikKandidat phorikKandidat) {
        this.phorikKandidat = phorikKandidat;
        return (SELF) this;
    }

    @Nullable
    public PhorikKandidat getPhorikKandidat() {
        return phorikKandidat;
    }

    /**
     * Zeit, die vergangen ist, während das das beschriebene geschehen ist
     */
    public AvTimeSpan getTimeElapsed() {
        return timeElapsed;
    }
}
