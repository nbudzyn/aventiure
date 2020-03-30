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
    default String getDescriptionHauptsatz(final DescribableAsDeklinierbarePhrase describable) {
        return mitObj(describable).getDescriptionHauptsatz();
    }

    /**
     * Gibt einen Satz zurück mit diesem Verb und diesem <code>describable</code> und
     * dieser adverbialen Angabe ("Aus Langeweile nimmst du den Ast")
     */
    default String getDescriptionHauptsatz(
            final DescribableAsDeklinierbarePhrase describable,
            final AdverbialeAngabe adverbialeAngabe) {
        return mitObj(describable).getDescriptionHauptsatz(adverbialeAngabe);
    }

    /**
     * Gibt eine Infinitivkonstruktion zurück mit diesem Verb und dieser Entity / diesem Konzept.
     * ("Den Frosch ignorieren", "Das Leben genießen")
     */
    default String getDescriptionInfinitiv(final DescribableAsDeklinierbarePhrase describable) {
        return mitObj(describable).getDescriptionInfinitiv();
    }
}
