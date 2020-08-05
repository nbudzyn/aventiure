package de.nb.aventiure2.data.world.syscomp.reaction;

/**
 * Implementierungen von <code>IRecations</code> bieten sich für Handlungen an, die
 * sich <i>nicht</i> an einen bestimmten Adressaten richten (z.B. X geht von A nach B).
 * <p>
 * Reaktionen, die sich einen bestimmten Adressaten richten (z.B: X sagt A zu Y) sollten nur
 * dann mittels <code>IRecations</code> implementiert werden, wenn auch andere Game Objects
 * auf die Handlung reagieren sollen (nicht nur der Adressat).
 */
public interface IReactions {
}
