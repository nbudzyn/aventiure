package de.nb.aventiure2.data.world.syscomp.wetter.temperatur;

import androidx.annotation.NonNull;

import org.junit.Test;

import java.util.List;

import de.nb.aventiure2.data.time.AvDateTime;
import de.nb.aventiure2.data.time.AvTime;
import de.nb.aventiure2.data.world.base.Change;
import de.nb.aventiure2.data.world.base.Temperatur;
import de.nb.aventiure2.data.world.syscomp.storingplace.DrinnenDraussen;
import de.nb.aventiure2.data.world.syscomp.wetter.base.WetterParamChange;
import de.nb.aventiure2.data.world.syscomp.wetter.tageszeit.TageszeitAdvAngabeWannDescriber;
import de.nb.aventiure2.data.world.syscomp.wetter.tageszeit.TageszeitPraedikativumDescriber;

import static com.google.common.truth.Truth.assertThat;
import static de.nb.aventiure2.data.time.AvTime.oClock;
import static de.nb.aventiure2.data.time.AvTimeSpan.secs;
import static java.util.Arrays.asList;

public class TemperaturSatzDescriberTest {
    @Test
    public void altSprungOderWechselNieLeer() {
        // GIVEN, WHEN, THEN
        final List<AvTime> relevantTimes = relevantTimes();

        for (int i = 0; i < relevantTimes.size(); i++) {
            final AvDateTime lastTime = new AvDateTime(1, relevantTimes.get(i));
            for (int j = 0; j < relevantTimes.size(); j++) {
                // (Wechsel, Sprung)

                AvDateTime currentTime =
                        j <= i ?
                                new AvDateTime(2, relevantTimes.get(j)) :
                                new AvDateTime(1, relevantTimes.get(j));
                if (currentTime.getTime().equals(lastTime)) {
                    currentTime = currentTime.plus(secs(1));
                }

                final Change<AvDateTime> timeChange = new Change<>(lastTime, currentTime);

                for (final Temperatur temperaturVorher : Temperatur.values()) {
                    for (final Temperatur temperaturNachher : Temperatur.values()) {
                        if (temperaturVorher != temperaturNachher) {
                            final WetterParamChange<Temperatur> temperaturChange =
                                    new WetterParamChange<>(temperaturVorher,
                                            temperaturNachher);

                            testAltSprungOderWechsel(timeChange, temperaturChange);
                        }
                    }
                }
            }
        }
    }

    private static void testAltSprungOderWechsel(final Change<AvDateTime> timeChange,
                                                 final WetterParamChange<Temperatur> temperaturChange) {
        final TemperaturSatzDescriber underTest =
                new TemperaturSatzDescriber(
                        new TageszeitPraedikativumDescriber(),
                        new TageszeitAdvAngabeWannDescriber(),
                        new TemperaturPraedikativumDescriber());

        System.out.println(timeChange + " "
                + (temperaturChange != null ? temperaturChange + " " : ""));

        assertThat(underTest.altSprungOderWechsel(timeChange,
                temperaturChange,
                DrinnenDraussen.DRINNEN, false)).isNotEmpty();

        assertThat(underTest.altSprungOderWechsel(timeChange,
                temperaturChange,
                DrinnenDraussen.DRINNEN, true)).isNotEmpty();

        assertThat(underTest.altSprungOderWechsel(timeChange,
                temperaturChange,
                DrinnenDraussen.DRAUSSEN_GESCHUETZT, false)).isNotEmpty();

        assertThat(underTest.altSprungOderWechsel(timeChange,
                temperaturChange,
                DrinnenDraussen.DRAUSSEN_GESCHUETZT, true)).isNotEmpty();

        assertThat(underTest.altSprungOderWechsel(timeChange,
                temperaturChange,
                DrinnenDraussen.DRAUSSEN_UNTER_OFFENEM_HIMMEL, false)).isNotEmpty();

        assertThat(underTest.altSprungOderWechsel(timeChange,
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
