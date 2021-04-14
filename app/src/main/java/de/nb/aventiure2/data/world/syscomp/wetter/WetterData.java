package de.nb.aventiure2.data.world.syscomp.wetter;

import androidx.annotation.NonNull;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

import java.util.List;

import javax.annotation.CheckReturnValue;
import javax.annotation.concurrent.Immutable;

import de.nb.aventiure2.data.time.AvDateTime;
import de.nb.aventiure2.data.time.AvTime;
import de.nb.aventiure2.data.time.AvTimeSpan;
import de.nb.aventiure2.data.time.Tageszeit;
import de.nb.aventiure2.data.world.syscomp.storingplace.DrinnenDraussen;
import de.nb.aventiure2.data.world.syscomp.wetter.bewoelkung.Bewoelkung;
import de.nb.aventiure2.data.world.syscomp.wetter.bewoelkung.BewoelkungAdvAngabeSkopusVerbWohinWoherDescriber;
import de.nb.aventiure2.data.world.syscomp.wetter.bewoelkung.BewoelkungDescDescriber;
import de.nb.aventiure2.data.world.syscomp.wetter.bewoelkung.BewoelkungPraedikativumDescriber;
import de.nb.aventiure2.data.world.syscomp.wetter.bewoelkung.BewoelkungPraepPhrDescriber;
import de.nb.aventiure2.data.world.syscomp.wetter.bewoelkung.BewoelkungSatzDescriber;
import de.nb.aventiure2.data.world.syscomp.wetter.blitzunddonner.BlitzUndDonner;
import de.nb.aventiure2.data.world.syscomp.wetter.tageszeit.TageszeitAdvAngabeSkopusVerbWohinWoherDescriber;
import de.nb.aventiure2.data.world.syscomp.wetter.tageszeit.TageszeitDescDescriber;
import de.nb.aventiure2.data.world.syscomp.wetter.tageszeit.TageszeitPraedikativumDescriber;
import de.nb.aventiure2.data.world.syscomp.wetter.tageszeit.TageszeitSatzDescriber;
import de.nb.aventiure2.data.world.syscomp.wetter.temperatur.TagestemperaturverlaufUtil;
import de.nb.aventiure2.data.world.syscomp.wetter.temperatur.Temperatur;
import de.nb.aventiure2.data.world.syscomp.wetter.temperatur.TemperaturAdvAngabeSkopusVerbWohinWoherDescriber;
import de.nb.aventiure2.data.world.syscomp.wetter.temperatur.TemperaturPraedikativumDescriber;
import de.nb.aventiure2.data.world.syscomp.wetter.temperatur.TemperaturSatzDescriber;
import de.nb.aventiure2.data.world.syscomp.wetter.windstaerke.Windstaerke;
import de.nb.aventiure2.german.base.EinzelneSubstantivischePhrase;
import de.nb.aventiure2.german.base.Praepositionalphrase;
import de.nb.aventiure2.german.base.ZweiPraedikativa;
import de.nb.aventiure2.german.description.AbstractDescription;
import de.nb.aventiure2.german.description.AltDescriptionsBuilder;
import de.nb.aventiure2.german.praedikat.AdvAngabeSkopusSatz;
import de.nb.aventiure2.german.praedikat.AdvAngabeSkopusVerbAllg;
import de.nb.aventiure2.german.praedikat.AdvAngabeSkopusVerbWohinWoher;
import de.nb.aventiure2.german.satz.EinzelnerSatz;
import de.nb.aventiure2.german.satz.Satz;

import static de.nb.aventiure2.data.time.Tageszeit.ABENDS;
import static de.nb.aventiure2.data.time.Tageszeit.NACHTS;
import static de.nb.aventiure2.data.time.Tageszeit.TAGSUEBER;
import static de.nb.aventiure2.data.world.syscomp.storingplace.DrinnenDraussen.DRAUSSEN_UNTER_OFFENEM_HIMMEL;
import static de.nb.aventiure2.german.adjektiv.AdjektivOhneErgaenzungen.HEISS;
import static de.nb.aventiure2.german.adjektiv.AdjektivOhneErgaenzungen.SCHOEN;
import static de.nb.aventiure2.german.adjektiv.AdjektivOhneErgaenzungen.SENGEND;
import static de.nb.aventiure2.german.adjektiv.AdjektivOhneErgaenzungen.STARK;
import static de.nb.aventiure2.german.adjektiv.AdjektivOhneErgaenzungen.WARM;
import static de.nb.aventiure2.german.base.Artikel.Typ.INDEF;
import static de.nb.aventiure2.german.base.NomenFlexionsspalte.ABEND;
import static de.nb.aventiure2.german.base.NomenFlexionsspalte.MITTAGSSONNE;
import static de.nb.aventiure2.german.base.NomenFlexionsspalte.SONNE;
import static de.nb.aventiure2.german.base.NomenFlexionsspalte.SONNENHITZE;
import static de.nb.aventiure2.german.base.NomenFlexionsspalte.SONNENSCHEIN;
import static de.nb.aventiure2.german.base.Nominalphrase.np;
import static de.nb.aventiure2.german.base.Personalpronomen.EXPLETIVES_ES;
import static de.nb.aventiure2.german.base.PraepositionMitKasus.IN_AKK;
import static de.nb.aventiure2.german.base.PraepositionMitKasus.IN_DAT;
import static de.nb.aventiure2.german.description.AltDescriptionsBuilder.alt;
import static de.nb.aventiure2.german.description.AltDescriptionsBuilder.altNeueSaetze;
import static de.nb.aventiure2.german.praedikat.PraedikativumPraedikatOhneLeerstellen.praedikativumPraedikatMit;
import static de.nb.aventiure2.german.praedikat.VerbSubj.BRENNEN;
import static de.nb.aventiure2.german.praedikat.VerbSubj.HERUNTERSCHEINEN;
import static de.nb.aventiure2.german.praedikat.VerbSubj.SCHEINEN;
import static de.nb.aventiure2.util.StreamUtil.*;

@Immutable
class WetterData {
    private static final TemperaturPraedikativumDescriber TEMPERATUR_PRAEDIKATIVUM_DESCRIBER =
            new TemperaturPraedikativumDescriber();

    private static final TageszeitPraedikativumDescriber TAGESZEIT_PRAEDIKATIVUM_DESCRIBER =
            new TageszeitPraedikativumDescriber();

    private static final TageszeitSatzDescriber TAGESZEIT_SATZ_DESCRIBER =
            new TageszeitSatzDescriber(TAGESZEIT_PRAEDIKATIVUM_DESCRIBER);

    private static final TageszeitDescDescriber TAGESZEIT_DESC_DESCRIBER =
            new TageszeitDescDescriber(TAGESZEIT_SATZ_DESCRIBER);

    private static final TageszeitAdvAngabeSkopusVerbWohinWoherDescriber
            TAGESZEIT_ADV_ANGABE_SKOPUS_VERB_WOHIN_WOHER_DESCRIBER =
            new TageszeitAdvAngabeSkopusVerbWohinWoherDescriber(TAGESZEIT_PRAEDIKATIVUM_DESCRIBER);

    private static final TemperaturAdvAngabeSkopusVerbWohinWoherDescriber
            TEMPERATUR_ADV_ANGABE_SKOPUS_VERB_WOHIN_WOHER_DESCRIBER =
            new TemperaturAdvAngabeSkopusVerbWohinWoherDescriber(
                    TEMPERATUR_PRAEDIKATIVUM_DESCRIBER);

    private static final TemperaturSatzDescriber TEMPERATUR_SATZ_DESCRIBER =
            new TemperaturSatzDescriber(
                    TAGESZEIT_PRAEDIKATIVUM_DESCRIBER,
                    TEMPERATUR_PRAEDIKATIVUM_DESCRIBER);

    private static final BewoelkungPraedikativumDescriber BEWOELKUNG_PRAEDIKATIVUM_DESCRIBER =
            new BewoelkungPraedikativumDescriber();

    private static final BewoelkungPraepPhrDescriber BEWOELKUNG_PRAEP_PHR_DESCRIBER =
            new BewoelkungPraepPhrDescriber(BEWOELKUNG_PRAEDIKATIVUM_DESCRIBER);

    private static final BewoelkungAdvAngabeSkopusVerbWohinWoherDescriber
            BEWOELKUNG_ADV_ANGABE_SKOPUS_VERB_WOHIN_WOHER_DESCRIBER =
            new BewoelkungAdvAngabeSkopusVerbWohinWoherDescriber(
                    BEWOELKUNG_PRAEDIKATIVUM_DESCRIBER);

    private static final BewoelkungSatzDescriber BEWOELKUNG_SATZ_DESCRIBER =
            new BewoelkungSatzDescriber(BEWOELKUNG_PRAEDIKATIVUM_DESCRIBER);

    private static final BewoelkungDescDescriber BEWOELKUNG_DESC_DESCRIBER =
            new BewoelkungDescDescriber(BEWOELKUNG_SATZ_DESCRIBER);

    private final Temperatur tageshoechsttemperatur;

    private final Temperatur tagestiefsttemperatur;

    private final Windstaerke windstaerke;

    private final Bewoelkung bewoelkung;

    private final BlitzUndDonner blitzUndDonner;

    @SuppressWarnings("WeakerAccess")
    public WetterData(final Temperatur tageshoechsttemperatur,
                      final Temperatur tagestiefsttemperatur,
                      final Windstaerke windstaerke,
                      final Bewoelkung bewoelkung,
                      final BlitzUndDonner blitzUndDonner) {
        this.tageshoechsttemperatur = tageshoechsttemperatur;
        this.tagestiefsttemperatur = tagestiefsttemperatur;
        this.windstaerke = windstaerke;
        this.bewoelkung = bewoelkung;
        this.blitzUndDonner = blitzUndDonner;
    }

    // FIXME Ist überall der Fall bedacht, dass der SC in einer undurchsichtigen Kiste sitzt?
    //  Machen die Methodennamen klar, wann es keinen Sinn macht, sie aufzurufen?

    // FIXME Versuchen, die folgenden Methoden von oben zu vereinfachen!
    //  Am besten sortieren nach: Temperatur relevant,
    //  Bewölkung relevant, beides relevant, Tageszeit relevant,
    //  Lichtverhältnisse relevant, nichts relevant...

    /**
     * Gibt alternative Wetterhinweise zurück (für draußen).
     * Die Methode geht davon aus, dass einer der Wetterhinweise auch ausgegeben wird
     * (und vermerkt entsprechend i.A., dass nicht gleich wieder ein Wetterhinweis nötig sein
     * wird).
     */
    ImmutableCollection<AbstractDescription<?>> altWetterHinweiseFuerDraussen(
            final AvTime time,
            final boolean unterOffenemHimmel) {
        // FIXME Windstärke berücksichtigen
        // FIXME Blitz und Donner berücksichtigen

        final AltDescriptionsBuilder alt = alt();

        final Temperatur temperatur = getTemperatur(time);

        alt.addAll(altDescUeberHeuteOderDenTagWennDraussenSinnvoll(time, unterOffenemHimmel));

        if (unterOffenemHimmel) {
            alt.addAll(altStatischSaetzeSonneWennUnterOffenemHimmelSinnvoll(
                    time, true));

            // "Es ist kühl und der Himmel ist bewölkt"
            alt.addAll(altNeueSaetze(
                    TEMPERATUR_SATZ_DESCRIBER.altStatisch(
                            temperatur, time.getTageszeit(), true),
                    "und",
                    BEWOELKUNG_SATZ_DESCRIBER
                            .altUnterOffenemHimmelStatisch(bewoelkung, time)));

            if (    // Eine leichte Bewölkung braucht man nicht unbedingt zu erwähnen
                    bewoelkung.isUnauffaellig(time.getTageszeit())) {
                // "Es ist kühl"
                alt.addAll(TEMPERATUR_SATZ_DESCRIBER.altStatisch(
                        temperatur, time.getTageszeit(), true));
            }

            if (temperatur.isUnauffaellig(time.getTageszeit())) {
                // Bei einer mittleren Temperatur braucht man die Temperatur nicht unbedingt zu
                // erwähnen.
                alt.addAll(BEWOELKUNG_DESC_DESCRIBER
                        .altUnterOffenemHimmelStatisch(bewoelkung, time));
            }
        } else {
            // "Es ist kühl"
            alt.addAll(TEMPERATUR_SATZ_DESCRIBER.altStatisch(
                    temperatur, time.getTageszeit(), true));
        }

        if (time.getTageszeit() == NACHTS) {
            if (!unterOffenemHimmel || bewoelkung.isUnauffaellig(time.getTageszeit())) {
                // "es ist dunkel und ziemlich kühl"
                alt.addAll(altDunkelSaetze(time));
            }
        } else {
            alt.addAll(altStatischDraussenSpeziellNichtNachts(time, unterOffenemHimmel));
        }

        return alt.schonLaenger().build();
    }

    @CheckReturnValue
    ImmutableCollection<EinzelnerSatz> altStatischeTemperaturBeschreibungSaetze(
            final AvTime time,
            final boolean draussen) {
        return TEMPERATUR_SATZ_DESCRIBER.altStatisch(
                getTemperatur(time), time.getTageszeit(), draussen);
    }

    @CheckReturnValue
    AltDescriptionsBuilder altKommtNachDraussen(
            final AvTime time,
            final boolean unterOffenenHimmel) {
        // FIXME Windstärke berücksichtigen
        // FIXME Blitz und Donner berücksichtigen

        final Temperatur temperatur = getTemperatur(time);

        final AltDescriptionsBuilder alt = alt();

        alt.addAll(altDescUeberHeuteOderDenTagWennDraussenSinnvoll(time, unterOffenenHimmel));

        if (unterOffenenHimmel) {
            alt.addAll(
                    altStatischSaetzeSonneWennUnterOffenemHimmelSinnvoll(
                            time, true, "draußen"));

            // "Draußen ist es kühl und der Himmel ist bewölkt"
            alt.addAll(altNeueSaetze(
                    TEMPERATUR_SATZ_DESCRIBER.altKommtNachDraussen(
                            temperatur, time.getTageszeit()),
                    "und",
                    BEWOELKUNG_SATZ_DESCRIBER
                            .altUnterOffenemHimmelStatisch(bewoelkung, time)));


            if (// Eine leichte Bewölkung braucht man nicht unbedingt zu erwähnen
                    bewoelkung.isUnauffaellig(time.getTageszeit())) {
                // "Draußen ist es kühl"
                alt.addAll(TEMPERATUR_SATZ_DESCRIBER.altKommtNachDraussen(
                        temperatur, time.getTageszeit()));
            }

            if (temperatur.isUnauffaellig(time.getTageszeit())) {
                // Bei einer mittleren Temperatur braucht man die Temperatur nicht unbedingt zu
                // erwähnen.
                // "Draußen ist der Himmel bewölkt"
                alt.addAll(BEWOELKUNG_DESC_DESCRIBER
                        .altKommtUnterOffenenHimmel(bewoelkung, time, true));
            }
        } else {
            // "Draußen ist es kühl"
            alt.addAll(TEMPERATUR_SATZ_DESCRIBER.altKommtNachDraussen(
                    temperatur, time.getTageszeit()));
        }

        if (time.getTageszeit() == NACHTS) {
            if (!unterOffenenHimmel || bewoelkung.isUnauffaellig(time.getTageszeit())) {
                // "draussen ist es dunkel und ziemlich kühl"
                alt.addAll(mapToSet(altDunkelSaetze(time),
                        s -> s.mitAdvAngabe(new AdvAngabeSkopusSatz("draußen"))));
            }
        } else {
            alt.addAll(
                    altScKommtNachDraussenSpeziellNichtNachts(time, unterOffenenHimmel));
        }

        return alt.schonLaenger();
    }

    @CheckReturnValue
    private AltDescriptionsBuilder altScKommtNachDraussenSpeziellNichtNachts(
            final AvTime time, final boolean unterOffenenHimmel) {
        // FIXME Windstärke berücksichtigen
        // FIXME Blitz und Donner berücksichtigen

        final Temperatur temperatur = getTemperatur(time);

        final AltDescriptionsBuilder alt = alt();

        if (time.getTageszeit() == TAGSUEBER
                && TemperaturSatzDescriber.ueberHeuteOderDenTagSinnvoll(
                temperatur, time)
                && unterOffenenHimmel) {
            alt.addAll(altNeueSaetze(
                    BEWOELKUNG_SATZ_DESCRIBER.altKommtUnterOffenenHimmel(bewoelkung, time, true),
                    ";",
                    TEMPERATUR_SATZ_DESCRIBER.altDraussenUeberHeuteOderDenTag(temperatur)));
        } else if (time.getTageszeit() == ABENDS) {
            alt.addAll(altScKommtNachDraussenSpeziellAbends(time, unterOffenenHimmel));
        }

        return alt;
    }

    @CheckReturnValue
    private AltDescriptionsBuilder altStatischDraussenSpeziellNichtNachts(
            final AvTime time, final boolean unterOffenenHimmel) {
        // FIXME Windstärke berücksichtigen
        // FIXME Blitz und Donner berücksichtigen

        final Temperatur temperatur = getTemperatur(time);

        final AltDescriptionsBuilder alt = alt();

        if (time.getTageszeit() == TAGSUEBER
                && TemperaturSatzDescriber.ueberHeuteOderDenTagSinnvoll(
                temperatur, time)
                && unterOffenenHimmel) {
            alt.addAll(altNeueSaetze(
                    BEWOELKUNG_SATZ_DESCRIBER.altUnterOffenemHimmelStatisch(bewoelkung, time),
                    ";",
                    TEMPERATUR_SATZ_DESCRIBER.altDraussenUeberHeuteOderDenTag(temperatur)));
        } else if (time.getTageszeit() == ABENDS) {
            alt.addAll(altStatischDraussenSpeziellAbends(time, unterOffenenHimmel));
        }

        return alt;
    }

    @CheckReturnValue
    private AltDescriptionsBuilder altScKommtNachDraussenSpeziellAbends(
            final AvTime time, final boolean unterOffenenHimmel) {
        // FIXME Windstärke und Blitz / Donner berücksichtigen

        final Temperatur temperatur = getTemperatur(time);

        final AltDescriptionsBuilder alt = alt();

        if (temperatur.isBetweenIncluding(Temperatur.WARM, Temperatur.RECHT_HEISS)) {
            if (unterOffenenHimmel) {
                alt.addAll(altNeueSaetze(
                        BEWOELKUNG_SATZ_DESCRIBER
                                .altKommtUnterOffenenHimmel(bewoelkung, time, true),
                        ";",
                        TEMPERATUR_SATZ_DESCRIBER.altEsIstNochDraussen(temperatur)));
            } else {
                alt.addAll(TEMPERATUR_SATZ_DESCRIBER.altEsIstNochDraussen(temperatur));
            }

            if (bewoelkung.isUnauffaellig(time.getTageszeit())) {
                alt.addAll(altStatischDraussenSaetzeUnauffaelligeBewoelkungSpeziellAbends(time,
                        unterOffenenHimmel, temperatur));
            }
        }

        return alt;
    }

    @CheckReturnValue
    private AltDescriptionsBuilder altStatischDraussenSpeziellAbends(
            final AvTime time, final boolean unterOffenenHimmel) {
        // FIXME Windstärke und Blitz / Donner berücksichtigen

        final Temperatur temperatur = getTemperatur(time);

        final AltDescriptionsBuilder alt = alt();

        if (temperatur.isBetweenIncluding(Temperatur.WARM, Temperatur.RECHT_HEISS)) {
            if (unterOffenenHimmel) {
                alt.addAll(altNeueSaetze(
                        BEWOELKUNG_SATZ_DESCRIBER
                                .altUnterOffenemHimmelStatisch(bewoelkung, time),
                        ";",
                        TEMPERATUR_SATZ_DESCRIBER.altEsIstNochDraussen(temperatur)));
            } else {
                alt.addAll(TEMPERATUR_SATZ_DESCRIBER.altEsIstNochDraussen(temperatur));
            }

            if (bewoelkung.isUnauffaellig(time.getTageszeit())) {
                alt.addAll(altStatischDraussenSaetzeUnauffaelligeBewoelkungSpeziellAbends(time,
                        unterOffenenHimmel, temperatur));
            }
        }

        return alt;
    }

    private AltDescriptionsBuilder altStatischDraussenSaetzeUnauffaelligeBewoelkungSpeziellAbends(
            final AvTime time,
            final boolean unterOffenenHimmel,
            final Temperatur temperatur) {
        final AltDescriptionsBuilder alt = alt();
        // "Es ist ein schöner Abend und noch ziemlich warm"
        alt.addAll(
                TEMPERATUR_PRAEDIKATIVUM_DESCRIBER.altStatischAdjPhr(temperatur).stream()
                        .map(tempAdjPhr -> new ZweiPraedikativa<>(
                                np(INDEF, SCHOEN, ABEND),
                                tempAdjPhr.mitAdvAngabe(
                                        new AdvAngabeSkopusSatz("noch")))
                                .alsEsIstSatz()));
        if (temperatur == Temperatur.WARM) {
            if (unterOffenenHimmel) {
                // "Es ist ein schöner Abend, die Sonne scheint"
                alt.addAll(altNeueSaetze(
                        praedikativumPraedikatMit(np(INDEF, SCHOEN, ABEND))
                                .alsSatzMitSubjekt(EXPLETIVES_ES),
                        ",",
                        BEWOELKUNG_SATZ_DESCRIBER
                                .altUnterOffenemHimmelStatisch(bewoelkung, time)));
            } else {
                // "Es ist ein schöner Abend"
                alt.add(praedikativumPraedikatMit(np(INDEF, SCHOEN, ABEND))
                        .alsSatzMitSubjekt(EXPLETIVES_ES));
            }
        }

        return alt;
    }

    private ImmutableCollection<Satz> altDunkelSaetze(final AvTime time) {
        final ImmutableCollection.Builder<Satz> alt = ImmutableSet.builder();

        final Temperatur temperatur = getTemperatur(time);
        if (temperatur.isUnauffaellig(time.getTageszeit())) {
            // Eine normale Nacht-Temperatur braucht man nicht unbedingt zu erwähnen -
            // selbst auf die Bewölkung kann man verzichten.
            alt.addAll(TAGESZEIT_SATZ_DESCRIBER.esIstSchonBereitsNochDunkelHellAdjPhr(time));
        }

        // "es ist (schon) dunkel und ziemlich kühl"
        alt.addAll(TEMPERATUR_SATZ_DESCRIBER
                .altStatischMitTageszeitLichtverhaeltnissen(temperatur, time));

        return alt.build();
    }

    /**
     * Gibt alternative Beschreibungen zurück für den Fall, dass diese Zeit vergangen ist -
     * zuallermeist leer.
     */
    @NonNull
    ImmutableCollection<AbstractDescription<?>> altTimePassed(
            final AvDateTime startTime,
            final AvDateTime endTime,
            final DrinnenDraussen drinnenDraussen) {
        if (endTime.minus(startTime).longerThan(AvTimeSpan.ONE_DAY)) {
            // Die Spieler-Action hat sicher ohnehin erzählt, was passiert ist.
            return ImmutableSet.of();
        }

        // Es gab also potenziell einen (oder mehrere) Tageszeitenwechsel oder Wetterwechsel
        // während einer Zeit von weniger als einem Tag
        return altTimePassed(startTime.getTageszeit(), endTime.getTageszeit(), drinnenDraussen);
    }

    /**
     * Gibt alternative Beschreibungen zurück für den Fall, dass innerhalb maximal eines Tages
     * dieser Tageszeitenwechsel geschehen ist - bei gleicher Tageszeit leer.
     */
    @NonNull
    @CheckReturnValue
    private ImmutableCollection<AbstractDescription<?>> altTimePassed(
            final Tageszeit lastTageszeit,
            final Tageszeit currentTageszeit,
            final DrinnenDraussen drinnenDraussen) {
        if (lastTageszeit == currentTageszeit) {
            // Entweder ist nur wenig Zeit vergangen - oder mehr als ein Tag, dann hat die
            // Action
            // sicher ohnehin erzählt, was passiert ist.

            // FIXME Über den Tag weitere Texte verteilen, so dass die Zeit spürbarer vergeht:
            //  - die Sonne (draußen, Bewölkung) steht schon hoch
            //  - es ist schon weit nach Mittag
            //  (als statische Texte oder als Veränderungen)
            return ImmutableSet.of();
        }

        final AltDescriptionsBuilder alt = alt();

        // "Langsam wird es Morgen" / "hell"
        alt.addAll(TAGESZEIT_DESC_DESCRIBER.altSprungOderWechsel(
                lastTageszeit, currentTageszeit, drinnenDraussen.isDraussen()));

        if (drinnenDraussen.isDraussen()) {
            // "Die Sterne verblassen und die Sonne ist am Horizont zu sehen"
            alt.addAll(BEWOELKUNG_DESC_DESCRIBER.altTageszeitenSprungOderWechsel(
                    getBewoelkung(),
                    lastTageszeit, currentTageszeit,
                    drinnenDraussen == DRAUSSEN_UNTER_OFFENEM_HIMMEL));

            // FIXME WENN lastTageszeit == TAGSUEBER && currentTageszeit == ABENDS:
            //  "Heute ist ein schönes
            //  Abendrot zu sehen" - welche Voraussetzungen gäbe es dafür?
            //  Nicht zu kalt? Etwas Bewölkung?!
        }

        return alt.schonLaenger().build();
    }

    // FIXME Idee zur Vereinheitlich (sofern das hilfreich ist):
    //  - Prio ermitteln: Was soll mit welcher Priorität erzählt werde?
    //   (1. Temp 2. Bewölkumg 3. Tageszeit o.Ä.)
    //  - Danach entsprechende Methoden eines Interfaces
    //    aufrufen, etwa altTemperaturBewoelkungTageszeit()
    //  - Das Interface ist ein paar mal implementiert, jeweils für
    //    statische Beschreibungssätze, für kommt-nach-draußen-Sätze,
    //    für Adjektive etc. (Die Implementierungen können sich problemlos
    //    gegenseitig aufrufen.)
    //    Damit wäre die Logik "was muss man mit welcher Prio erzählen"
    //    - die vielleicht immer gleich ist? - getrennt von
    //    dem "Rendering" als statischem Satz, kommt-nach-draußen-Description o.Ä.

    // FIXME Grundsätzlich könnte man sich die höchste und die niedrigste "heute" schon
    //  berichtete Temperatur merken. Ändert sich die diese Temperatur (z.B. der
    //  SC geht aus dem kühlen Schloss in die Hitze oder die Temperatur steigt draußen
    //  über den Tag), könnte es eine Ausgabe geben.
    //  Einfachste Lösung: wennDraussenDannWieder... auf true setzen.

    // FIXME Manche Wetterphänomene und der "tageszeitliche Himmel"
    //  ("du siehst ein schönes Abendrot") sollten nur dann erzählt werden, wenn der SC
    //  "draußen" ist bzw. sogar "einen Blick auf den Himmel hat" (drinnenDraussenSc).

    // FIXME Man könnte, wenn der Benutzer erstmals wieder nach draußen kommt, etwas
    //  schreiben wie "Inzwischen ist es dunkel geworden". Dazu müsste der "Tageszeit-Status"
    //  (oder zumindest der Zeitpunkt) gespeichert werden, wenn der Benutzer REIN GEHT
    //  und später beim RAUSTRETEN dieser Status mit dem aktuellen Tageszeitstatus verglichen
    //  werden. Oder es müsste bei einer Änderung notiert werden, dass später noch eine
    //  Information zu erfolgen hat (ggf. noch einer Prüfung).

    // FIXME Verschiedene Fälle unterscheiden:
    //  -SC ist draußen und die Tageszeit hat zwischen startTime und endTime gewechselt:
    //   "Langsam wird es hell", "Die Sonne geht auf", "Unterdessen ist es hell geworden", ...
    //  - SC ist draußen und die Tageszeit hat VOR startTime gewechselt:
    //   "Inzwischen ist es hell geworden", "Unterdessen ist es hell geworden",
    //   "Die Sonne ist aufgegangen", "Draußen ist es inzwischen hell geworden"
    //  - SC ist drinnen ohne Sicht nach draußen:
    //   "Dein Gefühl sagt dir: Allmählich ist es Morgen geworden"
    //   "Wahrscheinlich ist schon der nächste Tag angebrochen"
    //   "Ob wohl schon die Sonne aufgegangen ist?"

    // FIXME Weitere Formulierungen für Veränderungen, die man miterlebt
    //  "der erste Strahl der aufgehenden Sonne dringt am Himmel herauf"
    //  "Die Sonne geht auf"
    //  "Der erste Sonnenstrahl bricht hervor"
    //  "Nun kommt die Sonne"
    //  "Die Sonne geht unter"
    //  "Du siehst du die Sonne (hinter den Bergen) aufsteigen"
    //  "die Sonne sinkt und die Nacht bricht ein"
    //  "die Nacht bricht ein"

    // FIXME Veränderungen der Temperatur
    //  "es kühlt (deutlich) ab" (Temperatur)

    // FIXME Veränderungen der Bewölkung
    //  es klart auf / der Himmel bedeckt sich/ bezieht sich (Bewölkung)
    //  "Der Mond geht auf" / "Der Mond steigt (über dem Berg) auf"
    //  "Der Mond kommt"

    // FIXME Weitere Formulierungen für Veränderungen, die man erst danach bemerkt
    //  "es bricht eben der erste Sonnenstrahl hervor"
    //  "in dem Augenblick dringt der erste Strahl der aufgehenden Sonne am Himmel herauf"
    //  "Die Sonne will eben untergehen"
    //  "Die Sonne ist untergegangen"
    //  "Nun ist die Sonne unter"
    //  "die Sonne ist hinter (den Bergen) verschwunden"
    //  "du kommst aus (der Finsternis) heraus in das Tageslicht"
    //  "die Sonne ist (hinter die Berge) gesunken"

    // FIXME Nachträglich bemerkte Veränderungen der Bewölkung
    //  "Der Mond ist aufgegangen", "Der Mond ist schon aufgestiegen"
    //  (MACHT NUR SINN, WENN ES EINE ÄNDERUNG GEGENÜBER
    //  DEM LETZTEN INFORMATIONSSTAND IST)
    //  „Du trittst aus dem Wald hinaus. Purpurnes Abendrot erstreckt sich über den
    //  Horizont....“

    // FIXME Kombination: "Es hat deutlich abgekühlt und der Himmel bezieht sich."

    // FIXME Konzept am Hunger orientieren? Als "Erinnerung"?
    //  Vor allem auch bedenken, dass es ja über den Tag immer wärmer (und auch wieder
    //  kälter) wird. Die Änderungen müssen wohl also beschrieben werden, obwohl sich die
    //  eigentliche "Tagestemperatur" nicht ändern.
    //  Vielleicht muss man doch - wie bei der Müdigkeit - den letzten
    //  Temperatur-Wert speichern?

    @NonNull
    @CheckReturnValue
    private List<Satz> altStatischSaetzeSonneWennUnterOffenemHimmelSinnvoll(
            final AvTime time,
            final boolean auchMitBezugAufKonkreteTageszeit,
            final String advAngabeText) {
        return mapToList(
                altStatischSaetzeSonneWennUnterOffenemHimmelSinnvoll(time,
                        auchMitBezugAufKonkreteTageszeit),
                s -> s.mitAdvAngabe(new AdvAngabeSkopusSatz(advAngabeText)));
    }

    /**
     * Gibt alternative {@link AbstractDescription}s zurück, die sich auf "heute", "den Tag"
     * o.Ä.
     * beziehen - soweit draußen sinnvoll, sonst eine leere Collection.
     */
    @NonNull
    @CheckReturnValue
    ImmutableCollection<AbstractDescription<?>>
    altDescUeberHeuteOderDenTagWennDraussenSinnvoll(final AvTime time,
                                                    final boolean unterOffenemHimmel) {
        // FIXME Windstärke berücksichtigen
        // FIXME Blitz und Donner berücksichtigen

        final AltDescriptionsBuilder alt = alt();

        final Temperatur temperatur = getTemperatur(time);

        if (TemperaturSatzDescriber.ueberHeuteOderDenTagSinnvoll(temperatur, time)
                && time.getTageszeit() != NACHTS) {
            if (unterOffenemHimmel) {
                alt.addAll(
                        altStatischSaetzeSonneWennUnterOffenemHimmelSinnvoll(time,
                                false,
                                "heute"));
            }

            if (!unterOffenemHimmel
                    // Eine leichte Bewölkung braucht man nicht unbedingt zu erwähnen
                    || bewoelkung.isUnauffaellig(time.getTageszeit())
                    // Und nachts kann man die Bewölkung ebenfalls ignorieren
                    || time.getTageszeit() != NACHTS) {
                alt.addAll(altNeueSaetze(TEMPERATUR_SATZ_DESCRIBER
                        .altDraussenUeberHeuteOderDenTag(temperatur)));
            }
            if (time.getTageszeit() == TAGSUEBER
                    && temperatur.compareTo(Temperatur.RECHT_HEISS) >= 0
                    && unterOffenemHimmel) {
                // "der Tag ist warm, die Sonne sticht"
                alt.addAll(altNeueSaetze(
                        TEMPERATUR_SATZ_DESCRIBER.altDraussenUeberHeuteOderDenTag(temperatur),
                        ", die Sonne sticht"));
            }
        }

        return alt.schonLaenger().build();
    }

    /**
     * Gibt alternative Sätze zurück über die Sonnenhitze - wenn sinnvoll, sonst eine leere
     * {@link java.util.Collection}.
     */
    @NonNull
    @CheckReturnValue
    private ImmutableCollection<Satz>
    altStatischSaetzeSonneWennUnterOffenemHimmelSinnvoll(
            final AvTime time, final boolean auchMitBezugAufKonkreteTageszeit) {
        final Temperatur temperatur = getTemperatur(time);

        final ImmutableList.Builder<Satz> alt = ImmutableList.builder();

        // FIXME Windstärke berücksichtigen
        // FIXME Blitz und Donner berücksichtigen

        if (bewoelkung.isUnauffaellig(time.getTageszeit())) {
            if (temperatur.compareTo(Temperatur.RECHT_HEISS) == 0) {
                alt.add(SCHEINEN.alsSatzMitSubjekt(SONNE)
                                .mitAdvAngabe(new AdvAngabeSkopusVerbAllg(
                                        WARM.mitGraduativerAngabe("sehr"))),
                        SCHEINEN.alsSatzMitSubjekt(SONNE)
                                .mitAdvAngabe(new AdvAngabeSkopusVerbAllg(HEISS)),
                        HERUNTERSCHEINEN.alsSatzMitSubjekt(SONNE)
                                .mitAdvAngabe(new AdvAngabeSkopusVerbAllg(HEISS)));
            }

            if (temperatur.compareTo(Temperatur.SEHR_HEISS) == 0) {
                alt.add(BRENNEN.alsSatzMitSubjekt(SONNE)
                                .mitAdvAngabe(new AdvAngabeSkopusVerbAllg(HEISS)),
                        BRENNEN.alsSatzMitSubjekt(SONNENHITZE)
                                .mitAdvAngabe(new AdvAngabeSkopusVerbAllg(STARK))
                );

                if (auchMitBezugAufKonkreteTageszeit && time.gegenMittag()) {
                    alt.add(BRENNEN.alsSatzMitSubjekt(SONNE)
                            .mitAdvAngabe(new AdvAngabeSkopusSatz("zu Mittag"))
                            .mitAdvAngabe(new AdvAngabeSkopusVerbAllg(HEISS)));
                }
            }
        }

        if (auchMitBezugAufKonkreteTageszeit
                && time.kurzVorSonnenaufgang()
                && temperatur.isUnauffaellig(time.getTageszeit())
                && bewoelkung.isUnauffaellig(time.getTageszeit())) {
            // Eine normale Temperatur und leichte Bewölkung braucht man nicht
            // unbedingt zu erwähnen.
            alt.addAll(mapToSet(
                    BEWOELKUNG_SATZ_DESCRIBER.
                            altTageszeitenwechsel
                                    (bewoelkung, Tageszeit.MORGENS, true),
                    sonneGehtAuf -> sonneGehtAuf.mitAdvAngabe(
                            new AdvAngabeSkopusVerbAllg("bald"))));
        }

        return alt.build();
    }

    @NonNull
    @CheckReturnValue
    ImmutableSet<String> altWetterplauderrede(final AvTime time) {
        final Temperatur temperatur = getTemperatur(time);

        if (temperatur == Temperatur.KLIRREND_KALT
                || temperatur == Temperatur.SEHR_HEISS
                || bewoelkung == Bewoelkung.BEDECKT) {
            return ImmutableSet
                    .of("Was ein Wetter!", "Was für ein Wetter!", "Welch ein Wetter!");
        }

        if (bewoelkung == Bewoelkung.BEWOELKT) {
            return ImmutableSet.of("Das Wetter war ja auch schon mal besser.");
        }

        // FIXME Windstärke berücksichtigen
        // FIXME Blitz und Donner berücksichtigen

        return ImmutableSet.of("Schönes Wetter heut!", "Schönes Wetter heut.");
    }

    @NonNull
    @CheckReturnValue
    ImmutableCollection<AdvAngabeSkopusVerbWohinWoher> altWohinHinaus(
            final AvTime time,
            final boolean unterOffenenHimmel) {
        final Temperatur temperatur = getTemperatur(time);

        final ImmutableCollection.Builder<AdvAngabeSkopusVerbWohinWoher> alt =
                ImmutableSet.builder();

        if (unterOffenenHimmel) {
            alt.addAll(altInSonnenhitzeHinausWennUnterOffenemHimmelSinnvoll(time));
            if (temperatur.isUnauffaellig(time.getTageszeit())) {
                // Bei einer mittleren Temperatur braucht man die Temperatur nicht unbedingt zu
                // erwähnen.
                alt.addAll(BEWOELKUNG_ADV_ANGABE_SKOPUS_VERB_WOHIN_WOHER_DESCRIBER
                        .altWohinHinausUnterOffenenHimmel(bewoelkung, time));
            }
        } else if (temperatur.isUnauffaellig(time.getTageszeit())) {
            // Bei einer mittleren Temperatur braucht man die Temperatur nicht unbedingt zu
            // erwähnen.
            alt.addAll(TAGESZEIT_ADV_ANGABE_SKOPUS_VERB_WOHIN_WOHER_DESCRIBER
                    .altWohinHinaus(time));
        }

        if (temperatur.isUnauffaellig(time.getTageszeit())
                && bewoelkung.isUnauffaellig(time.getTageszeit())) {
            alt.addAll(TAGESZEIT_ADV_ANGABE_SKOPUS_VERB_WOHIN_WOHER_DESCRIBER
                    .altWohinHinaus(time));
        }

        if (time.getTageszeit() == NACHTS) {
            alt.addAll(altWohinHinausSpeziellNachts(time));
        } else {
            alt.addAll(altWohinHinausSpeziellNichtNachts(time, unterOffenenHimmel));
        }

        return alt.build();
    }

    @NonNull
    @CheckReturnValue
    private Iterable<AdvAngabeSkopusVerbWohinWoher>
    altInSonnenhitzeHinausWennUnterOffenemHimmelSinnvoll(
            final AvTime time) {
        return mapToSet(altSonnenHitzeWennUnterOffenemHimmelSinnvoll(time),
                sonnenhitze -> new AdvAngabeSkopusVerbWohinWoher(IN_AKK.mit(sonnenhitze)));
    }

    private Iterable<AdvAngabeSkopusVerbWohinWoher> altWohinHinausSpeziellNichtNachts(
            final AvTime time, final boolean unterOffenenHimmel) {
        final Temperatur temperatur = getTemperatur(time);

        final ImmutableCollection.Builder<AdvAngabeSkopusVerbWohinWoher> alt =
                ImmutableSet.builder();

        // FIXME Windstärke berücksichtigen
        // FIXME Blitz und Donner berücksichtigen

        if (!unterOffenenHimmel
                // Eine leichte Bewölkung braucht man nicht unbedingt zu erwähnen
                || bewoelkung.isUnauffaellig(time.getTageszeit())) {
            alt.addAll(TEMPERATUR_ADV_ANGABE_SKOPUS_VERB_WOHIN_WOHER_DESCRIBER
                    .altWohinHinaus(temperatur, time));

            if (temperatur.isUnauffaellig(time.getTageszeit())) {
                alt.addAll(TAGESZEIT_ADV_ANGABE_SKOPUS_VERB_WOHIN_WOHER_DESCRIBER
                        .altWohinHinaus(time));
            }
        }

        return alt.build();
    }

    @NonNull
    @CheckReturnValue
    private ImmutableCollection<AdvAngabeSkopusVerbWohinWoher> altWohinHinausSpeziellNachts(
            final AvTime time) {
        final Temperatur temperatur = getTemperatur(time);

        final ImmutableCollection.Builder<AdvAngabeSkopusVerbWohinWoher> alt =
                ImmutableList.builder();

        if (temperatur.isUnauffaellig(NACHTS)) {
            // Eine normale Nacht-Temperatur braucht man nicht unbedingt zu erwähnen -
            // selbst auf die Bewölkung kann man verzichten.
            alt.addAll(TAGESZEIT_ADV_ANGABE_SKOPUS_VERB_WOHIN_WOHER_DESCRIBER
                    .altWohinHinaus(time));
        }

        alt.addAll(TEMPERATUR_ADV_ANGABE_SKOPUS_VERB_WOHIN_WOHER_DESCRIBER
                .altWohinHinausDunkelheit(temperatur, time.getTageszeit()));

        return alt.build();
    }

    @NonNull
    @CheckReturnValue
    ImmutableSet<Praepositionalphrase> altUnterOffenemHimmel(final AvTime time) {
        final ImmutableSet.Builder<Praepositionalphrase> alt = ImmutableSet.builder();

        // FIXME Windstärke berücksichtigen?
        // FIXME Blitz und Donner berücksichtigen?

        alt.addAll(BEWOELKUNG_PRAEP_PHR_DESCRIBER
                .altUnterOffenemHimmel(getBewoelkung(), time.getTageszeit()));

        final ImmutableList<EinzelneSubstantivischePhrase> altSonnenhitzeWennSinnvoll =
                altSonnenHitzeWennUnterOffenemHimmelSinnvoll(time);

        alt.addAll(mapToSet(altSonnenhitzeWennSinnvoll, IN_DAT::mit));
        alt.addAll(mapToSet(altSonnenhitzeWennSinnvoll,
                substPhrOderReflexivpronomen ->
                        IN_DAT.mit(substPhrOderReflexivpronomen)
                                .mitModAdverbOderAdjektiv("mitten")));

        return alt.build();
    }

    @NonNull
    @CheckReturnValue
    private ImmutableList<EinzelneSubstantivischePhrase>
    altSonnenHitzeWennUnterOffenemHimmelSinnvoll(final AvTime time) {
        final Temperatur temperatur = getTemperatur(time);

        final ImmutableList.Builder<EinzelneSubstantivischePhrase> alt =
                ImmutableList.builder();

        // FIXME Windstärke berücksichtigen
        // FIXME Blitz und Donner berücksichtigen

        if (bewoelkung.isUnauffaellig(time.getTageszeit())) {
            if (temperatur.compareTo(Temperatur.RECHT_HEISS) == 0) {
                alt.add(np(HEISS, SONNENSCHEIN));
            }

            if (temperatur.compareTo(Temperatur.SEHR_HEISS) == 0) {
                alt.add(SONNENHITZE);
                alt.add(np(SENGEND, SONNE));

                if (time.gegenMittag()) {
                    alt.add(MITTAGSSONNE);
                }
            }
        }

        return alt.build();
    }

    @NonNull
    @CheckReturnValue
    ImmutableSet<Praepositionalphrase> altBeiLichtImLicht(final AvTime time,
                                                          final boolean unterOffenemHimmel) {
        return BEWOELKUNG_PRAEP_PHR_DESCRIBER
                .altBeiLichtImLicht(getBewoelkung(), time.getTageszeit(), unterOffenemHimmel);
    }

    @NonNull
    @CheckReturnValue
    ImmutableSet<Praepositionalphrase> altBeiTageslichtImLicht(final AvTime time,
                                                               final boolean unterOffenemHimmel) {
        return BEWOELKUNG_PRAEP_PHR_DESCRIBER
                .altBeiTageslichtImLicht(getBewoelkung(), time.getTageszeit(), unterOffenemHimmel);
    }

    @NonNull
    @CheckReturnValue
    ImmutableCollection<EinzelneSubstantivischePhrase> altLichtInDemEtwasLiegt(
            final AvTime time,
            final boolean unterOffenemHimmel) {
        return BEWOELKUNG_PRAEDIKATIVUM_DESCRIBER
                .altLichtInDemEtwasLiegt(getBewoelkung(), time.getTageszeit(), unterOffenemHimmel);
    }

    @NonNull
    @CheckReturnValue
    Temperatur getTemperatur(final AvTime time) {
        return TagestemperaturverlaufUtil
                .calcTemperatur(tageshoechsttemperatur, tagestiefsttemperatur, time);
    }

    // FIXME Wind in Kombination: "Der Himmel ist blau und eine frische Luft weht dir entgegen"
    //  - "Der Himmel ist blau, die Luft mild"

    // FIXME Verknüpfungen bei Änderungen:
    //  "Als ... steht die Sonne schon hoch am Himmel und scheint heiß herunter."
    //  "Sobald die Sonne untergegangen ist, " (Uhrzeit berücksichtigen:
    //  time.kurzNachSonnenuntergang())

    // FIXME Sonne:
    //  "mit Sonnenaufgang (machts du dich auch den Weg...)"
    //  "Als aber die ersten Sonnenstrahlen in den Garten fallen, so..."
    //  "und als du erwachst und wieder zu dir selber kommst, bist
    //   du auf einer schönen Wiese, wo die Sonne scheint"
    //  "als du siehst, wie die Sonnenstrahlen durch die Bäume hin- und hertanzen"
    //  "du liegst in der Sonne ausgestreckt"
    //  "Als aber die Sonne bald untergehen will, "
    //  "Bei Sonnenaufgang kommt schon..."
    //  "Bei Sonnenuntergang kommst du zu..."
    //  "Du kommst in den Wald, und da es darin kühl und lieblich ist und die Sonne heiß
    //  brennt, so..."
    //  "Die Sonne geht auf, und ..."
    //  "Du ... noch immer, als es schon hoher Tag ist"
    //  "Noch halb steht die Sonne über (dem Berg) und halb ist sie unter."
    //  "Nun ist die Sonne unter:"
    //  "Als nun die Sonne durchs Fensterlein scheint und..."
    //  "Wie du nun (dies und jenes tust) und zu Mittag die Sonne heiß brennt, wird dir so
    //  warm und verdrießlich zumut:"
    //  "Als / wie nun die Sonne über dir steht, "
    //  "Wie nun die Sonne kommt und du aufwachst..."
    //  "durch die dichtbelaubten Äste dringt kein Sonnenstrahl"
    //  "Als die Sonne untergeht..."
    //  "Es dauert nicht lange, so siehst du die Sonne (hinter den Bergen) aufsteigen"
    //  "du bist von der Sonnenhitze müde"
    //  "(Schwerter) blitzen in der Sonne"
    //  du legst dich "in die Sonne"
    //  "Die Sonne hat die Erde aufgetaut"
    //  "Die Abendsonne scheint über (die
    //  glänzenden Steine), sie schimmerten und leuchteten so prächtig
    //  in allen Farben, daß..."
    //  "aber was tust du die Augen auf, als du aus (der Finsternis)
    //   heraus in das Tageslicht kommst, und den grünen Wald,
    //   Blumen und Vögel und die Morgensonne am Himmel erblickst"
    //  "Als nun die Sonne mitten über dem Walde steht..."
    //  ", bis die Sonne sinkt und die Nacht einbricht."
    //  "Als du aber am Morgen bei hellem Sonnenschein aufwachst, "
    //  "die Sonne ist hinter (den Bergen) verschwunden"
    //  "mittendurch rauscht ein klarer Bach, auf dem die Sonne glitzert"
    //  "es bricht eben der erste Sonnenstrahl hervor"
    //  "gegen Abend, als die Sonne (hinter die Berge) gesunken ist"
    //  "Du erwachst vor Sonnenuntergang"
    //  "Die Sonne will eben untergehen, als du erwachst"
    //  "Du (bleibst unter der Linde sitzen), bis die Sonne untergeht"
    //  "Als die Sonne aufgeht, ..."
    //  "in dem Augenblick dringt der erste Strahl der aufgehenden Sonne am Himmel herauf"
    //  "der Mond lässt sein Licht über alle Felder leuchten"
    //  "Sobald die Sonne wieder warm scheint, gehst du..."

    // FIXME Nacht:
    //  "Bei einbrechender Nacht"
    //  "Der Mond scheint über..."

    // FIXME Wind / Sturm
    //  "ein kühles Lüftchen streicht durch das Laub"
    //  Der Wind wird stärker
    //  Der Wind pfeift dir ums Gesicht
    //  In der Ferne hörst du Donnergrollen
    //  Hat es eben geblitzt?
    //  "der Regen schlägt dir ins Gesicht und der Wind zaust dein Haar"
    //  Ein Sturm zieht auf
    //  Hoffentlich bleibt es wenigstens trocken
    //  (Kein Regen - keine nassen Klamotten o.Ä.)
    //  "Die Äste biegen sich"
    //  "das Gezweig"
    //  "es kommt ein starker Wind"
    //  "es weht beständig ein harter Wind"
    //  "der Wind raschelt in den Bäumen, und die Wolken ziehen ganz nah über deinem Haupt weg"
    //  "der Wind saust"
    //  Der Wind ist jetzt sehr kräftig und angenehm. Kalt ist es geworden.
    //  Der Sturm biegt die Bäume.
    //  "darin bist du vor Wind und Wetter geschützt"
    //  "Um Mitternacht geht der Wind so kalt, dass dir nicht warm werden will"
    //  "Die Hitze wird drückender, je näher der Mittag kommt" (KEIN WIND)
    //  "Sturm"
    //  "es stürmt", "du findest darin Schutz"
    //  "der Wind rauscht draußen in den Bäumen"
    //  "Weil aber das Wetter so schlecht geworden, und Wind und Regen stürmte,
    //   kannst du nicht weiter und kehrst [...] ein."
    //  Ein ziemlicher Krach (Hexe geht nicht mehr spazieren. Schlossfest?!)
    //  Der Sturm peitscht die Äste über dir und es ist ziemlich dunkel. Ein geschützter Platz
    //  wäre schön.
    //  Langsam scheint sich das Wetter wieder zu bessern / der Sturm flaut allmählich ab.
    //  "Der Wind legt sich, und auf den Bäumen vor [...] regt sich kein Blättchen mehr"
    //  "Es geht kein Wind, und bewegt sich kein Blättchen"
    //  "Kein Wind weht"

    // FIXME Aufwärmen
    //  "du bist halb erfroren und willst dich nur ein wenig wärmen"
    //  "du reibst die Hände"
    //  "du bist so erfroren"
    //  "dich wärmen"
    //  "du erwärmst dich"
    //  Geschlossenheit?
    //   Drinnen umbeheizt
    //   Draußen geschuetzt
    //   Max temoeratur...

    // FIXME
    //  Fürs Wetter lässt sich wohl einiges von Hunger oder Müdigkeit übernehmen.

    // FIXME
    //  Man braucht regelmäßige Hinweise (je nach Dramatik des Wettes).

    // FIXME Plan-Wetter nur dramaturgisch geändert, nicht automatisch? Oder
    //  zwei Plan-Wetter, dramaturgisch und automatisch? Oder Plan-Wetter-Priorität?!

    // FIXME Wetter beeinflusst Stimmung von SC, Rapunzel, Zauberin (Listener-Konzept!)
    //  "von der Hitze des Tages ermüdet"
    //  "du bist von der Sonnenhitze müde"

    @SuppressWarnings("WeakerAccess")
    @NonNull
    @CheckReturnValue
    Bewoelkung getBewoelkung() {
        return bewoelkung;
    }

    @NonNull
    @CheckReturnValue
    BlitzUndDonner getBlitzUndDonner() {
        return blitzUndDonner;
    }

    @NonNull
    @CheckReturnValue
    Temperatur getTageshoechsttemperatur() {
        return tageshoechsttemperatur;
    }

    @NonNull
    @CheckReturnValue
    Temperatur getTagestiefsttemperatur() {
        return tagestiefsttemperatur;
    }

    @NonNull
    @CheckReturnValue
    Windstaerke getWindstaerke() {
        return windstaerke;
    }

    // FIXME Silbentrennung macht Fehler - Silbentrennung auf seltener einstellen?
    //  Oder anders korrigieren?

}
