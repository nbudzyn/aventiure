package de.nb.aventiure2.german.satz;

import javax.annotation.concurrent.Immutable;

/**
 * Ein eigentlicher ("syntaktischer") Satz, in dem alle Diskursreferenten
 * (Personen, Objekte etc.) auf jeweils eine konkrete sprachliche Repräsentation (z.B. ein
 * konkretes Nomen oder Personalpronomen) festgelegt sind. (Um das sicherzustellen sollen alle
 * Implementierungen immutable sein.)
 */
@Immutable
interface SyntSatz {
    // FIXME Ergibt diese Interface Sinn?!
}
