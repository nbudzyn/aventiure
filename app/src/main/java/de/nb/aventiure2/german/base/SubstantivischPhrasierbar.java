package de.nb.aventiure2.german.base;

import de.nb.aventiure2.german.description.ITextContext;

/**
 * Etwas, das sich als {@link SubstantivischePhrase} ausdrücken lässt. In aller Regel
 * ein Diskursreferent (also eine Person, ein Gegenstand, ein Konzept o.Ä., vgl. auch
 * {@link IBezugsobjekt}).
 * <p>
 * Oft gibt es mehrere Möglichkeiten, etwas auszudrücken. Teilweise sind diese Möglichkeiten
 * vom textuellen Umfeld abhängig (also vom {@link ITextContext}). Teilweise werden die
 * Möglichkeiten auch bewusst stilistisch gewählt oder dienen nur der Abwechslung.
 */
public interface SubstantivischPhrasierbar {
    /**
     * Drückt dies als {@link SubstantivischePhrase} aus. Oft gibt es mehrere Möglichkeiten des
     * Ausdrucks. Teilweise sind diese Möglichkeiten
     * vom textuellen Umfeld abhängig (also vom {@link ITextContext}). Teilweise werden die
     * Möglichkeiten auch bewusst stilistisch gewählt oder dienen nur der Abwechslung.
     */
    SubstantivischePhrase alsSubstPhrase(ITextContext textContext);
}
