package de.nb.aventiure2.data.world.syscomp.wetter;

import static de.nb.aventiure2.data.time.AvTimeSpan.NO_TIME;
import static de.nb.aventiure2.data.time.AvTimeSpan.hours;
import static de.nb.aventiure2.data.time.AvTimeSpan.max;
import static de.nb.aventiure2.data.time.AvTimeSpan.mins;
import static de.nb.aventiure2.data.world.base.Temperatur.KUEHL;
import static de.nb.aventiure2.data.world.base.Temperatur.WARM;
import static de.nb.aventiure2.data.world.syscomp.wetter.bewoelkung.Bewoelkung.BEWOELKT;
import static de.nb.aventiure2.data.world.syscomp.wetter.blitzunddonner.BlitzUndDonner.DONNERGROLLEN_IN_DER_FERNE;
import static de.nb.aventiure2.data.world.syscomp.wetter.windstaerke.Windstaerke.LUEFTCHEN;
import static de.nb.aventiure2.data.world.syscomp.wetter.windstaerke.Windstaerke.WINDIG;

import javax.annotation.Nullable;

import de.nb.aventiure2.data.time.AvTimeSpan;
import de.nb.aventiure2.data.world.base.Temperatur;
import de.nb.aventiure2.data.world.syscomp.wetter.bewoelkung.Bewoelkung;
import de.nb.aventiure2.data.world.syscomp.wetter.blitzunddonner.BlitzUndDonner;
import de.nb.aventiure2.data.world.syscomp.wetter.windstaerke.Windstaerke;

/**
 * Ermittelt, wie man von einem "Wetter" (einem SemSatz von Wetter-Parametern) zu einem
 * anderen kommt.
 */
class WetterPathfinder {
    static AvTimeSpan getStandardDuration(final WetterData startWetter, final WetterData planwetter,
                                          final boolean firstStepTakesNoTime) {
        AvTimeSpan res = NO_TIME;
        boolean first = true;
        WetterData tmp = startWetter;

        while (true) {
            @Nullable final StandardWetterStep step = findFirstStep(tmp, planwetter);
            if (step == null) {
                return res;
            }
            res = (firstStepTakesNoTime && first) ? res : res.plus(step.getStandardDuration());
            first = false;
            tmp = step.getWetterTo();
        }
    }

    @Nullable
    static StandardWetterStep findFirstStep(
            final WetterData start, final @Nullable WetterData ziel) {
        if (ziel == null || start.equals(ziel)) {
            return null;
        }

        AvTimeSpan resDuration = NO_TIME;
        boolean changed = false;
        Temperatur resTageshoechsttemperatur = start.getTageshoechsttemperatur();
        Temperatur resTagestiefsttemperatur = start.getTagestiefsttemperatur();
        Windstaerke resWindstaerke = start.getWindstaerkeUnterOffenemHimmel();
        Bewoelkung resBewoelkung = start.getBewoelkung();
        BlitzUndDonner resBlitzUndDonner = start.getBlitzUndDonner();

        // Damit es keine Inkonsistenzen passen wir in einem Schritt am besten immer beide
        // Temperaturen (Höchst- und Tiefst-) an - oder keine.

        // Bedeckter oder bewölkter Himmel soll sich erst auflockern, bevor die
        // Temperatur steigt!
        final boolean temperaturUndBewoelkungUndWindstaerkeDuerfenSichAendern =
                start.getBlitzUndDonner().compareTo(DONNERGROLLEN_IN_DER_FERNE) <= 0
                        || start.getBlitzUndDonner().compareTo(ziel.getBlitzUndDonner()) <= 0;

        final boolean bewoelkungDarfAuflockernUndTemperaturDarfSteigen =
                temperaturUndBewoelkungUndWindstaerkeDuerfenSichAendern
                        && (start.getWindstaerkeUnterOffenemHimmel().compareTo(WINDIG) <= 0
                        || start.getWindstaerkeUnterOffenemHimmel()
                        .compareTo(ziel.getWindstaerkeUnterOffenemHimmel()) <= 0);

        final boolean temperaturDarfSteigen =
                bewoelkungDarfAuflockernUndTemperaturDarfSteigen
                        && (start.getBewoelkung().compareTo(BEWOELKT) < 0
                        || start.getBewoelkung().compareTo(ziel.getBewoelkung()) <= 0);

        if (start.getTageshoechsttemperatur().compareTo(ziel.getTageshoechsttemperatur()) < 0) {
            if (temperaturDarfSteigen) {
                resTageshoechsttemperatur = resTageshoechsttemperatur.getNachfolger();
                resDuration = max(resDuration, hours(1));
                changed = true;
            }
        } else if (start.getTageshoechsttemperatur().compareTo(
                ziel.getTageshoechsttemperatur()) > 0) {
            if (temperaturUndBewoelkungUndWindstaerkeDuerfenSichAendern) {
                resTageshoechsttemperatur = resTageshoechsttemperatur.getVorgaenger();
                resDuration = max(resDuration, hours(1));
                changed = true;
            }
        }

        if (start.getTagestiefsttemperatur().compareTo(ziel.getTagestiefsttemperatur()) < 0) {
            if (temperaturDarfSteigen) {
                resTagestiefsttemperatur = resTagestiefsttemperatur.getNachfolger();
                resDuration = max(resDuration, hours(1));
                changed = true;
            }
        } else if (start.getTagestiefsttemperatur()
                .compareTo(ziel.getTagestiefsttemperatur()) > 0) {
            if (temperaturUndBewoelkungUndWindstaerkeDuerfenSichAendern) {
                resTagestiefsttemperatur = resTagestiefsttemperatur.getVorgaenger();
                resDuration = max(resDuration, hours(1));
                changed = true;
            }
        }

        if (start.getBewoelkung().compareTo(ziel.getBewoelkung()) < 0) {
            if (temperaturUndBewoelkungUndWindstaerkeDuerfenSichAendern
                    && (!changed || resTageshoechsttemperatur.compareTo(WARM) <= 0)) {
                resBewoelkung = resBewoelkung.getNachfolger();
                resDuration = max(resDuration, mins(20));
                changed = true;
            }
        } else if (start.getBewoelkung().compareTo(ziel.getBewoelkung()) > 0) {
            if (bewoelkungDarfAuflockernUndTemperaturDarfSteigen) {
                resBewoelkung = resBewoelkung.getVorgaenger();
                resDuration = max(resDuration, mins(20));
                changed = true;
            }
        }

        if (start.getWindstaerkeUnterOffenemHimmel()
                .compareTo(ziel.getWindstaerkeUnterOffenemHimmel()) < 0) {
            if (temperaturUndBewoelkungUndWindstaerkeDuerfenSichAendern &&
                    (!changed
                            ||
                            (resTageshoechsttemperatur.compareTo(WARM) <= 0
                                    && (
                                    start.getWindstaerkeUnterOffenemHimmel().compareTo(LUEFTCHEN)
                                            <= 0
                                            || resBewoelkung.compareTo(BEWOELKT) >= 0))
                            ||
                            (resTageshoechsttemperatur.compareTo(KUEHL) <= 0
                                    && start.getWindstaerkeUnterOffenemHimmel().compareTo(WINDIG)
                                    <= 0
                                    && resBewoelkung.compareTo(BEWOELKT) >= 0))) {
                resWindstaerke = resWindstaerke.getNachfolger();
                resDuration = max(resDuration, mins(15));
                changed = true;
            }
        } else if (start.getWindstaerkeUnterOffenemHimmel()
                .compareTo(ziel.getWindstaerkeUnterOffenemHimmel()) > 0) {
            if (temperaturUndBewoelkungUndWindstaerkeDuerfenSichAendern) {
                resWindstaerke = resWindstaerke.getVorgaenger();
                resDuration = max(resDuration, mins(15));
                changed = true;
            }
        }

        if (start.getBlitzUndDonner().compareTo(ziel.getBlitzUndDonner()) < 0) {
            if (!changed) {
                resBlitzUndDonner = resBlitzUndDonner.getNachfolger();
                resDuration = max(resDuration, mins(5));
                changed = true;
            }
        } else if (start.getBlitzUndDonner().compareTo(ziel.getBlitzUndDonner()) > 0) {
            resBlitzUndDonner = resBlitzUndDonner.getVorgaenger();
            resDuration = max(resDuration, mins(10));
            changed = true;
        }

        if (!changed) {
            // Das sollte nie passieren...
            throw new IllegalStateException("Kein Wetterschritt! changed = false");
        }

        final WetterData resWetter = new WetterData(
                resTageshoechsttemperatur, resTagestiefsttemperatur,
                resWindstaerke, resBewoelkung,
                resBlitzUndDonner);

        return new StandardWetterStep(resWetter, resDuration);
    }
}
