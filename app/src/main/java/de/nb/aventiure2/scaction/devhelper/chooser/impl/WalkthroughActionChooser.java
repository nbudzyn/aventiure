package de.nb.aventiure2.scaction.devhelper.chooser.impl;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

import de.nb.aventiure2.scaction.AbstractScAction;
import de.nb.aventiure2.scaction.devhelper.chooser.ExpectedActionNotFoundException;
import de.nb.aventiure2.scaction.devhelper.chooser.IActionChooser;

/**
 * Geht einen vordefinierten Pfad von Aktionen
 */
public class WalkthroughActionChooser implements IActionChooser {
    private int index = -1;
    private final Walkthrough walkthrough;
    private final int maxSteps;

    public WalkthroughActionChooser(final Walkthrough walkthrough) {
        this(walkthrough, Integer.MAX_VALUE);
    }

    public WalkthroughActionChooser(final Walkthrough walkthrough, final int maxSteps) {
        this.walkthrough = walkthrough;
        this.maxSteps = maxSteps;
    }

    @Override
    @Nullable
    public AbstractScAction chooseAction(
            final List<? extends AbstractScAction> actionAlternatives)
            throws ExpectedActionNotFoundException {
        index++;

        if (index >= maxSteps || index >= walkthrough.numSteps()) {
            return null;
        }

        return findAction(actionAlternatives, walkthrough.getStep(index));
    }

    @NonNull
    private static AbstractScAction findAction(
            final List<? extends AbstractScAction> actionAlternatives,
            final String actionName) throws ExpectedActionNotFoundException {
        try {

            return actionAlternatives.stream()
                    .filter(a -> a.getName().equals(actionName))
                    .findAny()
                    .orElseThrow(() -> new ExpectedActionNotFoundException(
                            "Action missing: " + actionName + ". "
                                    + "Options are: " + actionAlternatives));
        } catch (final ExpectedActionNotFoundException | RuntimeException e) {
            throw e;
        } catch (final Throwable t) {
            // Android bug: .orElseThrow() throws Throwable

            throw new IllegalStateException("Unexpected exception or error thrown", t);
        }
    }
}
