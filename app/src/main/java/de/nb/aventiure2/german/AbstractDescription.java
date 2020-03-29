package de.nb.aventiure2.german;

import de.nb.aventiure2.data.world.time.AvTimeSpan;

/**
 * Abstract superclass for a description.
 */
public interface AbstractDescription {
    /**
     * Gibt die Beschreibung zurück, beginnend mit einem Hauptsatz
     */
    String getDescriptionHauptsatz();

    /**
     * Gibt die Beschreibung als Hauptsatz zurück, wenn nötig mit dem angegebenen
     * <code>konjunktionaladverb</code> ("dann", "darauf") im Vorfeld.
     */
    String
    getDescriptionHauptsatzMitKonjunktionaladverbWennNoetig(String konjunktionaladverb);

    boolean kommaStehtAus();

    boolean allowsAdditionalDuSatzreihengliedOhneSubjekt();

    boolean dann();

    /**
     * Zeit, die vergangen ist, während das das beschriebene geschehen ist
     */
    AvTimeSpan getTimeElapsed();
}
