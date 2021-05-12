package de.nb.aventiure2.data.world.syscomp.wetter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.junit.Test;

import java.util.List;

import de.nb.aventiure2.data.time.AvDateTime;
import de.nb.aventiure2.data.time.AvTime;
import de.nb.aventiure2.data.world.base.EnumRange;
import de.nb.aventiure2.data.world.base.Temperatur;
import de.nb.aventiure2.data.world.syscomp.storingplace.DrinnenDraussen;
import de.nb.aventiure2.data.world.syscomp.wetter.base.WetterParamChange;
import de.nb.aventiure2.data.world.syscomp.wetter.bewoelkung.Bewoelkung;
import de.nb.aventiure2.data.world.syscomp.wetter.blitzunddonner.BlitzUndDonner;
import de.nb.aventiure2.data.world.syscomp.wetter.windstaerke.Windstaerke;

import static com.google.common.truth.Truth.assertThat;
import static de.nb.aventiure2.data.time.AvTime.oClock;
import static java.util.Arrays.asList;

public class WetterDataTest {
    @Test
    public void altWetterhinweiseNieLeer() {
        // GIVEN, WHEN, THEN
        for (final Temperatur tagestiefsttemperatur : Temperatur.values()) {
            for (final Temperatur tageshoechsttemperatur : Temperatur.values()) {
                if (tagestiefsttemperatur.compareTo(tageshoechsttemperatur) <= 0) {
                    for (final Bewoelkung bewoelkung : Bewoelkung.values()) {
                        for (final Windstaerke windstaerkeUnterOffenemHimmel : Windstaerke
                                .values()) {
                            for (final BlitzUndDonner blitzUndDonner : BlitzUndDonner.values()) {
                                final WetterData underTest = new WetterData(tageshoechsttemperatur,
                                        tagestiefsttemperatur,
                                        windstaerkeUnterOffenemHimmel,
                                        bewoelkung,
                                        blitzUndDonner);
                                for (final DrinnenDraussen drinnenDraussen : DrinnenDraussen
                                        .values()) {
                                    for (final AvTime time : relevantTimes()) {
                                        System.out.println(
                                                tagestiefsttemperatur + " "
                                                        + tageshoechsttemperatur + " "
                                                        + bewoelkung + " "
                                                        + windstaerkeUnterOffenemHimmel + " "
                                                        + blitzUndDonner + " "
                                                        + drinnenDraussen + " "
                                                        + time);
                                        assertThat(underTest.altWetterhinweise(
                                                time, drinnenDraussen,
                                                EnumRange.of(tagestiefsttemperatur,
                                                        tageshoechsttemperatur),
                                                true)
                                        ).isNotEmpty();

                                        assertThat(underTest.altWetterhinweise(
                                                time, drinnenDraussen,
                                                EnumRange
                                                        .of(Temperatur.KNAPP_UNTER_DEM_GEFRIERPUNKT,
                                                                Temperatur.KUEHL),
                                                true)
                                        ).isNotEmpty();

                                        assertThat(underTest.altWetterhinweise(
                                                time, drinnenDraussen,
                                                EnumRange.of(tagestiefsttemperatur,
                                                        tageshoechsttemperatur),
                                                false)
                                        ).isNotEmpty();

                                        assertThat(underTest.altWetterhinweise(
                                                time, drinnenDraussen,
                                                EnumRange
                                                        .of(Temperatur.KNAPP_UNTER_DEM_GEFRIERPUNKT,
                                                                Temperatur.KUEHL),
                                                false)
                                        ).isNotEmpty();
                                    }
                                }
                            }
                        }
                    }

                }
            }
        }
    }

    @Test
    public void altKommtNachDraussenNieLeer() {
        // GIVEN, WHEN, THEN
        for (final Temperatur tagestiefsttemperatur : Temperatur.values()) {
            for (final Temperatur tageshoechsttemperatur : Temperatur.values()) {
                if (tagestiefsttemperatur.compareTo(tageshoechsttemperatur) <= 0) {
                    for (final Bewoelkung bewoelkung : Bewoelkung.values()) {
                        for (final Windstaerke windstaerkeUnterOffenemHimmel : Windstaerke
                                .values()) {
                            for (final BlitzUndDonner blitzUndDonner : BlitzUndDonner.values()) {
                                final WetterData underTest = new WetterData(tageshoechsttemperatur,
                                        tagestiefsttemperatur,
                                        windstaerkeUnterOffenemHimmel,
                                        bewoelkung,
                                        blitzUndDonner);
                                for (final AvTime time : relevantTimes()) {
                                    System.out.println(
                                            tagestiefsttemperatur + " "
                                                    + tageshoechsttemperatur + " "
                                                    + bewoelkung + " "
                                                    + windstaerkeUnterOffenemHimmel + " "
                                                    + blitzUndDonner + " "
                                                    + time);

                                    assertThat(underTest.altKommtNachDraussen(
                                            time, true,
                                            EnumRange.of(tagestiefsttemperatur,
                                                    tageshoechsttemperatur),
                                            true)
                                            .build()).isNotEmpty();

                                    assertThat(underTest.altKommtNachDraussen(
                                            time, true,
                                            EnumRange
                                                    .of(Temperatur.KNAPP_UNTER_DEM_GEFRIERPUNKT,
                                                            Temperatur.KUEHL),
                                            true)
                                            .build()).isNotEmpty();

                                    assertThat(underTest.altKommtNachDraussen(
                                            time, true,
                                            EnumRange.of(tagestiefsttemperatur,
                                                    tageshoechsttemperatur),
                                            false)
                                            .build()).isNotEmpty();

                                    assertThat(underTest.altKommtNachDraussen(
                                            time, true,
                                            EnumRange
                                                    .of(Temperatur.KNAPP_UNTER_DEM_GEFRIERPUNKT,
                                                            Temperatur.KUEHL),
                                            false)
                                            .build()).isNotEmpty();

                                    assertThat(underTest.altKommtNachDraussen(
                                            time, false,
                                            EnumRange.of(tagestiefsttemperatur,
                                                    tageshoechsttemperatur),
                                            true)
                                            .build().isEmpty())
                                            .isFalse();

                                    assertThat(underTest.altKommtNachDraussen(
                                            time, false,
                                            EnumRange
                                                    .of(Temperatur.KNAPP_UNTER_DEM_GEFRIERPUNKT,
                                                            Temperatur.KUEHL),
                                            true)
                                            .build()).isNotEmpty();

                                    assertThat(underTest.altKommtNachDraussen(
                                            time, false,
                                            EnumRange.of(tagestiefsttemperatur,
                                                    tageshoechsttemperatur),
                                            false)
                                            .build()).isNotEmpty();

                                    assertThat(underTest.altKommtNachDraussen(
                                            time, false,
                                            EnumRange
                                                    .of(Temperatur.KNAPP_UNTER_DEM_GEFRIERPUNKT,
                                                            Temperatur.KUEHL),
                                            false)
                                            .build()).isNotEmpty();

                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @Test
    public void altAngenehmereTemperaturOderWindAlsVorLocationNieLeer_Temperatur() {
        // GIVEN, WHEN, THEN
        for (final Temperatur tagestiefsttemperatur : Temperatur.values()) {
            for (final Temperatur tageshoechsttemperatur : Temperatur.values()) {
                if (tagestiefsttemperatur.compareTo(tageshoechsttemperatur) <= 0) {
                    final WetterData underTest = new WetterData(tageshoechsttemperatur,
                            tagestiefsttemperatur,
                            Windstaerke.LUEFTCHEN,
                            Bewoelkung.LEICHT_BEWOELKT,
                            BlitzUndDonner.KEIN_BLITZ_ODER_DONNER);
                    for (int deltaTemperatur = -3; deltaTemperatur <= 3; deltaTemperatur++) {
                        for (final AvTime time : relevantTimes()) {
                            System.out.println(
                                    tagestiefsttemperatur + " "
                                            + tageshoechsttemperatur + " "
                                            + time);

                            assertThat(underTest
                                    .altAngenehmereTemperaturOderWindAlsVorLocation(
                                            time,
                                            EnumRange.of(tagestiefsttemperatur,
                                                    tageshoechsttemperatur),
                                            deltaTemperatur, null, null)
                            ).isNotEmpty();

                            assertThat(underTest
                                    .altAngenehmereTemperaturOderWindAlsVorLocation(
                                            time,
                                            EnumRange
                                                    .of(Temperatur.KNAPP_UNTER_DEM_GEFRIERPUNKT,
                                                            Temperatur.KUEHL),
                                            deltaTemperatur, null, null)
                            ).isNotEmpty();

                            assertThat(underTest
                                    .altAngenehmereTemperaturOderWindAlsVorLocation(
                                            time,
                                            EnumRange
                                                    .of(Temperatur.KNAPP_UNTER_DEM_GEFRIERPUNKT,
                                                            Temperatur.KUEHL),
                                            deltaTemperatur, Windstaerke.WINDIG,
                                            Windstaerke.LUEFTCHEN)
                            ).isNotEmpty();
                        }
                    }
                }
            }
        }
    }

    @Test
    public void altWetterhinweiseWohinHinausNieLeer() {
        // GIVEN, WHEN, THEN
        for (final Temperatur tagestiefsttemperatur : Temperatur.values()) {
            for (final Temperatur tageshoechsttemperatur : Temperatur.values()) {
                if (tagestiefsttemperatur.compareTo(tageshoechsttemperatur) <= 0) {
                    for (final Bewoelkung bewoelkung : Bewoelkung.values()) {
                        for (final Windstaerke windstaerkeUnterOffenemHimmel : Windstaerke
                                .values()) {
                            for (final BlitzUndDonner blitzUndDonner : BlitzUndDonner.values()) {
                                final WetterData underTest = new WetterData(tageshoechsttemperatur,
                                        tagestiefsttemperatur,
                                        windstaerkeUnterOffenemHimmel,
                                        bewoelkung,
                                        blitzUndDonner);
                                for (final AvTime time : relevantTimes()) {
                                    System.out.println(
                                            tagestiefsttemperatur + " "
                                                    + tageshoechsttemperatur + " "
                                                    + bewoelkung + " "
                                                    + windstaerkeUnterOffenemHimmel + " "
                                                    + blitzUndDonner + " "
                                                    + time);
                                    assertThat(underTest.altWetterhinweiseWohinHinaus(
                                            time, true,
                                            EnumRange.of(tagestiefsttemperatur,
                                                    tageshoechsttemperatur),
                                            true)
                                    ).isNotEmpty();

                                    assertThat(underTest.altWetterhinweiseWohinHinaus(
                                            time, true,
                                            EnumRange
                                                    .of(Temperatur.KNAPP_UNTER_DEM_GEFRIERPUNKT,
                                                            Temperatur.KUEHL),
                                            true)
                                    ).isNotEmpty();
                                    assertThat(underTest.altWetterhinweiseWohinHinaus(
                                            time, true,
                                            EnumRange.of(tagestiefsttemperatur,
                                                    tageshoechsttemperatur),
                                            false)
                                    ).isNotEmpty();

                                    assertThat(underTest.altWetterhinweiseWohinHinaus(
                                            time, true,
                                            EnumRange
                                                    .of(Temperatur.KNAPP_UNTER_DEM_GEFRIERPUNKT,
                                                            Temperatur.KUEHL),
                                            false)
                                    ).isNotEmpty();
                                    assertThat(underTest.altWetterhinweiseWohinHinaus(
                                            time, false,
                                            EnumRange.of(tagestiefsttemperatur,
                                                    tageshoechsttemperatur),
                                            true)
                                    ).isNotEmpty();

                                    assertThat(underTest.altWetterhinweiseWohinHinaus(
                                            time, false,
                                            EnumRange
                                                    .of(Temperatur.KNAPP_UNTER_DEM_GEFRIERPUNKT,
                                                            Temperatur.KUEHL),
                                            true)
                                    ).isNotEmpty();
                                    assertThat(underTest.altWetterhinweiseWohinHinaus(
                                            time, false,
                                            EnumRange.of(tagestiefsttemperatur,
                                                    tageshoechsttemperatur),
                                            false)
                                    ).isNotEmpty();

                                    assertThat(underTest.altWetterhinweiseWohinHinaus(
                                            time, false,
                                            EnumRange
                                                    .of(Temperatur.KNAPP_UNTER_DEM_GEFRIERPUNKT,
                                                            Temperatur.KUEHL),
                                            false)
                                    ).isNotEmpty();
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @Test
    public void altWetterhinweisWoDraussenNieLeer() {
        // GIVEN, WHEN, THEN
        for (final Temperatur tagestiefsttemperatur : Temperatur.values()) {
            for (final Temperatur tageshoechsttemperatur : Temperatur.values()) {
                if (tagestiefsttemperatur.compareTo(tageshoechsttemperatur) <= 0) {
                    for (final Bewoelkung bewoelkung : Bewoelkung.values()) {
                        for (final Windstaerke windstaerkeUnterOffenemHimmel : Windstaerke
                                .values()) {
                            for (final BlitzUndDonner blitzUndDonner : BlitzUndDonner.values()) {
                                final WetterData underTest = new WetterData(tageshoechsttemperatur,
                                        tagestiefsttemperatur,
                                        windstaerkeUnterOffenemHimmel,
                                        bewoelkung,
                                        blitzUndDonner);
                                for (final AvTime time : relevantTimes()) {
                                    System.out.println(
                                            tagestiefsttemperatur + " "
                                                    + tageshoechsttemperatur + " "
                                                    + bewoelkung + " "
                                                    + windstaerkeUnterOffenemHimmel + " "
                                                    + blitzUndDonner + " "
                                                    + time);
                                    assertThat(underTest.altWetterhinweisWoDraussen(
                                            time, true,
                                            EnumRange.of(tagestiefsttemperatur,
                                                    tageshoechsttemperatur),
                                            true))
                                            .isNotEmpty();

                                    assertThat(underTest.altWetterhinweisWoDraussen(
                                            time, true,
                                            EnumRange
                                                    .of(Temperatur.KNAPP_UNTER_DEM_GEFRIERPUNKT,
                                                            Temperatur.KUEHL),
                                            true)
                                    ).isNotEmpty();
                                    assertThat(underTest.altWetterhinweisWoDraussen(
                                            time, true,
                                            EnumRange.of(tagestiefsttemperatur,
                                                    tageshoechsttemperatur),
                                            false)
                                    ).isNotEmpty();

                                    assertThat(underTest.altWetterhinweisWoDraussen(
                                            time, true,
                                            EnumRange
                                                    .of(Temperatur.KNAPP_UNTER_DEM_GEFRIERPUNKT,
                                                            Temperatur.KUEHL),
                                            false)
                                    ).isNotEmpty();
                                    assertThat(underTest.altWetterhinweisWoDraussen(
                                            time, false,
                                            EnumRange.of(tagestiefsttemperatur,
                                                    tageshoechsttemperatur),
                                            true)
                                    ).isNotEmpty();

                                    assertThat(underTest.altWetterhinweisWoDraussen(
                                            time, false,
                                            EnumRange
                                                    .of(Temperatur.KNAPP_UNTER_DEM_GEFRIERPUNKT,
                                                            Temperatur.KUEHL),
                                            true)
                                    ).isNotEmpty();
                                    assertThat(underTest.altWetterhinweisWoDraussen(
                                            time, false,
                                            EnumRange.of(tagestiefsttemperatur,
                                                    tageshoechsttemperatur),
                                            false)
                                    ).isNotEmpty();

                                    assertThat(underTest.altWetterhinweisWoDraussen(
                                            time, false,
                                            EnumRange
                                                    .of(Temperatur.KNAPP_UNTER_DEM_GEFRIERPUNKT,
                                                            Temperatur.KUEHL),
                                            false)
                                    ).isNotEmpty();
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @Test
    public void altTimePassedTageszeitenaenderungNichtBeschreiben() {
        // GIVEN, WHEN, THEN
        final List<AvTime> relevantTimes = relevantTimes();

        for (int i = 0; i < relevantTimes.size(); i++) {
            final AvDateTime lastTime = new AvDateTime(1, relevantTimes.get(i));
            for (int j = 0; j < relevantTimes.size(); j++) {
                // (Keine Änderung, Wechsel, Sprung)

                final AvDateTime currentTime =
                        j < i ?
                                new AvDateTime(2, relevantTimes.get(j)) :
                                new AvDateTime(1, relevantTimes.get(j));

                for (final Bewoelkung bewoelkungVorher : Bewoelkung
                        .values()) {
                    for (final Bewoelkung bewoelkungNachher : Bewoelkung
                            .values()) {
                        if (bewoelkungVorher != bewoelkungNachher) {
                            final WetterParamChange<Bewoelkung>
                                    bewoelkungChange =
                                    new WetterParamChange<>(
                                            bewoelkungVorher,
                                            bewoelkungNachher);

                            testAltTimePassedTageszeitenaenderungNichtBeschreiben(lastTime,
                                    currentTime,
                                    null, null,
                                    bewoelkungChange);
                        }
                    }
                }

                final WetterParamChange<Temperatur> temperaturChange =
                        new WetterParamChange<>(Temperatur.WARM,
                                Temperatur.RECHT_HEISS);

                testAltTimePassedTageszeitenaenderungNichtBeschreiben(lastTime,
                        currentTime,
                        null, temperaturChange, null);

                for (final Bewoelkung bewoelkungVorher : Bewoelkung
                        .values()) {
                    for (final Bewoelkung bewoelkungNachher : Bewoelkung
                            .values()) {
                        if (bewoelkungVorher != bewoelkungNachher) {
                            final WetterParamChange<Bewoelkung>
                                    bewoelkungChange =
                                    new WetterParamChange<>(
                                            bewoelkungVorher,
                                            bewoelkungNachher);

                            testAltTimePassedTageszeitenaenderungNichtBeschreiben(
                                    lastTime, currentTime,
                                    null, temperaturChange,
                                    bewoelkungChange);
                        }
                    }
                }
                for (final Windstaerke windstaerkeVorher : Windstaerke.values()) {
                    for (final Windstaerke windstaerkeNachher : Windstaerke.values()) {
                        if (windstaerkeNachher != windstaerkeVorher) {
                            final WetterParamChange<Windstaerke> windstaerkeChange =
                                    new WetterParamChange<>(windstaerkeVorher, windstaerkeNachher);

                            testAltTimePassedTageszeitenaenderungNichtBeschreiben(lastTime,
                                    currentTime,
                                    windstaerkeChange, null, null);

                            for (final Bewoelkung bewoelkungVorher : Bewoelkung
                                    .values()) {
                                for (final Bewoelkung bewoelkungNachher : Bewoelkung
                                        .values()) {
                                    if (bewoelkungVorher != bewoelkungNachher) {
                                        final WetterParamChange<Bewoelkung>
                                                bewoelkungChange =
                                                new WetterParamChange<>(
                                                        bewoelkungVorher,
                                                        bewoelkungNachher);

                                        testAltTimePassedTageszeitenaenderungNichtBeschreiben(
                                                lastTime, currentTime,
                                                windstaerkeChange, null,
                                                bewoelkungChange);
                                    }
                                }
                            }

                            testAltTimePassedTageszeitenaenderungNichtBeschreiben(
                                    lastTime, currentTime,
                                    windstaerkeChange, temperaturChange, null);

                            for (final Bewoelkung bewoelkungVorher : Bewoelkung
                                    .values()) {
                                for (final Bewoelkung bewoelkungNachher : Bewoelkung
                                        .values()) {
                                    if (bewoelkungVorher != bewoelkungNachher) {
                                        final WetterParamChange<Bewoelkung>
                                                bewoelkungChange =
                                                new WetterParamChange<>(
                                                        bewoelkungVorher,
                                                        bewoelkungNachher);

                                        testAltTimePassedTageszeitenaenderungNichtBeschreiben(
                                                lastTime, currentTime,
                                                windstaerkeChange, temperaturChange,
                                                bewoelkungChange);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @Test
    public void altTimePassedTageszeitenaenderung() {
        // GIVEN, WHEN, THEN
        final List<AvTime> relevantTimes = relevantTimes();

        for (int i = 0; i < relevantTimes.size(); i++) {
            final AvDateTime lastTime = new AvDateTime(1, relevantTimes.get(i));
            for (int j = 0; j < relevantTimes.size(); j++) {
                // (Wechsel, Sprung)

                final AvDateTime currentTime =
                        j < i ?
                                new AvDateTime(2, relevantTimes.get(j)) :
                                new AvDateTime(1, relevantTimes.get(j));

                if (lastTime.getTageszeit() == currentTime.getTageszeit()) {
                    break;
                }

                testAltTimePassedTageszeitenaenderung(lastTime, currentTime,
                        null, null, null);

                for (final Bewoelkung bewoelkungVorher : Bewoelkung
                        .values()) {
                    for (final Bewoelkung bewoelkungNachher : Bewoelkung
                            .values()) {
                        if (bewoelkungVorher != bewoelkungNachher) {
                            final WetterParamChange<Bewoelkung>
                                    bewoelkungChange =
                                    new WetterParamChange<>(
                                            bewoelkungVorher,
                                            bewoelkungNachher);

                            testAltTimePassedTageszeitenaenderung(lastTime, currentTime,
                                    null, null,
                                    bewoelkungChange);
                        }
                    }
                }

                final WetterParamChange<Temperatur> temperaturChange =
                        new WetterParamChange<>(Temperatur.WARM, Temperatur.KUEHL);

                testAltTimePassedTageszeitenaenderung(lastTime, currentTime,
                        null, temperaturChange, null);

                for (final Bewoelkung bewoelkungVorher : Bewoelkung
                        .values()) {
                    for (final Bewoelkung bewoelkungNachher : Bewoelkung
                            .values()) {
                        if (bewoelkungVorher != bewoelkungNachher) {
                            final WetterParamChange<Bewoelkung>
                                    bewoelkungChange =
                                    new WetterParamChange<>(
                                            bewoelkungVorher,
                                            bewoelkungNachher);

                            testAltTimePassedTageszeitenaenderung(lastTime, currentTime,
                                    null, temperaturChange,
                                    bewoelkungChange);
                        }
                    }
                }
                for (final Windstaerke windstaerkeVorher : Windstaerke.values()) {
                    for (final Windstaerke windstaerkeNachher : Windstaerke.values()) {
                        if (windstaerkeNachher != windstaerkeVorher) {
                            final WetterParamChange<Windstaerke> windstaerkeChange =
                                    new WetterParamChange<>(windstaerkeVorher, windstaerkeNachher);

                            testAltTimePassedTageszeitenaenderung(lastTime, currentTime,
                                    windstaerkeChange, null, null);

                            for (final Bewoelkung bewoelkungVorher : Bewoelkung
                                    .values()) {
                                for (final Bewoelkung bewoelkungNachher : Bewoelkung
                                        .values()) {
                                    if (bewoelkungVorher != bewoelkungNachher) {
                                        final WetterParamChange<Bewoelkung>
                                                bewoelkungChange =
                                                new WetterParamChange<>(
                                                        bewoelkungVorher,
                                                        bewoelkungNachher);

                                        testAltTimePassedTageszeitenaenderung(lastTime, currentTime,
                                                windstaerkeChange, null,
                                                bewoelkungChange);
                                    }
                                }
                            }

                            testAltTimePassedTageszeitenaenderung(lastTime, currentTime,
                                    windstaerkeChange, temperaturChange, null);

                            for (final Bewoelkung bewoelkungVorher : Bewoelkung
                                    .values()) {
                                for (final Bewoelkung bewoelkungNachher : Bewoelkung
                                        .values()) {
                                    if (bewoelkungVorher != bewoelkungNachher) {
                                        final WetterParamChange<Bewoelkung>
                                                bewoelkungChange =
                                                new WetterParamChange<>(
                                                        bewoelkungVorher,
                                                        bewoelkungNachher);

                                        testAltTimePassedTageszeitenaenderung(lastTime,
                                                currentTime,
                                                windstaerkeChange, temperaturChange,
                                                bewoelkungChange);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private static void testAltTimePassedTageszeitenaenderungNichtBeschreiben(
            final AvDateTime lastTime,
            final AvDateTime currentTime,
            @Nullable final WetterParamChange<Windstaerke> windstaerkeChange,
            @Nullable final WetterParamChange<Temperatur> temperaturChange,
            @Nullable final WetterParamChange<Bewoelkung> bewoelkungChange) {
        System.out.println(lastTime + " -> " + currentTime + " "
                + (windstaerkeChange != null ? windstaerkeChange + " " : "")
                + (temperaturChange != null ? temperaturChange + " " : "")
                + (bewoelkungChange != null ? bewoelkungChange + " " : ""));

        if (windstaerkeChange != null
                || temperaturChange != null
                || bewoelkungChange != null) {
            if (bewoelkungChange == null && windstaerkeChange == null) {
                assertThat(WetterData.altTimePassedTageszeitenaenderungNichtBeschreiben(lastTime,
                        currentTime,
                        windstaerkeChange,
                        temperaturChange,
                        bewoelkungChange,
                        DrinnenDraussen.DRINNEN, false)).isNotEmpty();
            }

            if ((windstaerkeChange != null
                    && windstaerkeChange.getVorher().getLokaleWindstaerkeDraussenGeschuetzt() !=
                    windstaerkeChange.getNachher().getLokaleWindstaerkeDraussenGeschuetzt())
                    || temperaturChange != null
                    || bewoelkungChange != null) {
                assertThat(WetterData.altTimePassedTageszeitenaenderungNichtBeschreiben(lastTime,
                        currentTime,
                        windstaerkeChange,
                        temperaturChange,
                        bewoelkungChange,
                        DrinnenDraussen.DRAUSSEN_GESCHUETZT, true)).isNotEmpty();
            } else {
                // Es soll nicht abstürzen
                WetterData.altTimePassedTageszeitenaenderungNichtBeschreiben(lastTime,
                        currentTime,
                        windstaerkeChange,
                        temperaturChange,
                        bewoelkungChange,
                        DrinnenDraussen.DRAUSSEN_GESCHUETZT, true);
            }

            assertThat(WetterData.altTimePassedTageszeitenaenderungNichtBeschreiben(lastTime,
                    currentTime,
                    windstaerkeChange,
                    temperaturChange,
                    bewoelkungChange,
                    DrinnenDraussen.DRAUSSEN_UNTER_OFFENEM_HIMMEL,
                    true)).isNotEmpty();
        } else {
            // Es soll nicht abstürzen
            WetterData.altTimePassedTageszeitenaenderungNichtBeschreiben(lastTime,
                    currentTime,
                    null,
                    null,
                    null,
                    DrinnenDraussen.DRINNEN, false);

            WetterData.altTimePassedTageszeitenaenderungNichtBeschreiben(lastTime,
                    currentTime,
                    null,
                    null,
                    null,
                    DrinnenDraussen.DRAUSSEN_GESCHUETZT, true);

            WetterData.altTimePassedTageszeitenaenderungNichtBeschreiben(lastTime,
                    currentTime,
                    null,
                    null,
                    null,
                    DrinnenDraussen.DRAUSSEN_UNTER_OFFENEM_HIMMEL, true);
        }
    }

    private static void testAltTimePassedTageszeitenaenderung(final AvDateTime lastTime,
                                                              final AvDateTime currentTime,
                                                              @Nullable
                                                              final WetterParamChange<Windstaerke> windstaerkeChange,
                                                              @Nullable
                                                              final WetterParamChange<Temperatur> temperaturChange,
                                                              @Nullable
                                                              final WetterParamChange<Bewoelkung> bewoelkungChange) {
        final WetterData underTest = new WetterData(Temperatur.SEHR_HEISS,
                Temperatur.KLIRREND_KALT,
                windstaerkeChange != null ? windstaerkeChange.getNachher() :
                        Windstaerke.LUEFTCHEN,
                bewoelkungChange != null ? bewoelkungChange.getNachher() :
                        Bewoelkung.LEICHT_BEWOELKT,
                BlitzUndDonner.KEIN_BLITZ_ODER_DONNER);

        System.out.println(lastTime + " -> " + currentTime + " "
                + (windstaerkeChange != null ? windstaerkeChange + " " : "")
                + (temperaturChange != null ? temperaturChange + " " : "")
                + (bewoelkungChange != null ? bewoelkungChange + " " : ""));

        if (bewoelkungChange == null && windstaerkeChange == null) {
            assertThat(underTest.altTimePassedTageszeitenaenderung(lastTime,
                    currentTime,
                    false,
                    windstaerkeChange,
                    temperaturChange,
                    bewoelkungChange,
                    DrinnenDraussen.DRINNEN)).isNotEmpty();
        }

        assertThat(underTest.altTimePassedTageszeitenaenderung(lastTime,
                currentTime,
                false,
                windstaerkeChange,
                temperaturChange,
                bewoelkungChange,
                DrinnenDraussen.DRAUSSEN_GESCHUETZT)).isNotEmpty();

        assertThat(underTest.altTimePassedTageszeitenaenderung(lastTime,
                currentTime,
                false,
                windstaerkeChange,
                temperaturChange,
                bewoelkungChange,
                DrinnenDraussen.DRAUSSEN_UNTER_OFFENEM_HIMMEL)).isNotEmpty();
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
