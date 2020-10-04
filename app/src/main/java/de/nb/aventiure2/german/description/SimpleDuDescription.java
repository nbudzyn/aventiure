package de.nb.aventiure2.german.description;

import de.nb.aventiure2.data.world.time.*;
import de.nb.aventiure2.german.base.StructuralElement;

/**
 * A description - assuming the player character is the (first) subject. Somehting like
 * "Du gehst in den Wald."
 */
public class SimpleDuDescription
        extends AbstractDuDescription<SimpleDuTextPart, SimpleDuDescription> {
    SimpleDuDescription(final StructuralElement startsNew,
                        final SimpleDuTextPart duTextPart,
                        final AvTimeSpan timeElapsed) {
        // TODO Alle du()-Aufrufe prüfen, ggf. auf SENTENCE setzen
        super(startsNew, duTextPart, timeElapsed);
    }
}
