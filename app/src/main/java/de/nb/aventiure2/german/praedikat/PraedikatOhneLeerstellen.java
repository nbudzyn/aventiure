package de.nb.aventiure2.german.praedikat;

import java.util.Collection;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import de.nb.aventiure2.german.base.Numerus;
import de.nb.aventiure2.german.base.Person;

import static java.util.Arrays.asList;

/**
 * Ein Prädikat, in dem alle Leerstellen besetzt sind. Beispiele:
 * <ul>
 *     <li>"das Buch nehmen"
 *     <li>"dem Frosch Angebote machen
 * </ul>
 * <p>
 * Adverbiale Angaben ("aus Langeweile") können immer noch eingefügt werden.
 */
public interface PraedikatOhneLeerstellen extends Praedikat {
    /**
     * Gibt einen Satz zurück mit diesem Prädikat
     * ("Du nimmst den Ast")
     * und ggf. diesen Modalpartikeln, die sich <i>auf das gesamte
     * Prädikat beziehen</i>
     * ("Du nimmst den Ast besser doch")
     */
    default String getDescriptionDuHauptsatz(final Modalpartikel... modalpartikeln) {
        return getDescriptionDuHauptsatz(asList(modalpartikeln));
    }

    /**
     * Gibt einen Satz zurück mit diesem Prädikat
     * ("Du nimmst den Ast")
     * und ggf. diesen Modalpartikeln, die sich <i>auf das gesamte
     * Prädikat beziehen</i>
     * ("Du nimmst den Ast besser doch")
     */
    String getDescriptionDuHauptsatz(Collection<Modalpartikel> modalpartikeln);

    /**
     * Gibt einen Satz zurück mit diesem Prädikat und
     * diesem Text im Vorfeld ("Aus Langeweile nimmst du den Ast")
     */
    String getDescriptionDuHauptsatz(@Nonnull AdverbialeAngabe adverbialeAngabe);

    /**
     * Ob sich ein durch {@link #getDescriptionDuHauptsatz(Collection)} oder
     * {@link #getDescriptionDuHauptsatz(AdverbialeAngabe)} mit einem weiteren Du-Hauptsatz
     * zusammenziehen lässt, wobei das zweite Subjekt ("du") entfiele.
     * <p>
     * Das ist im Regelfall möglich, sofern es nicht zu einem doppelten
     * "und" kommt ("Du... und ... und...").
     */
    boolean duHauptsatzLaesstSichMitNachfolgendemDuHauptsatzZusammenziehen();

    /**
     * Gibt eine Infinitivkonstruktion mit diesem
     * Prädikat zurück ("das Schwert nehmen")
     * <p>
     * Implizit (oder bei reflexiven Verben auch explizit) hat der
     * Infinitiv eine Person und einen Numerus - Beispiel:
     * "[Ich möchte] die Kugel an mich nehmen"
     * (nicht *"[Ich möchte] die Kugel an sich nehmen")
     */
    String getDescriptionInfinitiv(Person person, Numerus numerus);

    /**
     * Gibt eine Infinitivkonstruktion mit diesem
     * Prädikat zurück ("das Schwert erneut nehmen")
     * <p>
     * Implizit (oder bei reflexiven Verben auch explizit) hat der
     * Infinitiv eine Person und einen Numerus - Beispiel:
     * "[Ich möchte] die Kugel erneut an mich nehmen"
     * (nicht *"[Ich möchte] die Kugel erneut an sich nehmen")
     */
    String getDescriptionInfinitiv(Person person, Numerus numerus,
                                   @Nullable AdverbialeAngabe adverbialeAngabe);

    /**
     * Gibt eine Infinitivkonstruktion mit dem zu-Infinitiv mit diesem
     * Prädikat zurück ("das Schwert zu nehmen")
     * <p>
     * Implizit (oder bei reflexiven Verben auch explizit) hat der
     * zu-Infinitiv eine Person und einen Numerus - Beispiel:
     * "[Ich gedenke,] die Kugel an mich zu nehmen"
     * (nicht *"[Ich gedenke,] die Kugel an sich zu nehmen")
     */
    String getDescriptionZuInfinitiv(Person person, Numerus numerus);

    /**
     * Gibt eine Infinitivkonstruktion mit dem zu-Infinitiv mit diesem
     * Prädikat zurück ("das Schwert erneut zu nehmen")
     * <p>
     * Implizit (oder bei reflexiven Verben auch explizit) hat der
     * zu-Infinitiv eine Person und einen Numerus - Beispiel:
     * "[Ich gedenke,] die Kugel erneut an mich zu nehmen"
     * (nicht *"[Ich gedenke,] die Kugel erneut an sich zu nehmen")
     */
    String getDescriptionZuInfinitiv(Person person, Numerus numerus,
                                     @Nullable AdverbialeAngabe adverbialeAngabe);
}
