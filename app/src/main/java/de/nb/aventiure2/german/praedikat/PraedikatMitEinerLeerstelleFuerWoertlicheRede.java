package de.nb.aventiure2.german.praedikat;

import de.nb.aventiure2.german.base.Numerus;
import de.nb.aventiure2.german.base.Person;
import de.nb.aventiure2.german.base.WoertlicheRede;

/**
 * Ein Prädikat, in dem für wörtliche Rede eine Leerstelle besteht. Beispiele:
 * <ul>
 *     <li>"... rufen"
 *     <li>"... sagen"
 * </ul>
 */
public interface PraedikatMitEinerLeerstelleFuerWoertlicheRede extends Praedikat {
    /**
     * Füllt die Objekt-Leerstelle mit dieser Woertlichen Rede.
     */
    PraedikatMitWoertlicherRedeOhneLeerstellen mitWoertlicherRede(
            final WoertlicheRede woertlicheRede);

    /**
     * Gibt einen Satz zurück mit diesem Verb und dieser wörtlichen Rede
     * zurücke
     */
    default String getDescriptionDuHauptsatz(final WoertlicheRede woertlicheRede) {
        return mitWoertlicherRede(woertlicheRede).getDescriptionDuHauptsatz();
    }

    /**
     * Gibt einen Satz zurück mit diesem Verb und dieser wörtlichen Rede
     */
    default String getDescriptionDuHauptsatz(
            final WoertlicheRede woertlicheRede,
            final AdverbialeAngabe adverbialeAngabe) {
        return mitWoertlicherRede(woertlicheRede).getDescriptionDuHauptsatz(adverbialeAngabe);
    }

    /**
     * Gibt eine Infinitivkonstruktion mit diesem
     * Verb und dieser wörtlichen Rede zurück.
     * <p>
     * Implizit hat der
     * Infinitiv eine Person und einen Numerus - Beispiel:
     * "[Ich möchte] ... zu mir selbst sagen")
     */
    default String getDescriptionInfinitiv(
            final Person person, final Numerus numerus,
            final WoertlicheRede woertlicheRede) {
        return mitWoertlicherRede(woertlicheRede).getDescriptionInfinitiv(person, numerus);
    }

    /**
     * Gibt eine zu-Infinitivkonstruktion mit diesem
     * Verb und dieser wörtlichen Rede zurück.
     * <p>
     * Implizit (oder bei reflexiven Verben auch explizit) hat der
     * Infinitiv eine Person und einen Numerus - Beispiel:
     * "[Mir war wichtig,] mir ... zu sagen")
     */
    default String getDescriptionZuInfinitiv(
            final Person person, final Numerus numerus,
            final WoertlicheRede woertlicheRede) {
        return mitWoertlicherRede(woertlicheRede).getDescriptionInfinitiv(person, numerus);
    }
}
