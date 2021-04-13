package de.nb.aventiure2.data.world.syscomp.wetter.temperatur;

import javax.annotation.CheckReturnValue;

import de.nb.aventiure2.data.time.Tageszeit;
import de.nb.aventiure2.data.world.syscomp.wetter.base.Betweenable;

import static de.nb.aventiure2.data.time.Tageszeit.NACHTS;

@SuppressWarnings("DuplicateBranchesInSwitch")
public enum Temperatur implements Betweenable<Temperatur> {
    // Reihenfolge ist relevant, nicht ändern!
    KLIRREND_KALT,
    KNAPP_UNTER_DEM_GEFRIERPUNKT,
    KNAPP_UEBER_DEM_GEFRIERPUNKT,
    KUEHL,
    WARM,
    RECHT_HEISS,
    SEHR_HEISS;

    public static Temperatur interpolate(
            final Temperatur value1, final Temperatur value2, final float anteil) {
        int resOrdinal = Math.round(
                (value2.ordinal() - value1.ordinal()) * anteil
                        + value1.ordinal());
        final Temperatur[] values = Temperatur.values();
        // Rundungsfehler abfangen!
        if (resOrdinal < 0) {
            resOrdinal = 0;
        }

        if (resOrdinal >= values.length) {
            resOrdinal = values.length - 1;
        }

        return values[resOrdinal];
    }

    @CheckReturnValue
    public boolean isUnauffaellig(final Tageszeit tageszeit) {
        if (tageszeit == NACHTS) {
            return this == KUEHL;
        }

        return isBetweenIncluding(KUEHL, WARM);
    }

}
