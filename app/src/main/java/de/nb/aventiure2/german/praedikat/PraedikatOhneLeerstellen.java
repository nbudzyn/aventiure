package de.nb.aventiure2.german.praedikat;

/**
 * Ein Prädikat, in dem alle Leerstellen besetzt sind. Beispiele:
 * <ul>
 *     <li>"das Buch nehmen"
 *     <li>"dem Frosch Angebote machen
 * </ul>
 */
public interface PraedikatOhneLeerstellen extends Praedikat {
    /**
     * Gibt einen Satz zurück mit diesem Prädikat.
     * ("Du nimmst den Ast")
     */
    String getDescriptionHauptsatz();

    /**
     * Gibt eine Infinitivkonstruktion zurück mit diesem Prädikat.
     */
    String getDescriptionInfinitiv();

}
