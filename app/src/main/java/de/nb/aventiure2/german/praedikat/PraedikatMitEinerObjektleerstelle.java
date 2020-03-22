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
     * Gibt einen Satz zurück mit diesem Prädikat und diesem <code>describable</code>.
     * ("Du nimmst den Ast")
     */
    String getDescriptionHauptsatz(final DescribableAsDeklinierbarePhrase describable);

    /**
     * Gibt eine Infinitivkonstruktion zurück mit diesem Prädikat und dieser Entity / diesem
     * Konzept.
     */
    String getDescriptionInfinitiv(final DescribableAsDeklinierbarePhrase describable);
}