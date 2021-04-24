package de.nb.aventiure2.data.world.syscomp.wetter.temperatur;

import de.nb.aventiure2.data.time.AvTime;
import de.nb.aventiure2.data.world.base.Temperatur;

import static de.nb.aventiure2.data.time.AvTime.oClock;

/**
 * Statische Methoden zur Berechnung des Temperaturverlaufs über den Tag.
 */
public class TagestemperaturverlaufUtil {
    private TagestemperaturverlaufUtil() {
    }

    /**
     * Gibt zurück, ob zu dieser Uhrzeit Sätze über "heute" oder "den Tag" sinnvoll sind.
     */
    static boolean saetzeUeberHeuteOderDenTagVonDerUhrzeitHerSinnvoll(final AvTime time) {
        return time.isWithin(
                oClock(7, 59, 59),
                oClock(16, 15));
    }

    public static Temperatur calcTemperatur(final Temperatur tageshoechsttemperatur,
                                            final Temperatur tagestiefsttemperatur,
                                            final AvTime time) {
        return Temperatur.interpolate(tagestiefsttemperatur,
                tageshoechsttemperatur,
                calcAnteil(time));
    }

    private static float calcAnteil(final AvTime time) {
        if (time.isBefore(oClock(6, 30))) {
            return interpolate(0.15f, oClock(0),
                    0f, oClock(6, 30), time);
        }

        if (time.isBefore(oClock(13))) {
            return interpolate(0.0f, oClock(6, 30),
                    0.8f, oClock(13), time);
        }

        if (time.isBefore(oClock(17, 30))) {
            return interpolate(0.8f, oClock(13),
                    1f, oClock(17, 30), time);
        }

        if (time.isBefore(oClock(23))) {
            return interpolate(1f, oClock(17, 30),
                    0.2f, oClock(23), time);
        }

        return interpolate(0.2f, oClock(23),
                0.15f,
                oClock(23, 59, 59),
                time);
    }

    private static float interpolate(final float value1, final AvTime time1,
                                     final float value2, final AvTime time2,
                                     final AvTime time) {
        return ((float) time1.timeSpanUntil(time).getSecs()) / time1.timeSpanUntil(time2).getSecs()
                * (value2 - value1)
                + value1;
    }
}
