package de.nb.aventiure2.german.praedikat;

import de.nb.aventiure2.german.base.Konstituentenfolge;

/**
 * Eine Infinitivkonstruktion mit "zu" ("den Frosch zu ignorieren", "das Leben zu genießen").
 */
public class ZuInfinitiv extends AbstractInfinitiv {
    ZuInfinitiv(final Verb verb) {
        this(verb, TopolFelder.EMPTY);
    }

    /**
     * Schachtelt einen bestehenden Infinitiv in ein äußeres Verb (üblicherweise Modalverb)
     * ein (Schachtelt z.B. "Spannendes berichten: Odysseus ist zurück." ein in das
     * Modalverb "wollen": "Spannendes berichten zu wollen: Odysseus ist zurück.")
     * <p>
     * Erzeugt geschachtelte Kostruktionen wie "Spannendes berichten zu wollen",
     * "dich waschen zu wollen" oder "sagen zu wollen: „Hallo!“".
     */
    ZuInfinitiv(final Infinitiv lexikalischerKern,
                final Verb aeusseresVerb) {
        this(Konstituentenfolge.joinToKonstituentenfolge(
                lexikalischerKern.toKonstituentenfolgeOhneNachfeld(),
                // "Spannendes berichten"
                aeusseresVerb.getZuInfinitiv()), // zu wollen
                lexikalischerKern.getNachfeld()); // : Odysseus ist zurück.
    }

    /**
     * Schachtelt einen bestehenden zu-Infinitiv in ein äußeres Verb (üblicherweise Hilfsverb)
     * ein (Schachtelt z.B. "Spannendes zu berichten: Odysseus ist zurück." ein in das
     * Hilfsverb "haben": "Spannendes zu berichten zu haben: Odysseus ist zurück.")
     * <p>
     * Erzeugt geschachtelte Kostruktionen wie "Spannendes zu berichten zu haben",
     * "dich zu waschen zu haben" oder "zu sagen zu haben: „Hallo!“".
     */
    ZuInfinitiv(final ZuInfinitiv lexikalischerKern,
                final Verb aeusseresVerb) {
        this(Konstituentenfolge.joinToKonstituentenfolge(
                lexikalischerKern.toKonstituentenfolgeOhneNachfeld(),
                // "Spannendes zu berichten"
                aeusseresVerb.getZuInfinitiv()), // zu haben
                lexikalischerKern.getNachfeld()); // : Odysseus ist zurück.
    }

    ZuInfinitiv(final Verb verb, final TopolFelder topolFelder) {
        this(Konstituentenfolge.joinToKonstituentenfolge(
                topolFelder.getMittelfeld(), // "den Frosch"
                verb.getZuInfinitiv()), // "zu ignorieren"
                topolFelder.getNachfeld()); //"wegen seiner Hässlichkeit"
    }

    ZuInfinitiv(final Konstituentenfolge zuInfinitivOhneNachfeld,
                final Nachfeld nachfeld) {
        super(zuInfinitivOhneNachfeld, nachfeld);
    }

}
