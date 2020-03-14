package de.nb.aventiure2.playeraction.action;

import java.util.Random;

class PlayerActionUtil {
    private static final Random random = new Random();

    // Not to be called
    private PlayerActionUtil() {
    }

    /**
     * Returns a random number from 0 to num - 1.
     */
    public static int random(final int num) {
        return random.nextInt(num);
    }
}
