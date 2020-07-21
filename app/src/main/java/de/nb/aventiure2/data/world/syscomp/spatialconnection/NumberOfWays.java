package de.nb.aventiure2.data.world.syscomp.spatialconnection;

import org.jetbrains.annotations.Contract;

public enum NumberOfWays {
    /**
     * There is no way
     */
    NO_WAY,
    /**
     * Whether this is the only someone can take
     */
    ONLY_WAY,
    /**
     * There are two movement possibilities for someone to choose from
     */
    ONE_IN_ONE_OUT,
    /**
     * There are several ways
     */
    SEVERAL_WAYS;

    @Contract(pure = true)
    public static NumberOfWays get(final int numericalNumber) {
        switch (numericalNumber) {
            case 1:
                return ONLY_WAY;
            case 2:
                return ONE_IN_ONE_OUT;
            default:
                return SEVERAL_WAYS;
        }
    }
}
