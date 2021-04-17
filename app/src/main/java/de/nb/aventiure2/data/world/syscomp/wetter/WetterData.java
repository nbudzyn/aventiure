package de.nb.aventiure2.data.world.syscomp.wetter;

import androidx.annotation.NonNull;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

import javax.annotation.CheckReturnValue;
import javax.annotation.concurrent.Immutable;

import de.nb.aventiure2.data.time.AvTime;
import de.nb.aventiure2.data.time.Tageszeit;
import de.nb.aventiure2.data.world.syscomp.storingplace.DrinnenDraussen;
import de.nb.aventiure2.data.world.syscomp.wetter.bewoelkung.Bewoelkung;
import de.nb.aventiure2.data.world.syscomp.wetter.bewoelkung.BewoelkungAdvAngabeWohinDescriber;
import de.nb.aventiure2.data.world.syscomp.wetter.bewoelkung.BewoelkungDescDescriber;
import de.nb.aventiure2.data.world.syscomp.wetter.bewoelkung.BewoelkungPraedikativumDescriber;
import de.nb.aventiure2.data.world.syscomp.wetter.bewoelkung.BewoelkungPraepPhrDescriber;
import de.nb.aventiure2.data.world.syscomp.wetter.bewoelkung.BewoelkungSatzDescriber;
import de.nb.aventiure2.data.world.syscomp.wetter.blitzunddonner.BlitzUndDonner;
import de.nb.aventiure2.data.world.syscomp.wetter.tageszeit.TageszeitAdvAngabeWohinDescriber;
import de.nb.aventiure2.data.world.syscomp.wetter.tageszeit.TageszeitDescDescriber;
import de.nb.aventiure2.data.world.syscomp.wetter.tageszeit.TageszeitPraedikativumDescriber;
import de.nb.aventiure2.data.world.syscomp.wetter.tageszeit.TageszeitSatzDescriber;
import de.nb.aventiure2.data.world.syscomp.wetter.temperatur.TagestemperaturverlaufUtil;
import de.nb.aventiure2.data.world.syscomp.wetter.temperatur.Temperatur;
import de.nb.aventiure2.data.world.syscomp.wetter.temperatur.TemperaturAdvAngabeWohinDescriber;
import de.nb.aventiure2.data.world.syscomp.wetter.temperatur.TemperaturDescDescriber;
import de.nb.aventiure2.data.world.syscomp.wetter.temperatur.TemperaturPraedikativumDescriber;
import de.nb.aventiure2.data.world.syscomp.wetter.temperatur.TemperaturSatzDescriber;
import de.nb.aventiure2.data.world.syscomp.wetter.windstaerke.Windstaerke;
import de.nb.aventiure2.german.base.EinzelneSubstantivischePhrase;
import de.nb.aventiure2.german.base.Praepositionalphrase;
import de.nb.aventiure2.german.description.AbstractDescription;
import de.nb.aventiure2.german.description.AltDescriptionsBuilder;
import de.nb.aventiure2.german.praedikat.AdvAngabeSkopusVerbWohinWoher;
import de.nb.aventiure2.german.satz.EinzelnerSatz;
import de.nb.aventiure2.german.satz.Satz;

import static com.google.common.base.Preconditions.checkArgument;
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
import static de.nb.aventiure2.german.base.Nominalphrase.np;
import static de.nb.aventiure2.german.base.PraepositionMitKasus.IN_AKK;
import static de.nb.aventiure2.german.base.PraepositionMitKasus.IN_DAT;
import static de.nb.aventiure2.german.description.AltDescriptionsBuilder.alt;
import static de.nb.aventiure2.german.description.AltDescriptionsBuilder.altNeueSaetze;
import static de.nb.aventiure2.util.StreamUtil.*;

@Immutable
class WetterData {
    // Tageszeit-Describer
    private static final TageszeitPraedikativumDescriber TAGESZEIT_PRAEDIKATIVUM_DESCRIBER =
            new TageszeitPraedikativumDescriber();

    private static final TageszeitSatzDescriber TAGESZEIT_SATZ_DESCRIBER =
            new TageszeitSatzDescriber(TAGESZEIT_PRAEDIKATIVUM_DESCRIBER);

    private static final TageszeitDescDescriber TAGESZEIT_DESC_DESCRIBER =
            new TageszeitDescDescriber(TAGESZEIT_SATZ_DESCRIBER);

    private static final TageszeitAdvAngabeWohinDescriber
            TAGESZEIT_ADV_ANGABE_WOHIN_DESCRIBER =
            new TageszeitAdvAngabeWohinDescriber(TAGESZEIT_PRAEDIKATIVUM_DESCRIBER);

    // Temperatur-Describer
    private static final TemperaturPraedikativumDescriber TEMPERATUR_PRAEDIKATIVUM_DESCRIBER =
            new TemperaturPraedikativumDescriber();

    private static final TemperaturAdvAngabeWohinDescriber
            TEMPERATUR_ADV_ANGABE_WOHIN_DESCRIBER =
            new TemperaturAdvAngabeWohinDescriber(
                    TEMPERATUR_PRAEDIKATIVUM_DESCRIBER);

    private static final TemperaturSatzDescriber TEMPERATUR_SATZ_DESCRIBER =
            new TemperaturSatzDescriber(
                    TAGESZEIT_PRAEDIKATIVUM_DESCRIBER,
                    TEMPERATUR_PRAEDIKATIVUM_DESCRIBER);

    private static final TemperaturDescDescriber TEMPERATUR_DESC_DESCRIBER =
            new TemperaturDescDescriber(TEMPERATUR_SATZ_DESCRIBER);

    // Bewölkung-Describer
    private static final BewoelkungPraedikativumDescriber BEWOELKUNG_PRAEDIKATIVUM_DESCRIBER =
            new BewoelkungPraedikativumDescriber();

    private static final BewoelkungPraepPhrDescriber BEWOELKUNG_PRAEP_PHR_DESCRIBER =
            new BewoelkungPraepPhrDescriber(BEWOELKUNG_PRAEDIKATIVUM_DESCRIBER);

    private static final BewoelkungAdvAngabeWohinDescriber
            BEWOELKUNG_ADV_ANGABE_WOHIN_DESCRIBER =
            new BewoelkungAdvAngabeWohinDescriber(
                    BEWOELKUNG_PRAEDIKATIVUM_DESCRIBER);

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

    /**
     * Gibt alternative Beschreibungen des Wetters zurück, wie man es draußen erlebt
     *
     * @param auchEinmaligeErlebnisseNachTageszeitenwechselBeschreiben Ob auch Erlebnisse
     *                                                                 beschrieben werden sollen,
     *                                                                 die nach einem
     *                                                                 Tageszeitenwechsel nur
     *                                                                 einmalig auftreten
     */
    ImmutableCollection<AbstractDescription<?>> altWetterHinweiseFuerDraussen(
            final AvTime time, final boolean unterOffenemHimmel,
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

        final Temperatur temperatur = getTemperatur(time);

        if (tageszeitUndLichtverhaeltnisseGenuegen(time, unterOffenemHimmel,
                temperatur)) {
            // "es ist schon dunkel", "es ist Abend"
            alt.addAll(TAGESZEIT_SATZ_DESCRIBER.altDraussen(time));
        }

        if (temperaturUndEvtlTageszeitUndLichtverhaeltnisseGenuegen(time, unterOffenemHimmel)) {
            // Bewölkung muss nicht erwähnt werden

            // "Es ist kühl"
            alt.addAll(TEMPERATUR_DESC_DESCRIBER.alt(temperatur, time, drinnenDraussen));

            if (bewoelkung.isUnauffaellig(time.getTageszeit())) {
                // "Es ist ein schöner Abend und noch ziemlich warm"
                alt.addAll(
                        alt.addAll(
                                TEMPERATUR_SATZ_DESCRIBER
                                        .altSchoneTageszeitUndAberSchonNochAdjPhr(
                                                temperatur, time.getTageszeit())));
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
                    time, auchEinmaligeErlebnisseNachTageszeitenwechselBeschreiben));
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
            final boolean auchEinmaligeErlebnisseNachTageszeitenwechselBeschreiben) {
        final AltDescriptionsBuilder alt = alt();

        final Temperatur temperatur = getTemperatur(time);

        // "Es ist kühl und der Himmel ist bewölkt"
        alt.addAll(altNeueSaetze(
                TEMPERATUR_SATZ_DESCRIBER.alt(
                        temperatur, time, DRAUSSEN_UNTER_OFFENEM_HIMMEL).stream()
                        // Bandwurmsätze vermeiden - Ergebnis ist nicht leer!
                        .filter(EinzelnerSatz.class::isInstance),
                BEWOELKUNG_SATZ_DESCRIBER
                        .altUnterOffenemHimmel(bewoelkung, time,
                                auchEinmaligeErlebnisseNachTageszeitenwechselBeschreiben).stream()
                        // mehrfaches "und" vermeiden
                        .map(Satz::mitAnschlusswortUndSofernNichtSchonEnthalten)));

        final ImmutableCollection<Satz> heuteOderDerTagSaetze =
                TEMPERATUR_SATZ_DESCRIBER
                        .altDraussenHeuteDerTagSofernSinnvoll(temperatur, time,
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
                        TEMPERATUR_SATZ_DESCRIBER.altDraussenNoch(temperatur)));
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
     * man sie erlebt, wenn man nach draußen kommt
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

        final Temperatur temperatur = getTemperatur(time);

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
                    temperatur, time, unterOffenenHimmel));

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
                    time, auchEinmaligeErlebnisseNachTageszeitenwechselBeschreiben));
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
            final boolean auchEinmaligeErlebnisseNachTageszeitenwechselBeschreiben) {
        final AltDescriptionsBuilder alt = alt();

        final Temperatur temperatur = getTemperatur(time);

        alt.addAll(altNeueSaetze(
                TEMPERATUR_SATZ_DESCRIBER.altKommtNachDraussen(temperatur, time,
                        true).stream()
                        // Bandwurmsätze vermeiden - Ergebnis ist nicht leer!
                        .filter(EinzelnerSatz.class::isInstance),
                BEWOELKUNG_SATZ_DESCRIBER
                        .altUnterOffenemHimmel(bewoelkung, time,
                                auchEinmaligeErlebnisseNachTageszeitenwechselBeschreiben)
                        .stream()
                        .map(Satz::mitAnschlusswortUndSofernNichtSchonEnthalten)));

        final ImmutableCollection<Satz> heuteOderDerTagSaetze = TEMPERATUR_SATZ_DESCRIBER
                .altDraussenHeuteDerTagSofernSinnvoll(temperatur, time,
                        false);
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
                        TEMPERATUR_SATZ_DESCRIBER.altDraussenNoch(temperatur)));
            }
        }

        return alt.schonLaenger();
    }

    @CheckReturnValue
    ImmutableCollection<Satz> altStatischeTemperaturBeschreibungSaetze(
            final AvTime time,
            final DrinnenDraussen drinnenDraussen) {
        return TEMPERATUR_SATZ_DESCRIBER.alt(getTemperatur(time), time, drinnenDraussen);
    }

    /**
     * Gibt alternative Beschreibungen für einen Tageszeitensprung oder -wechsel zurück.
     */
    @NonNull
    @CheckReturnValue
    ImmutableCollection<AbstractDescription<?>> altTageszeitensprungOderWechsel(
            final Tageszeit lastTageszeit,
            final Tageszeit currentTageszeit,
            final DrinnenDraussen drinnenDraussen) {
        checkArgument(lastTageszeit != currentTageszeit,
                "Keine Tageszeitensprung oder -wechsel: " + lastTageszeit);

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
            //  Abendrot zu sehen" - das sollte - auch - vom Planwetter abhängen:
            //  - Es muss LEICHT_BEWOELKT bis BEWOELKT sein
            //  - Die Bewölkung soll laut Planwetter abnehmen, mindestens auf
            //    LEICHT_BEWOELKT
            //  „Du trittst aus dem Wald hinaus. Purpurnes Abendrot erstreckt sich über den
            //  Horizont....“
        }

        return alt.schonLaenger().build();
    }

    // IDEA Idee zur Vereinheitlich (sofern das hilfreich ist):
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

    // FIXME Veränderungen der Temperatur
    //  "es kühlt (deutlich) ab" (Temperatur)
    //  Konzept am Hunger orientieren?
    //  Bedenken, dass es ja über den Tag immer wärmer (und auch wieder
    //  kälter) wird. Die Änderungen müssen wohl also beschrieben werden, obwohl sich die
    //  eigentliche "Tagestemperatur" nicht ändern (sofern die Temperatur nicht vorher unerheblich
    //  ist und bleibt)
    //  Vielleicht muss man doch - wie bei der Müdigkeit - den letzten
    //  Temperatur-Wert speichern?

    // FIXME Veränderungen der Bewölkung
    //  es klart auf / der Himmel bedeckt sich/ bezieht sich (Bewölkung)

    // FIXME Veränderung von Temperatur und Bewölkung: "Es hat deutlich abgekühlt und der Himmel
    //  bezieht sich."
    // FIXME "Draußen hat sich das Wetter verändert. Es hat deutlich abgekühlt und der
    //  Himmel bezieht sich."

    // FIXME Bei extremen / dramtischen Wetterlagen "Erinnerungen", ähnlich dem Hunger-Konzept?

    /**
     * Gibt alternative {@link AbstractDescription}s zurück, die sich auf "heute", "den Tag"
     * o.Ä. beziehen - soweit draußen sinnvoll, sonst eine leere Collection.
     */
    @NonNull
    @CheckReturnValue
    ImmutableCollection<AbstractDescription<?>>
    altHeuteDerTagWennDraussenSinnvoll(final AvTime time, final boolean unterOffenemHimmel) {
        // FIXME Windstärke berücksichtigen
        // FIXME Blitz und Donner berücksichtigen

        if (unterOffenemHimmel && !bewoelkung.isUnauffaellig(time.getTageszeit())) {
            return ImmutableSet.of();
        }

        // Bewoelkung muss nicht erwähnt werden
        return TEMPERATUR_DESC_DESCRIBER.altHeuteDerTagWennDraussenSinnvoll(
                getTemperatur(time), time, unterOffenemHimmel);
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
                alt.addAll(BEWOELKUNG_ADV_ANGABE_WOHIN_DESCRIBER
                        .altHinausUnterOffenenHimmel(bewoelkung, time));
            }
        } else if (temperatur.isUnauffaellig(time.getTageszeit())) {
            // Bei einer mittleren Temperatur braucht man die Temperatur nicht unbedingt zu
            // erwähnen.
            alt.addAll(TAGESZEIT_ADV_ANGABE_WOHIN_DESCRIBER
                    .altWohinHinaus(time));
        }

        if (temperatur.isUnauffaellig(time.getTageszeit())
                && bewoelkung.isUnauffaellig(time.getTageszeit())) {
            alt.addAll(TAGESZEIT_ADV_ANGABE_WOHIN_DESCRIBER
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
            alt.addAll(TEMPERATUR_ADV_ANGABE_WOHIN_DESCRIBER
                    .altWohinHinaus(temperatur, time));

            if (temperatur.isUnauffaellig(time.getTageszeit())) {
                alt.addAll(TAGESZEIT_ADV_ANGABE_WOHIN_DESCRIBER
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
            alt.addAll(TAGESZEIT_ADV_ANGABE_WOHIN_DESCRIBER
                    .altWohinHinaus(time));
        }

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
                .altBeiLichtImLicht(getBewoelkung(), time.getTageszeit(), unterOffenemHimmel);
    }

    @NonNull
    @CheckReturnValue
    ImmutableCollection<EinzelneSubstantivischePhrase> altLichtInDemEtwasLiegt(
            final AvTime time,
            final boolean unterOffenemHimmel) {
        return BEWOELKUNG_PRAEDIKATIVUM_DESCRIBER
                .altLichtInDemEtwasLiegt(getBewoelkung(), time.getTageszeit(),
                        unterOffenemHimmel);
    }

    @NonNull
    @CheckReturnValue
    Temperatur getTemperatur(final AvTime time) {
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

    // FIXME Wetterbeschreibungen als Adverbiale Angaben mit Satz-Skopus:
    //  "mit Sonnenaufgang (machts du dich auch den Weg...)"
    //  "Bei Sonnenaufgang kommt schon..."
    //  "Bei Sonnenuntergang kommst du zu..."
    //  "Als du aber am Morgen bei hellem Sonnenschein aufwachst, "
    //  "gegen Abend, als die Sonne untergegangen ist,"
    //  "Du erwachst vor Sonnenuntergang"
    //  "Bei einbrechender Nacht"

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
    //  "der Wind (raschelt in den Bäumen), und die Wolken ziehen ganz nah über deinem Haupt weg"
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

    // FIXME Wind in Kombination: "Der Himmel ist blau und eine frische Luft weht dir entgegen"
    //  - "Der Himmel ist blau, die Luft mild"

    // FIXME Aufwärmen / Auftauen
    //  "Die Sonne hat die Erde aufgetaut"
    //  "du bist halb erfroren und willst dich nur ein wenig wärmen"
    //  "du reibst die Hände"
    //  "du bist so erfroren"
    //  "dich wärmen"
    //  "du erwärmst dich"

    // FIXME Temperatur-Grenzwerte für einzelne Räume festlegen?
    //  - Im Schloss ist es vielleicht unbeheizt, aber nie maximal warm...
    //  - Am Brunnen ist es auch nie maximal warm
    //  - Maximaltemperatur und Minimaltemperatur für jeden Raum festlegen?
    //  - Aber dann braucht es Texte in der Art "Hier ist es angenehm kühl", und die Texte
    //   "es ist heiß" etc. dürfen dann nicht kommen, wenn sie außerhalb der Grenzwerte liegen.
    //   Sie sollten aber vielleicht kommen, wenn der Ort verlassen wird.
    //  "Du kommst in den Wald, und da es darin kühl und lieblich ist und die Sonne heiß
    //  brennt, so..."

    // FIXME Plan-Wetter nur dramaturgisch geändert, nicht automatisch? Oder
    //  zwei Plan-Wetter, dramaturgisch und automatisch? Oder Plan-Wetter-Priorität?!

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
}
