package de.nb.aventiure2.german.satz;

import javax.annotation.concurrent.Immutable;

import de.nb.aventiure2.german.base.Konstituentenfolge;

/**
 * Ein eigentlicher ("syntaktischer") Satz, in dem alle Diskursreferenten
 * (Personen, Objekte etc.) auf jeweils eine konkrete sprachliche Repräsentation (z.B. ein
 * konkretes Nomen oder Personalpronomen) festgelegt sind. (Um das sicherzustellen sollen alle
 * Implementierungen immutable sein.)
 */
@Immutable
public
interface SyntSatz {
    /**
     * Gibt einen Relativsatz zurück: Etwas wie
     * <ul>
     * <li>das du zu berichten hast
     * <li>der etwas zu berichten hat
     * <li>der was zu berichten hat
     * <li>mit dem sie sich treffen wird
     * <li>dessen Heldentaten wer zu berichten hat
     * <li>das zu erzählen du beginnen wirst
     * <li>das du zu erzählen beginnen wirst
     * <li>der wie geholfen hat
     * </ul>
     */
    Konstituentenfolge getRelativsatz();
}
