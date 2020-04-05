package de.nb.aventiure2.german.praedikat;

import de.nb.aventiure2.german.base.DescribableAsDeklinierbarePhrase;

/**
 * Ein Prädikat, in dem (noch) für genau ein Objekt eine Leerstelle besteht. Beispiele:
 * <ul>
 *     <li>"... nehmen" (z.B. "das Buch nehmen")
 *     <li>"... Angebote machen" (z.B. "dem Frosch Angebote machen)
 * </ul>
 */
public interface PraedikatMitEinerObjektleerstelle extends Praedikat {
    /**
     * Füllt die Objekt-Leerstelle mit diesem Objekt.
     */
    PraedikatOhneLeerstellen mitObj(final DescribableAsDeklinierbarePhrase describable);

    /**
     * Gibt einen Satz zurück mit diesem Verb und diesem <code>describable</code>.
     * ("Du nimmst den Ast")
     */
    default String getDescriptionDuHauptsatz(final DescribableAsDeklinierbarePhrase describable) {
        return mitObj(describable).getDescriptionDuHauptsatz();
    }

    /**
     * Ob sich ein <code>getDescriptionDuHauptsatz()</code> erzeugter Du-Hauptsatz  mit einem
     * weiteren Du-Hauptsatz zusammenziehen lässt, wobei das zweite Subjekt ("du") entfiele.
     * <p>
     * Das ist im Regelfall möglich, sofern es nicht zu einem doppelten
     * "und" kommt ("Du... und ... und...").
     */
    boolean duHauptsatzLaesstSichMitNachfolgendemDuHauptsatzZusammenziehen();

    /**
     * Gibt einen Satz zurück mit diesem Verb und diesem <code>describable</code> und
     * dieser adverbialen Angabe ("Aus Langeweile nimmst du den Ast")
     */
    default String getDescriptionDuHauptsatz(
            final DescribableAsDeklinierbarePhrase describable,
            final AdverbialeAngabe adverbialeAngabe) {
        return mitObj(describable).getDescriptionDuHauptsatz(adverbialeAngabe);
    }

    /**
     * Gibt eine Infinitivkonstruktion zurück mit diesem Verb und dieser Entity / diesem Konzept.
     * ("Den Frosch ignorieren", "Das Leben genießen")
     */
    default String getDescriptionInfinitiv(final DescribableAsDeklinierbarePhrase describable) {
        return mitObj(describable).getDescriptionInfinitiv();
    }
}
