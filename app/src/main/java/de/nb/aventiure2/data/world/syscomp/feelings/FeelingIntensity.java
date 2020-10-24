package de.nb.aventiure2.data.world.syscomp.feelings;

import static com.google.common.base.Preconditions.checkArgument;

public class FeelingIntensity {
    private FeelingIntensity() {
    }

    public static final int NEUTRAL = 0;
    public static final int NUR_LEICHT = 1;
    public static final int MERKLICH = 2;
    public static final int DEUTLICH = 3;
    public static final int STARK = 4;
    public static final int SEHR_STARK = 5;
    public static final int PATHOLOGISCH = 6;

    public static void checkValue(final double intensity) {
        checkArgument(intensity >= NEUTRAL && intensity <= PATHOLOGISCH,
                "Ungültige Intensität: " + intensity);
    }
}
