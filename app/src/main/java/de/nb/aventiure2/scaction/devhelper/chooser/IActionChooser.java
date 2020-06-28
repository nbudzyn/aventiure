package de.nb.aventiure2.scaction.devhelper.chooser;

import androidx.annotation.Nullable;

import java.util.List;

import de.nb.aventiure2.scaction.AbstractScAction;

/**
 * Interface für Klassen, die aus einer Liste von {@link AbstractScAction}s eine auswählen.
 */
public interface IActionChooser {
    @Nullable
    AbstractScAction chooseAction(
            final List<? extends AbstractScAction> actionAlternatives)
            throws ExpectedActionNotFoundException;
}
