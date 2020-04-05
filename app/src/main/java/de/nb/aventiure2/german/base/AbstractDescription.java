package de.nb.aventiure2.german.base;

import de.nb.aventiure2.data.world.time.AvTimeSpan;

/**
 * Abstract superclass for a description.
 */
public abstract class AbstractDescription {
    /**
     * Ob ein Komma aussteht. Wenn ein Komma aussteht, muss als Nächstes ein Komma folgen -
     * oder das Satzende.
     */
    private final boolean kommaStehtAus;

    private final boolean allowsAdditionalDuSatzreihengliedOhneSubjekt;

    private final boolean dann;

    private final AvTimeSpan timeElapsed;

    public AbstractDescription(final boolean kommaStehtAus,
                               final boolean allowsAdditionalDuSatzreihengliedOhneSubjekt,
                               final boolean dann,
                               final AvTimeSpan timeElapsed) {
        this.kommaStehtAus = kommaStehtAus;
        this.allowsAdditionalDuSatzreihengliedOhneSubjekt =
                allowsAdditionalDuSatzreihengliedOhneSubjekt;
        this.dann = dann;
        this.timeElapsed = timeElapsed;
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

    public boolean kommaStehtAus() {
        return kommaStehtAus;
    }

    public boolean allowsAdditionalDuSatzreihengliedOhneSubjekt() {
        return allowsAdditionalDuSatzreihengliedOhneSubjekt;
    }

    public boolean dann() {
        return dann;
    }


    /**
     * Zeit, die vergangen ist, während das das beschriebene geschehen ist
     */
    public AvTimeSpan getTimeElapsed() {
        return timeElapsed;
    }
}
