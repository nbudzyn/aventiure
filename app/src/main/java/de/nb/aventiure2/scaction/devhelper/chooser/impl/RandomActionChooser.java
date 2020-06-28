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
    private final static int NUM_STEPS = 15;

    private final Random rand;
    private int count = 0;

    public RandomActionChooser() {
        rand = new Random();
    }

    @Override
    @Nullable
    public AbstractScAction chooseAction(
            final List<? extends AbstractScAction> actionAlternatives) {
        count++;
        if (count > NUM_STEPS) {
            return null;
        }

        return actionAlternatives.get(
                rand.nextInt(actionAlternatives.size()));
    }
}
