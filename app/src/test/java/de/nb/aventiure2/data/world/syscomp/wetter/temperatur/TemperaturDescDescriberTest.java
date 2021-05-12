package de.nb.aventiure2.data.world.syscomp.wetter.temperatur;

import androidx.annotation.NonNull;

import org.junit.Test;

import java.util.List;

import de.nb.aventiure2.data.time.AvDateTime;
import de.nb.aventiure2.data.time.AvTime;
import de.nb.aventiure2.data.time.Tageszeit;
import de.nb.aventiure2.data.world.base.Temperatur;
import de.nb.aventiure2.data.world.syscomp.storingplace.DrinnenDraussen;
import de.nb.aventiure2.data.world.syscomp.wetter.base.WetterParamChange;
import de.nb.aventiure2.data.world.syscomp.wetter.tageszeit.TageszeitAdvAngabeWannDescriber;
import de.nb.aventiure2.data.world.syscomp.wetter.tageszeit.TageszeitDescDescriber;
import de.nb.aventiure2.data.world.syscomp.wetter.tageszeit.TageszeitPraedikativumDescriber;
import de.nb.aventiure2.data.world.syscomp.wetter.tageszeit.TageszeitSatzDescriber;

import static com.google.common.truth.Truth.assertThat;
import static de.nb.aventiure2.data.time.AvTime.oClock;
import static java.util.Arrays.asList;

public class TemperaturDescDescriberTest {
    private final TemperaturDescDescriber underTest =
            new TemperaturDescDescriber(
                    new TageszeitDescDescriber(
                            new TageszeitSatzDescriber(
                                    new TageszeitPraedikativumDescriber()
                            )
                    ),
                    new TageszeitAdvAngabeWannDescriber(),
                    new TemperaturPraedikativumDescriber(),
                    new TemperaturSatzDescriber(
                            new TageszeitPraedikativumDescriber(),
                            new TageszeitAdvAngabeWannDescriber(),
                            new TemperaturPraedikativumDescriber()));

    @Test
    public void altSprungOderWechselNieLeer() {
        // GIVEN, WHEN, THEN
        final List<AvTime> relevantTimes = relevantTimes();

        for (int i = 0; i < relevantTimes.size(); i++) {
            final AvTime lastTime = relevantTimes.get(i);
            for (int j = 0; j < relevantTimes.size(); j++) {
                // (Wechsel, Sprung)

                if (i != j) {
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

                                System.out.println(lastTime + " -> " + currentTime + " "
                                        + temperaturChange);

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
                                        DrinnenDraussen.DRAUSSEN_UNTER_OFFENEM_HIMMEL, false))
                                        .isNotEmpty();

                                assertThat(underTest.altSprungOderWechsel(lastTime,
                                        currentTime,
                                        temperaturChange,
                                        DrinnenDraussen.DRAUSSEN_UNTER_OFFENEM_HIMMEL, true))
                                        .isNotEmpty();
                            }
                        }
                    }
                }
            }
        }
    }

    @Test
    public void altTemperaturUndTageszeitenSprungOderWechselNieLeer() {
        // GIVEN, WHEN, THEN
        final List<AvTime> relevantTimes = relevantTimes();

        for (final Tageszeit lastTageszeit : Tageszeit.values()) {
            for (int j = 0; j < relevantTimes.size(); j++) {
                // (Wechsel, Sprung)

                if (lastTageszeit != relevantTimes.get(j).getTageszeit()) {
                    final AvTime currentTime = relevantTimes.get(j);

                    System.out.println(lastTageszeit + " -> " + currentTime);

                    assertThat(underTest.altTemperaturUndTageszeitenSprungOderWechsel(lastTageszeit,
                            currentTime,
                            false,
                            null,
                            DrinnenDraussen.DRINNEN).build()).isNotEmpty();

                    assertThat(underTest.altTemperaturUndTageszeitenSprungOderWechsel(lastTageszeit,
                            currentTime,
                            true,
                            null,
                            DrinnenDraussen.DRINNEN).build()).isNotEmpty();

                    assertThat(underTest.altTemperaturUndTageszeitenSprungOderWechsel(lastTageszeit,
                            currentTime,
                            false,
                            null,
                            DrinnenDraussen.DRAUSSEN_GESCHUETZT).build()).isNotEmpty();

                    assertThat(underTest.altTemperaturUndTageszeitenSprungOderWechsel(lastTageszeit,
                            currentTime,
                            true,
                            null,
                            DrinnenDraussen.DRAUSSEN_GESCHUETZT).build()).isNotEmpty();

                    assertThat(underTest.altTemperaturUndTageszeitenSprungOderWechsel(lastTageszeit,
                            currentTime,
                            false,
                            null,
                            DrinnenDraussen.DRAUSSEN_UNTER_OFFENEM_HIMMEL).build()).isNotEmpty();

                    assertThat(underTest.altTemperaturUndTageszeitenSprungOderWechsel(lastTageszeit,
                            currentTime,
                            true,
                            null,
                            DrinnenDraussen.DRAUSSEN_UNTER_OFFENEM_HIMMEL).build()).isNotEmpty();

                    for (final Temperatur temperaturNachher : Temperatur.values()) {
                        System.out.println(lastTageszeit + " -> " + currentTime + " "
                                + temperaturNachher);

                        assertThat(underTest
                                .altTemperaturUndTageszeitenSprungOderWechsel(lastTageszeit,
                                        currentTime,
                                        false,
                                        temperaturNachher,
                                        DrinnenDraussen.DRINNEN).build()).isNotEmpty();

                        assertThat(underTest
                                .altTemperaturUndTageszeitenSprungOderWechsel(lastTageszeit,
                                        currentTime,
                                        true,
                                        temperaturNachher,
                                        DrinnenDraussen.DRINNEN).build()).isNotEmpty();

                        assertThat(underTest
                                .altTemperaturUndTageszeitenSprungOderWechsel(lastTageszeit,
                                        currentTime,
                                        false,
                                        temperaturNachher,
                                        DrinnenDraussen.DRAUSSEN_GESCHUETZT).build()).isNotEmpty();

                        assertThat(underTest
                                .altTemperaturUndTageszeitenSprungOderWechsel(lastTageszeit,
                                        currentTime,
                                        true,
                                        temperaturNachher,
                                        DrinnenDraussen.DRAUSSEN_GESCHUETZT).build()).isNotEmpty();

                        assertThat(underTest
                                .altTemperaturUndTageszeitenSprungOderWechsel(lastTageszeit,
                                        currentTime,
                                        false,
                                        temperaturNachher,
                                        DrinnenDraussen.DRAUSSEN_UNTER_OFFENEM_HIMMEL).build())
                                .isNotEmpty();

                        assertThat(underTest
                                .altTemperaturUndTageszeitenSprungOderWechsel(lastTageszeit,
                                        currentTime,
                                        true,
                                        temperaturNachher,
                                        DrinnenDraussen.DRAUSSEN_UNTER_OFFENEM_HIMMEL).build())
                                .isNotEmpty();
                    }
                }
            }
        }
    }

    @Test
    public void altNieLeer() {
        // GIVEN, WHEN, THEN
        final List<AvTime> relevantTimes = relevantTimes();

        for (int j = 0; j < relevantTimes.size(); j++) {
            final AvTime currentTime = relevantTimes.get(j);

            for (final Temperatur temperatur : Temperatur.values()) {
                System.out.println(currentTime + " " + temperatur);

                testAlt(currentTime, temperatur, DrinnenDraussen.DRINNEN);
                testAlt(currentTime, temperatur, DrinnenDraussen.DRAUSSEN_GESCHUETZT);
                testAlt(currentTime, temperatur, DrinnenDraussen.DRAUSSEN_UNTER_OFFENEM_HIMMEL);
            }
        }
    }

    private void testAlt(final AvTime currentTime, final Temperatur temperatur,
                         final DrinnenDraussen drinnenDraussen) {
        assertThat(underTest
                .alt(temperatur, false,
                        currentTime,
                        drinnenDraussen,
                        false))
                .isNotEmpty();

        assertThat(underTest
                .alt(temperatur, false,
                        currentTime,
                        drinnenDraussen,
                        true))
                .isNotEmpty();

        assertThat(underTest
                .alt(temperatur, true,
                        currentTime,
                        drinnenDraussen,
                        false))
                .isNotEmpty();

        assertThat(underTest
                .alt(temperatur, true,
                        currentTime,
                        drinnenDraussen,
                        true))
                .isNotEmpty();
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
