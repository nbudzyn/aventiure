package de.nb.aventiure2.scaction.devhelper.chooser.impl;

import androidx.annotation.Nullable;

import java.util.List;
import java.util.Random;

import de.nb.aventiure2.scaction.AbstractScAction;
import de.nb.aventiure2.scaction.devhelper.chooser.IActionChooser;

/**
 * Wählt Aktionen zufällig aus
 */
public class RandomActionChooser implements IActionChooser {
    private final int numSteps;

    private final Random rand;
    private int count = 0;

    public RandomActionChooser(final int numSteps) {
        this.numSteps = numSteps;
        rand = new Random();
    }

    @Override
    @Nullable
    public AbstractScAction chooseAction(
            final List<? extends AbstractScAction> actionAlternatives) {
        count++;
        if (count > numSteps) {
            return null;
        }

        return actionAlternatives.get(
                rand.nextInt(actionAlternatives.size()));
    }
}
