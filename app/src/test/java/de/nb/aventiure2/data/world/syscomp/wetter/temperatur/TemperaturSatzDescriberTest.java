package de.nb.aventiure2.data.world.syscomp.wetter.temperatur;

import androidx.annotation.NonNull;

import org.junit.Test;

import java.util.List;

import de.nb.aventiure2.data.time.AvDateTime;
import de.nb.aventiure2.data.time.AvTime;
import de.nb.aventiure2.data.world.base.Temperatur;
import de.nb.aventiure2.data.world.syscomp.storingplace.DrinnenDraussen;
import de.nb.aventiure2.data.world.syscomp.wetter.base.WetterParamChange;
import de.nb.aventiure2.data.world.syscomp.wetter.tageszeit.TageszeitAdvAngabeWannDescriber;
import de.nb.aventiure2.data.world.syscomp.wetter.tageszeit.TageszeitPraedikativumDescriber;

import static com.google.common.truth.Truth.assertThat;
import static de.nb.aventiure2.data.time.AvTime.oClock;
import static java.util.Arrays.asList;

public class TemperaturSatzDescriberTest {
    @Test
    public void altSprungOderWechselNieLeer() {
        // GIVEN, WHEN, THEN
        final List<AvTime> relevantTimes = relevantTimes();

        for (int i = 0; i < relevantTimes.size(); i++) {
            final AvTime lastTime = relevantTimes.get(i);
            for (int j = 0; j < relevantTimes.size(); j++) {
                // (Wechsel, Sprung)

                final AvDateTime currentTime =
                        j < i ?
                                new AvDateTime(2, relevantTimes.get(j)) :
                                new AvDateTime(1, relevantTimes.get(j));

                for (final Temperatur temperaturVorher : Temperatur.values()) {
                    for (final Temperatur temperaturNachher : Temperatur.values()) {
                        if (temperaturVorher != temperaturNachher) {
                            final WetterParamChange<Temperatur> temperaturChange =
                                    new WetterParamChange<>(temperaturVorher,
                                            temperaturNachher);

                            testAltSprungOderWechsel(lastTime, currentTime, temperaturChange);
                        }
                    }
                }
            }
        }
    }

    private static void testAltSprungOderWechsel(final AvTime lastTime,
                                                 final AvDateTime currentTime,
                                                 final WetterParamChange<Temperatur> temperaturChange) {
        final TemperaturSatzDescriber underTest =
                new TemperaturSatzDescriber(
                        new TageszeitPraedikativumDescriber(),
                        new TageszeitAdvAngabeWannDescriber(),
                        new TemperaturPraedikativumDescriber());

        System.out.println(lastTime + " -> " + currentTime + " "
                + (temperaturChange != null ? temperaturChange + " " : ""));

        assertThat(underTest.altSprungOderWechsel(lastTime,
                currentTime,
                temperaturChange,
                DrinnenDraussen.DRINNEN, false)).isNotEmpty();

        assertThat(underTest.altSprungOderWechsel(lastTime,
                currentTime,
                temperaturChange,
                DrinnenDraussen.DRINNEN, true)).isNotEmpty();

        assertThat(underTest.altSprungOderWechsel(lastTime,
                currentTime,
                temperaturChange,
                DrinnenDraussen.DRAUSSEN_GESCHUETZT, false)).isNotEmpty();

        assertThat(underTest.altSprungOderWechsel(lastTime,
                currentTime,
                temperaturChange,
                DrinnenDraussen.DRAUSSEN_GESCHUETZT, true)).isNotEmpty();

        assertThat(underTest.altSprungOderWechsel(lastTime,
                currentTime,
                temperaturChange,
                DrinnenDraussen.DRAUSSEN_UNTER_OFFENEM_HIMMEL, false)).isNotEmpty();

        assertThat(underTest.altSprungOderWechsel(lastTime,
                currentTime,
                temperaturChange,
                DrinnenDraussen.DRAUSSEN_UNTER_OFFENEM_HIMMEL, true)).isNotEmpty();
    }


    @NonNull
    private static List<AvTime> relevantTimes() {
        return asList(oClock(5, 45),
                oClock(6, 15),
                oClock(12), oClock(14), oClock(18, 20),
                oClock(18, 45),
                oClock(21), oClock(23));
    }
}
