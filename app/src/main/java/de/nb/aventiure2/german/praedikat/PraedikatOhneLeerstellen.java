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
     * Gibt eine Infinitivkonstruktion mit diesem
     * Prädikat zurück. Implizit (oder bei reflexiven Verben auch explizit) bezieht sich der
     * Infinitiv auf die 1. Person - Beispiele: "[Ich möchte] Das Schwert nehmen",
     * "[Ich möchte] Die Kugel an mich nehmen"
     */
    String getDescriptionInfinitiv();

}
