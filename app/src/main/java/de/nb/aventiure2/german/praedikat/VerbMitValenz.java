package de.nb.aventiure2.german.praedikat;

/**
 * Repräsentiert ein Verb als Lexem, von dem Wortformen gebildet werden können - einschließlich
 * der Informationen zur Valenz. Typischerweise enthält so ein Objekt ein
 * {@link Verb}-Objekt (ohne die Informationen zur Valenz.
 */
public interface VerbMitValenz extends Praedikat {
    Verb getVerb();
}
