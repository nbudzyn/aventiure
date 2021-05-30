package de.nb.aventiure2.data.world.syscomp.wetter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;

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
import de.nb.aventiure2.data.world.base.Change;
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
import de.nb.aventiure2.data.world.syscomp.wetter.tageszeit.TageszeitAdvAngabeWannDescriber;
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
import de.nb.aventiure2.data.world.syscomp.wetter.windstaerke.WindstaerkeAdvAngabeWoDescriber;
import de.nb.aventiure2.data.world.syscomp.wetter.windstaerke.WindstaerkeAdvAngabeWohinDescriber;
import de.nb.aventiure2.data.world.syscomp.wetter.windstaerke.WindstaerkeDescDescriber;
import de.nb.aventiure2.data.world.syscomp.wetter.windstaerke.WindstaerkePraedikativumDescriber;
import de.nb.aventiure2.data.world.syscomp.wetter.windstaerke.WindstaerkePraepPhrDescriber;
import de.nb.aventiure2.data.world.syscomp.wetter.windstaerke.WindstaerkeSatzDescriber;
import de.nb.aventiure2.german.adjektiv.AdjPhrOhneLeerstellen;
import de.nb.aventiure2.german.adjektiv.ZweiAdjPhrOhneLeerstellen;
import de.nb.aventiure2.german.base.EinzelneSubstantivischePhrase;
import de.nb.aventiure2.german.base.GermanUtil;
import de.nb.aventiure2.german.base.NomenFlexionsspalte;
import de.nb.aventiure2.german.base.Personalpronomen;
import de.nb.aventiure2.german.base.Praepositionalphrase;
import de.nb.aventiure2.german.description.AbstractDescription;
import de.nb.aventiure2.german.description.AltDescriptionsBuilder;
import de.nb.aventiure2.german.praedikat.AdvAngabeSkopusSatz;
import de.nb.aventiure2.german.praedikat.AdvAngabeSkopusVerbAllg;
import de.nb.aventiure2.german.praedikat.AdvAngabeSkopusVerbWohinWoher;
import de.nb.aventiure2.german.satz.EinzelnerSatz;
import de.nb.aventiure2.german.satz.Satz;
import de.nb.aventiure2.german.satz.Satzreihe;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.collect.ImmutableSet.toImmutableSet;
import static de.nb.aventiure2.data.time.AvTimeSpan.span;
import static de.nb.aventiure2.data.time.Tageszeit.ABENDS;
import static de.nb.aventiure2.data.time.Tageszeit.NACHTS;
import static de.nb.aventiure2.data.world.base.Temperatur.KUEHL;
import static de.nb.aventiure2.data.world.base.Temperatur.WARM;
import static de.nb.aventiure2.data.world.gameobject.World.*;
import static de.nb.aventiure2.data.world.syscomp.storingplace.DrinnenDraussen.DRAUSSEN_GESCHUETZT;
import static de.nb.aventiure2.data.world.syscomp.storingplace.DrinnenDraussen.DRAUSSEN_UNTER_OFFENEM_HIMMEL;
import static de.nb.aventiure2.data.world.syscomp.wetter.bewoelkung.Bewoelkung.BEWOELKT;
import static de.nb.aventiure2.data.world.syscomp.wetter.bewoelkung.Bewoelkung.LEICHT_BEWOELKT;
import static de.nb.aventiure2.data.world.syscomp.wetter.bewoelkung.Bewoelkung.WOLKENLOS;
import static de.nb.aventiure2.data.world.syscomp.wetter.windstaerke.Windstaerke.KRAEFTIGER_WIND;
import static de.nb.aventiure2.data.world.syscomp.wetter.windstaerke.Windstaerke.STURM;
import static de.nb.aventiure2.data.world.syscomp.wetter.windstaerke.Windstaerke.WINDIG;
import static de.nb.aventiure2.data.world.syscomp.wetter.windstaerke.Windstaerke.WINDSTILL;
import static de.nb.aventiure2.german.adjektiv.AdjektivOhneErgaenzungen.BRUETEND;
import static de.nb.aventiure2.german.adjektiv.AdjektivOhneErgaenzungen.DRUECKEND;
import static de.nb.aventiure2.german.adjektiv.AdjektivOhneErgaenzungen.HART;
import static de.nb.aventiure2.german.adjektiv.AdjektivOhneErgaenzungen.HEFTIG;
import static de.nb.aventiure2.german.adjektiv.AdjektivOhneErgaenzungen.HEISS;
import static de.nb.aventiure2.german.adjektiv.AdjektivOhneErgaenzungen.KRAEFTIG;
import static de.nb.aventiure2.german.adjektiv.AdjektivOhneErgaenzungen.KRAFTVOLL;
import static de.nb.aventiure2.german.adjektiv.AdjektivOhneErgaenzungen.PFEIFEND;
import static de.nb.aventiure2.german.adjektiv.AdjektivOhneErgaenzungen.RAU;
import static de.nb.aventiure2.german.adjektiv.AdjektivOhneErgaenzungen.SENGEND;
import static de.nb.aventiure2.german.adjektiv.AdjektivOhneErgaenzungen.STEHEND;
import static de.nb.aventiure2.german.base.Artikel.Typ.INDEF;
import static de.nb.aventiure2.german.base.NomenFlexionsspalte.HAAR;
import static de.nb.aventiure2.german.base.NomenFlexionsspalte.HIMMEL;
import static de.nb.aventiure2.german.base.NomenFlexionsspalte.HITZE;
import static de.nb.aventiure2.german.base.NomenFlexionsspalte.LUFT;
import static de.nb.aventiure2.german.base.NomenFlexionsspalte.LUFTZUG;
import static de.nb.aventiure2.german.base.NomenFlexionsspalte.MITTAGSHITZE;
import static de.nb.aventiure2.german.base.NomenFlexionsspalte.MITTAGSSONNE;
import static de.nb.aventiure2.german.base.NomenFlexionsspalte.SONNE;
import static de.nb.aventiure2.german.base.NomenFlexionsspalte.SONNENHITZE;
import static de.nb.aventiure2.german.base.NomenFlexionsspalte.SONNENSCHEIN;
import static de.nb.aventiure2.german.base.NomenFlexionsspalte.WIND;
import static de.nb.aventiure2.german.base.NomenFlexionsspalte.WOLKENFETZEN;
import static de.nb.aventiure2.german.base.Nominalphrase.BRUETENDE_HITZE_DER_MITTAGSSONNE;
import static de.nb.aventiure2.german.base.Nominalphrase.BRUETENDE_HITZE_DER_SONNE;
import static de.nb.aventiure2.german.base.Nominalphrase.DEIN_HAAR;
import static de.nb.aventiure2.german.base.Nominalphrase.DRUECKENDE_HITZE_DER_MITTAGSSONNE;
import static de.nb.aventiure2.german.base.Nominalphrase.DRUECKENDE_HITZE_DER_SONNE;
import static de.nb.aventiure2.german.base.Nominalphrase.VON_DER_SONNE_AUFGEHEIZTE_STEHENDE_LUFT;
import static de.nb.aventiure2.german.base.Nominalphrase.np;
import static de.nb.aventiure2.german.base.Personalpronomen.EXPLETIVES_ES;
import static de.nb.aventiure2.german.base.PraepositionMitKasus.DURCH;
import static de.nb.aventiure2.german.base.PraepositionMitKasus.IN_AKK;
import static de.nb.aventiure2.german.base.PraepositionMitKasus.IN_DAT;
import static de.nb.aventiure2.german.base.PraepositionMitKasus.UEBER_AKK;
import static de.nb.aventiure2.german.base.StructuralElement.SENTENCE;
import static de.nb.aventiure2.german.description.AltDescriptionsBuilder.alt;
import static de.nb.aventiure2.german.description.AltDescriptionsBuilder.altNeueSaetze;
import static de.nb.aventiure2.german.praedikat.VerbSubj.WEHEN;
import static de.nb.aventiure2.german.praedikat.VerbSubjObj.ENTGEGENBLASEN;
import static de.nb.aventiure2.german.praedikat.VerbSubjObj.ENTGEGENWEHEN;
import static de.nb.aventiure2.german.praedikat.VerbSubjObj.PFEIFEN;
import static de.nb.aventiure2.german.praedikat.VerbSubjObj.STREICHEN;
import static de.nb.aventiure2.german.praedikat.VerbSubjObj.TREIBEN;
import static de.nb.aventiure2.german.praedikat.VerbSubjObj.ZAUSEN;
import static de.nb.aventiure2.german.praedikat.Witterungsverb.STUERMEN;
import static de.nb.aventiure2.german.satz.Satzreihe.altGereihtStandard;
import static de.nb.aventiure2.util.StreamUtil.*;
import static java.util.stream.Collectors.toSet;

@SuppressWarnings("DuplicateBranchesInSwitch")
@Immutable
public class WetterData {
    // Tageszeit-Describer
    private static final TageszeitPraedikativumDescriber TAGESZEIT_PRAEDIKATIVUM_DESCRIBER =
            new TageszeitPraedikativumDescriber();

    private static final TageszeitSatzDescriber TAGESZEIT_SATZ_DESCRIBER =
            new TageszeitSatzDescriber(TAGESZEIT_PRAEDIKATIVUM_DESCRIBER);

    private static final TageszeitDescDescriber TAGESZEIT_DESC_DESCRIBER =
            new TageszeitDescDescriber(
                    TAGESZEIT_SATZ_DESCRIBER);

    private static final TageszeitPraepPhrDescriber TAGESZEIT_PRAEP_PHR_DESCRIBER =
            new TageszeitPraepPhrDescriber(TAGESZEIT_PRAEDIKATIVUM_DESCRIBER);

    private static final TageszeitAdvAngabeWoDescriber TAGESZEIT_ADV_ANGABE_WO_DESCRIBER =
            new TageszeitAdvAngabeWoDescriber(TAGESZEIT_PRAEP_PHR_DESCRIBER);

    private static final TageszeitAdvAngabeWohinDescriber
            TAGESZEIT_ADV_ANGABE_WOHIN_DESCRIBER =
            new TageszeitAdvAngabeWohinDescriber(
                    TAGESZEIT_PRAEP_PHR_DESCRIBER);

    private static final TageszeitAdvAngabeWannDescriber TAGESZEIT_ADV_ANGABE_WANN_DESCRIBER =
            new TageszeitAdvAngabeWannDescriber();

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
                    TAGESZEIT_ADV_ANGABE_WANN_DESCRIBER, TEMPERATUR_PRAEDIKATIVUM_DESCRIBER);

    private static final TemperaturDescDescriber TEMPERATUR_DESC_DESCRIBER =
            new TemperaturDescDescriber(TAGESZEIT_DESC_DESCRIBER,
                    TAGESZEIT_ADV_ANGABE_WANN_DESCRIBER, TEMPERATUR_PRAEDIKATIVUM_DESCRIBER,
                    TEMPERATUR_SATZ_DESCRIBER);

    // Bewölkung-Describer
    private static final BewoelkungPraedikativumDescriber BEWOELKUNG_PRAEDIKATIVUM_DESCRIBER =
            new BewoelkungPraedikativumDescriber();

    private static final BewoelkungPraepPhrDescriber BEWOELKUNG_PRAEP_PHR_DESCRIBER =
            new BewoelkungPraepPhrDescriber(BEWOELKUNG_PRAEDIKATIVUM_DESCRIBER);

    private static final BewoelkungAdvAngabeWoDescriber
            BEWOELKUNG_ADV_ANGABE_WO_DESCRIBER =
            new BewoelkungAdvAngabeWoDescriber(
                    BEWOELKUNG_PRAEP_PHR_DESCRIBER);

    private static final BewoelkungAdvAngabeWohinDescriber
            BEWOELKUNG_ADV_ANGABE_WOHIN_DESCRIBER =
            new BewoelkungAdvAngabeWohinDescriber(
                    BEWOELKUNG_PRAEP_PHR_DESCRIBER);

    private static final BewoelkungSatzDescriber BEWOELKUNG_SATZ_DESCRIBER =
            new BewoelkungSatzDescriber(TAGESZEIT_ADV_ANGABE_WANN_DESCRIBER,
                    BEWOELKUNG_PRAEDIKATIVUM_DESCRIBER);

    private static final BewoelkungDescDescriber BEWOELKUNG_DESC_DESCRIBER =
            new BewoelkungDescDescriber(TAGESZEIT_ADV_ANGABE_WANN_DESCRIBER,
                    BEWOELKUNG_SATZ_DESCRIBER);

    // Wind-Describer
    private static final WindstaerkePraedikativumDescriber WINDSTAERKE_PRAEDIKATIVUM_DESCRIBER =
            new WindstaerkePraedikativumDescriber();

    private static final WindstaerkePraepPhrDescriber WINDSTAERKE_PRAEP_PHR_DESCRIBER =
            new WindstaerkePraepPhrDescriber(WINDSTAERKE_PRAEDIKATIVUM_DESCRIBER);

    private static final WindstaerkeAdvAngabeWoDescriber
            WINDSTAERKE_ADV_ANGABE_WO_DESCRIBER =
            new WindstaerkeAdvAngabeWoDescriber(
                    WINDSTAERKE_PRAEP_PHR_DESCRIBER);

    private static final WindstaerkeAdvAngabeWohinDescriber
            WINDSTAERKE_ADV_ANGABE_WOHIN_DESCRIBER =
            new WindstaerkeAdvAngabeWohinDescriber(
                    WINDSTAERKE_PRAEP_PHR_DESCRIBER);

    private static final WindstaerkeSatzDescriber WINDSTAERKE_SATZ_DESCRIBER =
            new WindstaerkeSatzDescriber(TAGESZEIT_ADV_ANGABE_WANN_DESCRIBER
            );

    private static final WindstaerkeDescDescriber WINDSTAERKE_DESC_DESCRIBER =
            new WindstaerkeDescDescriber(
                    TAGESZEIT_ADV_ANGABE_WANN_DESCRIBER, WINDSTAERKE_SATZ_DESCRIBER,
                    WINDSTAERKE_PRAEDIKATIVUM_DESCRIBER);


    // Wetterparameter etc.
    private final Temperatur tageshoechsttemperatur;

    private final Temperatur tagestiefsttemperatur;

    /**
     * Die Windstärke und offenem Himmel. Die Windstärke kann lokal niedriger sein, z.B.
     * an einem geschützten Ort draußen. Wir gehen außerdem davon aus, dass drinnen
     * kein Wind weht.
     */
    final Windstaerke windstaerkeUnterOffenemHimmel;

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
                LEICHT_BEWOELKT,
                BlitzUndDonner.KEIN_BLITZ_ODER_DONNER);
    }

    // FIXME altSparse(), wenn nicht für eine Kombination von
    //  Parametern eine leere Collection zurückkommt.
    //  Überall umstellen (Suchen nach of() und "leer"),
    //  Unnötige Kommentare entfernen

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

        final Windstaerke windstaerke = windstaerkeUnterOffenemHimmel
                .getLokaleWindstaerkeDraussen(unterOffenemHimmel);
        final Temperatur temperatur = getLokaleTemperatur(time, locationTemperaturRange);
        final boolean generelleTemperaturOutsideLocationTemperaturRange =
                !locationTemperaturRange.isInRange(getAktuelleGenerelleTemperatur(time));

        final boolean windMussBeschriebenWerden = windMussDraussenBeschriebenWerden(windstaerke);
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
                    generelleTemperaturOutsideLocationTemperaturRange,
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
            alt.addAll(WINDSTAERKE_DESC_DESCRIBER.alt(time, windstaerke));
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
            alt.addAll(altStatischWindUndTemperatur(time, unterOffenemHimmel,
                    auchEinmaligeErlebnisseNachTageszeitenwechselBeschreiben,
                    windstaerke, temperatur));
        }

        if (!windMussBeschriebenWerden && unterOffenemHimmel) {
            // Temperatur und Bewölkung werden beide erwähnt
            alt.addAll(altStatischBewoelkungUndTemperaturUnterOffenemHimmel(
                    time,
                    locationTemperaturRange,
                    auchEinmaligeErlebnisseNachTageszeitenwechselBeschreiben));
        }

        // Für den seltenen Fall windstaerkeMussBeschriebenWerden &&
        //  !temperaturMussBeschriebenWerden &&
        //  bewoelkungMussbeschriebenWerden sehen wir keine speziellen Beschreibungen vor.
        //  Dann werden Windstärke, Temperatur und Bewölkung eben alle beschrieben.

        if (unterOffenemHimmel) {
            // Wind, Temperatur und Bewölkung werden alle erwähnt
            alt.addAll(altStatischWindBewoelkungUndTemperaturUnterOffenemHimmel(
                    time,
                    windstaerke,
                    locationTemperaturRange,
                    generelleTemperaturOutsideLocationTemperaturRange,
                    auchEinmaligeErlebnisseNachTageszeitenwechselBeschreiben));
        }

        return alt.schonLaenger().build();
    }


    /**
     * Gibt alternative statische Beschreibungen von Wind, Bewölkung <i>und</i> Temperatur
     * zurück, wie man sie unter offenem Himmel erlebt
     *
     * @param auchEinmaligeErlebnisseNachTageszeitenwechselBeschreiben Ob auch Erlebnisse
     *                                                                 beschrieben werden sollen,
     *                                                                 die nach einem
     *                                                                 Tageszeitenwechsel nur
     *                                                                 einmalig auftreten
     */
    private ImmutableCollection<AbstractDescription<?>>
    altStatischWindBewoelkungUndTemperaturUnterOffenemHimmel(
            final AvTime time,
            final Windstaerke windstaerke,
            final EnumRange<Temperatur> locationTemperaturRange,
            final boolean generelleTemperaturOutsideLocationTemperaturRange,
            final boolean auchEinmaligeErlebnisseNachTageszeitenwechselBeschreiben) {
        final AltDescriptionsBuilder alt = alt();

        final Temperatur temperatur = getLokaleTemperatur(time, locationTemperaturRange);

        // "Es weht ein kühler Wind, der Himmel ist bewölkt"
        final ImmutableCollection<Satz> altStatischeWindUndTemperaturSaetze =
                altStatischeWindUndTemperaturSaetze(time, windstaerke, temperatur,
                        false);
        alt.addAll(altNeueSaetze(
                altStatischeWindUndTemperaturSaetze,
                ",",
                BEWOELKUNG_SATZ_DESCRIBER
                        .altUnterOffenemHimmel(bewoelkung, time,
                                auchEinmaligeErlebnisseNachTageszeitenwechselBeschreiben)));

        if (windstaerke.compareTo(WINDIG) <= 0) {
            alt.addAll(altGereihtStandard(
                    BEWOELKUNG_SATZ_DESCRIBER
                            .altUnterOffenemHimmel(bewoelkung, time,
                                    auchEinmaligeErlebnisseNachTageszeitenwechselBeschreiben),
                    altStatischeWindUndTemperaturSaetze));
        }

        switch (windstaerke) {
            case WINDSTILL:
                break;
            case LUEFTCHEN:
                break;
            case WINDIG:
                if (bewoelkung == LEICHT_BEWOELKT && time.getTageszeit() != NACHTS) {
                    alt.addAll(altNeueSaetze(
                            TEMPERATUR_DESC_DESCRIBER.alt(temperatur,
                                    generelleTemperaturOutsideLocationTemperaturRange,
                                    time, DRAUSSEN_UNTER_OFFENEM_HIMMEL,
                                    true),
                            SENTENCE,
                            "Weiße Wölkchen ziehen am blauen Himmel über dir vorbei"));
                }
                if (bewoelkung == BEWOELKT && time.getTageszeit() != NACHTS) {
                    alt.addAll(altNeueSaetze(
                            TEMPERATUR_DESC_DESCRIBER.alt(temperatur,
                                    generelleTemperaturOutsideLocationTemperaturRange,
                                    time, DRAUSSEN_UNTER_OFFENEM_HIMMEL,
                                    true),
                            SENTENCE,
                            "Dunkle Wolken ziehen am Himmel über dir vorbei"));
                }
                break;
            case KRAEFTIGER_WIND:
                if (bewoelkung.isBetweenIncluding(LEICHT_BEWOELKT, BEWOELKT)) {
                    alt.addAll(altNeueSaetze(
                            altStatischeWindUndTemperaturSaetze
                                    .stream().filter(s -> !s.isSatzreihungMitUnd()),
                            "und die Wolken ziehen ganz nah über deinem Haupt weg"));

                    // "Der kalte Wind treibt Wolkenfetzen über den Sternenhimmel"
                    alt.addAll(TEMPERATUR_PRAEDIKATIVUM_DESCRIBER
                            .altLuftAdjPhr(temperatur, time.getTageszeit()).stream()
                            .flatMap(kalt -> windstaerke.altNomenFlexionsspalte().stream()
                                    .flatMap(wind ->
                                            time.getTageszeit()
                                                    .altWolkenloserHimmel().stream()
                                                    .map(himmel -> TREIBEN.mit(WOLKENFETZEN)
                                                            .alsSatzMitSubjekt(wind.mit(kalt))
                                                            .mitAdvAngabe(
                                                                    new AdvAngabeSkopusVerbAllg(
                                                                            UEBER_AKK
                                                                                    .mit(himmel)))))));
                }
                break;
            case STURM:
                if (bewoelkung.compareTo(BEWOELKT) >= 0) {
                    alt.addAll(altNeueSaetze(
                            altStatischeWindUndTemperaturSaetze,
                            SENTENCE,
                            ImmutableList.of(
                                    "Hoffentlich bleibt es wenigstens trocken!",
                                    "Hoffentlich regnet es nicht auch noch!",
                                    "Hoffenlicht fängt nicht auch noch ein Platzregen an!"
                            )));
                }
                break;
            case SCHWERER_STURM:
                break;
            default:
                throw new IllegalStateException("Unexpected windstaerke: " + windstaerke);
        }

        return alt.schonLaenger().build();
    }

    private static ImmutableCollection<AbstractDescription<?>> altStatischWindUndTemperatur(
            final AvTime time, final boolean unterOffenemHimmel,
            final boolean auchEinmaligeErlebnisseDraussenNachTageszeitenwechselBeschreiben,
            final Windstaerke windstaerke, final Temperatur temperatur) {
        final AltDescriptionsBuilder alt = alt();

        // "Es ist kalt und windig"
        alt.addAll(altStatischeWindUndTemperaturSaetze(time, windstaerke, temperatur,
                false));

        // "Der Wind saust; die Luft ist kalt"
        alt.addAll(altNeueSaetze(
                WINDSTAERKE_SATZ_DESCRIBER.alt(time, windstaerke,
                        false, false),
                ";",
                TEMPERATUR_SATZ_DESCRIBER.alt(temperatur, time,
                        unterOffenemHimmel ? DRAUSSEN_UNTER_OFFENEM_HIMMEL : DRAUSSEN_GESCHUETZT,
                        auchEinmaligeErlebnisseDraussenNachTageszeitenwechselBeschreiben,
                        false)));

        // "Der Wind saust; kalt ist es"
        alt.addAll(altNeueSaetze(
                WINDSTAERKE_SATZ_DESCRIBER.alt(time, windstaerke,
                        false, false),
                ";",
                TEMPERATUR_PRAEDIKATIVUM_DESCRIBER.altAdjPhr(
                        temperatur, false).stream()
                        .map(kalt -> kalt.getPraedikativ(Personalpronomen.EXPLETIVES_ES)),
                "ist es"));

        final ImmutableList<AdjPhrOhneLeerstellen> adjPhrLuft =
                TEMPERATUR_PRAEDIKATIVUM_DESCRIBER.altLuftAdjPhr(temperatur, time.getTageszeit());

        switch (windstaerke) {
            case WINDSTILL:
                break;
            case LUEFTCHEN:
                alt.addAll(mapToSet(adjPhrLuft,
                        kalt -> altNeueSaetze("kaum bewegt sich", LUFT.mit(kalt).nomK())));
                break;
            case WINDIG:
                alt.addAll(mapToSet(adjPhrLuft,
                        kalt -> altNeueSaetze("es geht", np(INDEF, kalt, WIND).nomK())));
                alt.addAll(mapToSet(adjPhrLuft,
                        kalt -> altNeueSaetze("es weht", np(INDEF, kalt, WIND).nomK())));
                alt.addAll(mapToSet(adjPhrLuft,
                        kalt -> altNeueSaetze(kalt.alsAdvAngabeSkopusSatz().getDescription(WIND),
                                "geht", WIND.nomK())));
                break;
            case KRAEFTIGER_WIND:
                // "es geht ein kräftiger, kalter Wind"
                alt.addAll(mapToSet(adjPhrLuft,
                        kalt -> altNeueSaetze("es geht",
                                np(INDEF,
                                        new ZweiAdjPhrOhneLeerstellen(
                                                KRAEFTIG, true,
                                                kalt), WIND).nomK())));

                // "es weht ein kräftiger, kalter Wind"
                alt.addAll(mapToSet(adjPhrLuft,
                        kalt -> altNeueSaetze("es weht",
                                np(INDEF,
                                        new ZweiAdjPhrOhneLeerstellen(
                                                KRAEFTIG, true,
                                                kalt), WIND).nomK())));
                break;
            case STURM:
                break;
            case SCHWERER_STURM:
                // "Es tobt ein heftiges Unwetter. Kalt ist es"
                alt.addAll(altNeueSaetze(
                        "es tobt ein heftiges Unwetter",
                        SENTENCE,
                        TEMPERATUR_SATZ_DESCRIBER.alt(temperatur, time,
                                unterOffenemHimmel ? DRAUSSEN_UNTER_OFFENEM_HIMMEL :
                                        DRAUSSEN_GESCHUETZT,
                                auchEinmaligeErlebnisseDraussenNachTageszeitenwechselBeschreiben,
                                false)));

                alt.addAll(mapToSet(adjPhrLuft,
                        kalt -> altNeueSaetze("es braust",
                                np(INDEF,
                                        new ZweiAdjPhrOhneLeerstellen(
                                                HEFTIG,
                                                true,
                                                kalt),
                                        NomenFlexionsspalte.STURM).nomK(),
                                SENTENCE,
                                ImmutableList.of(
                                        "Nur mit Mühe kannst du dich auf den "
                                                + "Beinen halten",
                                        "Ein jeder Schritt kostet viel Kraft"
                                ))));
                break;
            default:
                throw new IllegalArgumentException("Unexpected Windstaerke");
        }

        return alt.schonLaenger().build();
    }

    /**
     * Gibt Sätze zu Wind und Temperatur zurück.
     */
    private static ImmutableCollection<Satz> altStatischeWindUndTemperaturSaetze(
            final AvTime time,
            final Windstaerke windstaerke,
            final Temperatur temperatur,
            final boolean nurFuerZusaetzlicheAdverbialerAngabeSkopusSatzGeeignete) {
        final ImmutableSet.Builder<Satz> alt = ImmutableSet.builder();

        final ImmutableList<AdjPhrOhneLeerstellen> tempAltAdjPhrPraedikativ =
                TEMPERATUR_PRAEDIKATIVUM_DESCRIBER.altAdjPhr(
                        temperatur, false);

        if (!nurFuerZusaetzlicheAdverbialerAngabeSkopusSatzGeeignete) {
            // "der Morgen ist kalt und windig"
            alt.addAll(windstaerke.altAdjPhrWetter().stream()
                    .flatMap(windig ->
                            tempAltAdjPhrPraedikativ.stream()
                                    .map(kalt -> new ZweiAdjPhrOhneLeerstellen(
                                            kalt, true,
                                            windig).alsPraedikativumPraedikat()
                                            .alsSatzMitSubjekt(
                                                    time.getTageszeit().getNomenFlexionsspalte())))
                    .collect(toImmutableSet()));
        }

        // "es ist kalt und windig"
        alt.addAll(windstaerke.altAdjPhrWetter().stream()
                .flatMap(windig ->
                        tempAltAdjPhrPraedikativ.stream()
                                .map(kalt -> new ZweiAdjPhrOhneLeerstellen(
                                        kalt, true,
                                        windig).alsPraedikativumPraedikat()
                                        .alsSatzMitSubjekt(EXPLETIVES_ES)))
                .collect(toImmutableSet()));

        // "Es ist kalt und der Wind stürmt"
        alt.addAll(tempAltAdjPhrPraedikativ.stream()
                .flatMap(kalt -> WINDSTAERKE_SATZ_DESCRIBER.alt(time, windstaerke,
                        nurFuerZusaetzlicheAdverbialerAngabeSkopusSatzGeeignete, false)
                        .stream()
                        .map(derWindStuermt -> new Satzreihe(
                                kalt.alsPraedikativumPraedikat()
                                        .alsSatzMitSubjekt(EXPLETIVES_ES),
                                derWindStuermt)))
                .collect(toSet()));

        final ImmutableList<AdjPhrOhneLeerstellen> tempAltAdjPhrAttributiv =
                TEMPERATUR_PRAEDIKATIVUM_DESCRIBER.altAdjPhr(
                        temperatur, true);

        switch (windstaerke) {
            case WINDSTILL:
                if (temperatur == Temperatur.SEHR_HEISS) {
                    alt.add(DRUECKEND.alsPraedikativumPraedikat().alsSatzMitSubjekt(HITZE));
                    if (time.gegenMittag()
                            && !nurFuerZusaetzlicheAdverbialerAngabeSkopusSatzGeeignete) {
                        alt.add(DRUECKEND.alsPraedikativumPraedikat()
                                .alsSatzMitSubjekt(MITTAGSHITZE));
                    }
                }

                break;
            case LUEFTCHEN:
                // "ein kühler Luftzug streicht dir durchs Haar"
                alt.addAll(mapToSet(tempAltAdjPhrAttributiv,
                        kuehl -> STREICHEN.mit(duSc()).mitAdvAngabe(
                                new AdvAngabeSkopusVerbAllg(DURCH.mit(HAAR)))
                                .alsSatzMitSubjekt(LUFTZUG.mit(kuehl))));
                break;
            case WINDIG:
                break;
            case KRAEFTIGER_WIND:
                // "kräftig weht ein kuehler Wind"
                if (!nurFuerZusaetzlicheAdverbialerAngabeSkopusSatzGeeignete) {
                    alt.addAll(mapToSet(tempAltAdjPhrAttributiv,
                            kuehl -> WEHEN.mitAdvAngabe(new AdvAngabeSkopusSatz(KRAEFTIG))
                                    .alsSatzMitSubjekt(np(INDEF, kuehl, WIND))));
                }
                alt.addAll(mapToSet(tempAltAdjPhrAttributiv,
                        kuehl -> PFEIFEN.mit(duSc()).mitAdvAngabe(
                                new AdvAngabeSkopusVerbAllg("ums Gesicht"))
                                .alsSatzMitSubjekt(np(INDEF, kuehl, WIND))));
                alt.addAll(mapToSet(tempAltAdjPhrAttributiv,
                        kuehl -> ZAUSEN.mit(DEIN_HAAR)
                                .alsSatzMitSubjekt(np(INDEF, kuehl, WIND))));
                if (temperatur.compareTo(KUEHL) <= 0
                        && !nurFuerZusaetzlicheAdverbialerAngabeSkopusSatzGeeignete) {
                    // "es ist ein rauer Morgen"
                    // (aber nicht ?"draußen ist es ein rauer Morgen"!)
                    alt.add(np(INDEF, RAU, time.getTageszeit().getNomenFlexionsspalte())
                            .alsEsIstSatz());
                }
                break;
            case STURM:
                alt.addAll(mapToSet(tempAltAdjPhrAttributiv,
                        kuehl -> STUERMEN
                                .mitAdvAngabe(new AdvAngabeSkopusSatz(kuehl))
                                .alsSatzMitSubjekt(WIND)));
                break;
            case SCHWERER_STURM:
                break;
            default:
                throw new IllegalArgumentException("Unexpected Windstaerke");
        }

        return alt.build();
    }

    // IDEA Man könnte (wo? / wie?) spezielle Beschreibungen dafür einauen, dass sich das SC
    //  im Wald befindet (oder allgemein in einer Landschaftsform???):
    //  - "als du siehst, wie die Sonnenstrahlen durch die Bäume hin- und hertanzen"
    //  - "durch die dichtbelaubten Äste dringt kein Sonnenstrahl"
    //  - "Als nun die Sonne mitten über dem Walde steht..." (eigentlich beschreiben
    //    wir im Wald - geschützt! - keine Sonne)
    //  - "der Wind raschelt in den Bäumen, und die Wolken ziehen ganz nah über deinem Haupt
    //  weg"

    /**
     * Gibt alternative Sätze zu Windgeräuschen zurück - kann leer sein.
     */
    ImmutableCollection<EinzelnerSatz> altWindgeraeuscheSaetze(
            final AvTime time, final boolean unterOffenemHimmel) {
        final Windstaerke windstaerke = windstaerkeUnterOffenemHimmel
                .getLokaleWindstaerkeDraussen(unterOffenemHimmel);

        return WINDSTAERKE_SATZ_DESCRIBER.alt(time, windstaerke,
                false,
                true);
    }

    // IDEA: Statische Beschreibungen von Wind / Sturm unter Bezug auf Features der Umwelt
    //  (Laub, Blätter, Bäume, Äste, Wald; etwas, das Schutz bietet)?
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

    // IDEA Bewölkung, Wind und Temperatur unter Bezug auf Features der Umwelt:
    //  "Die Sonne scheint hell, die Vögel singen, und ein kühles Lüftchen streicht durch das
    //   Laub..."

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
        alt.addAll(altGereihtStandard(
                TEMPERATUR_SATZ_DESCRIBER.alt(
                        temperatur, time, DRAUSSEN_UNTER_OFFENEM_HIMMEL,
                        auchEinmaligeErlebnisseNachTageszeitenwechselBeschreiben,
                        false).stream()
                        // Bandwurmsätze vermeiden - Ergebnis ist nicht leer!
                        .filter(EinzelnerSatz.class::isInstance)
                        .collect(toImmutableSet()),
                BEWOELKUNG_SATZ_DESCRIBER
                        .altUnterOffenemHimmel(bewoelkung, time,
                                auchEinmaligeErlebnisseNachTageszeitenwechselBeschreiben)));

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

        final Windstaerke windstaerke = windstaerkeUnterOffenemHimmel
                .getLokaleWindstaerkeDraussen(unterOffenenHimmel);
        final Temperatur temperatur = getLokaleTemperatur(time, locationTemperaturRange);
        final boolean generelleTemperaturOutsideLocationTemperaturRange =
                !locationTemperaturRange.isInRange(getAktuelleGenerelleTemperatur(time));

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
                    generelleTemperaturOutsideLocationTemperaturRange,
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
            alt.addAll(WINDSTAERKE_DESC_DESCRIBER.altKommtNachDraussen(time, windstaerke));
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
            alt.addAll(altKommtNachDraussenMitWindUndTemperatur(time, unterOffenenHimmel,
                    auchEinmaligeErlebnisseNachTageszeitenwechselBeschreiben,
                    windstaerke, temperatur));
        }

        if (!windMussBeschriebenWerden && unterOffenenHimmel) {
            // "Draußen ist es kühl und der Himmel ist bewölkt"
            alt.addAll(altKommtUnterOffenenHimmelMitBewoelkungUndTemperatur(
                    time, locationTemperaturRange,
                    auchEinmaligeErlebnisseNachTageszeitenwechselBeschreiben));
        }

        // Für den seltenen Fall windstaerkeMussBeschriebenWerden &&
        //  !temperaturMussBeschriebenWerden &&
        //  bewoelkungMussbeschriebenWerden sehen wir keine speziellen Beschreibungen vor.
        //  Dann werden Windstärke, Temperatur und Bewölkung eben alle beschrieben.

        if (unterOffenenHimmel) {
            // Wind, Temperatur und Bewölkung werden alle erwähnt

            alt.addAll(altKommtUnterOffenenHimmelMitWindBewoelkungUndTemperatur(
                    time,
                    windstaerke,
                    locationTemperaturRange,
                    generelleTemperaturOutsideLocationTemperaturRange,
                    auchEinmaligeErlebnisseNachTageszeitenwechselBeschreiben));
        }


        return alt.schonLaenger();
    }


    /**
     * Gibt alternative Beschreibungen von Wind, Bewölkung <i>und</i> Temperatur
     * zurück, wie man der SC sie erlebt, wenn er unter offenen Himmel tritt.
     *
     * @param auchEinmaligeErlebnisseNachTageszeitenwechselBeschreiben Ob auch Erlebnisse
     *                                                                 beschrieben werden sollen,
     *                                                                 die nach einem
     *                                                                 Tageszeitenwechsel nur
     *                                                                 einmalig auftreten
     */
    private ImmutableCollection<AbstractDescription<?>>
    altKommtUnterOffenenHimmelMitWindBewoelkungUndTemperatur(
            final AvTime time,
            final Windstaerke windstaerke,
            final EnumRange<Temperatur> locationTemperaturRange,
            final boolean generelleTemperaturOutsideLocationTemperaturRange,
            final boolean auchEinmaligeErlebnisseNachTageszeitenwechselBeschreiben) {
        final AltDescriptionsBuilder alt = alt();

        final Temperatur temperatur = getLokaleTemperatur(time, locationTemperaturRange);

        // "Draußen weht ein kühler Wind, der Himmel ist bewölkt"
        final ImmutableCollection<Satz> altStatischeWindUndTemperaturSaetze =
                altStatischeWindUndTemperaturSaetze(time, windstaerke, temperatur,
                        true);
        final ImmutableCollection<Satz> altKommtNachDraussenWindUndTemperaturSaetze =
                altStatischeWindUndTemperaturSaetze.stream()
                        .map(s -> s.mitAdvAngabe(new AdvAngabeSkopusSatz("draußen")))
                        .collect(toImmutableSet());

        alt.addAll(altNeueSaetze(
                altKommtNachDraussenWindUndTemperaturSaetze,
                ",",
                BEWOELKUNG_SATZ_DESCRIBER
                        .altUnterOffenemHimmel(bewoelkung, time,
                                auchEinmaligeErlebnisseNachTageszeitenwechselBeschreiben)));

        if (windstaerke.compareTo(WINDIG) <= 0) {
            alt.addAll(altGereihtStandard(
                    BEWOELKUNG_SATZ_DESCRIBER
                            .altKommtUnterOffenenHimmel(bewoelkung, time,
                                    true,
                                    auchEinmaligeErlebnisseNachTageszeitenwechselBeschreiben),
                    // "Verortung" mit "draußen" nur am Anfang
                    altStatischeWindUndTemperaturSaetze));
        }

        switch (windstaerke) {
            case WINDSTILL:
                break;
            case LUEFTCHEN:
                break;
            case WINDIG:
                if (bewoelkung == LEICHT_BEWOELKT && time.getTageszeit() != NACHTS) {
                    alt.addAll(altNeueSaetze(
                            TEMPERATUR_DESC_DESCRIBER.altKommtNachDraussen(temperatur,
                                    generelleTemperaturOutsideLocationTemperaturRange,
                                    time, true,
                                    true),
                            SENTENCE,
                            "Weiße Wölkchen ziehen am blauen Himmel über dir vorbei"));
                }
                if (bewoelkung == BEWOELKT && time.getTageszeit() != NACHTS) {
                    alt.addAll(altNeueSaetze(
                            TEMPERATUR_DESC_DESCRIBER.altKommtNachDraussen(temperatur,
                                    generelleTemperaturOutsideLocationTemperaturRange,
                                    time, true,
                                    true),
                            SENTENCE,
                            "Dunkle Wolken ziehen am Himmel über dir vorbei"));
                }
                if (bewoelkung == WOLKENLOS) {
                    // "Der Himmel ist blau und eine frische Luft weht dir entgegen"
                    alt.addAll(
                            time.getTageszeit().altAltAdjPhrWolkenloserHimmel().stream()
                                    .flatMap(blau ->
                                            TEMPERATUR_PRAEDIKATIVUM_DESCRIBER
                                                    .altLuftAdjPhr(temperatur,
                                                            time.getTageszeit()).stream()
                                                    .map(frisch ->
                                                            new Satzreihe(
                                                                    blau.alsPraedikativumPraedikat()
                                                                            .alsSatzMitSubjekt(
                                                                                    HIMMEL),
                                                                    ENTGEGENWEHEN
                                                                            .mit(duSc())
                                                                            .alsSatzMitSubjekt(
                                                                                    np(INDEF,
                                                                                            frisch,
                                                                                            LUFT)
                                                                            )))));
                }
                break;
            case KRAEFTIGER_WIND:
                if (bewoelkung.isBetweenIncluding(LEICHT_BEWOELKT, BEWOELKT)) {
                    alt.addAll(altNeueSaetze(
                            altKommtNachDraussenWindUndTemperaturSaetze,
                            SENTENCE,
                            "die Wolken ziehen ganz nah über deinem Haupt weg"));

                    // "Der kalte Wind treibt Wolkenfetzen über den Sternenhimmel"
                    alt.addAll(TEMPERATUR_PRAEDIKATIVUM_DESCRIBER
                            .altLuftAdjPhr(temperatur, time.getTageszeit()).stream()
                            .flatMap(kalt -> windstaerke.altNomenFlexionsspalte().stream()
                                    .flatMap(wind ->
                                            time.getTageszeit()
                                                    .altWolkenloserHimmel().stream()
                                                    .map(himmel -> TREIBEN.mit(WOLKENFETZEN)
                                                            .alsSatzMitSubjekt(wind.mit(kalt))
                                                            .mitAdvAngabe(
                                                                    new AdvAngabeSkopusVerbAllg(
                                                                            UEBER_AKK
                                                                                    .mit(himmel)))))));
                }
                break;
            case STURM:
                if (bewoelkung.compareTo(BEWOELKT) >= 0) {
                    alt.addAll(altNeueSaetze(
                            altKommtNachDraussenWindUndTemperaturSaetze,
                            SENTENCE,
                            ImmutableList.of(
                                    "Hoffentlich bleibt es wenigstens trocken!",
                                    "Hoffentlich regnet es nicht auch noch!",
                                    "Hoffenlicht fängt nicht auch noch ein Platzregen an!"
                            )));
                }
                break;
            case SCHWERER_STURM:
                break;
            default:
                throw new IllegalStateException("Unexpected windstaerke: " + windstaerke);
        }

        return alt.schonLaenger().build();
    }


    private static ImmutableCollection<AbstractDescription<?>>
    altKommtNachDraussenMitWindUndTemperatur(
            final AvTime time, final boolean unterOffenenHimmel,
            final boolean auchEinmaligeErlebnisseDraussenNachTageszeitenwechselBeschreiben,
            final Windstaerke windstaerke, final Temperatur temperatur) {
        final AltDescriptionsBuilder alt = alt();

        // "Draußen ist es kalt und windig"
        alt.addAll(mapToList(
                altStatischeWindUndTemperaturSaetze(time, windstaerke, temperatur,
                        true),
                s -> s.mitAdvAngabe((new AdvAngabeSkopusSatz("draußen")))));

        if (windstaerke.compareTo(Windstaerke.WINDIG) > 0) {
            // "Es ist kalt und windig"
            alt.addAll(altStatischeWindUndTemperaturSaetze(time, windstaerke, temperatur,
                    false));
        }

        // "Draußen saust der Wind; die Luft ist kalt"
        alt.addAll(altNeueSaetze(
                WINDSTAERKE_SATZ_DESCRIBER.alt(time, windstaerke,
                        true, false).stream()
                        .map(s -> s.mitAdvAngabe(new AdvAngabeSkopusSatz("draußen"))),
                ";",
                TEMPERATUR_SATZ_DESCRIBER.alt(temperatur, time,
                        unterOffenenHimmel ? DRAUSSEN_UNTER_OFFENEM_HIMMEL : DRAUSSEN_GESCHUETZT,
                        auchEinmaligeErlebnisseDraussenNachTageszeitenwechselBeschreiben,
                        false)));

        // "Draußen saust der Wind; kalt ist es"
        alt.addAll(altNeueSaetze(
                WINDSTAERKE_SATZ_DESCRIBER.alt(time, windstaerke,
                        true, false).stream()
                        .map(s -> s.mitAdvAngabe(new AdvAngabeSkopusSatz("draußen"))),
                ";",
                TEMPERATUR_PRAEDIKATIVUM_DESCRIBER.altAdjPhr(
                        temperatur, false).stream()
                        .map(kalt -> kalt.getPraedikativ(Personalpronomen.EXPLETIVES_ES)),
                "ist es"));

        final ImmutableList<AdjPhrOhneLeerstellen> adjPhrLuft =
                TEMPERATUR_PRAEDIKATIVUM_DESCRIBER.altLuftAdjPhr(temperatur, time.getTageszeit());

        switch (windstaerke) {
            case WINDSTILL:
                break;
            case LUEFTCHEN:
                break;
            case WINDIG:
                alt.addAll(mapToSet(adjPhrLuft,
                        kalt -> altNeueSaetze("draußen geht", np(INDEF, kalt, WIND).nomK())));
                alt.addAll(mapToSet(adjPhrLuft,
                        kalt -> altNeueSaetze("draußen weht", np(INDEF, kalt, WIND).nomK())));
                alt.addAll(mapToSet(adjPhrLuft,
                        kalt -> altNeueSaetze(kalt.alsAdvAngabeSkopusSatz().getDescription(WIND),
                                "geht draußen", WIND.nomK())));
                // "ein warmer Wind bläst dir entgegen"
                alt.addAll(TEMPERATUR_PRAEDIKATIVUM_DESCRIBER
                        .altLuftAdjPhr(temperatur, time.getTageszeit())
                        .stream()
                        .map(warm ->
                                ENTGEGENBLASEN
                                        .mit(duSc())
                                        .alsSatzMitSubjekt(np(INDEF, warm, WIND))));
                break;
            case KRAEFTIGER_WIND:
                // "draußen geht ein kräftiger, kalter Wind"
                alt.addAll(mapToSet(adjPhrLuft,
                        kalt -> altNeueSaetze("draußen geht",
                                np(INDEF,
                                        new ZweiAdjPhrOhneLeerstellen(
                                                KRAEFTIG, true,
                                                kalt), WIND).nomK())));

                // "draußen weht ein kräftiger, kalter Wind"
                alt.addAll(mapToSet(adjPhrLuft,
                        kalt -> altNeueSaetze("draußen weht",
                                np(INDEF,
                                        new ZweiAdjPhrOhneLeerstellen(
                                                KRAEFTIG, true,
                                                kalt), WIND).nomK())));
                break;
            case STURM:
                break;
            case SCHWERER_STURM:
                // "Draußen tobt ein heftiges Unwetter. Kalt ist es"
                alt.addAll(altNeueSaetze(
                        "draußen tobt ein heftiges Unwetter",
                        SENTENCE,
                        TEMPERATUR_SATZ_DESCRIBER.alt(temperatur, time,
                                unterOffenenHimmel ? DRAUSSEN_UNTER_OFFENEM_HIMMEL :
                                        DRAUSSEN_GESCHUETZT,
                                auchEinmaligeErlebnisseDraussenNachTageszeitenwechselBeschreiben,
                                false)));

                alt.addAll(mapToSet(adjPhrLuft,
                        kalt -> altNeueSaetze("draußen braust",
                                np(INDEF,
                                        new ZweiAdjPhrOhneLeerstellen(
                                                HEFTIG,
                                                true,
                                                kalt),
                                        NomenFlexionsspalte.STURM).nomK(),
                                SENTENCE,
                                "Nur mit Mühe kannst du dich auf den "
                                        + "Beinen halten")));
                break;
            default:
                throw new IllegalArgumentException("Unexpected Windstaerke");
        }

        return alt.schonLaenger().build();
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

        alt.addAll(altGereihtStandard(
                TEMPERATUR_SATZ_DESCRIBER.altKommtNachDraussen(temperatur, time,
                        true
                        , auchEinmaligeErlebnisseNachTageszeitenwechselBeschreiben).stream()
                        // Bandwurmsätze vermeiden - Ergebnis ist nicht leer!
                        .filter(EinzelnerSatz.class::isInstance)
                        .collect(toImmutableSet()),
                BEWOELKUNG_SATZ_DESCRIBER
                        .altUnterOffenemHimmel(bewoelkung, time,
                                auchEinmaligeErlebnisseNachTageszeitenwechselBeschreiben)));

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

    // FIXME Regelmäßig sollte es blitzen / donnern, ähnlich dem Müdigkeits-Konzept

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

        final Windstaerke windstaerke = windstaerkeUnterOffenemHimmel
                .getLokaleWindstaerkeDraussen(unterOffenemHimmel);
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
    altAngenehmereTemperaturOderWindAlsVorLocation(
            final AvTime time, final EnumRange<Temperatur> locationTemperaturRange,
            final int deltaTemperatur,
            @Nullable final Windstaerke windstaerkeFromSofernRelevant,
            @Nullable final Windstaerke windstaerkeTo) {
        checkArgument(windstaerkeTo == null
                        || windstaerkeFromSofernRelevant != null,
                "Wenn eine windstaerkeTo angegeben ist, muss auch eine "
                        + "(relevante) Windstärke-From angegeben sein");

        if (windstaerkeFromSofernRelevant != null) {
            // Wind-Beschreibung ist hier relevanter als Temperatur.
            // Lässt der Wind nach, merkt man eine gleichzeitig nachlassende
            // Temperatur gar nicht.
            if (windstaerkeTo == null) {
                return WINDSTAERKE_DESC_DESCRIBER.altKommtNachDrinnen(
                        time, windstaerkeFromSofernRelevant);
            }

            return WINDSTAERKE_DESC_DESCRIBER.altAngenehmerAlsVorLocation(
                    windstaerkeFromSofernRelevant, windstaerkeTo);
        }

        return alt()
                .addAll(TEMPERATUR_SATZ_DESCRIBER.altDeutlicherUnterschiedZuVorLocation(
                        getLokaleTemperatur(time, locationTemperaturRange),
                        deltaTemperatur))
                .schonLaenger()
                .build();
    }

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

        final Windstaerke windstaerke = windstaerkeUnterOffenemHimmel
                .getLokaleWindstaerkeDraussen(unterOffenenHimmel);
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
            alt.addAll(WINDSTAERKE_ADV_ANGABE_WOHIN_DESCRIBER.altWohinHinaus(windstaerke, time));
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

        if (!windMussBeschriebenWerden && unterOffenenHimmel) {
            // Temperatur und Bewölkung werden beide erwähnt
            alt.addAll(altWohinInBewoelkungUndTemperaturHinausUnterOffenenHimmel(
                    time, locationTemperaturRange));
        }

        // Für den seltenen Fall windstaerkeMussBeschriebenWerden &&
        //  !temperaturMussBeschriebenWerden &&
        //  bewoelkungMussbeschriebenWerden sehen wir keine speziellen Beschreibungen vor.
        //  Dann werden Windstärke, Temperatur und Bewölkung eben alle beschrieben.

        if (unterOffenenHimmel) {
            // Alles beschreiben: Wind, Temperatur und Bewölkung
            alt.addAll(altWohinInWindBewoelkungUndTemperaturHinausUnterOffenenHimmel(
                    time, windstaerke, locationTemperaturRange));
        }

        return alt.build();
    }

    @NonNull
    @CheckReturnValue
    private ImmutableCollection<AdvAngabeSkopusVerbWohinWoher>
    altWohinInWindBewoelkungUndTemperaturHinausUnterOffenenHimmel(
            final AvTime time, final Windstaerke windstaerke,
            final EnumRange<Temperatur> locationTemperaturRange) {
        final ImmutableSet.Builder<AdvAngabeSkopusVerbWohinWoher> alt = ImmutableSet.builder();

        final Temperatur temperatur = getLokaleTemperatur(time, locationTemperaturRange);

        alt.addAll(mapToSet(altWindBewoelkungUndTemperaturNominalphrasenUnterOffenemHimmel(
                time, windstaerke, locationTemperaturRange),
                np -> new AdvAngabeSkopusVerbWohinWoher(IN_AKK.mit(np))));

        alt.addAll(altWohinInWindUndTemperaturHinausPraepPhr(time, windstaerke, temperatur).stream()
                .flatMap(inDieKaelte ->
                        BEWOELKUNG_PRAEP_PHR_DESCRIBER.altUnterOffenenHimmelAkk(
                                bewoelkung, time.getTageszeit()).stream()
                                .filter(unterHimmel -> !unterHimmel.getDescription()
                                        .kommaStehtAus())
                                .map(unterHimmel ->
                                        // "in den kalten Wind unter den bewölkten Himmel"
                                        new AdvAngabeSkopusVerbWohinWoher(
                                                GermanUtil.joinToString(
                                                        inDieKaelte.getDescription(),
                                                        unterHimmel.getDescription()))))
                .collect(toSet()));

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


    private ImmutableSet<EinzelneSubstantivischePhrase>
    altWindBewoelkungUndTemperaturNominalphrasenUnterOffenemHimmel(
            final AvTime time, final Windstaerke windstaerke,
            final EnumRange<Temperatur> locationTemperaturRange) {
        final ImmutableSet.Builder<EinzelneSubstantivischePhrase> alt = ImmutableSet.builder();

        final Temperatur temperatur = getLokaleTemperatur(time, locationTemperaturRange);

        if (bewoelkung.isUnauffaellig(time.getTageszeit())
                && windstaerke == WINDSTILL) {
            if (temperatur.compareTo(Temperatur.RECHT_HEISS) == 0) {
                // "von der Sonne aufgeheizte stehende Luft"
                alt.add(VON_DER_SONNE_AUFGEHEIZTE_STEHENDE_LUFT);
            }

            if (temperatur.compareTo(Temperatur.SEHR_HEISS) == 0) {
                alt.add(BRUETENDE_HITZE_DER_SONNE, DRUECKENDE_HITZE_DER_SONNE);

                if (time.gegenMittag()) {
                    alt.add(BRUETENDE_HITZE_DER_MITTAGSSONNE, DRUECKENDE_HITZE_DER_MITTAGSSONNE);
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

        final Windstaerke windstaerke = windstaerkeUnterOffenemHimmel
                .getLokaleWindstaerkeDraussen(unterOffenemHimmel);
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
            alt.addAll(WINDSTAERKE_ADV_ANGABE_WO_DESCRIBER.altWoDraussen(windstaerke, time));
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

        if (!windMussBeschriebenWerden && unterOffenemHimmel) {
            // Temperatur und Bewölkung werden beide erwähnt
            alt.addAll(altWoInBewoelkungUndTemperaturUnterOffenemHimmel(
                    time, locationTemperaturRange));
        }

        // Für den seltenen Fall windstaerkeMussBeschriebenWerden &&
        //  !temperaturMussBeschriebenWerden &&
        //  bewoelkungMussbeschriebenWerden sehen wir keine speziellen Beschreibungen vor.
        //  Dann werden Windstärke, Temperatur und Bewölkung eben alle beschrieben.

        if (unterOffenemHimmel) {
            // Alles beschreiben: Wind, Temperatur und Bewölkung
            alt.addAll(altWoInWindBewoelkungUndTemperaturUnterOffenemHimmel(
                    time, windstaerke, locationTemperaturRange));
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
            default:
                throw new IllegalArgumentException("Unexpected Windstaerke: " + windstaerke);
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

    private ImmutableCollection<AdvAngabeSkopusVerbAllg>
    altWoInWindBewoelkungUndTemperaturUnterOffenemHimmel(
            final AvTime time,
            final Windstaerke windstaerke,
            final EnumRange<Temperatur> locationTemperaturRange) {
        final ImmutableSet.Builder<AdvAngabeSkopusVerbAllg> alt = ImmutableSet.builder();

        final Temperatur temperatur = getLokaleTemperatur(time, locationTemperaturRange);

        alt.addAll(mapToSet(altWindBewoelkungUndTemperaturNominalphrasenUnterOffenemHimmel(
                time, windstaerke, locationTemperaturRange),
                np -> new AdvAngabeSkopusVerbAllg(IN_DAT.mit(np))));

        alt.addAll(altWoInWindUndTemperaturPraepPhr(time, windstaerke, temperatur).stream()
                .flatMap(imKaltenWind ->
                        BEWOELKUNG_PRAEP_PHR_DESCRIBER.altUnterOffenemHimmelDat(
                                bewoelkung, time.getTageszeit()).stream()
                                .filter(unterHimmel -> !unterHimmel.getDescription()
                                        .kommaStehtAus())
                                .map(unterHimmel ->
                                        // "im kalten Wind unter dem bewölkten Himmel"
                                        new AdvAngabeSkopusVerbAllg(
                                                GermanUtil.joinToString(
                                                        imKaltenWind.getDescription(),
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

    // FIXME Donner / Blitz
    //  In der Ferne hörst du Donnergrollen
    //  Hat es eben geblitzt?

    // FIXME Automatisch generieren: "Das Wetter ist soundso und es passiert dies und das"
    //  -- "Die Sonne geht auf, und ..."
    //  -- "Die Sonne scheint hell, die Vögel singen, und ein kühles Lüftchen streicht durch das
    //      Laub, und du..."

    // FIXME Automatisch generieren:  "Ein Tageszeitenwechsel ist eingetreten: Es passiert dies
    //  und das"
    //  -- "Nun ist die Sonne unter:"

    // IDEA Automatisch generieren:  "Als (nun / wie nun) das Wetter soundso ist, passiert dies
    //  und das" (an sich nicht besonders kritisch, weil ohnehin nur Sätze derselben SC-Aktion
    //  miteinander verbunden werden):
    //   Automatisch erzeugen, wenn .als() dran steht?! Allerdings wiedersprechen sich
    //   .als() und schonLaenger(), vielleicht genügt also auch ein Fehlen von
    //   .schonLaenger() - dann wäre "du siehst die Sonne aufsteigen"
    //   praktisch eine Alternative zu "(Dann) steigt die Sonne auf" - wobei man das "dann"
    //   nicht schreiben darf, weil der Aktor vorher nicht der Mond ist...!
    //  -- "Als der Mond kommt,..."
    //  -- "Als du die Sonne aufsteigen siehst, ..."
    //  -- "Als / wie nun die Sonne über dir steht, "
    //  (Hier geht es um das Wetter, nicht um Zeitwechsel! Es ist etwas anderes als
    //   die aus dem TageszeitAdvAngabeWannDescriber gelieferten Angabensätze!)

    // IDEA Automatisch generieren: "Sobald das Wetter soundso ist, passsiert dies und das:"
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

    // FIXME automatisch generieren: "Du tust dies und jenes, folgende angenehme lokale
    //  Wetterveränderung zu nutzen":
    //  "Du gehst nach dem Wald zu, dort ein wenig Schutz vor dem Wetter zu suchen"

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

    // IDEA Man könnte Gegenstände in die Wetterbeschreibung einbauen
    //  "die goldene Kugel blitzt in der Sonne"
    //  "Die Abendsonne scheint über (die
    //   glänzenden Steine), sie schimmeren und leuchten so prächtig
    //   in allen Farben, dass..."

    // IDEA Sonne scheint durchs Fenster hinein
    //  - "Als nun die Sonne durchs Fensterlein scheint und..."

    // IDEA Wetterbeschreibungen als "Ortsangaben"
    //  "du liegst in der Sonne ausgestreckt"

    // IDEA Wetterbeschreibung als "Relativsatz" (im weiteren Sinne):
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

    // IDEA Jemand wärmt sich auf (z.B. am Feuer oder Drinnen)
    //  "du bist halb erfroren und willst dich nur ein wenig wärmen"
    //  "du reibst die Hände"
    //  "du bist so erfroren"
    //  "dich wärmen"
    //  "du erwärmst dich" (am Feuer)
    //  "so bist du vor der Kälte geschützt"

    // IDEA Du kommst in den Wald, und da es darin kühl und lieblich ist
    //  [lokale Temperatur] und die Sonne heiß brennt [generelle Temperatur], so..."

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
    public Windstaerke getWindstaerkeUnterOffenemHimmel() {
        return windstaerkeUnterOffenemHimmel;
    }

    // IDEA Wenn man drinnen ist und die Wände dünn: "Draußen rauscht der Wind"

    /**
     * Gibt alternative Beschreibungen zurück für den Fall, dass diese Zeit vergangen ist -
     * zuallermeist leer.
     *
     * @param windstaerkeChangeSofernRelevant Änderung der Windstärke, falls eine beschrieben
     *                                        werden soll, sonst {@code null}
     * @param temperaturChangeSofernRelevant  Temperaturänderung, falls eine beschrieben
     *                                        werden soll, sonst {@code null}
     * @param bewoelkungChangeSofernRelevant  Änderung der Bewölkung, falls eine beschrieben
     *                                        werden soll, sonst {@code null}
     */
    ImmutableCollection<AbstractDescription<?>> altTimePassed(
            final Change<AvDateTime> change,
            final boolean tageszeitaenderungMussBeschriebenWerden,
            final boolean generelleTemperaturOutsideLocationTemperaturRange,
            @Nullable final WetterParamChange<Windstaerke> windstaerkeChangeSofernRelevant,
            @Nullable final WetterParamChange<Temperatur> temperaturChangeSofernRelevant,
            @Nullable final WetterParamChange<Bewoelkung> bewoelkungChangeSofernRelevant,
            final DrinnenDraussen drinnenDraussen) {
        checkArgument(bewoelkungChangeSofernRelevant == null
                        || drinnenDraussen.isDraussen(),
                "Bewölkungsänderungen werden nur draußen erzählt");
        checkArgument(windstaerkeChangeSofernRelevant == null
                        || drinnenDraussen.isDraussen(),
                "Windstärke-Änderungen werden nur draußen erzählt");

        if (!tageszeitaenderungMussBeschriebenWerden) {
            // Es soll keine Tageszeitänderung beschrieben werden
            return altTimePassedTageszeitenaenderungNichtBeschreiben(change,
                    windstaerkeChangeSofernRelevant, temperaturChangeSofernRelevant,
                    bewoelkungChangeSofernRelevant,
                    drinnenDraussen, drinnenDraussen.isDraussen());
        }

        // Es soll eine Tageszeitänderung beschrieben werden
        return altTimePassedTageszeitenaenderung(change,
                generelleTemperaturOutsideLocationTemperaturRange,
                windstaerkeChangeSofernRelevant, temperaturChangeSofernRelevant,
                bewoelkungChangeSofernRelevant,
                drinnenDraussen);
    }

    @VisibleForTesting
    static ImmutableCollection<AbstractDescription<?>>
    altTimePassedTageszeitenaenderungNichtBeschreiben(
            final Change<AvDateTime> change,
            @Nullable final WetterParamChange<Windstaerke> windstaerkeChangeSofernRelevant,
            @Nullable final WetterParamChange<Temperatur> temperaturChangeSofernRelevant,
            @Nullable final WetterParamChange<Bewoelkung> bewoelkungChangeSofernRelevant,
            final DrinnenDraussen drinnenDraussen,
            final boolean auchZeitwechselreferenzen) {
        final boolean windstaerkeaenderungMussBeschriebenWerden =
                windstaerkeChangeSofernRelevant != null;

        final boolean temperaturaenderungMussBeschriebenWerden =
                temperaturChangeSofernRelevant != null;

        final boolean bewoelkungsaenderungMussBeschriebenWerden =
                bewoelkungChangeSofernRelevant != null;

        if (!windstaerkeaenderungMussBeschriebenWerden
                && !temperaturaenderungMussBeschriebenWerden
                && !bewoelkungsaenderungMussBeschriebenWerden) {
            // Nichts beschreiben - außer höchtestens untertägigem Tageszeitenwechsel
            if (span(change).shorterThan(AvTimeSpan.ONE_DAY)
                    && change.getVorher().getTageszeit() == change.getNachher().getTageszeit()) {
                return TAGESZEIT_DESC_DESCRIBER.altZwischentageszeitlicherWechsel(
                        change.map(AvDateTime::getTime),
                        drinnenDraussen.isDraussen());
            }

            return ImmutableList.of();
        }

        if (!windstaerkeaenderungMussBeschriebenWerden
                && temperaturaenderungMussBeschriebenWerden
                && !bewoelkungsaenderungMussBeschriebenWerden) {
            // Nur Temperatur beschreiben
            return TEMPERATUR_DESC_DESCRIBER.altSprungOderWechsel(
                    change, temperaturChangeSofernRelevant, drinnenDraussen,
                    drinnenDraussen.isDraussen());
            // IDEA  "Die Sonne hat die Erde aufgetaut" (bei (aktuell) unauffälliger Bewölkung)
        }

        if (!windstaerkeaenderungMussBeschriebenWerden
                && !temperaturaenderungMussBeschriebenWerden) {
            // Nur Bewölkung beschreiben
            return BEWOELKUNG_DESC_DESCRIBER.altSprungOderWechselDraussen(
                    change, bewoelkungChangeSofernRelevant,
                    drinnenDraussen == DRAUSSEN_UNTER_OFFENEM_HIMMEL,
                    auchZeitwechselreferenzen && drinnenDraussen.isDraussen());
        }

        if (windstaerkeaenderungMussBeschriebenWerden
                && !temperaturaenderungMussBeschriebenWerden
                && !bewoelkungsaenderungMussBeschriebenWerden) {
            // Nur Windstärkeänderung beschreiben
            return WINDSTAERKE_DESC_DESCRIBER.altSprungOderWechsel(
                    change, windstaerkeChangeSofernRelevant,
                    auchZeitwechselreferenzen && drinnenDraussen.isDraussen());
        }

        if (windstaerkeaenderungMussBeschriebenWerden
                && temperaturaenderungMussBeschriebenWerden
                && !bewoelkungsaenderungMussBeschriebenWerden) {
            return alt()
                    .addAll(WINDSTAERKE_SATZ_DESCRIBER.altSprungOderWechsel(
                            change, windstaerkeChangeSofernRelevant,
                            auchZeitwechselreferenzen && drinnenDraussen.isDraussen()).stream()
                            .flatMap(windSatz ->
                                    TEMPERATUR_SATZ_DESCRIBER.altSprungOderWechsel(
                                            change, temperaturChangeSofernRelevant,
                                            drinnenDraussen,
                                            false)
                                            .stream()
                                            // Bandwurmsätze vermeiden
                                            .filter(EinzelnerSatz.class::isInstance)
                                            .map(tempSatz ->
                                                    new Satzreihe(
                                                            windSatz,
                                                            (EinzelnerSatz) tempSatz)))
                            .collect(toImmutableSet()))
                    .addAll(altNeueSaetze(WINDSTAERKE_DESC_DESCRIBER.altSprungOderWechsel(
                            change, windstaerkeChangeSofernRelevant,
                            auchZeitwechselreferenzen && drinnenDraussen.isDraussen()),
                            SENTENCE,
                            TEMPERATUR_DESC_DESCRIBER.altSprungOderWechsel(
                                    change, temperaturChangeSofernRelevant,
                                    drinnenDraussen,
                                    false)))
                    .addAll(altNeueSaetze("Das Wetter ändert sich:",
                            SENTENCE,
                            WINDSTAERKE_DESC_DESCRIBER.altSprungOderWechsel(
                                    change,
                                    windstaerkeChangeSofernRelevant,
                                    false),
                            SENTENCE,
                            TEMPERATUR_DESC_DESCRIBER.altSprungOderWechsel(
                                    change, temperaturChangeSofernRelevant,
                                    drinnenDraussen,
                                    false)))
                    .build();
        }

        if (windstaerkeaenderungMussBeschriebenWerden
                && !temperaturaenderungMussBeschriebenWerden) {
            // Nur Windstärkeänderung und Bewölungsänderung beschreiben
            return alt()
                    .addAll(WINDSTAERKE_SATZ_DESCRIBER.altSprungOderWechsel(
                            change, windstaerkeChangeSofernRelevant,
                            auchZeitwechselreferenzen && drinnenDraussen.isDraussen())
                            .stream()
                            .flatMap(windSatz ->
                                    BEWOELKUNG_SATZ_DESCRIBER
                                            .altSprungOderWechselUnterOffenemHimmel(
                                                    change,
                                                    bewoelkungChangeSofernRelevant,
                                                    false)
                                            .stream()
                                            // Bandwurmsätze vermeiden
                                            .filter(EinzelnerSatz.class::isInstance)
                                            .map(bewSatz ->
                                                    new Satzreihe(
                                                            windSatz,
                                                            (EinzelnerSatz) bewSatz)))
                            .collect(toImmutableSet()))
                    .addAll(altNeueSaetze(
                            WINDSTAERKE_DESC_DESCRIBER.altSprungOderWechsel(
                                    change,
                                    windstaerkeChangeSofernRelevant,
                                    auchZeitwechselreferenzen && drinnenDraussen.isDraussen()),
                            SENTENCE,
                            BEWOELKUNG_DESC_DESCRIBER.altSprungOderWechselDraussen(
                                    change,
                                    bewoelkungChangeSofernRelevant,
                                    drinnenDraussen ==
                                            DRAUSSEN_UNTER_OFFENEM_HIMMEL,
                                    false)))
                    .build();
        }

        if (!windstaerkeaenderungMussBeschriebenWerden) {
            // Nur Temperaturänderung und Bewölkungsänderung beschreiben

            return alt()
                    .addAll(TEMPERATUR_SATZ_DESCRIBER.altSprungOderWechsel(
                            change,
                            temperaturChangeSofernRelevant,
                            drinnenDraussen,
                            auchZeitwechselreferenzen && drinnenDraussen.isDraussen())
                            .stream()
                            .filter(EinzelnerSatz.class::isInstance)
                            .flatMap(tempSatz ->
                                    BEWOELKUNG_SATZ_DESCRIBER
                                            .altSprungOderWechselUnterOffenemHimmel(
                                                    change,
                                                    bewoelkungChangeSofernRelevant,
                                                    false)
                                            .stream()
                                            // Bandwurmsätze vermeiden
                                            .filter(EinzelnerSatz.class::isInstance)
                                            .map(bewSatz ->
                                                    new Satzreihe(
                                                            tempSatz,
                                                            (EinzelnerSatz) bewSatz)))
                            .collect(toImmutableSet()))
                    .addAll(altNeueSaetze(
                            TEMPERATUR_DESC_DESCRIBER.altSprungOderWechsel(
                                    change,
                                    temperaturChangeSofernRelevant,
                                    drinnenDraussen, true),
                            SENTENCE,
                            BEWOELKUNG_DESC_DESCRIBER.altSprungOderWechselDraussen(
                                    change,
                                    bewoelkungChangeSofernRelevant,
                                    drinnenDraussen ==
                                            DRAUSSEN_UNTER_OFFENEM_HIMMEL,
                                    false)))
                    .build();
        }

        return alt()
                .addAll(altNeueSaetze("Das Wetter ändert sich:",
                        SENTENCE,
                        WINDSTAERKE_DESC_DESCRIBER.altSprungOderWechsel(
                                change, windstaerkeChangeSofernRelevant,
                                false),
                        SENTENCE,
                        TEMPERATUR_SATZ_DESCRIBER.altSprungOderWechsel(
                                change,
                                temperaturChangeSofernRelevant,
                                drinnenDraussen, false)
                                .stream()
                                .filter(EinzelnerSatz.class::isInstance)
                                .flatMap(tempSatz ->
                                        BEWOELKUNG_SATZ_DESCRIBER
                                                .altSprungOderWechselUnterOffenemHimmel(
                                                        change,
                                                        bewoelkungChangeSofernRelevant,
                                                        false)
                                                .stream()
                                                // Bandwurmsätze vermeiden
                                                .filter(EinzelnerSatz.class::isInstance)
                                                .map(bewSatz ->
                                                        new Satzreihe(
                                                                tempSatz,
                                                                (EinzelnerSatz) bewSatz)))))
                .addAll(altNeueSaetze("Das Wetter ist umgeschwungen:",
                        SENTENCE,
                        WINDSTAERKE_SATZ_DESCRIBER.altSprungOderWechsel(
                                change, windstaerkeChangeSofernRelevant,
                                false).stream()
                                .flatMap(windSatz ->
                                        TEMPERATUR_SATZ_DESCRIBER.altSprungOderWechsel(
                                                change, temperaturChangeSofernRelevant,
                                                drinnenDraussen,
                                                false)
                                                .stream()
                                                .filter(EinzelnerSatz.class::isInstance)
                                                .map(tempSatz ->
                                                        new Satzreihe(
                                                                (EinzelnerSatz) windSatz,
                                                                (EinzelnerSatz) tempSatz))),
                        SENTENCE,
                        BEWOELKUNG_DESC_DESCRIBER.altSprungOderWechselDraussen(
                                change, bewoelkungChangeSofernRelevant,
                                drinnenDraussen == DRAUSSEN_UNTER_OFFENEM_HIMMEL,
                                false)))
                .build();
    }

    @VisibleForTesting
    ImmutableCollection<AbstractDescription<?>> altTimePassedTageszeitenaenderung(
            final Change<AvDateTime> change,
            final boolean generelleTemperaturOutsideLocationTemperaturRange,
            @Nullable final WetterParamChange<Windstaerke> windstaerkeChangeSofernRelevant,
            @Nullable final WetterParamChange<Temperatur> temperaturChangeSofernRelevant,
            @Nullable final WetterParamChange<Bewoelkung> bewoelkungChangeSofernRelevant,
            final DrinnenDraussen drinnenDraussen) {
        // FIXME Gegen Mitternacht... (Wenn nicht schon eingebaut!)

        final boolean windstaerkeaenderungMussBeschriebenWerden =
                windstaerkeChangeSofernRelevant != null;

        final boolean bewoelkungsaenderungMussBeschriebenWerden =
                bewoelkungChangeSofernRelevant != null;


        if (!bewoelkungsaenderungMussBeschriebenWerden
                && !windstaerkeaenderungMussBeschriebenWerden) {

            // Nur Tageszeitenänderung und - evtl. - Temperaturänderung beschreiben
            return altTageszeitenUndEvtlTemperaturaenderung(change.getVorher().getTageszeit(),
                    change.getNachher().getTime(),
                    generelleTemperaturOutsideLocationTemperaturRange,
                    temperaturChangeSofernRelevant != null ?
                            temperaturChangeSofernRelevant.getNachher() : null,
                    drinnenDraussen);
        }

        if (!windstaerkeaenderungMussBeschriebenWerden) {
            // Bewölkung und Tageszeitenänderung und - evtl. - Temperaturänderung
            // beschreiben
            return alt()
                    .addAll(altNeueSaetze(
                            // FIXME Vielleicht in altNeueSaetze bei jeder neuen Anfühgung etwas
                            //  einbauen wie "chooseBest()" - und sehr schlechte ausschließen?!
                            //  Es wäre schön, "Jetzt... Jetzt.." zu vermeiden...
                            //  (Vielleicht gibt es dafür auch bessere Ideen....)
                            altTageszeitenUndEvtlTemperaturaenderung(
                                    change.getVorher().getTageszeit(),
                                    change.getNachher().getTime(),
                                    generelleTemperaturOutsideLocationTemperaturRange,
                                    temperaturChangeSofernRelevant != null ?
                                            temperaturChangeSofernRelevant.getNachher() :
                                            null,
                                    drinnenDraussen),
                            SENTENCE,
                            BEWOELKUNG_DESC_DESCRIBER.altSprungOderWechselDraussen(
                                    change,
                                    bewoelkungChangeSofernRelevant,
                                    drinnenDraussen ==
                                            DRAUSSEN_UNTER_OFFENEM_HIMMEL, false)
                    )).build();
        }

        // Tageszeitenänderung, Windstärkeänderung, Bewölkung und - evtl. - Temperaturänderung
        // beschreiben
        return alt()
                .addAll(altNeueSaetze(
                        TAGESZEIT_DESC_DESCRIBER.altSprungOderWechsel(
                                change.map(AvDateTime::getTageszeit),
                                drinnenDraussen.isDraussen()),
                        SENTENCE,
                        altTimePassedTageszeitenaenderungNichtBeschreiben(
                                change,
                                windstaerkeChangeSofernRelevant,
                                temperaturChangeSofernRelevant,
                                bewoelkungChangeSofernRelevant, drinnenDraussen,
                                false)
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

    // FIXME WetterRections.onBlitzOderDonner(IN_DER_FERNE / MIT_BLITZ)
    //  (Rapunzel zuckt zusammmen und bekommt Angst)

    // IDEA Wetter beeinflusst Stimmung von SC, Rapunzel, Zauberin (Listener-Konzept:
    //  onWetterwechsel()? onTemperaturWechsel()? WetterReactions.onChange()?)
    //  - Müde von der Hitze werden:
    //    "von der Hitze des Tages ermüdet"
    //    "du bist von der Sonnenhitze müde"
    //   "Wie nun zu Mittag die Sonne heiß brennt, wird dir so warm und verdrießlich zumut"
    //   "du bist von der Sonnenhitze müde"
    //  - Gutes Wetter -> gute Laune:
    //    "Die Sonne scheint hell, ein ... kühles Lüftchen streicht.. und du bist voll Freude und
    //    Lust."
}