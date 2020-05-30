package de.nb.aventiure2.german.praedikat;

import de.nb.aventiure2.german.base.Numerus;
import de.nb.aventiure2.german.base.Person;
import de.nb.aventiure2.german.base.SubstantivischePhrase;

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
    PraedikatOhneLeerstellen mitObj(final SubstantivischePhrase describable);

    /**
     * Gibt einen Satz zurück mit diesem Verb und diesem <code>describable</code>.
     * ("Du nimmst den Ast")
     */
    default String getDescriptionDuHauptsatz(final SubstantivischePhrase describable) {
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
            final SubstantivischePhrase describable,
            final AdverbialeAngabe adverbialeAngabe) {
        return mitObj(describable).getDescriptionDuHauptsatz(adverbialeAngabe);
    }

    /**
     * Gibt eine Infinitivkonstruktion mit diesem
     * Verb und diesem <code>describable</code> zurück ("das Schwert nehmen")
     * <p>
     * Implizit (oder bei reflexiven Verben auch explizit) hat der
     * Infinitiv eine Person und einen Numerus - Beispiel:
     * "[Ich möchte] die Kugel an mich nehmen"
     * (nicht *"[Ich möchte] die Kugel an sich nehmen" oder
     * *"[Ich möchte] die Kugel an uns nehmen")
     */
    default String getDescriptionInfinitiv(
            final Person person, final Numerus numerus,
            final SubstantivischePhrase describable) {
        return mitObj(describable).getDescriptionInfinitiv(person, numerus);
    }

    /**
     * Gibt eine zu-Infinitivkonstruktion mit diesem
     * Verb und diesem <code>describable</code> zurück ("das Schwert zu nehmen")
     * <p>
     * Implizit (oder bei reflexiven Verben auch explizit) hat der
     * Infinitiv eine Person und einen Numerus - Beispiel:
     * "[Mir ist wichtig,] die Kugel an mich zu nehmen"
     * (nicht *"[Mir ist wichtig,] die Kugel an sich zu nehmen")
     */
    default String getDescriptionZuInfinitiv(
            final Person person, final Numerus numerus,
            final SubstantivischePhrase describable) {
        return mitObj(describable).getDescriptionInfinitiv(person, numerus);
    }
}
