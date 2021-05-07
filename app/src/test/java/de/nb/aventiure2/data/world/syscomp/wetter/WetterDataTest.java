package de.nb.aventiure2.data.world.syscomp.wetter;

import androidx.annotation.NonNull;

import org.junit.Test;

import java.util.List;

import de.nb.aventiure2.data.time.AvTime;
import de.nb.aventiure2.data.world.base.EnumRange;
import de.nb.aventiure2.data.world.base.Temperatur;
import de.nb.aventiure2.data.world.syscomp.storingplace.DrinnenDraussen;
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
                                    for (final AvTime time :
                                            relevantTimes()) {
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
                                                .isEmpty()).isFalse();

                                        assertThat(underTest.altWetterhinweise(
                                                time, drinnenDraussen,
                                                EnumRange
                                                        .of(Temperatur.KNAPP_UNTER_DEM_GEFRIERPUNKT,
                                                                Temperatur.KUEHL),
                                                true)
                                                .isEmpty()).isFalse();

                                        assertThat(underTest.altWetterhinweise(
                                                time, drinnenDraussen,
                                                EnumRange.of(tagestiefsttemperatur,
                                                        tageshoechsttemperatur),
                                                false)
                                                .isEmpty()).isFalse();

                                        assertThat(underTest.altWetterhinweise(
                                                time, drinnenDraussen,
                                                EnumRange
                                                        .of(Temperatur.KNAPP_UNTER_DEM_GEFRIERPUNKT,
                                                                Temperatur.KUEHL),
                                                false)
                                                .isEmpty()).isFalse();
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
                                for (final AvTime time :
                                        relevantTimes()) {
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
                                            .build().isEmpty()).isFalse();

                                    assertThat(underTest.altKommtNachDraussen(
                                            time, true,
                                            EnumRange
                                                    .of(Temperatur.KNAPP_UNTER_DEM_GEFRIERPUNKT,
                                                            Temperatur.KUEHL),
                                            true)
                                            .build().isEmpty()).isFalse();

                                    assertThat(underTest.altKommtNachDraussen(
                                            time, true,
                                            EnumRange.of(tagestiefsttemperatur,
                                                    tageshoechsttemperatur),
                                            false)
                                            .build().isEmpty()).isFalse();

                                    assertThat(underTest.altKommtNachDraussen(
                                            time, true,
                                            EnumRange
                                                    .of(Temperatur.KNAPP_UNTER_DEM_GEFRIERPUNKT,
                                                            Temperatur.KUEHL),
                                            false)
                                            .build().isEmpty()).isFalse();

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
                                            .build().isEmpty()).isFalse();

                                    assertThat(underTest.altKommtNachDraussen(
                                            time, false,
                                            EnumRange.of(tagestiefsttemperatur,
                                                    tageshoechsttemperatur),
                                            false)
                                            .build().isEmpty()).isFalse();

                                    assertThat(underTest.altKommtNachDraussen(
                                            time, false,
                                            EnumRange
                                                    .of(Temperatur.KNAPP_UNTER_DEM_GEFRIERPUNKT,
                                                            Temperatur.KUEHL),
                                            false)
                                            .build().isEmpty()).isFalse();

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
                    for (int delta = -3; delta <= 3; delta++) {
                        for (final AvTime time :
                                relevantTimes()) {
                            System.out.println(
                                    tagestiefsttemperatur + " "
                                            + tageshoechsttemperatur + " "
                                            + time);

                            assertThat(underTest
                                    .altAngenehmereTemperaturOderWindAlsVorLocation(
                                            time,
                                            EnumRange.of(tagestiefsttemperatur,
                                                    tageshoechsttemperatur),
                                            delta, Windstaerke.STURM, Windstaerke.STURM)
                                    .isEmpty()).isFalse();

                            assertThat(underTest
                                    .altAngenehmereTemperaturOderWindAlsVorLocation(
                                            time,
                                            EnumRange
                                                    .of(Temperatur.KNAPP_UNTER_DEM_GEFRIERPUNKT,
                                                            Temperatur.KUEHL),
                                            delta, Windstaerke.LUEFTCHEN, Windstaerke.LUEFTCHEN)
                                    .isEmpty()).isFalse();
                        }
                    }
                }
            }
        }
    }

    @NonNull
    private static List<AvTime> relevantTimes() {
        return asList(oClock(5, 45),
                oClock(6, 15),
                oClock(12), oClock(14), oClock(18, 20),
                oClock(18, 45),
                oClock(21), oClock(23));
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
                                for (final AvTime time :
                                        relevantTimes()) {
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
                                            .isEmpty()).isFalse();

                                    assertThat(underTest.altWetterhinweiseWohinHinaus(
                                            time, true,
                                            EnumRange
                                                    .of(Temperatur.KNAPP_UNTER_DEM_GEFRIERPUNKT,
                                                            Temperatur.KUEHL),
                                            true)
                                            .isEmpty()).isFalse();
                                    assertThat(underTest.altWetterhinweiseWohinHinaus(
                                            time, true,
                                            EnumRange.of(tagestiefsttemperatur,
                                                    tageshoechsttemperatur),
                                            false)
                                            .isEmpty()).isFalse();

                                    assertThat(underTest.altWetterhinweiseWohinHinaus(
                                            time, true,
                                            EnumRange
                                                    .of(Temperatur.KNAPP_UNTER_DEM_GEFRIERPUNKT,
                                                            Temperatur.KUEHL),
                                            false)
                                            .isEmpty()).isFalse();
                                    assertThat(underTest.altWetterhinweiseWohinHinaus(
                                            time, false,
                                            EnumRange.of(tagestiefsttemperatur,
                                                    tageshoechsttemperatur),
                                            true)
                                            .isEmpty()).isFalse();

                                    assertThat(underTest.altWetterhinweiseWohinHinaus(
                                            time, false,
                                            EnumRange
                                                    .of(Temperatur.KNAPP_UNTER_DEM_GEFRIERPUNKT,
                                                            Temperatur.KUEHL),
                                            true)
                                            .isEmpty()).isFalse();
                                    assertThat(underTest.altWetterhinweiseWohinHinaus(
                                            time, false,
                                            EnumRange.of(tagestiefsttemperatur,
                                                    tageshoechsttemperatur),
                                            false)
                                            .isEmpty()).isFalse();

                                    assertThat(underTest.altWetterhinweiseWohinHinaus(
                                            time, false,
                                            EnumRange
                                                    .of(Temperatur.KNAPP_UNTER_DEM_GEFRIERPUNKT,
                                                            Temperatur.KUEHL),
                                            false)
                                            .isEmpty()).isFalse();
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
                                for (final AvTime time :
                                        relevantTimes()) {
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
                                            true)
                                            .isEmpty()).isFalse();

                                    assertThat(underTest.altWetterhinweisWoDraussen(
                                            time, true,
                                            EnumRange
                                                    .of(Temperatur.KNAPP_UNTER_DEM_GEFRIERPUNKT,
                                                            Temperatur.KUEHL),
                                            true)
                                            .isEmpty()).isFalse();
                                    assertThat(underTest.altWetterhinweisWoDraussen(
                                            time, true,
                                            EnumRange.of(tagestiefsttemperatur,
                                                    tageshoechsttemperatur),
                                            false)
                                            .isEmpty()).isFalse();

                                    assertThat(underTest.altWetterhinweisWoDraussen(
                                            time, true,
                                            EnumRange
                                                    .of(Temperatur.KNAPP_UNTER_DEM_GEFRIERPUNKT,
                                                            Temperatur.KUEHL),
                                            false)
                                            .isEmpty()).isFalse();
                                    assertThat(underTest.altWetterhinweisWoDraussen(
                                            time, false,
                                            EnumRange.of(tagestiefsttemperatur,
                                                    tageshoechsttemperatur),
                                            true)
                                            .isEmpty()).isFalse();

                                    assertThat(underTest.altWetterhinweisWoDraussen(
                                            time, false,
                                            EnumRange
                                                    .of(Temperatur.KNAPP_UNTER_DEM_GEFRIERPUNKT,
                                                            Temperatur.KUEHL),
                                            true)
                                            .isEmpty()).isFalse();
                                    assertThat(underTest.altWetterhinweisWoDraussen(
                                            time, false,
                                            EnumRange.of(tagestiefsttemperatur,
                                                    tageshoechsttemperatur),
                                            false)
                                            .isEmpty()).isFalse();

                                    assertThat(underTest.altWetterhinweisWoDraussen(
                                            time, false,
                                            EnumRange
                                                    .of(Temperatur.KNAPP_UNTER_DEM_GEFRIERPUNKT,
                                                            Temperatur.KUEHL),
                                            false)
                                            .isEmpty()).isFalse();
                                }
                            }
                        }
                    }
                }
            }
        }
    }


// FIXME Tests anlegen für die anderen WetterComp / WetterData-Methoden
//  für alle Kombinationen von Wetterparametern, die
//  sicherstellen, dass niemals eine leere Menge geschrieben werden soll.
}
