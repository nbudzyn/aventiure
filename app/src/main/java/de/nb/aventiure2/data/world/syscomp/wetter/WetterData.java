package de.nb.aventiure2.data.world.syscomp.wetter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

import java.util.Objects;
import java.util.stream.Stream;

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
import de.nb.aventiure2.german.adjektiv.ZweiAdjPhrOhneLeerstellen;
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
import static de.nb.aventiure2.data.world.base.Temperatur.KUEHL;
import static de.nb.aventiure2.data.world.base.Temperatur.WARM;
import static de.nb.aventiure2.data.world.syscomp.storingplace.DrinnenDraussen.DRAUSSEN_GESCHUETZT;
import static de.nb.aventiure2.data.world.syscomp.storingplace.DrinnenDraussen.DRAUSSEN_UNTER_OFFENEM_HIMMEL;
import static de.nb.aventiure2.data.world.syscomp.wetter.windstaerke.Windstaerke.KRAEFTIGER_WIND;
import static de.nb.aventiure2.data.world.syscomp.wetter.windstaerke.Windstaerke.LUEFTCHEN;
import static de.nb.aventiure2.data.world.syscomp.wetter.windstaerke.Windstaerke.STURM;
import static de.nb.aventiure2.data.world.syscomp.wetter.windstaerke.Windstaerke.WINDIG;
import static de.nb.aventiure2.german.adjektiv.AdjektivOhneErgaenzungen.BRUETEND;
import static de.nb.aventiure2.german.adjektiv.AdjektivOhneErgaenzungen.DRUECKEND;
import static de.nb.aventiure2.german.adjektiv.AdjektivOhneErgaenzungen.HART;
import static de.nb.aventiure2.german.adjektiv.AdjektivOhneErgaenzungen.HEISS;
import static de.nb.aventiure2.german.adjektiv.AdjektivOhneErgaenzungen.KRAEFTIG;
import static de.nb.aventiure2.german.adjektiv.AdjektivOhneErgaenzungen.KRAFTVOLL;
import static de.nb.aventiure2.german.adjektiv.AdjektivOhneErgaenzungen.PFEIFEND;
import static de.nb.aventiure2.german.adjektiv.AdjektivOhneErgaenzungen.RAU;
import static de.nb.aventiure2.german.adjektiv.AdjektivOhneErgaenzungen.SENGEND;
import static de.nb.aventiure2.german.adjektiv.AdjektivOhneErgaenzungen.STEHEND;
import static de.nb.aventiure2.german.base.NomenFlexionsspalte.HITZE;
import static de.nb.aventiure2.german.base.NomenFlexionsspalte.LUFT;
import static de.nb.aventiure2.german.base.NomenFlexionsspalte.MITTAGSHITZE;
import static de.nb.aventiure2.german.base.NomenFlexionsspalte.MITTAGSSONNE;
import static de.nb.aventiure2.german.base.NomenFlexionsspalte.SONNE;
import static de.nb.aventiure2.german.base.NomenFlexionsspalte.SONNENHITZE;
import static de.nb.aventiure2.german.base.NomenFlexionsspalte.SONNENSCHEIN;
import static de.nb.aventiure2.german.base.NomenFlexionsspalte.WIND;
import static de.nb.aventiure2.german.base.PraepositionMitKasus.IN_AKK;
import static de.nb.aventiure2.german.base.PraepositionMitKasus.IN_DAT;
import static de.nb.aventiure2.german.base.StructuralElement.SENTENCE;
import static de.nb.aventiure2.german.description.AltDescriptionsBuilder.alt;
import static de.nb.aventiure2.german.description.AltDescriptionsBuilder.altNeueSaetze;
import static de.nb.aventiure2.util.StreamUtil.*;
import static java.util.stream.Collectors.toSet;

@SuppressWarnings("DuplicateBranchesInSwitch")
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

    /**
     * Die Windstärke und offenem Himmel. Die Windstärke kann lokal niedriger sein, z.B.
     * an einem geschützten Ort draußen. Wir gehen außerdem davon aus, dass drinnen
     * kein Wind weht.
     */
    private final Windstaerke windstaerkeUnterOffenemHimmel;

    private final Bewoelkung bewoelkung;

    private final BlitzUndDonner blitzUndDonner;

    @SuppressWarnings("WeakerAccess")
    public WetterData(final Temperatur tageshoechsttemperatur,
                      final Temperatur tagestiefsttemperatur,
                      final Windstaerke windstaerkeUnterOffenemHimmel,
                      final Bewoelkung bewoelkung,
                      final BlitzUndDonner blitzUndDonner) {
        this.tageshoechsttemperatur = tageshoechsttemperatur;
        this.tagestiefsttemperatur = tagestiefsttemperatur;
        this.windstaerkeUnterOffenemHimmel = windstaerkeUnterOffenemHimmel;
        this.bewoelkung = bewoelkung;
        this.blitzUndDonner = blitzUndDonner;
    }

    @NonNull
    public static WetterData getDefault() {
        return new WetterData(
                WARM, Temperatur.KUEHL,
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
        // FIXME Blitz und Donner berücksichtigen

        final AltDescriptionsBuilder alt = alt();

        final DrinnenDraussen drinnenDraussen =
                unterOffenemHimmel ? DRAUSSEN_UNTER_OFFENEM_HIMMEL : DRAUSSEN_GESCHUETZT;

        final Windstaerke windstaerke = getLokaleWindstaerkeDraussen(unterOffenemHimmel);
        final Temperatur temperatur = getLokaleTemperatur(time, locationTemperaturRange);

        final boolean windMussBeschriebenWerden =
                windMussDraussenBeschriebenWerden(windstaerke);
        final boolean temperaturMussBeschriebenWerden =
                temperaturMussDraussenBeschriebenWerden(time, unterOffenemHimmel, temperatur,
                        windstaerke);
        final boolean bewoelkungMussBeschriebenWerden =
                bewoelkungMussDraussenBeschriebenWerden(time, unterOffenemHimmel, windstaerke);

        if (!windMussBeschriebenWerden
                && !temperaturMussBeschriebenWerden
                && !bewoelkungMussBeschriebenWerden) {
            // "es ist schon dunkel", "es ist Abend"
            alt.addAll(TAGESZEIT_DESC_DESCRIBER.altDraussen(time,
                    auchEinmaligeErlebnisseNachTageszeitenwechselBeschreiben));
        }

        if (!windMussBeschriebenWerden && !bewoelkungMussBeschriebenWerden) {
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

        if (!temperaturMussBeschriebenWerden && !bewoelkungMussBeschriebenWerden) {
            // FIXME nur Wind beschreiben - ähnlich altWoInWindUndTemperatur()
        }

        if (!windMussBeschriebenWerden && !temperaturMussBeschriebenWerden) {
            if (unterOffenemHimmel && temperatur.isUnauffaellig(time.getTageszeit())) {
                alt.addAll(BEWOELKUNG_DESC_DESCRIBER.altUnterOffenemHimmel(bewoelkung, time,
                        auchEinmaligeErlebnisseNachTageszeitenwechselBeschreiben));
            }

            if (temperatur == WARM && time.getTageszeit() == ABENDS) {
                if (bewoelkung.isUnauffaellig(time.getTageszeit())) {
                    // "Es ist ein schöner Abend, die Sonne scheint"
                    alt.addAll(BEWOELKUNG_DESC_DESCRIBER.altSchoeneTageszeit(
                            bewoelkung, time, unterOffenemHimmel,
                            auchEinmaligeErlebnisseNachTageszeitenwechselBeschreiben));
                }
            }
        }

        if (!bewoelkungMussBeschriebenWerden) {
            // FIXME ähnlich alt.addAll(altWoInWindUndTemperatur(time, windstaerke, temperatur));
        }

        // FIXME Häufiger Fall: !windMussBeschriebenWerden;

        // FIXME Sehr seltener Fall: !temperaturMussBeschriebenWerden

        // FIXME Immer außerdem nach allen ifs: Alles beschreiben - Wind, Temperatur und Bewölkung!


        // FIXME Je nach windMussBeschriebenWerden ggf. Wind beschreiben
        // FIXME: Tendenziell lassen sich Wind und Temperatur häufig gut in einem
        //  beschreiben.

        // FIXME Hier muss evtl. der Wind mitbeschrieben werden!

        if (unterOffenemHimmel) {
            // Temperatur und Bewölkung werden beide erwähnt
            alt.addAll(altStatischBewoelkungUndTemperaturUnterOffenemHimmel(
                    time,
                    locationTemperaturRange,
                    auchEinmaligeErlebnisseNachTageszeitenwechselBeschreiben));
        }

        return alt.schonLaenger().build();
    }

    // FIXME Wind / Sturm - statisch, ohne Bezug auf Features des Umwelt:
    //  WINDSTILL:
    //   "die Luft steht"
    //   "...und kein Lüftchen"
    //  "Es geht kein Wind"
    //  "Kein Wind weht"
    //  LUEFTCHEN:
    //   "...und ein Lüftchen streicht..."
    //  WINDIG:
    //  "der Wind"
    //  "der Wind saust"
    //  KRAEFTIGER_WIND:
    //  "draußen geht ein kräftiger, kalter Wind"
    //  "es weht beständig ein Wind"
    //  "es weht beständig ein harter Wind"
    //   Der Wind pfeift dir ums Gesicht
    //   "...und der Wind zaust dein Haar"
    //  Der Wind ist sehr kräftig und unangenehm.
    //  "Um Mitternacht geht der Wind..."
    //  "der Wind rauscht draußen"
    //  STURM:
    //  "es stürmt"
    //  "Sturm"
    //   "Der Wind stürmt"
    //  SCHWERER STURM:
    //   Hoffentlich bleibt es wenigstens trocken
    //   Ein ziemlicher Krach (Hexe geht nicht mehr spazieren. Schlossfest?!)
    //   Gehen kostet dich einige Mühe

    // FIXME Wind / Sturm - statisch, unter Bezug auf Features des Umwelt
    //  (Laub, Blätter, Bäume, Äste, Wald; etwas, das Schutz bietet)
    //  WINDSTILL:
    //   "...und kein Lüftchen streicht durch das Laub"
    //  "Es geht kein Wind, und bewegt sich kein Blättchen"
    //  LUEFTCHEN:
    //   "...und ein Lüftchen streicht durch das Laub"
    //  WINDIG:
    //  "der Wind raschelt in den Bäumen"
    //  KRAEFTIGER_WIND:
    //   "das Gezweig"
    //  "der Wind rauscht draußen in den Bäumen"
    //  STURM:
    //   "Die Äste biegen sich"
    //  SCHWERER STURM:
    //  Der Sturm biegt die Bäume.
    //   "...und gehst nach dem Wald zu, dort ein wenig Schutz vor dem Wetter zu suchen"
    //   Hoffentlich bleibt es wenigstens trocken
    //  "darin bist du vor dem Wind geschützt"
    //  "du findest darin Schutz"
    //  Der Sturm peitscht die Äste über dir... Ein geschützter Platz wäre schön.
    //  Der Sturm peitscht die Äste über dir und es ist ziemlich dunkel. Ein geschützter Platz
    //  wäre schön.

    // FIXME Wind in Kombination NUR MIT TEMPERATUR (oder Tageszeit) - statisch
    //  WINDSTILL:
    //  LUEFTCHEN:
    //  "ein kühles Lüftchen"
    //  "ein kühles Lüftchen streicht..."
    //  "ein kühles Lüftchen streicht durch das Laub"
    //  WINDIG:
    //  "Der Wind geht kalt"
    //  "Der Wind geht so kalt, dass dir nicht warm werden will"
    //  "Um Mitternacht geht der Wind so kalt, dass dir nicht warm werden will"
    //  KRAEFTIGER_WIND:
    //  STURM:
    //  SCHWERER STURM:
    //  ORKAN:

    // FIXME Wind in Kombination NICHT ODER NICHT NUR MIT TEMPERATUR - statisch
    //  WINDSTILL:
    //  LUEFTCHEN:
    //  "Die Sonne scheint hell, die Vögel singen, und ein kühles Lüftchen streichts durch das
    //  Laub, und du..."
    //  - "Der Himmel ist blau, die Luft mild"?!
    //  WINDIG:
    //   "Weiße Wölkchen ziehen am blauen Himmel über dir vorbei"
    //  "Der Himmel ist blau und eine frische Luft weht dir entgegen"
    //  KRAEFTIGER_WIND:
    //  "der Wind ... und die Wolken ziehen ganz nah über deinem Haupt weg"
    //  "Der Wind treibt Wolkenfetzen über den Sternenhimmel"
    //  STURM:
    //  SCHWERER STURM:
    //  ORKAN:


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
            if (temperatur.isBetweenIncluding(WARM, Temperatur.RECHT_HEISS)) {
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

    private static boolean windMussDraussenBeschriebenWerden(final Windstaerke windstaerke) {
        return !windstaerke.isUnauffaellig();
    }

    private boolean temperaturMussDraussenBeschriebenWerden(final AvTime time,
                                                            final boolean unterOffenemHimmel,
                                                            final Temperatur temperatur,
                                                            final Windstaerke windstaerke) {
        // Bei gewissen Windstärken versteht sich die Temperatur von selbst und
        // muss nur beschrieben werden, wenn sie sehr ungewöhnlich ist

        if (windstaerke.compareTo(STURM) >= 0
                && temperatur.isBetweenIncluding(KUEHL, Temperatur.WARM)) {
            return false;
        }

        if (windstaerke.compareTo(WINDIG) >= 0 && temperatur == KUEHL) {
            return false;
        }

        // Wann soll die Temperatur draußen beschrieben werden?
        return // Immer, wenn es tagsüber ist!
                time.getTageszeit() != NACHTS
                        // Und immer, wenn die Temperatur auffällig ist!
                        || !temperatur.isUnauffaellig(NACHTS)
                        // Und immer, wenn man den Himmel sieht und die Bewölkung auffällig ist!
                        || (unterOffenemHimmel && !bewoelkung.isUnauffaellig(time.getTageszeit()));
    }

    private boolean bewoelkungMussDraussenBeschriebenWerden(final AvTime time,
                                                            final boolean unterOffenemHimmel,
                                                            final Windstaerke windstaerke) {
        // Wann soll die Bewölkung draußen beschrieben werden?

        if (windstaerke.compareTo(KRAEFTIGER_WIND) >= 0) {
            // Ab gewisser Windstärke verliert die Bewölkung an Bedeutung
            return false;
        }

        return
                // Immer, wenn man den HImmel sieht und die Bewölkung auffällig ist!
                unterOffenemHimmel && !bewoelkung.isUnauffaellig(time.getTageszeit());
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
        // FIXME Blitz und Donner berücksichtigen

        final AltDescriptionsBuilder alt = alt();

        final Windstaerke windstaerke = getLokaleWindstaerkeDraussen(unterOffenenHimmel);
        final Temperatur temperatur = getLokaleTemperatur(time, locationTemperaturRange);

        final boolean windMussBeschriebenWerden = windMussDraussenBeschriebenWerden(windstaerke);
        final boolean temperaturMussBeschriebenWerden =
                temperaturMussDraussenBeschriebenWerden(time, unterOffenenHimmel, temperatur,
                        windstaerke);
        final boolean bewoelkungMussBeschriebenWerden =
                bewoelkungMussDraussenBeschriebenWerden(time, unterOffenenHimmel, windstaerke);
        if (!windMussBeschriebenWerden
                && !temperaturMussBeschriebenWerden
                && !bewoelkungMussBeschriebenWerden) {
            // "draußen ist es schon dunkel"
            alt.addAll(
                    TAGESZEIT_DESC_DESCRIBER.altKommtNachDraussen(time,
                            auchEinmaligeErlebnisseNachTageszeitenwechselBeschreiben));
        }

        if (!windMussBeschriebenWerden && !bewoelkungMussBeschriebenWerden) {
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
        if (!temperaturMussBeschriebenWerden && !bewoelkungMussBeschriebenWerden) {
            // FIXME nur Wind beschreiben - ähnlich altWoInWindUndTemperatur()
        }

        if (!windMussBeschriebenWerden && !temperaturMussBeschriebenWerden) {
            if (unterOffenenHimmel && temperatur.isUnauffaellig(time.getTageszeit())) {
                // "Draußen ist der Himmel bewölkt"
                alt.addAll(BEWOELKUNG_DESC_DESCRIBER
                        .altKommtUnterOffenenHimmel(bewoelkung, time, true,
                                auchEinmaligeErlebnisseNachTageszeitenwechselBeschreiben));
            }

            if (temperatur == WARM && time.getTageszeit() == ABENDS) {
                if (bewoelkung.isUnauffaellig(time.getTageszeit())) {
                    // "Es ist ein schöner Abend, die Sonne scheint"
                    alt.addAll(BEWOELKUNG_DESC_DESCRIBER.altSchoeneTageszeit(
                            bewoelkung, time, unterOffenenHimmel,
                            auchEinmaligeErlebnisseNachTageszeitenwechselBeschreiben));
                }
            }
        }

        if (!bewoelkungMussBeschriebenWerden) {
            // FIXME Ähnlich diesem alt.addAll(altWoInWindUndTemperatur(time, windstaerke,
            //  temperatur));
        }

        // FIXME Häufiger Fall: !windMussBeschriebenWerden;

        // FIXME Sehr seltener Fall: !temperaturMussBeschriebenWerden

        // FIXME Immer außerdem nach allen ifs: Alles beschreiben - Wind, Temperatur und Bewölkung!


        // FIXME !temperaturMussBeschriebenWerden? Irrelevant, da draußen quasi
        //  immer die Temperatur beschrieben wird!

        // FIXME Wind ggf.  mitbeschreiben

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
            if (temperatur.isBetweenIncluding(WARM, Temperatur.RECHT_HEISS)) {
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
        if (unterOffenemHimmel && !bewoelkung.isUnauffaellig(time.getTageszeit())) {
            // Die Bewölkung soll nicht unterschlagen werden.
            return ImmutableSet.of();
        }

        final Windstaerke windstaerke = getLokaleWindstaerkeDraussen(unterOffenemHimmel);
        if (windstaerke.compareTo(STURM) >= 0) {
            // Sturm steht definitiv im Vordergrund - aber Sturm geht selten den ganzen Tag!
            return ImmutableSet.of();
        }

        // FIXME Blitz und Donner berücksichtigen, wohl ggf. auch einfach hier abbrechen,
        //  weil Gewitter nicht den ganzen Tag geht.

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

    // FIXME altWindstaerkeUnterschiedZuVorLocation,
    //  getLokaleWindstärkeDraussen(unterOffenemHimmel) verwenden!
    //  Ggf. zusammenfassen in der Art xyz soll beschrieben werden?!!

    @NonNull
    @CheckReturnValue
    ImmutableSet<String> altWetterplauderrede(final AvTime time) {
        final Temperatur temperatur = getAktuelleGenerelleTemperatur(time);

        // FIXME Blitz und Donner berücksichtigen

        if (windstaerkeUnterOffenemHimmel.compareTo(STURM) >= 0) {
            return ImmutableSet
                    .of("Das ist ein Wetter, als wenn die Welt untergehen sollte!");
        }

        if (temperatur == Temperatur.KLIRREND_KALT
                || temperatur == Temperatur.SEHR_HEISS
                || bewoelkung == Bewoelkung.BEDECKT
                || windstaerkeUnterOffenemHimmel.compareTo(KRAEFTIGER_WIND) >= 0) {
            return ImmutableSet
                    .of("Was ein Wetter!", "Was für ein Wetter!", "Welch ein Wetter!");
        }

        if (bewoelkung == Bewoelkung.BEWOELKT
                || windstaerkeUnterOffenemHimmel.compareTo(WINDIG) >= 0) {
            return ImmutableSet.of("Das Wetter war ja auch schon mal besser.");
        }

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

        // FIXME Blitz und Donner berücksichtigen?

        final Windstaerke windstaerke = getLokaleWindstaerkeDraussen(unterOffenenHimmel);
        final Temperatur temperatur = getLokaleTemperatur(time, locationTemperaturRange);

        final boolean windMussBeschriebenWerden = windMussDraussenBeschriebenWerden(windstaerke);
        final boolean temperaturMussBeschriebenWerden =
                temperaturMussDraussenBeschriebenWerden(time, unterOffenenHimmel, temperatur,
                        windstaerke);
        final boolean bewoelkungMussBeschriebenWerden =
                bewoelkungMussDraussenBeschriebenWerden(time, unterOffenenHimmel, windstaerke);
        if (!windMussBeschriebenWerden
                && !temperaturMussBeschriebenWerden
                && !bewoelkungMussBeschriebenWerden) {
            alt.addAll(TAGESZEIT_ADV_ANGABE_WOHIN_DESCRIBER.altWohinHinaus(
                    time, auchEinmaligeErlebnisseNachTageszeitenwechselBeschreiben));
        }

        if (!windMussBeschriebenWerden && !bewoelkungMussBeschriebenWerden) {
            alt.addAll(TEMPERATUR_ADV_ANGABE_WOHIN_DESCRIBER.altWohinHinaus(temperatur, time));
        }

        if (!temperaturMussBeschriebenWerden && !bewoelkungMussBeschriebenWerden) {
            // FIXME nur Wind beschreiben - ähnlich altWoInWindUndTemperatur()
        }

        if (!windMussBeschriebenWerden && !temperaturMussBeschriebenWerden) {
            if (unterOffenenHimmel && temperatur.isUnauffaellig(time.getTageszeit())) {
                alt.addAll(BEWOELKUNG_ADV_ANGABE_WOHIN_DESCRIBER
                        .altHinausUnterOffenenHimmel(bewoelkung, time,
                                auchEinmaligeErlebnisseNachTageszeitenwechselBeschreiben));
            }

            if (temperatur == WARM && time.getTageszeit() == ABENDS) {
                if (bewoelkung.isUnauffaellig(time.getTageszeit())) {
                    alt.add(BEWOELKUNG_ADV_ANGABE_WOHIN_DESCRIBER.schoeneTageszeit(
                            time.getTageszeit()));
                }
            }
        }

        if (!bewoelkungMussBeschriebenWerden) {
            alt.addAll(altWohinInWindUndTemperaturHinaus(time, windstaerke, temperatur));
        }

        // FIXME Häufiger Fall: !windMussBeschriebenWerden;

        // FIXME Sehr seltener Fall: !temperaturMussBeschriebenWerden

        // FIXME Immer außerdem nach allen ifs: Alles beschreiben - Wind, Temperatur und Bewölkung!


        // FIXME Hier muss evtl. der Wind mitbeschrieben werden!
        //  WINDSTILL:
        //  LUEFTCHEN:
        //  WINDIG:
        //  "in den sausenden Wind"
        //  KRAEFTIGER_WIND
        //  "in den kräftigen Wind"
        //  "in den beständigen Wind"?
        //  "in den harten Wind"
        //  "in den pfeifenden Wind"
        //  "in den kräfigen, unangenehmen Wind"
        //  "in den rauschenden Wind"
        //  STURM:
        //  "in das Unwetter" ? (sehr unspezifizisch)
        //  "in den Sturm"
        //  "in den stürmenden Wind"
        //  "in den stürmischen Tag"
        //  "in den nächtlichen Sturm"
        //  SCHWERER STURM:
        //  "in den mit aller Kraft blasenden Sturm" (nur wohin?)
        //  "in den schweren Sturm"
        //  "in den tobenden Sturm"
        //  "in den tosenden Sturm"

        if (unterOffenenHimmel) {
            // Temperatur und Bewölkung werden beide erwähnt
            alt.addAll(altWohinInBewoelkungUndTemperaturHinausUnterOffenenHimmel(
                    time, locationTemperaturRange));
        }

        return alt.build();
    }

    private static ImmutableList<AdvAngabeSkopusVerbWohinWoher> altWohinInWindUndTemperaturHinaus(
            final AvTime time,
            final Windstaerke windstaerke,
            final Temperatur temperatur) {
        return mapToList(altWohinInWindUndTemperaturHinausPraepPhr(time, windstaerke, temperatur),
                AdvAngabeSkopusVerbWohinWoher::new);
    }

    private static ImmutableSet<Praepositionalphrase> altWohinInWindUndTemperaturHinausPraepPhr(
            final AvTime time,
            final Windstaerke windstaerke,
            final Temperatur temperatur) {
        return mapToSet(altDraussenWindUndTemperaturSubstPhr(time, windstaerke, temperatur),
                IN_AKK::mit);
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

        // FIXME Blitz und Donner berücksichtigen?

        final Windstaerke windstaerke = getLokaleWindstaerkeDraussen(unterOffenemHimmel);
        final Temperatur temperatur = getLokaleTemperatur(time, locationTemperaturRange);

        final boolean windMussBeschriebenWerden = windMussDraussenBeschriebenWerden(windstaerke);
        final boolean temperaturMussBeschriebenWerden =
                temperaturMussDraussenBeschriebenWerden(time, unterOffenemHimmel, temperatur,
                        windstaerke);
        final boolean bewoelkungMussBeschriebenWerden =
                bewoelkungMussDraussenBeschriebenWerden(time, unterOffenemHimmel, windstaerke);

        if (!windMussBeschriebenWerden
                && !temperaturMussBeschriebenWerden
                && !bewoelkungMussBeschriebenWerden) {
            alt.addAll(TAGESZEIT_ADV_ANGABE_WO_DESCRIBER.altWoDraussen(
                    time, auchEinmaligeErlebnisseNachTageszeitenwechselBeschreiben));
        }

        if (!windMussBeschriebenWerden && !bewoelkungMussBeschriebenWerden) {
            alt.addAll(TEMPERATUR_ADV_ANGABE_WO_DESCRIBER.altWoDraussen(temperatur, time));
        }

        if (!temperaturMussBeschriebenWerden && !bewoelkungMussBeschriebenWerden) {
            // FIXME nur Wind beschreiben - ähnlich altWoInWindUndTemperatur()
        }

        if (!windMussBeschriebenWerden && !temperaturMussBeschriebenWerden) {
            if (unterOffenemHimmel && temperatur.isUnauffaellig(time.getTageszeit())) {
                alt.addAll(BEWOELKUNG_ADV_ANGABE_WO_DESCRIBER
                        .altUnterOffenemHimmel(bewoelkung, time,
                                auchEinmaligeErlebnisseNachTageszeitenwechselBeschreiben));

            }
        }

        if (!bewoelkungMussBeschriebenWerden) {
            alt.addAll(altWoInWindUndTemperatur(time, windstaerke, temperatur));
        }

        // FIXME Häufiger Fall: !windMussBeschriebenWerden;

        // FIXME Sehr seltener Fall: !temperaturMussBeschriebenWerden

        // FIXME Immer außerdem nach allen ifs: Alles beschreiben - Wind, Temperatur und Bewölkung!


        // FIXME Hier muss evtl. der Wind mitbeschrieben werden!
        //  WINDSTILL:
        //  LUEFTCHEN:
        //  WINDIG:
        //  "im sausenden Wind"
        //  KRAEFTIGER_WIND
        //  "im kräftigen Wind"
        //  "im beständigen Wind"?
        //  "im harten Wind"
        //  "im pfeifenden Wind"
        //  "im kräfigen, unangenehmen Wind"
        //  "im rauschenden Wind"
        //  STURM:
        //  "im Unwetter" ? (sehr unspezifische - stürmischen..?)
        //  "im den Sturm"
        //  "im den stürmenden Wind"
        //  "im nächtlichen Sturm"
        //  SCHWERER STURM:
        //  "mitten im blasenden Sturm"
        //  "(mitten) im schweren Sturm"
        //  "(mitten) im tobenden Sturm"
        //  "(mitten) im tosenden Sturm"

        if (unterOffenemHimmel) {
            // Temperatur und Bewölkung werden beide erwähnt
            alt.addAll(altWoInBewoelkungUndTemperaturUnterOffenemHimmel(
                    time, locationTemperaturRange));
        }

        return alt.build();
    }

    private static ImmutableList<AdvAngabeSkopusVerbAllg> altWoInWindUndTemperatur(
            final AvTime time,
            final Windstaerke windstaerke,
            final Temperatur temperatur) {
        return mapToList(altWoInWindUndTemperaturPraepPhr(time, windstaerke, temperatur),
                AdvAngabeSkopusVerbAllg::new);
    }

    private static ImmutableSet<Praepositionalphrase> altWoInWindUndTemperaturPraepPhr(
            final AvTime time,
            final Windstaerke windstaerke,
            final Temperatur temperatur) {
        return mapToSet(altDraussenWindUndTemperaturSubstPhr(time, windstaerke, temperatur),
                IN_DAT::mit);
    }

    private static ImmutableSet<EinzelneSubstantivischePhrase>
    altDraussenWindUndTemperaturSubstPhr(final AvTime time,
                                         final Windstaerke windstaerke,
                                         final Temperatur temperatur) {

        final ImmutableSet.Builder<EinzelneSubstantivischePhrase> alt = ImmutableSet.builder();

        // "der kalte Wind" (evtl. leer)
        alt.addAll(TEMPERATUR_PRAEDIKATIVUM_DESCRIBER
                .altLuftAdjPhr(temperatur, time.getTageszeit())
                .stream()
                .flatMap(kalt -> windstaerke.altNomenFlexionsspalte().stream()
                        .map(wind -> wind.mit(kalt)))
                .collect(toSet()));

        // "der windige, kalte Morgen" (evtl. leer)
        alt.addAll(
                TEMPERATUR_PRAEDIKATIVUM_DESCRIBER.altAdjPhr(temperatur, true)
                        .stream()
                        .flatMap(kalt -> windstaerke.altAdjPhrWetter().stream()
                                .map(windig -> new ZweiAdjPhrOhneLeerstellen(
                                        windig,
                                        true,
                                        kalt)))
                        .map(windigKommaKalt ->
                                time.getTageszeit().getNomenFlexionsspalte()
                                        .mit(windigKommaKalt))
                        .collect(toImmutableSet()));

        switch (windstaerke) {
            case WINDSTILL:
                if (temperatur == Temperatur.SEHR_HEISS) {
                    alt.add(HITZE.mit(DRUECKEND));
                    alt.add(HITZE.mit(BRUETEND));

                    if (time.gegenMittag()) {
                        alt.add(MITTAGSHITZE.mit(DRUECKEND));
                    }
                }
                if (temperatur.compareTo(WARM) >= 0) {
                    // "die stehende, heiße Luft"
                    alt.addAll(mapToSet(
                            TEMPERATUR_PRAEDIKATIVUM_DESCRIBER.altAdjPhr(
                                    temperatur, true),
                            heiss -> LUFT.mit(
                                    new ZweiAdjPhrOhneLeerstellen(
                                            STEHEND,
                                            true,
                                            heiss))));
                }
                break;
            case LUEFTCHEN:
                break;
            case WINDIG:
                break;
            case KRAEFTIGER_WIND:
                //  "der kräftige, kalte Wind"
                alt.addAll(Stream.of(KRAEFTIG, KRAFTVOLL, HART, PFEIFEND)
                        .flatMap(kraeftig ->
                                TEMPERATUR_PRAEDIKATIVUM_DESCRIBER.altAdjPhr(
                                        temperatur, true).stream()
                                        .map(kalt -> WIND.mit(
                                                new ZweiAdjPhrOhneLeerstellen(
                                                        kraeftig,
                                                        true,
                                                        kalt))))
                        .collect(toSet()));
                if (temperatur.compareTo(KUEHL) <= 0) {
                    // "der raue Morgen"
                    alt.add(time.getTageszeit().getNomenFlexionsspalte().mit(RAU));
                }
                break;
            case STURM:
                break;
            case SCHWERER_STURM:
                break;
        }

        return alt.build();
    }

    private ImmutableCollection<AdvAngabeSkopusVerbAllg>
    altWoInBewoelkungUndTemperaturUnterOffenemHimmel(
            final AvTime time,
            final EnumRange<Temperatur> locationTemperaturRange) {
        final ImmutableSet.Builder<AdvAngabeSkopusVerbAllg> alt = ImmutableSet.builder();

        // FIXME getLokaleWindstärkeDraussen(unterOffenemHimmel) verwenden!
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

    private Windstaerke getLokaleWindstaerkeDraussen(final boolean unterOffenemHimmel) {
        if (unterOffenemHimmel) {
            return windstaerkeUnterOffenemHimmel;
        }

        return getLokaleWindstaerkeDraussenGeschuetzt();
    }

    private Windstaerke getLokaleWindstaerkeDraussenGeschuetzt() {
        if (windstaerkeUnterOffenemHimmel.compareTo(LUEFTCHEN) <= 0) {
            return windstaerkeUnterOffenemHimmel;
        }

        if (windstaerkeUnterOffenemHimmel.compareTo(KRAEFTIGER_WIND) <= 0) {
            return windstaerkeUnterOffenemHimmel.getVorgaenger();
        }

        return windstaerkeUnterOffenemHimmel.getVorgaenger().getVorgaenger();
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


    // FIXME Donner / Blitz
    //  In der Ferne hörst du Donnergrollen
    //  Hat es eben geblitzt?


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

    // FIXME Automatisch generieren: "Jemand tut dies und das, bis eine Wetterveränderung
    //  eintritt":
    //  -- "..., bis der Mond aufgeht."
    //  -- "..., bis die Sonne sinkt und die Nacht einbricht."
    //  -- "Du (bleibst unter der Linde sitzen), bis die Sonne untergeht"

    // FIXME Automatisch generieren: "Als / wie du nun etwas tust und das Wetter soundso ist,
    //  passiert irgendwas":
    //  -- "Wie du nun (dies und jenes tust) und zu Mittag die Sonne heiß brennt, wird dir so
    //     warm und verdrießlich zumut:"
    //  -- "Wie nun die Sonne kommt und du aufwachst..."

    // FIXME Man könnte (wo? / wie?) spezielle Beschreibungen dafür einauen, dass sich das SC
    //  im Wald befindet (oder allgemein in einer Landschaftsform???):
    //  - "als du siehst, wie die Sonnenstrahlen durch die Bäume hin- und hertanzen"
    //  - "durch die dichtbelaubten Äste dringt kein Sonnenstrahl"
    //  - "Als nun die Sonne mitten über dem Walde steht..." (eigentlich beschreiben
    //    wir im Wald - geschützt! - keine Sonne)
    //  - "der Wind raschelt in den Bäumen, und die Wolken ziehen ganz nah über deinem Haupt
    //  weg"

    // IDEA Man könnte auch andere Features der Landschaft in die Wetterbeschreibungen einbauen:
    //  - "Du siehst die Sonne hinter den Bergen aufsteigen"
    //  - "Als aber die ersten Sonnenstrahlen in den Garten fallen, so..."
    //  - "Noch halb steht die Sonne über (dem Berg) und halb ist sie unter."
    //  - "aber was tust du die Augen auf, als du aus (der Finsternis)
    //    heraus in das Tageslicht kommst, und den grünen Wald,
    //    Blumen und Vögel und die Morgensonne am Himmel erblickst"
    //  - "die Sonne ist hinter (den Bergen) verschwunden"
    //  - "mittendurch rauscht ein klarer Bach, auf dem die Sonne glitzert"
    //  - "gegen Abend, als die Sonne hinter die Berge gesunken ist"
    //  - "der Mond lässt sein Licht über alle Felder leuchten"

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

    // IDEA: Regen (Problem: Dinge werden nass und trocknen nur langsam - das muss man
    //  später berücksichtigen)
    //  - "der Regen schlägt dir ins Gesicht und der Wind zaust dein Haar"
    //  - "Wind und Regen stürmen"
    //  - "Das Wetter wird (aber) (so) schlecht, und Wind und Regen stürmen"
    //  - "Das Wetter ist (aber) (so) schlecht geworden, und Wind und Regen stürmt"
    //  - "Weil aber das Wetter so schlecht geworden ist, und Wind und Regen stürmt, kannst du
    //     nicht weiter"
    //  - "Weil aber das Wetter so schlecht geworden, und Wind und Regen stürmte,
    //    kannst du nicht weiter und kehrst [...] ein."
    //  - "Wind und böses Wetter"
    //  - "so bist du vor der Kälte und dem bösen Wetter geschützt"
    //  - "darin bist du vor Wind und Wetter geschützt"
    //  "Und alsbald fängt der Himmel an zu regnen"
    //  "und weil es anfängt zu regnen"
    //  "Tropfen fallen"

    // FIXME Jemand wärmt sich auf (z.B. am Feuer oder Drinnen)
    //  "du bist halb erfroren und willst dich nur ein wenig wärmen"
    //  "du reibst die Hände"
    //  "du bist so erfroren"
    //  "dich wärmen"
    //  "du erwärmst dich" (am Feuer)
    //  "so bist du vor der Kälte geschützt"

    // FIXME "Du kommst in den Wald, und da es darin kühl und lieblich ist
    //  [lokale Temperatur] und die Sonne heiß brennt [generelle Temperatur], so..."

    // FIXME Wetter beeinflusst Stimmung von SC, Rapunzel, Zauberin (Listener-Konzept:
    //  onWetterwechsel()? onTemperaturWechsel()?)
    //  "von der Hitze des Tages ermüdet" (SC wird im Wind oder in Hitze schneller müde)
    //  "du bist von der Sonnenhitze müde"
    //  "Wie nun zu Mittag die Sonne heiß brennt, wird dir so warm und verdrießlich zumut"
    //  "du bist von der Sonnenhitze müde"
    //  "Die Sonne scheint hell, die Vögel singen, und ein kühles Lüftchen streichts durch das
    //  Laub, und du bist voll Freude und Lust."
    //  SC kann bei Wind oder bei Sturm draußen nicht einschlafen

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
    Windstaerke getWindstaerkeUnterOffenemHimmel() {
        return windstaerkeUnterOffenemHimmel;
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
            final boolean tageszeitaenderungMussBeschriebenWerden,
            final boolean generelleTemperaturOutsideLocationTemperaturRange,
            @Nullable final WetterParamChange<Temperatur> temperaturChangeSofernRelevant,
            @Nullable final WetterParamChange<Bewoelkung> bewoelkungChangeSofernRelevant,
            final DrinnenDraussen drinnenDraussen) {
        checkArgument(bewoelkungChangeSofernRelevant == null
                        || drinnenDraussen.isDraussen(),
                "Bewölkungsänderungen werden nur draußen erzählt");

        // FIXME Änderung der Windstärke (soweit der SC sie mitbekommen haben kann)

        // FIXME Wind / Sturm - dynamisch:
        //  Der Wind wird stärker
        //  < ->  KRAEFTIGER_WIND: Der Wind ist jetzt sehr kräftig und unangenehm.
        //  < ->  KRAEFTIGER_WIND: "es kommt ein starker Wind"
        //  < -> STURM: Ein Sturm zieht auf
        //  STURM -> > Langsam scheint sich das Wetter wieder zu bessern / der Sturm flaut
        //  allmählich
        //  ab.

        // FIXME Wind / Sturm - dynamisch, unter Bezug auf Features des Umwelt
        //  (Blätter)
        //  Der Wind wird stärker
        //  WINDIG -> WINDSTILL "Der Wind legt sich, und auf den Bäumen vor [...] regt sich kein
        //  Blättchen mehr"

        // FIXME Wind in Kombination - dynamisch
        //  < ->  KRAEFTIGER_WIND: Der Wind ist jetzt sehr kräftig und unangenehm. Kalt ist es
        //  geworden.
        //  WINDSTILL, TEMPERATURANSTIEG: "Die Hitze wird drückender, je näher der Mittag kommt"

        // FIXME Eine Temperaturänderung könnte eine gute Gelegenheit sein, auf stärkeren Wind
        //  hinzuweisen...

        final boolean temperaturaenderungMussBeschriebenWerden =
                temperaturChangeSofernRelevant != null;

        final boolean bewoelkungsaenderungMussBeschriebenWerden =
                bewoelkungChangeSofernRelevant != null;

        if (!tageszeitaenderungMussBeschriebenWerden) {
            // Es soll keine Tageszeitänderung beschrieben werden
            return altTimePassedKeineTageszeitenaenderung(lastTime, currentTime,
                    temperaturChangeSofernRelevant,
                    bewoelkungChangeSofernRelevant, drinnenDraussen,
                    temperaturaenderungMussBeschriebenWerden,
                    bewoelkungsaenderungMussBeschriebenWerden);
        }

        // Es soll eine Tageszeitänderung beschrieben werden
        return altTimePassedTageszeitenaenderung(lastTime, currentTime,
                generelleTemperaturOutsideLocationTemperaturRange,
                temperaturChangeSofernRelevant,
                bewoelkungChangeSofernRelevant, drinnenDraussen,
                bewoelkungsaenderungMussBeschriebenWerden);
    }

    private static ImmutableCollection<AbstractDescription<?>>
    altTimePassedKeineTageszeitenaenderung(
            final AvDateTime lastTime, final AvDateTime currentTime,
            @Nullable final WetterParamChange<Temperatur> temperaturChangeSofernRelevant,
            @Nullable final WetterParamChange<Bewoelkung> bewoelkungChangeSofernRelevant,
            final DrinnenDraussen drinnenDraussen,
            final boolean temperaturaenderungMussBeschriebenWerden,
            final boolean bewoelkungsaenderungMussBeschriebenWerden) {
        if (!temperaturaenderungMussBeschriebenWerden
                && !bewoelkungsaenderungMussBeschriebenWerden) {
            // Nichts beschreiben - außer höchtestensuntertägigem Tageszeitenwechsel
            if (!currentTime.minus(lastTime).longerThan(AvTimeSpan.ONE_DAY)
                    && lastTime.getTageszeit() == currentTime.getTageszeit()) {
                return TAGESZEIT_DESC_DESCRIBER.altZwischentageszeitlicherWechsel(
                        lastTime.getTime(), currentTime.getTime(),
                        drinnenDraussen.isDraussen());
            }

            return ImmutableList.of();
        }

        if (temperaturaenderungMussBeschriebenWerden
                && !bewoelkungsaenderungMussBeschriebenWerden) {
            // Nur Temperatur beschreiben
            return TEMPERATUR_DESC_DESCRIBER.altSprungOderWechsel(
                    currentTime, temperaturChangeSofernRelevant, drinnenDraussen);
            // IDEA  "Die Sonne hat die Erde aufgetaut" (bei (aktuell) unauffälliger Bewölkung)
        }

        if (!temperaturaenderungMussBeschriebenWerden
                && bewoelkungsaenderungMussBeschriebenWerden) {
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
                // IDEA "Das Wetter ändert sich:...", "Das Wetter schwingt um / ist
                //  umgeschwungen"
                .build();
    }

    private ImmutableCollection<AbstractDescription<?>> altTimePassedTageszeitenaenderung(
            final AvDateTime lastTime, final AvDateTime currentTime,
            final boolean generelleTemperaturOutsideLocationTemperaturRange,
            @Nullable final WetterParamChange<Temperatur> temperaturChangeSofernRelevant,
            @Nullable final WetterParamChange<Bewoelkung> bewoelkungChangeSofernRelevant,
            final DrinnenDraussen drinnenDraussen,
            final boolean bewoelkungsaenderungMussBeschriebenWerden) {
        if (!bewoelkungsaenderungMussBeschriebenWerden) {
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
                windstaerkeUnterOffenemHimmel == that.windstaerkeUnterOffenemHimmel &&
                bewoelkung == that.bewoelkung &&
                blitzUndDonner == that.blitzUndDonner;
    }

    @Override
    public int hashCode() {
        return Objects.hash(tageshoechsttemperatur, tagestiefsttemperatur,
                windstaerkeUnterOffenemHimmel, bewoelkung,
                blitzUndDonner);
    }
}
