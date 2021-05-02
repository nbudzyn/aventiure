package de.nb.aventiure2.data.world.syscomp.wetter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

import java.util.Objects;

import javax.annotation.CheckReturnValue;
import javax.annotation.concurrent.Immutable;

import de.nb.aventiure2.data.time.AvDateTime;
import de.nb.aventiure2.data.time.AvTime;
import de.nb.aventiure2.data.time.AvTimeSpan;
import de.nb.aventiure2.data.time.Tageszeit;
import de.nb.aventiure2.data.world.base.EnumRange;
import de.nb.aventiure2.data.world.base.Temperatur;
import de.nb.aventiure2.data.world.syscomp.storingplace.DrinnenDraussen;
import de.nb.aventiure2.data.world.syscomp.wetter.base.WetterParamChange;
import de.nb.aventiure2.data.world.syscomp.wetter.bewoelkung.Bewoelkung;
import de.nb.aventiure2.data.world.syscomp.wetter.bewoelkung.BewoelkungAdvAngabeWoDescriber;
import de.nb.aventiure2.data.world.syscomp.wetter.bewoelkung.BewoelkungAdvAngabeWohinDescriber;
import de.nb.aventiure2.data.world.syscomp.wetter.bewoelkung.BewoelkungDescDescriber;
import de.nb.aventiure2.data.world.syscomp.wetter.bewoelkung.BewoelkungPraedikativumDescriber;
import de.nb.aventiure2.data.world.syscomp.wetter.bewoelkung.BewoelkungPraepPhrDescriber;
import de.nb.aventiure2.data.world.syscomp.wetter.bewoelkung.BewoelkungSatzDescriber;
import de.nb.aventiure2.data.world.syscomp.wetter.blitzunddonner.BlitzUndDonner;
import de.nb.aventiure2.data.world.syscomp.wetter.tageszeit.TageszeitAdvAngabeWoDescriber;
import de.nb.aventiure2.data.world.syscomp.wetter.tageszeit.TageszeitAdvAngabeWohinDescriber;
import de.nb.aventiure2.data.world.syscomp.wetter.tageszeit.TageszeitDescDescriber;
import de.nb.aventiure2.data.world.syscomp.wetter.tageszeit.TageszeitPraedikativumDescriber;
import de.nb.aventiure2.data.world.syscomp.wetter.tageszeit.TageszeitPraepPhrDescriber;
import de.nb.aventiure2.data.world.syscomp.wetter.tageszeit.TageszeitSatzDescriber;
import de.nb.aventiure2.data.world.syscomp.wetter.temperatur.TagestemperaturverlaufUtil;
import de.nb.aventiure2.data.world.syscomp.wetter.temperatur.TemperaturAdvAngabeWoDescriber;
import de.nb.aventiure2.data.world.syscomp.wetter.temperatur.TemperaturAdvAngabeWohinDescriber;
import de.nb.aventiure2.data.world.syscomp.wetter.temperatur.TemperaturDescDescriber;
import de.nb.aventiure2.data.world.syscomp.wetter.temperatur.TemperaturPraedikativumDescriber;
import de.nb.aventiure2.data.world.syscomp.wetter.temperatur.TemperaturPraepPhrDescriber;
import de.nb.aventiure2.data.world.syscomp.wetter.temperatur.TemperaturSatzDescriber;
import de.nb.aventiure2.data.world.syscomp.wetter.windstaerke.Windstaerke;
import de.nb.aventiure2.german.base.EinzelneSubstantivischePhrase;
import de.nb.aventiure2.german.base.GermanUtil;
import de.nb.aventiure2.german.base.Praepositionalphrase;
import de.nb.aventiure2.german.description.AbstractDescription;
import de.nb.aventiure2.german.description.AltDescriptionsBuilder;
import de.nb.aventiure2.german.praedikat.AdvAngabeSkopusVerbAllg;
import de.nb.aventiure2.german.praedikat.AdvAngabeSkopusVerbWohinWoher;
import de.nb.aventiure2.german.satz.EinzelnerSatz;
import de.nb.aventiure2.german.satz.Satz;
import de.nb.aventiure2.german.satz.ZweiSaetze;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.collect.ImmutableSet.toImmutableSet;
import static de.nb.aventiure2.data.time.Tageszeit.ABENDS;
import static de.nb.aventiure2.data.time.Tageszeit.NACHTS;
import static de.nb.aventiure2.data.world.syscomp.storingplace.DrinnenDraussen.DRAUSSEN_GESCHUETZT;
import static de.nb.aventiure2.data.world.syscomp.storingplace.DrinnenDraussen.DRAUSSEN_UNTER_OFFENEM_HIMMEL;
import static de.nb.aventiure2.german.adjektiv.AdjektivOhneErgaenzungen.HEISS;
import static de.nb.aventiure2.german.adjektiv.AdjektivOhneErgaenzungen.SENGEND;
import static de.nb.aventiure2.german.base.NomenFlexionsspalte.MITTAGSSONNE;
import static de.nb.aventiure2.german.base.NomenFlexionsspalte.SONNE;
import static de.nb.aventiure2.german.base.NomenFlexionsspalte.SONNENHITZE;
import static de.nb.aventiure2.german.base.NomenFlexionsspalte.SONNENSCHEIN;
import static de.nb.aventiure2.german.base.PraepositionMitKasus.IN_AKK;
import static de.nb.aventiure2.german.base.PraepositionMitKasus.IN_DAT;
import static de.nb.aventiure2.german.base.StructuralElement.SENTENCE;
import static de.nb.aventiure2.german.description.AltDescriptionsBuilder.alt;
import static de.nb.aventiure2.german.description.AltDescriptionsBuilder.altNeueSaetze;
import static de.nb.aventiure2.util.StreamUtil.*;
import static java.util.stream.Collectors.toSet;

@Immutable
public
class WetterData {
    // Tageszeit-Describer
    private static final TageszeitPraedikativumDescriber TAGESZEIT_PRAEDIKATIVUM_DESCRIBER =
            new TageszeitPraedikativumDescriber();

    private static final TageszeitSatzDescriber TAGESZEIT_SATZ_DESCRIBER =
            new TageszeitSatzDescriber(TAGESZEIT_PRAEDIKATIVUM_DESCRIBER);

    private static final TageszeitDescDescriber TAGESZEIT_DESC_DESCRIBER =
            new TageszeitDescDescriber(
                    TAGESZEIT_PRAEDIKATIVUM_DESCRIBER,
                    TAGESZEIT_SATZ_DESCRIBER);

    private static final TageszeitPraepPhrDescriber TAGESZEIT_PRAEP_PHR_DESCRIBER =
            new TageszeitPraepPhrDescriber(TAGESZEIT_PRAEDIKATIVUM_DESCRIBER);

    private static final TageszeitAdvAngabeWoDescriber
            TAGESZEIT_ADV_ANGABE_WO_DESCRIBER =
            new TageszeitAdvAngabeWoDescriber(TAGESZEIT_PRAEDIKATIVUM_DESCRIBER,
                    TAGESZEIT_PRAEP_PHR_DESCRIBER);

    private static final TageszeitAdvAngabeWohinDescriber
            TAGESZEIT_ADV_ANGABE_WOHIN_DESCRIBER =
            new TageszeitAdvAngabeWohinDescriber(TAGESZEIT_PRAEDIKATIVUM_DESCRIBER,
                    TAGESZEIT_PRAEP_PHR_DESCRIBER);

    // Temperatur-Describer
    private static final TemperaturPraedikativumDescriber TEMPERATUR_PRAEDIKATIVUM_DESCRIBER =
            new TemperaturPraedikativumDescriber();

    private static final TemperaturPraepPhrDescriber TEMPERATUR_PRAEP_PHR_DESCRIBER =
            new TemperaturPraepPhrDescriber(TEMPERATUR_PRAEDIKATIVUM_DESCRIBER);

    private static final TemperaturAdvAngabeWoDescriber
            TEMPERATUR_ADV_ANGABE_WO_DESCRIBER =
            new TemperaturAdvAngabeWoDescriber(
                    TEMPERATUR_PRAEP_PHR_DESCRIBER);

    private static final TemperaturAdvAngabeWohinDescriber
            TEMPERATUR_ADV_ANGABE_WOHIN_DESCRIBER =
            new TemperaturAdvAngabeWohinDescriber(
                    TEMPERATUR_PRAEP_PHR_DESCRIBER);

    private static final TemperaturSatzDescriber TEMPERATUR_SATZ_DESCRIBER =
            new TemperaturSatzDescriber(
                    TAGESZEIT_PRAEDIKATIVUM_DESCRIBER,
                    TEMPERATUR_PRAEDIKATIVUM_DESCRIBER);

    private static final TemperaturDescDescriber TEMPERATUR_DESC_DESCRIBER =
            new TemperaturDescDescriber(TAGESZEIT_DESC_DESCRIBER,
                    TEMPERATUR_PRAEDIKATIVUM_DESCRIBER,
                    TEMPERATUR_SATZ_DESCRIBER);

    // Bewölkung-Describer
    private static final BewoelkungPraedikativumDescriber BEWOELKUNG_PRAEDIKATIVUM_DESCRIBER =
            new BewoelkungPraedikativumDescriber();

    private static final BewoelkungPraepPhrDescriber BEWOELKUNG_PRAEP_PHR_DESCRIBER =
            new BewoelkungPraepPhrDescriber(BEWOELKUNG_PRAEDIKATIVUM_DESCRIBER);

    private static final BewoelkungAdvAngabeWoDescriber
            BEWOELKUNG_ADV_ANGABE_WO_DESCRIBER =
            new BewoelkungAdvAngabeWoDescriber(
                    BEWOELKUNG_PRAEP_PHR_DESCRIBER, BEWOELKUNG_PRAEDIKATIVUM_DESCRIBER);

    private static final BewoelkungAdvAngabeWohinDescriber
            BEWOELKUNG_ADV_ANGABE_WOHIN_DESCRIBER =
            new BewoelkungAdvAngabeWohinDescriber(
                    BEWOELKUNG_PRAEP_PHR_DESCRIBER, BEWOELKUNG_PRAEDIKATIVUM_DESCRIBER);

    private static final BewoelkungSatzDescriber BEWOELKUNG_SATZ_DESCRIBER =
            new BewoelkungSatzDescriber(BEWOELKUNG_PRAEDIKATIVUM_DESCRIBER);

    private static final BewoelkungDescDescriber BEWOELKUNG_DESC_DESCRIBER =
            new BewoelkungDescDescriber(BEWOELKUNG_SATZ_DESCRIBER);

    // Wetterparameter etc.
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

    @NonNull
    public static WetterData getDefault() {
        return new WetterData(
                Temperatur.WARM, Temperatur.KUEHL,
                Windstaerke.LUEFTCHEN,
                Bewoelkung.LEICHT_BEWOELKT,
                BlitzUndDonner.KEIN_BLITZ_ODER_DONNER);
    }

    /**
     * Gibt alternative Beschreibungen des "Wetters" zurück, wie man es drinnen
     * (nur Temperatur) oder draußen erlebt - oder eine leere Menge.
     *
     * @param auchEinmaligeErlebnisseDraussenNachTageszeitenwechselBeschreiben Ob auch Erlebnisse
     *                                                                         draußen beschrieben
     *                                                                         werden sollen, die
     *                                                                         nach einem
     *                                                                         Tageszeitenwechsel
     *                                                                         nur einmalig
     *                                                                         auftreten
     */
    ImmutableCollection<AbstractDescription<?>> altWetterhinweise(
            final AvTime time, final DrinnenDraussen drinnenDraussen,
            final EnumRange<Temperatur> locationTemperaturRange,
            final boolean auchEinmaligeErlebnisseDraussenNachTageszeitenwechselBeschreiben) {
        if (drinnenDraussen.isDraussen()) {
            return altWetterHinweiseFuerDraussen(time,
                    drinnenDraussen == DRAUSSEN_UNTER_OFFENEM_HIMMEL,
                    locationTemperaturRange,
                    auchEinmaligeErlebnisseDraussenNachTageszeitenwechselBeschreiben);
        }

        // drinnen!
        return TEMPERATUR_DESC_DESCRIBER.alt(getLokaleTemperatur(time, locationTemperaturRange),
                !locationTemperaturRange.isInRange(getAktuelleGenerelleTemperatur(time)),
                time, drinnenDraussen,
                auchEinmaligeErlebnisseDraussenNachTageszeitenwechselBeschreiben);
    }

    /**
     * Gibt alternative Beschreibungen des Wetters zurück, wie man es draußen erlebt -
     * oder eine leere Menge.
     *
     * @param auchEinmaligeErlebnisseNachTageszeitenwechselBeschreiben Ob auch Erlebnisse
     *                                                                 beschrieben werden sollen,
     *                                                                 die nach einem
     *                                                                 Tageszeitenwechsel nur
     *                                                                 einmalig auftreten
     */
    private ImmutableCollection<AbstractDescription<?>> altWetterHinweiseFuerDraussen(
            final AvTime time, final boolean unterOffenemHimmel,
            final EnumRange<Temperatur> locationTemperaturRange,
            final boolean auchEinmaligeErlebnisseNachTageszeitenwechselBeschreiben) {
        // FIXME Windstärke berücksichtigen
        // FIXME Blitz und Donner berücksichtigen

        // IDEA:
        //  boolean tageszeitUndLichtverhaeltnisseRelevant = ;
        //  boolean temperaturRelevant = ;
        //  boolean bewoelkungRelevant = ;
        //  if (nurTemperaturUndEvtlTageszeitRelevant()) {....}
        //  if (nurTemperaturUndBewoelkungUndEvtlTageszeitRelevant() {...}

        final AltDescriptionsBuilder alt = alt();

        final DrinnenDraussen drinnenDraussen =
                unterOffenemHimmel ? DRAUSSEN_UNTER_OFFENEM_HIMMEL : DRAUSSEN_GESCHUETZT;

        final Temperatur temperatur = getLokaleTemperatur(time, locationTemperaturRange);

        if (tageszeitUndLichtverhaeltnisseGenuegen(time, unterOffenemHimmel,
                temperatur)) {
            // "es ist schon dunkel", "es ist Abend"
            alt.addAll(TAGESZEIT_DESC_DESCRIBER.altDraussen(time,
                    auchEinmaligeErlebnisseNachTageszeitenwechselBeschreiben));
        }

        if (temperaturUndEvtlTageszeitUndLichtverhaeltnisseGenuegen(time, unterOffenemHimmel)) {
            // Bewölkung muss nicht erwähnt werden

            // "Es ist kühl"
            alt.addAll(TEMPERATUR_DESC_DESCRIBER.alt(temperatur,
                    !locationTemperaturRange.isInRange(getAktuelleGenerelleTemperatur(time)),
                    time, drinnenDraussen,
                    auchEinmaligeErlebnisseNachTageszeitenwechselBeschreiben));

            if (bewoelkung.isUnauffaellig(time.getTageszeit())) {
                // "Es ist ein schöner Abend und noch ziemlich warm"
                alt.addAll(
                        TEMPERATUR_SATZ_DESCRIBER
                                .altSchoneTageszeitUndAberSchonNochAdjPhr(
                                        temperatur, time.getTageszeit()));
            }
        }

        if (bewoelkungUndEvtlTageszeitUndLichtverhaeltnisseGenuegen(time, unterOffenemHimmel,
                temperatur)) {
            if (unterOffenemHimmel && temperatur.isUnauffaellig(time.getTageszeit())) {
                // Temperatur muss nicht erwähnt werden

                alt.addAll(BEWOELKUNG_DESC_DESCRIBER.altUnterOffenemHimmel(bewoelkung, time,
                        auchEinmaligeErlebnisseNachTageszeitenwechselBeschreiben));
            }

            if (temperatur == Temperatur.WARM && time.getTageszeit() == ABENDS) {
                // Temperatur muss nicht erwähnt werden

                if (bewoelkung.isUnauffaellig(time.getTageszeit())) {
                    // "Es ist ein schöner Abend, die Sonne scheint"
                    alt.addAll(BEWOELKUNG_DESC_DESCRIBER.altSchoeneTageszeit(
                            bewoelkung, time, unterOffenemHimmel,
                            auchEinmaligeErlebnisseNachTageszeitenwechselBeschreiben));
                }
            }
        }

        if (unterOffenemHimmel) {
            // Temperatur und Bewölkung werden beide erwähnt
            alt.addAll(altStatischBewoelkungUndTemperaturUnterOffenemHimmel(
                    time,
                    locationTemperaturRange,
                    auchEinmaligeErlebnisseNachTageszeitenwechselBeschreiben));
        }

        return alt.schonLaenger().build();
    }


    /**
     * Gibt alternative statische Beschreibungen von Bewölkung <i>und</i> Temperatur zurück, wie
     * man sie unter offenem Himmel erlebt
     *
     * @param auchEinmaligeErlebnisseNachTageszeitenwechselBeschreiben Ob auch Erlebnisse
     *                                                                 beschrieben werden sollen,
     *                                                                 die nach einem
     *                                                                 Tageszeitenwechsel nur
     *                                                                 einmalig auftreten
     */
    private ImmutableCollection<AbstractDescription<?>>
    altStatischBewoelkungUndTemperaturUnterOffenemHimmel(
            final AvTime time,
            final EnumRange<Temperatur> locationTemperaturRange,
            final boolean auchEinmaligeErlebnisseNachTageszeitenwechselBeschreiben) {
        final AltDescriptionsBuilder alt = alt();

        final Temperatur temperatur = getLokaleTemperatur(time, locationTemperaturRange);

        // "Es ist kühl und der Himmel ist bewölkt"
        alt.addAll(altNeueSaetze(
                TEMPERATUR_SATZ_DESCRIBER.alt(
                        temperatur, time, DRAUSSEN_UNTER_OFFENEM_HIMMEL,
                        auchEinmaligeErlebnisseNachTageszeitenwechselBeschreiben,
                        false).stream()
                        // Bandwurmsätze vermeiden - Ergebnis ist nicht leer!
                        .filter(EinzelnerSatz.class::isInstance),
                BEWOELKUNG_SATZ_DESCRIBER
                        .altUnterOffenemHimmel(bewoelkung, time,
                                auchEinmaligeErlebnisseNachTageszeitenwechselBeschreiben).stream()
                        // mehrfaches "und" vermeiden
                        .map(Satz::mitAnschlusswortUndSofernNichtSchonEnthalten)));

        final ImmutableCollection<Satz> heuteOderDerTagSaetze =
                TEMPERATUR_SATZ_DESCRIBER
                        .altDraussenHeuteDerTagSofernSinnvoll(
                                temperatur,
                                !locationTemperaturRange
                                        .isInRange(getAktuelleGenerelleTemperatur(time)),
                                time,
                                false);
        if (!heuteOderDerTagSaetze.isEmpty()) {
            alt.addAll(altNeueSaetze(
                    BEWOELKUNG_SATZ_DESCRIBER.altUnterOffenemHimmel(
                            bewoelkung, time,
                            auchEinmaligeErlebnisseNachTageszeitenwechselBeschreiben),
                    ";",
                    heuteOderDerTagSaetze));
        }

        if (time.getTageszeit() == ABENDS) {
            if (temperatur.isBetweenIncluding(Temperatur.WARM, Temperatur.RECHT_HEISS)) {
                alt.addAll(altNeueSaetze(
                        BEWOELKUNG_SATZ_DESCRIBER
                                .altUnterOffenemHimmel(bewoelkung, time,
                                        auchEinmaligeErlebnisseNachTageszeitenwechselBeschreiben),
                        ";",
                        TEMPERATUR_SATZ_DESCRIBER.altDraussenNoch(temperatur, ABENDS)));
            }
        }

        return alt.schonLaenger().build();
    }

    private static boolean bewoelkungUndEvtlTageszeitUndLichtverhaeltnisseGenuegen(
            final AvTime time,
            final boolean unterOffenemHimmel,
            final Temperatur temperatur) {
        return (unterOffenemHimmel && temperatur.isUnauffaellig(time.getTageszeit()))
                ||
                (temperatur == Temperatur.WARM && time.getTageszeit() == ABENDS);
    }

    private boolean temperaturUndEvtlTageszeitUndLichtverhaeltnisseGenuegen(
            final AvTime time, final boolean unterOffenemHimmel) {
        return !unterOffenemHimmel || bewoelkung.isUnauffaellig(time.getTageszeit());
    }

    private boolean tageszeitUndLichtverhaeltnisseGenuegen(final AvTime time,
                                                           final boolean unterOffenemHimmel,
                                                           final Temperatur temperatur) {
        return time.getTageszeit() == NACHTS
                && (!unterOffenemHimmel || bewoelkung.isUnauffaellig(time.getTageszeit()))
                && temperatur.isUnauffaellig(NACHTS);
    }

    /**
     * Gibt alternative Beschreibungen des Wetters zurück, wie
     * man sie erlebt, wenn man nach draußen kommt, - ggf. eine leere Menge.
     *
     * @param auchEinmaligeErlebnisseNachTageszeitenwechselBeschreiben Ob auch Erlebnisse
     *                                                                 beschrieben werden sollen,
     *                                                                 die nach einem
     *                                                                 Tageszeitenwechsel nur
     *                                                                 einmalig auftreten
     */
    @CheckReturnValue
    AltDescriptionsBuilder altKommtNachDraussen(
            final AvTime time, final boolean unterOffenenHimmel,
            final EnumRange<Temperatur> locationTemperaturRange,
            final boolean auchEinmaligeErlebnisseNachTageszeitenwechselBeschreiben) {
        // FIXME Windstärke berücksichtigen
        // FIXME Blitz und Donner berücksichtigen

        // IDEA:
        //  boolean tageszeitUndLichtverhaeltnisseRelevant = ;
        //  boolean temperaturRelevant = ;
        //  boolean bewoelkungRelevant = ;
        //  if (nurTemperaturUndEvtlTageszeitRelevant()) {....}
        //  if (nurTemperaturUndBewoelkungUndEvtlTageszeitRelevant() {...}

        final AltDescriptionsBuilder alt = alt();

        final Temperatur temperatur = getLokaleTemperatur(time, locationTemperaturRange);

        if (tageszeitUndLichtverhaeltnisseGenuegen(time, unterOffenenHimmel,
                temperatur)) {
            // "draußen ist es schon dunkel"
            alt.addAll(
                    TAGESZEIT_DESC_DESCRIBER.altKommtNachDraussen(time,
                            auchEinmaligeErlebnisseNachTageszeitenwechselBeschreiben));
        }

        if (temperaturUndEvtlTageszeitUndLichtverhaeltnisseGenuegen(time, unterOffenenHimmel)) {
            // Bewölkung muss nicht erwähnt werden

            // "Draußen ist es kühl"
            alt.addAll(TEMPERATUR_DESC_DESCRIBER.altKommtNachDraussen(
                    temperatur,
                    !locationTemperaturRange.isInRange(getAktuelleGenerelleTemperatur(time)),
                    time, unterOffenenHimmel,
                    auchEinmaligeErlebnisseNachTageszeitenwechselBeschreiben));

            if (bewoelkung.isUnauffaellig(time.getTageszeit())) {
                // "Es ist ein schöner Abend und noch ziemlich warm"
                alt.addAll(
                        TEMPERATUR_SATZ_DESCRIBER
                                .altSchoneTageszeitUndAberSchonNochAdjPhr(
                                        temperatur, time.getTageszeit()));
            }
        }

        if (bewoelkungUndEvtlTageszeitUndLichtverhaeltnisseGenuegen(time, unterOffenenHimmel,
                temperatur)) {
            if (unterOffenenHimmel && temperatur.isUnauffaellig(time.getTageszeit())) {
                // Temperatur muss nicht erwähnt werden

                // "Draußen ist der Himmel bewölkt"
                alt.addAll(BEWOELKUNG_DESC_DESCRIBER
                        .altKommtUnterOffenenHimmel(bewoelkung, time, true,
                                auchEinmaligeErlebnisseNachTageszeitenwechselBeschreiben));
            }

            if (temperatur == Temperatur.WARM && time.getTageszeit() == ABENDS) {
                // Temperatur muss nicht erwähnt werden

                if (bewoelkung.isUnauffaellig(time.getTageszeit())) {
                    // "Es ist ein schöner Abend, die Sonne scheint"
                    alt.addAll(BEWOELKUNG_DESC_DESCRIBER.altSchoeneTageszeit(
                            bewoelkung, time, unterOffenenHimmel,
                            auchEinmaligeErlebnisseNachTageszeitenwechselBeschreiben));
                }
            }
        }

        if (unterOffenenHimmel) {
            // "Draußen ist es kühl und der Himmel ist bewölkt"
            alt.addAll(altKommtUnterOffenenHimmelMitBewoelkungUndTemperatur(
                    time, locationTemperaturRange,
                    auchEinmaligeErlebnisseNachTageszeitenwechselBeschreiben));
        }

        return alt.schonLaenger();
    }

    /**
     * Gibt alternative Beschreibungen von Bewölkung und Temperatur zurück, wenn der SC unter
     * offenen Himmel gekommen ist
     *
     * @param auchEinmaligeErlebnisseNachTageszeitenwechselBeschreiben Ob auch
     *                                                                 Erlebnisse
     *                                                                 beschrieben werden
     *                                                                 sollen, die nur
     *                                                                 einmalig auftreten
     */
    private AltDescriptionsBuilder altKommtUnterOffenenHimmelMitBewoelkungUndTemperatur(
            final AvTime time,
            final EnumRange<Temperatur> locationTemperaturRange,
            final boolean auchEinmaligeErlebnisseNachTageszeitenwechselBeschreiben) {
        final AltDescriptionsBuilder alt = alt();

        final Temperatur temperatur = getLokaleTemperatur(time, locationTemperaturRange);

        alt.addAll(altNeueSaetze(
                TEMPERATUR_SATZ_DESCRIBER.altKommtNachDraussen(temperatur, time,
                        true
                        , auchEinmaligeErlebnisseNachTageszeitenwechselBeschreiben).stream()
                        // Bandwurmsätze vermeiden - Ergebnis ist nicht leer!
                        .filter(EinzelnerSatz.class::isInstance),
                BEWOELKUNG_SATZ_DESCRIBER
                        .altUnterOffenemHimmel(bewoelkung, time,
                                auchEinmaligeErlebnisseNachTageszeitenwechselBeschreiben)
                        .stream()
                        .map(Satz::mitAnschlusswortUndSofernNichtSchonEnthalten)));

        final ImmutableCollection<Satz> heuteOderDerTagSaetze = TEMPERATUR_SATZ_DESCRIBER
                .altDraussenHeuteDerTagSofernSinnvoll(
                        temperatur,
                        !locationTemperaturRange.isInRange(getAktuelleGenerelleTemperatur(time)),
                        time,
                        true);
        if (!heuteOderDerTagSaetze.isEmpty()) {
            alt.addAll(altNeueSaetze(
                    BEWOELKUNG_SATZ_DESCRIBER
                            .altKommtUnterOffenenHimmel(bewoelkung, time, true,
                                    auchEinmaligeErlebnisseNachTageszeitenwechselBeschreiben),
                    ";",
                    heuteOderDerTagSaetze));
        }

        if (time.getTageszeit() == ABENDS) {
            if (temperatur.isBetweenIncluding(Temperatur.WARM, Temperatur.RECHT_HEISS)) {
                alt.addAll(altNeueSaetze(
                        BEWOELKUNG_SATZ_DESCRIBER
                                .altKommtUnterOffenenHimmel(bewoelkung, time, true,
                                        auchEinmaligeErlebnisseNachTageszeitenwechselBeschreiben),
                        ";",
                        TEMPERATUR_SATZ_DESCRIBER.altDraussenNoch(temperatur, ABENDS)));
            }
        }

        return alt.schonLaenger();
    }

    /**
     * Gibt alternative Beschreibungen für einen Tageszeitensprung oder -wechsel zurück -
     * und eventuell auch eine Beschreibung einer Temperaturänderung.
     *
     * @param currentLokaleTemperaturBeiRelevanterAenderung Falls eine Temperaturänderung
     *                                                      beschrieben werden soll, so steht
     *                                                      hier die lokale Temperatur nach  der
     *                                                      Änderung
     */
    @NonNull
    @CheckReturnValue
    private ImmutableCollection<AbstractDescription<?>> altTageszeitenUndEvtlTemperaturaenderung(
            final Tageszeit lastTageszeit,
            final AvTime currentTime,
            final boolean generelleTemperaturOutsideLocationTemperaturRange,
            @Nullable final Temperatur currentLokaleTemperaturBeiRelevanterAenderung,
            final DrinnenDraussen drinnenDraussen) {
        checkArgument(lastTageszeit != currentTime.getTageszeit(),
                "Keine Tageszeitensprung oder -wechsel: %s", lastTageszeit);

        final AltDescriptionsBuilder alt = alt();

        // "Langsam wird es hell" / "Ein kühlerer Morgen bricht an"
        alt.addAll(TEMPERATUR_DESC_DESCRIBER.altTemperaturUndTageszeitenSprungOderWechsel(
                lastTageszeit, currentTime,
                generelleTemperaturOutsideLocationTemperaturRange,
                currentLokaleTemperaturBeiRelevanterAenderung,
                drinnenDraussen));

        if (drinnenDraussen.isDraussen()) {
            if (currentLokaleTemperaturBeiRelevanterAenderung != null) {
                alt.addAll(altTageszeitenUndTemperaturaenderungMitBewoelkungDraussen(
                        lastTageszeit, currentTime,
                        generelleTemperaturOutsideLocationTemperaturRange,
                        currentLokaleTemperaturBeiRelevanterAenderung,
                        drinnenDraussen));
            } else {
                // "Die Sterne verblassen und die Sonne ist am Horizont zu sehen"
                alt.addAll(BEWOELKUNG_DESC_DESCRIBER.altTageszeitensprungOderWechsel(
                        getBewoelkung(),
                        lastTageszeit, currentTime.getTageszeit(),
                        drinnenDraussen == DRAUSSEN_UNTER_OFFENEM_HIMMEL));
            }

            // FIXME WENN lastTageszeit == TAGSUEBER && currentTageszeit == ABENDS:
            //  "Heute ist ein schönes
            //  Abendrot zu sehen" - das sollte - auch - vom Planwetter abhängen:
            //  - Es muss LEICHT_BEWOELKT bis BEWOELKT sein
            //  - Die Bewölkung soll laut Planwetter abnehmen, mindestens auf
            //    LEICHT_BEWOELKT
            //  „Du trittst aus dem Wald hinaus. Purpurnes Abendrot erstreckt sich über den
            //  Horizont....“
        }

        return alt.schonLaenger().build();
    }


    /**
     * Gibt alternative Beschreibungen für draußen für eine gleichzeitige Änderung von Temperatur
     * und Tageszeit zurück und beschreibt dabei auch die Bewölkung.
     *
     * @param currentLokaleTemperatur Die lokale Temperatur nach  der Änderung
     *                                Änderung
     */
    @NonNull
    @CheckReturnValue
    private ImmutableCollection<AbstractDescription<?>>
    altTageszeitenUndTemperaturaenderungMitBewoelkungDraussen(
            final Tageszeit lastTageszeit,
            final AvTime currentTime,
            final boolean generelleTemperaturOutsideLocationTemperaturRange,
            final Temperatur currentLokaleTemperatur,
            final DrinnenDraussen drinnenDraussen) {
        checkArgument(lastTageszeit != currentTime.getTageszeit(),
                "Kein Tageszeitensprung oder -wechsel: %s", lastTageszeit);

        final AltDescriptionsBuilder alt = alt();

        alt.addAll(altNeueSaetze(
                BEWOELKUNG_DESC_DESCRIBER.altTageszeitensprungOderWechsel(
                        bewoelkung,
                        lastTageszeit, currentTime.getTageszeit(),
                        drinnenDraussen == DRAUSSEN_UNTER_OFFENEM_HIMMEL),
                SENTENCE,
                TEMPERATUR_DESC_DESCRIBER.alt(currentLokaleTemperatur,
                        generelleTemperaturOutsideLocationTemperaturRange,
                        currentTime, drinnenDraussen,
                        true)));

        return alt.schonLaenger().build();
    }

    // FIXME Bei extremen / dramtischen Wetterlagen regelmäßig "Erinnerungen", ähnlich dem
    //  Müdigkeits-Konzept?

    /**
     * Gibt alternative {@link AbstractDescription}s zurück, die sich auf "heute", "den Tag"
     * o.Ä. beziehen - soweit draußen sinnvoll, sonst eine leere Collection.
     */
    @NonNull
    @CheckReturnValue
    ImmutableCollection<AbstractDescription<?>>
    altHeuteDerTagWennDraussenSinnvoll(
            final AvTime time,
            final boolean unterOffenemHimmel,
            final EnumRange<Temperatur> locationTemperaturRange) {
        // FIXME Windstärke berücksichtigen
        // FIXME Blitz und Donner berücksichtigen

        if (unterOffenemHimmel && !bewoelkung.isUnauffaellig(time.getTageszeit())) {
            // Die Bewölkung soll nicht unterschlagen werden.
            return ImmutableSet.of();
        }

        final Temperatur temperatur = getLokaleTemperatur(time, locationTemperaturRange);

        // Bewoelkung muss nicht erwähnt werden
        return TEMPERATUR_DESC_DESCRIBER.altHeuteDerTagWennDraussenSinnvoll(
                temperatur,
                !locationTemperaturRange.isInRange(getAktuelleGenerelleTemperatur(time)),
                time, unterOffenemHimmel);
    }

    @NonNull
    @CheckReturnValue
    ImmutableCollection<AbstractDescription<?>>
    altTemperaturUnterschiedZuVorLocation(
            final AvTime time, final EnumRange<Temperatur> locationTemperaturRange,
            final int delta) {
        return alt()
                .addAll(TEMPERATUR_SATZ_DESCRIBER.altDeutlicherUnterschiedZuVorLocation(
                        getLokaleTemperatur(time, locationTemperaturRange),
                        delta))
                .schonLaenger()
                .build();
    }


    @NonNull
    @CheckReturnValue
    ImmutableSet<String> altWetterplauderrede(final AvTime time) {
        final Temperatur temperatur = getAktuelleGenerelleTemperatur(time);

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

        // IDEA "Das ist ein Wetter, als wenn die Welt untergehen sollte!"

        return ImmutableSet.of("Schönes Wetter heut!", "Schönes Wetter heut.");
    }

    @NonNull
    @CheckReturnValue
    ImmutableCollection<AdvAngabeSkopusVerbWohinWoher> altWetterhinweiseWohinHinaus(
            final AvTime time,
            final boolean unterOffenenHimmel,
            final EnumRange<Temperatur> locationTemperaturRange,
            final boolean auchEinmaligeErlebnisseNachTageszeitenwechselBeschreiben) {
        final ImmutableCollection.Builder<AdvAngabeSkopusVerbWohinWoher> alt =
                ImmutableSet.builder();

        // FIXME Windstärke berücksichtigen?
        // FIXME Blitz und Donner berücksichtigen?

        final Temperatur temperatur = getLokaleTemperatur(time, locationTemperaturRange);

        if (tageszeitUndLichtverhaeltnisseGenuegen(time, unterOffenenHimmel,
                temperatur)) {
            alt.addAll(TAGESZEIT_ADV_ANGABE_WOHIN_DESCRIBER.altWohinHinaus(
                    time, auchEinmaligeErlebnisseNachTageszeitenwechselBeschreiben));
        }

        if (temperaturUndEvtlTageszeitUndLichtverhaeltnisseGenuegen(time, unterOffenenHimmel)) {
            alt.addAll(TEMPERATUR_ADV_ANGABE_WOHIN_DESCRIBER.altWohinHinaus(temperatur, time));
        }

        if (bewoelkungUndEvtlTageszeitUndLichtverhaeltnisseGenuegen(time, unterOffenenHimmel,
                temperatur)) {
            if (unterOffenenHimmel && temperatur.isUnauffaellig(time.getTageszeit())) {
                // Temperatur muss nicht erwähnt werden
                alt.addAll(BEWOELKUNG_ADV_ANGABE_WOHIN_DESCRIBER
                        .altHinausUnterOffenenHimmel(bewoelkung, time,
                                auchEinmaligeErlebnisseNachTageszeitenwechselBeschreiben));
            }

            if (temperatur == Temperatur.WARM && time.getTageszeit() == ABENDS) {
                // Temperatur muss nicht erwähnt werden
                if (bewoelkung.isUnauffaellig(time.getTageszeit())) {
                    alt.add(BEWOELKUNG_ADV_ANGABE_WOHIN_DESCRIBER.schoeneTageszeit(
                            time.getTageszeit()));
                }
            }
        }

        if (unterOffenenHimmel) {
            // Temperatur und Bewölkung werden beide erwähnt
            alt.addAll(altWohinInBewoelkungUndTemperaturHinausUnterOffenenHimmel(
                    time, locationTemperaturRange));
        }

        return alt.build();
    }

    @NonNull
    @CheckReturnValue
    private ImmutableCollection<AdvAngabeSkopusVerbWohinWoher>
    altWohinInBewoelkungUndTemperaturHinausUnterOffenenHimmel(
            final AvTime time,
            final EnumRange<Temperatur> locationTemperaturRange) {
        final ImmutableSet.Builder<AdvAngabeSkopusVerbWohinWoher> alt = ImmutableSet.builder();

        final Temperatur temperatur = getLokaleTemperatur(time, locationTemperaturRange);

        alt.addAll(mapToSet(altBewoelkungUndTemperaturNominalphrasenUnterOffenemHimmel(
                time, locationTemperaturRange),
                np -> new AdvAngabeSkopusVerbWohinWoher(IN_AKK.mit(np))));

        alt.addAll(TEMPERATUR_PRAEP_PHR_DESCRIBER.altWohinHinaus(temperatur, time).stream()
                .flatMap(inDieKaelte ->
                        BEWOELKUNG_PRAEP_PHR_DESCRIBER.altUnterOffenenHimmelAkk(
                                bewoelkung, time.getTageszeit()).stream()
                                .filter(unterHimmel -> !unterHimmel.getDescription()
                                        .kommaStehtAus())
                                .map(unterHimmel ->
                                        // "in die Kälte unter den bewölkten Himmel"
                                        new AdvAngabeSkopusVerbWohinWoher(
                                                GermanUtil.joinToString(
                                                        inDieKaelte.getDescription(),
                                                        unterHimmel.getDescription()))))
                .collect(toSet()));

        return alt.build();
    }

    private ImmutableSet<EinzelneSubstantivischePhrase>
    altBewoelkungUndTemperaturNominalphrasenUnterOffenemHimmel(
            final AvTime time, final EnumRange<Temperatur> locationTemperaturRange) {
        final ImmutableSet.Builder<EinzelneSubstantivischePhrase> alt = ImmutableSet.builder();

        final Temperatur temperatur = getLokaleTemperatur(time, locationTemperaturRange);

        if (bewoelkung.isUnauffaellig(time.getTageszeit())) {
            if (temperatur.compareTo(Temperatur.RECHT_HEISS) == 0) {
                alt.add(SONNENSCHEIN.mit(HEISS));
            }

            if (temperatur.compareTo(Temperatur.SEHR_HEISS) == 0) {
                alt.add(SONNENHITZE);
                alt.add(SONNE.mit(SENGEND));

                if (time.gegenMittag()) {
                    alt.add(MITTAGSSONNE);
                }
            }
        }

        return alt.build();
    }

    @NonNull
    @CheckReturnValue
    ImmutableSet<AdvAngabeSkopusVerbAllg> altWetterhinweisWoDraussen(
            final AvTime time,
            final boolean unterOffenemHimmel,
            final EnumRange<Temperatur> locationTemperaturRange,
            final boolean auchEinmaligeErlebnisseNachTageszeitenwechselBeschreiben) {
        final ImmutableSet.Builder<AdvAngabeSkopusVerbAllg> alt = ImmutableSet.builder();

        // FIXME Windstärke berücksichtigen?
        // FIXME Blitz und Donner berücksichtigen?

        final Temperatur temperatur = getLokaleTemperatur(time, locationTemperaturRange);

        if (tageszeitUndLichtverhaeltnisseGenuegen(time, unterOffenemHimmel,
                temperatur)) {
            alt.addAll(TAGESZEIT_ADV_ANGABE_WO_DESCRIBER.altWoDraussen(
                    time, auchEinmaligeErlebnisseNachTageszeitenwechselBeschreiben));
        }

        if (temperaturUndEvtlTageszeitUndLichtverhaeltnisseGenuegen(time, unterOffenemHimmel)) {
            alt.addAll(TEMPERATUR_ADV_ANGABE_WO_DESCRIBER.altWoDraussen(temperatur, time));
        }

        if (bewoelkungUndEvtlTageszeitUndLichtverhaeltnisseGenuegen(time, unterOffenemHimmel,
                temperatur)) {
            if (unterOffenemHimmel && temperatur.isUnauffaellig(time.getTageszeit())) {
                // Temperatur muss nicht erwähnt werden
                alt.addAll(BEWOELKUNG_ADV_ANGABE_WO_DESCRIBER
                        .altUnterOffenemHimmel(bewoelkung, time,
                                auchEinmaligeErlebnisseNachTageszeitenwechselBeschreiben));

            }
        }

        if (unterOffenemHimmel) {
            // Temperatur und Bewölkung werden beide erwähnt
            alt.addAll(altWoInBewoelkungUndTemperaturUnterOffenemHimmel(
                    time, locationTemperaturRange));
        }

        return alt.build();
    }

    private ImmutableCollection<AdvAngabeSkopusVerbAllg>
    altWoInBewoelkungUndTemperaturUnterOffenemHimmel(
            final AvTime time,
            final EnumRange<Temperatur> locationTemperaturRange) {
        final ImmutableSet.Builder<AdvAngabeSkopusVerbAllg> alt = ImmutableSet.builder();

        final Temperatur temperatur = getLokaleTemperatur(time, locationTemperaturRange);

        final ImmutableSet<EinzelneSubstantivischePhrase> altSonnenhitzeWennSinnvoll =
                altBewoelkungUndTemperaturNominalphrasenUnterOffenemHimmel(
                        time, locationTemperaturRange);
        alt.addAll(mapToSet(altSonnenhitzeWennSinnvoll,
                np -> new AdvAngabeSkopusVerbAllg(IN_DAT.mit(np))));
        alt.addAll(mapToSet(altSonnenhitzeWennSinnvoll,
                substPhr -> new AdvAngabeSkopusVerbAllg(
                        IN_DAT.mit(substPhr)
                                .mitModAdverbOderAdjektiv("mitten"))));

        alt.addAll(mapToSet(altBewoelkungUndTemperaturNominalphrasenUnterOffenemHimmel(
                time, locationTemperaturRange),
                np -> new AdvAngabeSkopusVerbAllg(IN_DAT.mit(np))));

        alt.addAll(TEMPERATUR_PRAEP_PHR_DESCRIBER.altWoDraussen(temperatur, time).stream()
                .flatMap(inDerKaelte ->
                        BEWOELKUNG_PRAEP_PHR_DESCRIBER.altUnterOffenemHimmelDat(
                                bewoelkung, time.getTageszeit()).stream()
                                .filter(unterHimmel -> !unterHimmel.getDescription()
                                        .kommaStehtAus())
                                .map(unterHimmel ->
                                        // "in der Kälte unter dem bewölkten Himmel"
                                        new AdvAngabeSkopusVerbAllg(
                                                GermanUtil.joinToString(
                                                        inDerKaelte.getDescription(),
                                                        unterHimmel.getDescription()))))
                .collect(toSet()));

        return alt.build();
    }

    // IDEA: altWann() analog zu altWetterhinweiseWoDraussen(), aber mit Satz-Skopus, und keine
    //  vollwertigen Wetterhinweise! (Flags nicht zurücksetzen!)
    //  - "mit Sonnenaufgang (machts du dich auf den Weg...)"
    //  - "Bei Sonnenaufgang kommt schon..."
    //  - "Bei Sonnenuntergang kommst du zu..."
    //  - "Du erwachst vor Sonnenuntergang"
    //  - "Bei einbrechender Nacht"
    //  - "gegen Mittag"
    //  - "bei Mittagssonnenschein"
    //  - "Als du aber am Morgen bei hellem Sonnenschein aufwachst, " (Problem: "Adverbiale
    //  Angaben" mit Folgekomme unterstützen wir wohl derzeit nicht)
    //  - "gegen Abend, als die Sonne untergegangen ist," (Problem: "Adverbiale Angaben"
    //    mit Folgekomme unterstützen wir wohl derzeit nicht)
    //  - "als heller Mittag ist..." (Komma...)
    //  Jeder "Zeitpunkt" sollt nur einmal verwendet werden - am besten dann, wenn er
    //  überschritten wird.

    /**
     * Gibt {@link Praepositionalphrase}n zurück wie "bei Licht" "bei Tageslicht",
     * "im Morgenlicht" o.Ä. Bewölkung, Temperatur und Tageszeit werden nur ansatzweise
     * beschrieben.
     */
    @NonNull
    @CheckReturnValue
    ImmutableSet<Praepositionalphrase> altBeiLichtImLicht(
            final AvTime time,
            final boolean unterOffenemHimmel) {
        // FIXME Blitze (aber nicht Donner?) berücksichtigen?

        return BEWOELKUNG_PRAEP_PHR_DESCRIBER
                .altBeiLichtImLicht(getBewoelkung(), time.getTageszeit(), unterOffenemHimmel);
    }

    /**
     * Gibt alternativen Beschreibungen des Lichts zurück, in dem etwas liegt
     * ("Morgenlicht" o.Ä.). Bewölkung, Temperatur und Tageszeit werden nur ansatzweise
     * beschrieben.
     */
    @NonNull
    @CheckReturnValue
    ImmutableCollection<EinzelneSubstantivischePhrase> altLichtInDemEtwasLiegt(
            final AvTime time,
            final boolean unterOffenemHimmel) {
        // FIXME Blitze (aber nicht Donner?) berücksichtigen?

        return BEWOELKUNG_PRAEDIKATIVUM_DESCRIBER
                .altLichtInDemEtwasLiegt(getBewoelkung(),
                        time.getTageszeit(),
                        unterOffenemHimmel);
    }

    @NonNull
    @CheckReturnValue
    Temperatur getLokaleTemperatur(final AvTime time,
                                   final EnumRange<Temperatur> locationTemperaturRange) {
        return locationTemperaturRange.clamp(getAktuelleGenerelleTemperatur(time));
    }

    Temperatur getAktuelleGenerelleTemperatur(final AvTime time) {
        return TagestemperaturverlaufUtil
                .calcTemperatur(tageshoechsttemperatur, tagestiefsttemperatur, time);
    }

    // FIXME Automatisch generieren: "Das Wetter ist soundso und es passiert dies und das"
    //  -- "Die Sonne geht auf, und ..."

    // FIXME Automatisch generieren:  "Ein Tageszeitenwechsel ist eingetreten: Es passiert dies
    //  und das"
    //  -- "Nun ist die Sonne unter:"

    // FIXME Automatisch generieren:  "Als (nun / wie nun) das Wetter soundso ist, passiert dies
    //  und das" (an sich nicht besonders kritisch, weil ohnehin nur Sätze derselben SC-Aktion
    //  miteinander verbunden werden):
    //   Automatisch erzeugen, wenn .als() dran steht?! Allerdings wiedersprechen sich
    //   .als() und schonLaenger(), vielleicht genügt also auch ein Fehlen von
    //   .schonLaenger() - dann wäre "du siehst die Sonne aufsteigen"
    //   praktisch eine Alternative zu "(Dann) steigt die Sonne auf" - wobei man das "dann"
    //   nicht schreiben darf, weil der Aktor vorher nicht der Mond ist...!
    //  -- "Als der Mond kommt,..."
    //  -- "Als du die Sonne aufsteigen siehst, ..."
    //  -- "Als aber die Sonne bald untergehen will, "
    //  -- "Als die Sonne aufgeht, ..."
    //  -- "Als die Sonne untergeht..."
    //  -- "Als / wie nun die Sonne über dir steht, "

    // FIXME Automatisch generieren: "Sobald das Wetter soundso ist, passsiert dies und das:"
    //  -- "Sobald die Sonne untergegangen ist, " (Uhrzeit berücksichtigen:
    //     time.kurzNachSonnenuntergang())
    //  -- "Sobald die Sonne wieder warm scheint, gehst du..."

    // FIXME Automatisch generieren: "Als dies und das passiert, ist das Wetter soundso":
    //   "Als ... steht die Sonne schon hoch am Himmel und scheint heiß herunter."

    // FIXME Automatisch generieren: "Das Wetter ist soundso, als etwas passiert":
    //  -- "Die Sonne will eben untergehen, als du erwachst"

    // FIXME Automatisch generieren: "Jemand tut dies und das, bis eine Wetterveränderung eintritt":
    //  -- "..., bis der Mond aufgeht."
    //  -- "..., bis die Sonne sinkt und die Nacht einbricht."
    //  -- "Du (bleibst unter der Linde sitzen), bis die Sonne untergeht"

    // FIXME Automatisch generieren: "Als / wie du nun etwas tust und das Wetter soundso ist,
    //  passiert irgendwas":
    //  -- "Wie du nun (dies und jenes tust) und zu Mittag die Sonne heiß brennt, wird dir so
    //     warm und verdrießlich zumut:"
    //  -- "Wie nun die Sonne kommt und du aufwachst..."

    // IDEA Man könnte Features der Landschaft in die Wetterbeschreibungen einbauen:
    //  - "Du siehst die Sonne hinter den Bergen aufsteigen"
    //  - "Als aber die ersten Sonnenstrahlen in den Garten fallen, so..."
    //  - "als du siehst, wie die Sonnenstrahlen durch die Bäume hin- und hertanzen"
    //  - "Noch halb steht die Sonne über (dem Berg) und halb ist sie unter."
    //  - "durch die dichtbelaubten Äste dringt kein Sonnenstrahl"
    //  - "aber was tust du die Augen auf, als du aus (der Finsternis)
    //    heraus in das Tageslicht kommst, und den grünen Wald,
    //    Blumen und Vögel und die Morgensonne am Himmel erblickst"
    //  - "Als nun die Sonne mitten über dem Walde steht..."
    //  - "die Sonne ist hinter (den Bergen) verschwunden"
    //  - "mittendurch rauscht ein klarer Bach, auf dem die Sonne glitzert"
    //  - "gegen Abend, als die Sonne hinter die Berge gesunken ist"
    //  - "der Mond lässt sein Licht über alle Felder leuchten"
    //  -- "der Wind raschelt in den Bäumen, und die Wolken ziehen ganz nah über deinem Haupt weg"

    // FIXME Man könnte Gegenstände in die Wetterbeschreibung einbauen
    //  "die goldene Kugel blitzt in der Sonne"
    //  "Die Abendsonne scheint über (die
    //  glänzenden Steine), sie schimmeren und leuchten so prächtig
    //  in allen Farben, dass..."

    // IDEA Sonne scheint durchs Fenster hinein
    //  - "Als nun die Sonne durchs Fensterlein scheint und..."

    // FIXME Wetterbeschreibungen als "Ortsangaben"
    //  "du liegst in der Sonne ausgestreckt"

    // FIXME Wetterbeschreibung als "Relativsatz" (im weiteren Sinne):
    //  "und als du erwachst und wieder zu dir selber kommst, bist
    //   du auf einer schönen Wiese, wo die Sonne scheint"

    // FIXME Tageszeitwechsel-Angaben (oder untertageszeitliche Wechel) nach langen oder
    //  mehrfachen Aktionen:
    //  "Du ... noch immer, als es schon hoher Tag ist"

    // FIXME Tageszeitwechsel-Angaben nach Wartezeiten
    //  "Es dauert nicht lange, so siehst du die Sonne (hinter den Bergen) aufsteigen"

    // FIXME Wind / Sturm
    //  "ein kühles Lüftchen streicht durch das Laub"
    //  "Die Sonne scheint hell, die Vögel singen, und ein kühles Lüftchen streichts durch das
    //  Laub,
    //  und du bist voll Freude und Lust."
    //  Der Wind wird stärker
    //  Der Wind pfeift dir ums Gesicht
    //  In der Ferne hörst du Donnergrollen
    //  Hat es eben geblitzt?
    //  "der Regen schlägt dir ins Gesicht und der Wind zaust dein Haar"
    //  "Der Wind stürmt"
    //  "Der Wind treibt Wolkenfetzen über den Sternenhimmel"
    //  "Weiße Wölkchen ziehen am blauen Himmel über dir vorbei"
    //  "Wind und Regen stürmen"
    //  "Das Wetter wird (aber) (so) schlecht, und Wind und Regen stürmen"
    //  "Das Wetter ist (aber) (so) schlecht geworden, und Wind und Regen stürmt"
    //  "Weil aber das Wetter so schlecht geworden ist, und Wind und Regen stürmt, kannst du
    //  nicht weiter"
    //  "...und gehts nach dem Wald zu, dort ein wenig Schutz vor dem Wetter zu suchen"
    //  Ein Sturm zieht auf
    //  Hoffentlich bleibt es wenigstens trocken
    //  (Kein Regen - keine nassen Klamotten o.Ä.)
    //  "Die Äste biegen sich"
    //  "das Gezweig"
    //  "es kommt ein starker Wind"
    //  "Wind und böses Wetter"
    //  "es weht beständig ein harter Wind"
    //  "der Wind (raschelt in den Bäumen), und die Wolken ziehen ganz nah über deinem Haupt weg"
    //  "der Wind saust"
    //  Der Wind ist jetzt sehr kräftig und angenehm. Kalt ist es geworden.
    //  Der Sturm biegt die Bäume.
    //  "so bist du vor der Kälte und dem bösen Wetter geschützt"
    //  "darin bist du vor Wind und Wetter geschützt"
    //  "Um Mitternacht geht der Wind so kalt, dass dir nicht warm werden will"
    //  "Die Hitze wird drückender, je näher der Mittag kommt" (KEIN WIND)
    //  "Sturm"
    //  "es stürmt", "du findest darin Schutz"
    //  "der Wind rauscht draußen in den Bäumen"
    //  "Weil aber das Wetter so schlecht geworden, und Wind und Regen stürmte,
    //   kannst du nicht weiter und kehrst [...] ein."
    //  "Und alsbald fängt der Himmel an zu regnen"
    //  "und weil es anfängt zu regnen"
    //  "Tropfen fallen"
    //  Ein ziemlicher Krach (Hexe geht nicht mehr spazieren. Schlossfest?!)
    //  Der Sturm peitscht die Äste über dir und es ist ziemlich dunkel. Ein geschützter Platz
    //  wäre schön.
    //  Langsam scheint sich das Wetter wieder zu bessern / der Sturm flaut allmählich ab.
    //  "Der Wind legt sich, und auf den Bäumen vor [...] regt sich kein Blättchen mehr"
    //  "Es geht kein Wind, und bewegt sich kein Blättchen"
    //  "Kein Wind weht"

    // FIXME Wind in Kombination: "Der Himmel ist blau und eine frische Luft weht dir entgegen"
    //  - "Der Himmel ist blau, die Luft mild"

    // FIXME Jemand wärmt sich auf (z.B. am Feuer oder Drinnen)
    //  "du bist halb erfroren und willst dich nur ein wenig wärmen"
    //  "du reibst die Hände"
    //  "du bist so erfroren"
    //  "dich wärmen"
    //  "du erwärmst dich" (am Feuer)

    // FIXME "Du kommst in den Wald, und da es darin kühl und lieblich ist
    //  [lokale Temperatur] und die Sonne heiß brennt [generelle Temperatur], so..."

    // FIXME Wetter beeinflusst Stimmung von SC, Rapunzel, Zauberin (Listener-Konzept:
    //  onWetterwechsel()? onTemperaturWechsel()?)
    //  "von der Hitze des Tages ermüdet"
    //  "du bist von der Sonnenhitze müde"
    //  "Wie nun zu Mittag die Sonne heiß brennt, wird dir so warm und verdrießlich zumut"
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

    /**
     * Gibt alternative Beschreibungen zurück für den Fall, dass diese Zeit vergangen ist -
     * zuallermeist leer.
     *
     * @param temperaturChangeSofernRelevant Temperaturänderung, falls eine beschrieben
     *                                       werden soll, sonst {@code null}
     * @param bewoelkungChangeSofernRelevant Änderung der Bewölkung, falls eine beschrieben
     *                                       werden soll, sonst {@code null}
     */
    ImmutableCollection<AbstractDescription<?>> altTimePassed(
            final AvDateTime lastTime,
            final AvDateTime currentTime,
            final boolean tageszeitaenderungSollBeschriebenWerden,
            final boolean generelleTemperaturOutsideLocationTemperaturRange,
            @Nullable final WetterParamChange<Temperatur> temperaturChangeSofernRelevant,
            @Nullable final WetterParamChange<Bewoelkung> bewoelkungChangeSofernRelevant,
            final DrinnenDraussen drinnenDraussen) {
        checkArgument(bewoelkungChangeSofernRelevant == null
                        || drinnenDraussen.isDraussen(),
                "Bewölkungsänderungen werden nur draußen erzählt");

        final boolean temperaturaenderungSollBeschriebenWerden =
                temperaturChangeSofernRelevant != null;

        final boolean bewoelkungsaenderungSollBeschriebenWerden =
                bewoelkungChangeSofernRelevant != null;

        if (!tageszeitaenderungSollBeschriebenWerden) {
            // Es soll keine Tageszeitänderung beschrieben werden
            return altTimePassedKeineTageszeitenaenderung(lastTime, currentTime,
                    temperaturChangeSofernRelevant,
                    bewoelkungChangeSofernRelevant, drinnenDraussen,
                    temperaturaenderungSollBeschriebenWerden,
                    bewoelkungsaenderungSollBeschriebenWerden);
        }

        // Es soll eine Tageszeitänderung beschrieben werden
        return altTimePassedTageszeitenaenderung(lastTime, currentTime,
                generelleTemperaturOutsideLocationTemperaturRange, temperaturChangeSofernRelevant,
                bewoelkungChangeSofernRelevant, drinnenDraussen,
                bewoelkungsaenderungSollBeschriebenWerden);
    }

    private static ImmutableCollection<AbstractDescription<?>>
    altTimePassedKeineTageszeitenaenderung(
            final AvDateTime lastTime, final AvDateTime currentTime,
            @Nullable final WetterParamChange<Temperatur> temperaturChangeSofernRelevant,
            @Nullable final WetterParamChange<Bewoelkung> bewoelkungChangeSofernRelevant,
            final DrinnenDraussen drinnenDraussen,
            final boolean temperaturaenderungSollBeschriebenWerden,
            final boolean bewoelkungsaenderungSollBeschriebenWerden) {
        if (!temperaturaenderungSollBeschriebenWerden
                && !bewoelkungsaenderungSollBeschriebenWerden) {
            // Nichts beschreiben - außer höchtestensuntertägigem Tageszeitenwechsel
            if (!currentTime.minus(lastTime).longerThan(AvTimeSpan.ONE_DAY)
                    && lastTime.getTageszeit() == currentTime.getTageszeit()) {
                return TAGESZEIT_DESC_DESCRIBER.altZwischentageszeitlicherWechsel(
                        lastTime.getTime(), currentTime.getTime(),
                        drinnenDraussen.isDraussen());
            }

            return ImmutableList.of();
        }

        if (temperaturaenderungSollBeschriebenWerden
                && !bewoelkungsaenderungSollBeschriebenWerden) {
            // Nur Temperatur beschreiben
            return TEMPERATUR_DESC_DESCRIBER.altSprungOderWechsel(
                    currentTime, temperaturChangeSofernRelevant, drinnenDraussen);
            // IDEA  "Die Sonne hat die Erde aufgetaut" (bei (aktuell) unauffälliger Bewölkung)
        }

        if (!temperaturaenderungSollBeschriebenWerden
                && bewoelkungsaenderungSollBeschriebenWerden) {
            // Nur Bewölkung beschreiben
            return BEWOELKUNG_DESC_DESCRIBER.altSprungOderWechselDraussen(
                    currentTime, bewoelkungChangeSofernRelevant,
                    drinnenDraussen == DRAUSSEN_UNTER_OFFENEM_HIMMEL);
        }

        // Nur Temperaturänderung und Bewölkungsänderung beschreiben
        return alt()
                .addAll(TEMPERATUR_SATZ_DESCRIBER.altSprungOderWechsel(
                        currentTime, temperaturChangeSofernRelevant, drinnenDraussen)
                        .stream()
                        .filter(EinzelnerSatz.class::isInstance)
                        .flatMap(tempSatz ->
                                BEWOELKUNG_SATZ_DESCRIBER
                                        .altSprungOderWechselUnterOffenemHimmel(
                                                currentTime, bewoelkungChangeSofernRelevant)
                                        .stream()
                                        .filter(EinzelnerSatz.class::isInstance)
                                        .map(bewSatz ->
                                                new ZweiSaetze(
                                                        (EinzelnerSatz) tempSatz,
                                                        (EinzelnerSatz) bewSatz)))
                        .collect(toImmutableSet()))
                .addAll(altNeueSaetze(
                        TEMPERATUR_DESC_DESCRIBER.altSprungOderWechsel(
                                currentTime, temperaturChangeSofernRelevant,
                                drinnenDraussen),
                        SENTENCE,
                        BEWOELKUNG_DESC_DESCRIBER.altSprungOderWechselDraussen(
                                currentTime, bewoelkungChangeSofernRelevant,
                                drinnenDraussen ==
                                        DRAUSSEN_UNTER_OFFENEM_HIMMEL)))
                // IDEA "Das Wetter ändert sich:...", "Das Wetter schwingt um / ist umgeschwungen"
                .build();
    }

    private ImmutableCollection<AbstractDescription<?>> altTimePassedTageszeitenaenderung(
            final AvDateTime lastTime, final AvDateTime currentTime,
            final boolean generelleTemperaturOutsideLocationTemperaturRange, @Nullable final
            WetterParamChange<Temperatur> temperaturChangeSofernRelevant,
            @Nullable final
            WetterParamChange<Bewoelkung> bewoelkungChangeSofernRelevant,
            final DrinnenDraussen drinnenDraussen,
            final boolean bewoelkungsaenderungSollBeschriebenWerden) {
        if (!bewoelkungsaenderungSollBeschriebenWerden) {
            // Nur Tageszeitenänderunge und - evtl. - Temperaturänderung beschreiben

            return altTageszeitenUndEvtlTemperaturaenderung(lastTime.getTageszeit(),
                    currentTime.getTime(),
                    generelleTemperaturOutsideLocationTemperaturRange,
                    temperaturChangeSofernRelevant != null ?
                            temperaturChangeSofernRelevant.getNachher() : null,
                    drinnenDraussen);
        }

        // Bewölkung und Tageszeitenänderung und - evtl. - Temperaturänderung
        // beschreiben
        return alt()
                .addAll(altNeueSaetze(
                        altTageszeitenUndEvtlTemperaturaenderung(
                                lastTime.getTageszeit(),
                                currentTime.getTime(),
                                generelleTemperaturOutsideLocationTemperaturRange,
                                temperaturChangeSofernRelevant != null ?
                                        temperaturChangeSofernRelevant.getNachher() :
                                        null,
                                drinnenDraussen),
                        SENTENCE,
                        BEWOELKUNG_DESC_DESCRIBER.altSprungOderWechselDraussen(
                                currentTime, bewoelkungChangeSofernRelevant,
                                drinnenDraussen ==
                                        DRAUSSEN_UNTER_OFFENEM_HIMMEL)
                )).build();
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final WetterData that = (WetterData) o;
        return tageshoechsttemperatur == that.tageshoechsttemperatur &&
                tagestiefsttemperatur == that.tagestiefsttemperatur &&
                windstaerke == that.windstaerke &&
                bewoelkung == that.bewoelkung &&
                blitzUndDonner == that.blitzUndDonner;
    }

    @Override
    public int hashCode() {
        return Objects.hash(tageshoechsttemperatur, tagestiefsttemperatur, windstaerke, bewoelkung,
                blitzUndDonner);
    }
}
