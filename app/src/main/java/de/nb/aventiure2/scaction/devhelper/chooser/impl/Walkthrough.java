package de.nb.aventiure2.scaction.devhelper.chooser.impl;

import com.google.common.collect.ImmutableList;

import java.util.List;

import static java.util.Arrays.asList;

/**
 * Eine vordefinierte Aktionsfolge
 */
@SuppressWarnings("WeakerAccess")
public class Walkthrough {

    private final ImmutableList<String> steps;

    Walkthrough(final String... steps) {
        this(asList(steps));
    }

    private Walkthrough(final List<String> steps) {
        this.steps = ImmutableList.copyOf(steps);
    }

    Walkthrough append(final Walkthrough other) {
        return new Walkthrough(
                ImmutableList.<String>builder().addAll(steps).addAll(other.steps).build()
        );
    }

    public Walkthrough truncate(final int numSteps) {
        return new Walkthrough(steps.subList(0, numSteps));
    }

    public int numSteps() {
        return steps.size();
    }

    String getStep(final int index) {
        return steps.get(index);
    }
}
