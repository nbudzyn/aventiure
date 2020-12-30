package de.nb.aventiure2.german.description;

import de.nb.aventiure2.german.base.StructuralElement;

/**
 * A description - assuming the player character is the (first) subject. Somehting like
 * "Du gehst in den Wald."
 */
public class SimpleDuDescription
        extends AbstractDuDescription<SimpleDuTextPart, SimpleDuDescription> {
    public SimpleDuDescription(final StructuralElement startsNew,
                               final SimpleDuTextPart duTextPart) {
        this(startsNew, duTextPart, false);
    }

    SimpleDuDescription(final StructuralElement startsNew,
                        final SimpleDuTextPart duTextPart,
                        final boolean kommaStehtAus) {
        // FIXME Alle du()-Aufrufe prüfen, ggf. auf SENTENCE setzen
        super(startsNew, duTextPart, kommaStehtAus);
    }
}
