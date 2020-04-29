package de.nb.aventiure2.german.base;

import de.nb.aventiure2.data.storystate.StoryState;
import de.nb.aventiure2.data.world.time.AvTimeSpan;

/**
 * Abstract superclass for a description.
 */
public abstract class AbstractDescription<SELF extends AbstractDescription<?>> {
    /**
     * This {@link StoryState} starts a new ... (paragraph, e.g.)
     */
    private final StructuralElement startsNew;
    // TODO startsNew verwenden und aus StoryState ausbauen.

    /**
     * This {@link StoryState} ends this ... (paragraph, e.g.)
     */
    private StructuralElement endsThis = StructuralElement.WORD;
    // TODO beendet...() etc. wie bei StoryState anbieten
    // TODO endsThis verwenden und aus StoryState ausbauen.

    /**
     * Ob ein Komma aussteht. Wenn ein Komma aussteht, muss als Nächstes ein Komma folgen -
     * oder das Satzende.
     */
    private boolean kommaStehtAus = false;

    private boolean allowsAdditionalDuSatzreihengliedOhneSubjekt = false;

    private boolean dann = false;

    private final AvTimeSpan timeElapsed;

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

    /**
     * Zeit, die vergangen ist, während das das beschriebene geschehen ist
     */
    public AvTimeSpan getTimeElapsed() {
        return timeElapsed;
    }
}
