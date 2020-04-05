package de.nb.aventiure2.german.praedikat;

import javax.annotation.Nonnull;

/**
 * Ein Prädikat, in dem alle Leerstellen besetzt sind. Beispiele:
 * <ul>
 *     <li>"das Buch nehmen"
 *     <li>"dem Frosch Angebote machen
 * </ul>
 * <p>
 * Adverbiale Angaben ("aus Langeweile") können immer noch eingefügt werden.
 */
public interface PraedikatOhneLeerstellen extends Praedikat {
    /**
     * Gibt einen Satz zurück mit diesem Prädikat.
     * ("Du nimmst den Ast")
     */
    String getDescriptionDuHauptsatz();

    /**
     * Gibt einen Satz zurück mit diesem Prädikat und
     * diesem Text im Vorfeld ("Aus Langeweile nimmst du den Ast")
     */
    String getDescriptionDuHauptsatz(@Nonnull AdverbialeAngabe adverbialeAngabe);

    /**
     * Ob sich ein durch {@link #getDescriptionDuHauptsatz()} oder {
     *
     * @link #getDescriptionDuHauptsatz(AdverbialeAngabe)} mit einem weiteren Du-Hauptsatz
     * zusammenziehen lässt, wobei das zweite Subjekt ("du") entfiele.
     * <p>
     * Das ist im Regelfall möglich, sofern es nicht zu einem doppelten
     * "und" kommt ("Du... und ... und...").
     */
    boolean duHauptsatzLaesstSichMitNachfolgendemDuHauptsatzZusammenziehen();

    /**
     * Gibt eine Infinitivkonstruktion mit diesem
     * Prädikat zurück. Implizit (oder bei reflexiven Verben auch explizit) bezieht sich der
     * Infinitiv auf die 1. Person - Beispiele: "[Ich möchte] Das Schwert nehmen",
     * "[Ich möchte] Die Kugel an mich nehmen"
     */
    String getDescriptionInfinitiv();
}
