package de.nb.aventiure2.german.praedikat;

import de.nb.aventiure2.german.base.Konstituentenfolge;

/**
 * Eine Infinitivkonstruktion ("den Frosch ignorieren", "das Leben genießen") ohne "zu".
 */
public class Infinitiv extends AbstractInfinitiv {

    public Infinitiv(final Verb verb) {
        this(verb, TopolFelder.EMPTY);
    }

    /**
     * Schachtelt einen bestehenden Infinitiv in ein äußeres Verb (üblicherweise Modalverb)
     * ein (Schachtelt z.B. "Spannendes berichten: Odysseus ist zurück." ein in das
     * Modalverb "wollen": "Spannendes berichten wollen: Odysseus ist zurück.")
     * <p>
     * Erzeugt geschachtelte Infinitive wie "Spannendes berichten wollen",
     * "dich waschen wollen" oder "sagen wollen: „Hallo!“".
     */
    public Infinitiv(final Infinitiv lexikalischerKern,
                     final Verb aeusseresVerb) {
        this(Konstituentenfolge.joinToKonstituentenfolge(
                lexikalischerKern.toKonstituentenfolgeOhneNachfeld(),
                // "Spannendes berichten"
                aeusseresVerb.getInfinitiv()), // wollen
                lexikalischerKern.getNachfeld()); // : Odysseus ist zurück.
    }

    /**
     * Schachtelt einen bestehenden zu-Infinitiv in ein äußeres Verb (üblicherweise Hilfsverb)
     * ein (Schachtelt z.B. "Spannendes zu berichten: Odysseus ist zurück." ein in das
     * Hilfsverb "haben": "Spannendes zu berichten haben: Odysseus ist zurück.")
     * <p>
     * Erzeugt geschachtelte Kostruktionen wie "Spannendes zu berichten haben",
     * "dich zu waschen haben" oder "zu sagen haben: „Hallo!“".
     */
    Infinitiv(final ZuInfinitiv lexikalischerKern,
              final Verb aeusseresVerb) {
        this(Konstituentenfolge.joinToKonstituentenfolge(
                lexikalischerKern.toKonstituentenfolgeOhneNachfeld(),
                // "Spannendes zu berichten"
                aeusseresVerb.getInfinitiv()), // haben
                lexikalischerKern.getNachfeld()); // : Odysseus ist zurück.
    }

    public Infinitiv(final Verb verb, final TopolFelder topolFelder) {
        this(Konstituentenfolge.joinToKonstituentenfolge(
                topolFelder.getMittelfeld(), // "den Frosch"
                verb.getInfinitiv()), // "ignorieren"
                topolFelder.getNachfeld()); //"wegen seiner Hässlichkeit"
    }

    public Infinitiv(final Konstituentenfolge infinitivOhneNachfeld,
                     final Nachfeld nachfeld) {
        super(infinitivOhneNachfeld, nachfeld);
    }
}
