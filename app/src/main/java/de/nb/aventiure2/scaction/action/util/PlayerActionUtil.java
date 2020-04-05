package de.nb.aventiure2.scaction.action.util;

import java.util.Random;

public class PlayerActionUtil {
    private static final Random random = new Random();

    // Not to be called
    private PlayerActionUtil() {
    }

    /**
     * Returns a random number from 0 to num - 1.
     *
     * @Deprecated In many cases {@link de.nb.aventiure2.data.world.counter.CounterDao}
     * is the better idea as it is predictable
     */
    @Deprecated
    public static int random(final int num) {
        return random.nextInt(num);
    }
}
