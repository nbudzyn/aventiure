package de.nb.aventiure2.data.world.syscomp.transform;

import java.util.ArrayList;
import java.util.List;

/**
 * Funktionalität, bei der Objekte ineinandere überführt werden.
 *
 * @see de.nb.aventiure2.data.world.syscomp.state.IHasStateGO
 */
public class TransformationSystem {
    private final List<GOTransformation> transformations = new ArrayList<>();

    public TransformationSystem() {
    }

    public void add(final GOTransformation transformation) {
        transformations.add(transformation);
    }
}
