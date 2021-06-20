package de.nb.aventiure2.data.world.syscomp.wetter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.junit.Test;

import java.util.List;

import de.nb.aventiure2.data.time.AvDateTime;
import de.nb.aventiure2.data.time.AvTime;
import de.nb.aventiure2.data.time.AvTimeSpan;
import de.nb.aventiure2.data.world.base.Change;
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
                                        assertThat(underTest.altSpWetterhinweise(
                                                time, drinnenDraussen,
                                                EnumRange.of(tagestiefsttemperatur,
                                                        tageshoechsttemperatur),
                                                true, WetterParamFlags.keine())
                                        ).isNotEmpty();

                                        assertThat(underTest.altSpWetterhinweise(
                                                time, drinnenDraussen,
                                                EnumRange
                                                        .of(Temperatur.KNAPP_UNTER_DEM_GEFRIERPUNKT,
                                                                Temperatur.KUEHL),
                                                true, WetterParamFlags.keine())
                                        ).isNotEmpty();

                                        assertThat(underTest.altSpWetterhinweise(
                                                time, drinnenDraussen,
                                                EnumRange.of(tagestiefsttemperatur,
                                                        tageshoechsttemperatur),
                                                false, WetterParamFlags.keine())
                                        ).isNotEmpty();

                                        assertThat(underTest.altSpWetterhinweise(
                                                time, drinnenDraussen,
                                                EnumRange
                                                        .of(Temperatur.KNAPP_UNTER_DEM_GEFRIERPUNKT,
                                                                Temperatur.KUEHL),
                                                false, WetterParamFlags.keine())
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

                                    assertThat(underTest.altSpKommtNachDraussen(
                                            time, true,
                                            EnumRange.of(tagestiefsttemperatur,
                                                    tageshoechsttemperatur),
                                            true)
                                            .build()).isNotEmpty();

                                    assertThat(underTest.altSpKommtNachDraussen(
                                            time, true,
                                            EnumRange
                                                    .of(Temperatur.KNAPP_UNTER_DEM_GEFRIERPUNKT,
                                                            Temperatur.KUEHL),
                                            true)
                                            .build()).isNotEmpty();

                                    assertThat(underTest.altSpKommtNachDraussen(
                                            time, true,
                                            EnumRange.of(tagestiefsttemperatur,
                                                    tageshoechsttemperatur),
                                            false)
                                            .build()).isNotEmpty();

                                    assertThat(underTest.altSpKommtNachDraussen(
                                            time, true,
                                            EnumRange
                                                    .of(Temperatur.KNAPP_UNTER_DEM_GEFRIERPUNKT,
                                                            Temperatur.KUEHL),
                                            false)
                                            .build()).isNotEmpty();

                                    assertThat(underTest.altSpKommtNachDraussen(
                                            time, false,
                                            EnumRange.of(tagestiefsttemperatur,
                                                    tageshoechsttemperatur),
                                            true)
                                            .build().isEmpty())
                                            .isFalse();

                                    assertThat(underTest.altSpKommtNachDraussen(
                                            time, false,
                                            EnumRange
                                                    .of(Temperatur.KNAPP_UNTER_DEM_GEFRIERPUNKT,
                                                            Temperatur.KUEHL),
                                            true)
                                            .build()).isNotEmpty();

                                    assertThat(underTest.altSpKommtNachDraussen(
                                            time, false,
                                            EnumRange.of(tagestiefsttemperatur,
                                                    tageshoechsttemperatur),
                                            false)
                                            .build()).isNotEmpty();

                                    assertThat(underTest.altSpKommtNachDraussen(
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
                                    .altSpAngenehmereTemperaturOderWindAlsVorLocation(
                                            time,
                                            EnumRange.of(tagestiefsttemperatur,
                                                    tageshoechsttemperatur),
                                            deltaTemperatur, null, null)
                            ).isNotEmpty();

                            assertThat(underTest
                                    .altSpAngenehmereTemperaturOderWindAlsVorLocation(
                                            time,
                                            EnumRange
                                                    .of(Temperatur.KNAPP_UNTER_DEM_GEFRIERPUNKT,
                                                            Temperatur.KUEHL),
                                            deltaTemperatur, null, null)
                            ).isNotEmpty();

                            assertThat(underTest
                                    .altSpAngenehmereTemperaturOderWindAlsVorLocation(
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
    public void altSpTimePassedTageszeitenaenderungNichtBeschreiben() {
        // GIVEN, WHEN, THEN
        final List<AvTime> relevantTimes = relevantTimes();

        for (int i = 0; i < relevantTimes.size(); i++) {
            final AvDateTime lastTime = new AvDateTime(1, relevantTimes.get(i));
            for (int j = 0; j < relevantTimes.size(); j++) {
                // (Keine Änderung, Wechsel, Sprung)

                AvDateTime currentTime =
                        j < i ?
                                new AvDateTime(2, relevantTimes.get(j)) :
                                new AvDateTime(1, relevantTimes.get(j));

                if (lastTime.equals(currentTime)) {
                    currentTime = currentTime.plus(AvTimeSpan.secs(1));
                }

                final Change<AvDateTime> timeChange = new Change<>(lastTime, currentTime);

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

                            testAltSpTimePassedTageszeitenaenderungNichtBeschreiben(timeChange,
                                    null, null,
                                    bewoelkungChange);
                        }
                    }
                }

                final WetterParamChange<Temperatur> temperaturChange =
                        new WetterParamChange<>(Temperatur.WARM,
                                Temperatur.RECHT_HEISS);

                testAltSpTimePassedTageszeitenaenderungNichtBeschreiben(timeChange,
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

                            testAltSpTimePassedTageszeitenaenderungNichtBeschreiben(
                                    timeChange,
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

                            testAltSpTimePassedTageszeitenaenderungNichtBeschreiben(timeChange,
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

                                        testAltSpTimePassedTageszeitenaenderungNichtBeschreiben(
                                                timeChange,
                                                windstaerkeChange, null,
                                                bewoelkungChange);
                                    }
                                }
                            }

                            testAltSpTimePassedTageszeitenaenderungNichtBeschreiben(timeChange,
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

                                        testAltSpTimePassedTageszeitenaenderungNichtBeschreiben(
                                                timeChange,
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

                final Change<AvDateTime> timeChange = new Change<>(lastTime, currentTime);

                testAltSpTimePassedTageszeitenaenderung(timeChange,
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

                            testAltSpTimePassedTageszeitenaenderung(timeChange,
                                    null, null,
                                    bewoelkungChange);
                        }
                    }
                }

                final WetterParamChange<Temperatur> temperaturChange =
                        new WetterParamChange<>(Temperatur.WARM, Temperatur.KUEHL);

                testAltSpTimePassedTageszeitenaenderung(timeChange,
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

                            testAltSpTimePassedTageszeitenaenderung(timeChange,
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

                            testAltSpTimePassedTageszeitenaenderung(timeChange,
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

                                        testAltSpTimePassedTageszeitenaenderung(timeChange,
                                                windstaerkeChange, null,
                                                bewoelkungChange);
                                    }
                                }
                            }

                            testAltSpTimePassedTageszeitenaenderung(timeChange,
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

                                        testAltSpTimePassedTageszeitenaenderung(timeChange,
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
    private static void testAltSpTimePassedTageszeitenaenderungNichtBeschreiben(
            final Change<AvDateTime> timeChange,
            @Nullable final WetterParamChange<Windstaerke> windstaerkeChange,
            @Nullable final WetterParamChange<Temperatur> temperaturChange,
            @Nullable final WetterParamChange<Bewoelkung> bewoelkungChange) {
        System.out.println(timeChange + " "
                + (windstaerkeChange != null ? windstaerkeChange + " " : "")
                + (temperaturChange != null ? temperaturChange + " " : "")
                + (bewoelkungChange != null ? bewoelkungChange + " " : ""));

        if (windstaerkeChange != null
                || temperaturChange != null
                || bewoelkungChange != null) {
            if (bewoelkungChange == null && windstaerkeChange == null) {
                assertThat(
                        WetterData.altSpTimePassedTageszeitenaenderungNichtBeschreiben(timeChange,
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
                assertThat(
                        WetterData.altSpTimePassedTageszeitenaenderungNichtBeschreiben(timeChange,
                                windstaerkeChange,
                                temperaturChange,
                                bewoelkungChange,
                                DrinnenDraussen.DRAUSSEN_GESCHUETZT, true)).isNotEmpty();
            } else {
                // Es soll nicht abstürzen
                WetterData.altSpTimePassedTageszeitenaenderungNichtBeschreiben(timeChange,
                        windstaerkeChange,
                        temperaturChange,
                        bewoelkungChange,
                        DrinnenDraussen.DRAUSSEN_GESCHUETZT, true);
            }

            assertThat(WetterData.altSpTimePassedTageszeitenaenderungNichtBeschreiben(timeChange,
                    windstaerkeChange,
                    temperaturChange,
                    bewoelkungChange,
                    DrinnenDraussen.DRAUSSEN_UNTER_OFFENEM_HIMMEL,
                    true)).isNotEmpty();
        } else {
            // Es soll nicht abstürzen
            WetterData.altSpTimePassedTageszeitenaenderungNichtBeschreiben(timeChange,
                    null,
                    null,
                    null,
                    DrinnenDraussen.DRINNEN, false);

            WetterData.altSpTimePassedTageszeitenaenderungNichtBeschreiben(timeChange,
                    null,
                    null,
                    null,
                    DrinnenDraussen.DRAUSSEN_GESCHUETZT, true);

            WetterData.altSpTimePassedTageszeitenaenderungNichtBeschreiben(timeChange,
                    null,
                    null,
                    null,
                    DrinnenDraussen.DRAUSSEN_UNTER_OFFENEM_HIMMEL, true);
        }
    }

    private static void testAltSpTimePassedTageszeitenaenderung(final Change<AvDateTime> timeChange,
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

        System.out.println(timeChange + " "
                + (windstaerkeChange != null ? windstaerkeChange + " " : "")
                + (temperaturChange != null ? temperaturChange + " " : "")
                + (bewoelkungChange != null ? bewoelkungChange + " " : ""));

        if (bewoelkungChange == null && windstaerkeChange == null) {
            assertThat(underTest.altSpTimePassedTageszeitenaenderung(timeChange,
                    false,
                    windstaerkeChange,
                    temperaturChange,
                    bewoelkungChange,
                    DrinnenDraussen.DRINNEN)).isNotEmpty();
        }

        assertThat(underTest.altSpTimePassedTageszeitenaenderung(timeChange,
                false,
                windstaerkeChange,
                temperaturChange,
                bewoelkungChange,
                DrinnenDraussen.DRAUSSEN_GESCHUETZT)).isNotEmpty();

        assertThat(underTest.altSpTimePassedTageszeitenaenderung(timeChange,
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
